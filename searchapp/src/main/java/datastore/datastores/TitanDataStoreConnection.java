package datastore.datastores;

import com.thinkaurelius.titan.core.*;
import com.thinkaurelius.titan.core.TitanIndexQuery.Result;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import datastore.DataStoreConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import services.Response;
import utility.ResponseCodes;

import java.io.*;
import java.util.*;

public class TitanDataStoreConnection extends DataStoreConnection {

	private TitanGraph graph;
	final private String USER_ID_KEY = "userId", USER_NAME = "username", FRIEND_KEY = "friend", ID_INDEX = "byID",
			NAME_INDEX = "byName";
	final private String PROPS_PATH = "./searchapp/config/titan_properties.properties";

	@Override
	public StringBuffer execute(Map<String, Object> parameters) throws Exception {
		this.connect();

		List<?> result = null;

		switch (parameters.get("action").toString()) {
		case "add_user":
			this.addUser(Long.parseLong((String) parameters.get("user_id")), (String) parameters.get("user_name"));
			break;
		case "search_by_name":
			result = this.searchUserByName((String) parameters.get("user_name"));
			break;
		case "add_friend":
			this.addFriend(Long.parseLong((String) parameters.get("user_id")), Long.parseLong((String) parameters.get("friend_id")));
			break;
		case "get_friends_at":
			result = this.getFriendsAt(Long.parseLong((String) parameters.get("user_id")), (Integer) parameters.get("at"));
			break;
		case "get_friends_up_to":
			result = this.getFriendsUpTo(Long.parseLong((String) parameters.get("user_id")), (Integer) parameters.get("at"));
			break;
		case "remove_user":
			this.removeUser(Long.parseLong((String) parameters.get("user_id")));
			break;
		case "remove_friend":
			break;
		case "clear_database":
			this.clear();
			break;
		default:
			break;
		}
		Response response = new Response(ResponseCodes.STATUS_OK);
		if (result != null) {
			Map<String, List<?>> resultMap = new HashMap<String, List<?>>();
			resultMap.put("results", result);
			response.addToResponse("response_load", resultMap);
			return response.toJson();
		}

		return response.toJson();
	}

	/**
	 * Connects to the graph database using configuration stored in a
	 * configuration file.
	 */
	private void connect() {
		String dataDir = "./searchapp/config/titan-cassandra-es.properties";
		this.graph = TitanFactory.open(dataDir);

		this.initialize();
	}

	/**
	 * Initializes the graph database by defining a schema.
	 */
	private void initialize() {
		String propertiesPath = PROPS_PATH;
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			properties.load(in);
			in.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String initializedString = properties.getProperty("did_initialize_schema");
		Boolean initialized = Boolean.parseBoolean(initializedString);
		if (initialized) {
			System.out.println("Titan Database Already Initialized");
			return;
		}

		/**
		 * Used to define the schema of the graph.
		 */
		TitanManagement management = this.graph.openManagement();
		// add an edge to the schema (with multiplicity multi: each vertex could
		// have mutiple edges
		// with label friend going out of it or in it.
		management.makeEdgeLabel("friend").multiplicity(Multiplicity.MULTI).make();
		// define the schema for each node (only name, userId)
		// with cardinality (single): so the key: userId can have only oneValue
		PropertyKey id = management.makePropertyKey(USER_ID_KEY).dataType(Long.class).cardinality(Cardinality.SINGLE)
				.make();
		PropertyKey name = management.makePropertyKey(USER_NAME).dataType(String.class).cardinality(Cardinality.SINGLE)
				.make();
		// build index on the name (to be able to search with name using
		// elasticsearch)
		// could add uniqueness to name by adding .addKey(name).unique().
		management.buildIndex(ID_INDEX, Vertex.class).addKey(id).buildCompositeIndex();
		management.buildIndex(NAME_INDEX, Vertex.class).addKey(name, Mapping.STRING.asParameter())
				.buildMixedIndex("search");

		management.commit();

		properties.setProperty("did_initialize_schema", "true");
		File file = new File(propertiesPath);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			properties.store(out, "Titan properties");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new vertex to the graph and assigns the userId and userName
	 * properties as necessary.
	 * 
	 * @param userId
	 *            The value of the userId property.
	 * @param userName
	 *            The value of the userName property.
	 */
	private void addUser(long userId, String userName) {
		System.out.println("Adding: " + userId + " - " + userName);
		Vertex user = this.graph.newTransaction().addVertex();
		user.property(USER_ID_KEY, userId);
		user.property(USER_NAME, userName.toLowerCase());
		user.graph().tx().commit();
	}

	/**
	 * Creates an edge in the graph corresponding to the friendship relation.
	 * 
	 * @param userId
	 *            user who accepted the friend request
	 * @param friendId
	 *            user who sent the request
	 */
	private void addFriend(long userId, long friendId) {
		System.out.println("Connecting: " + userId + " with: " + friendId);
		Vertex from = this.searchUserById(userId);
		Vertex to = this.searchUserById(friendId);
		from.addEdge(FRIEND_KEY, to).graph().tx().commit();
	}

	/**
	 * Removes a user (vertex) from the graph along with all of its friends
	 * (edges).
	 * 
	 * @param userId
	 *            The value of the userId property.
	 */
	private void removeUser(long userId) {
		this.searchUserById(userId).remove();
		this.graph.tx().commit();
	}

	/**
	 * Searches for a user (vertex) by a given userId value.
	 * 
	 * @param userId
	 *            The value to search for.
	 * @return The user (vertex) if found.
	 */
	private Vertex searchUserById(Long userId) {
		return (Vertex) graph.query().has(USER_ID_KEY, userId).limit(1).vertices().iterator().next();
	}

	/**
	 * Searches for users (vertices) by a given userName value.
	 * 
	 * @param userName
	 *            The value to search for.
	 * @return A list of users (vertices) at most ten, if found.
	 */
	private List<Long> searchUserByName(String userName) {
		List<Long> list = new ArrayList<Long>();
		Iterable<Result<TitanVertex>> vertices = this.graph.indexQuery(NAME_INDEX, USER_NAME + ":(*" + userName + "*)")
				.limit(10).vertices();

		for (TitanIndexQuery.Result<TitanVertex> result : vertices) {
//			System.out.println("LOOP: " + result.getElement().value(USER_NAME));
			list.add(result.getElement().value(USER_ID_KEY));
		}
		return list;
	}

	private List<Object> getFriendsAt(long userId, int level) {
		Vertex userNode = this.searchUserById(userId);

		return this.graph.traversal().V(userNode).repeat(__.both(FRIEND_KEY)).times(level).hasId().values(USER_ID_KEY)
				.next(Integer.MAX_VALUE);
	}

	private List<Object> getFriendsUpTo(long userId, int level) {
		Vertex userNode = this.searchUserById(userId);
		return this.graph.traversal().V(userNode).repeat(__.both(FRIEND_KEY).aggregate("all")).times(level).cap("all")
				.unfold().values(USER_ID_KEY).next(Integer.MAX_VALUE);
	}

	/**
	 * Used only for testing purposes.
	 */
	private void clear() {
		connect();
		this.graph.close();
		TitanCleanup.clear(this.graph);
		
		String propertiesPath = PROPS_PATH;
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			properties.load(in);
			in.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		properties.setProperty("did_initialize_schema", "false");
		File file = new File(propertiesPath);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			properties.store(out, "Titan properties");
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
