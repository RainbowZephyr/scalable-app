import com.thinkaurelius.titan.core.*;
import com.thinkaurelius.titan.core.schema.Mapping;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.core.util.TitanCleanup;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TitanDB {

    final static String USER_ID_KEY = "userId", USER_NAME = "username",
            FRIEND_KEY = "friend", ID_INDEX = "byID", NAME_INDEX = "byName";
    static TitanGraph graph;

    public static void main(String[] args) throws IOException {
//        clearDatabase();
        startConnectionToTitan(); // should be called only once I think
        Long id = Long.parseLong("716284862545507294");
//        updateName(id, "7amada");
//        System.out.println(getFriends(id, 2));
//        setupSchema(); // should be called only once (sure)
//        Long a = generateUserId();
//        Long b = generateUserId();
//        Long c = generateUserId();
//        Long d = generateUserId();
//        Long e = generateUserId();
//        Long f = generateUserId();
//        Long g = generateUserId();
//
//        addUserToGraph(a, "a");
//        addUserToGraph(b, "b");
//        addUserToGraph(c, "c");
//        addUserToGraph(d, "d");
//        addUserToGraph(e, "e");
//        addUserToGraph(f, "f");
//        addUserToGraph(g, "g");
//
//        addRelationFriend(a, b);
//        addRelationFriend(a, d);
//        addRelationFriend(b, g);
//        addRelationFriend(g, d);
//        addRelationFriend(d, f);
//        addRelationFriend(a, c);
//        addRelationFriend(c, e);

        graph.io(IoCore.graphson()).writeGraph("graph.json"); // useless (just prints graph in file)
        // retrieves all users with their names containing "o" (check the method lw 3awez t3adel)
//        System.out.println(getUser("o"));
        graph.close(); // should not be called in production ela lw 7asal exception ma
    }

    /**
     * Creates an Edge in the graph corresponding to the friendship relation
     * @param userId : user who accepted the friend request
     * @param friendUserId: user who sent the request
     */
    static void addRelationFriend(long userId, long friendUserId){
        Vertex from = getUser(userId);
        Vertex to = getUser(friendUserId);
        from.addEdge(FRIEND_KEY, to).graph().tx().commit();
    }

    /**
     *
     * @param username
     * @return List of all id matching the username
     */
    static List<Long> getUser(String username){
        List<Long> list = new ArrayList<Long>();
        for (TitanIndexQuery.Result<TitanVertex> result :graph.indexQuery(NAME_INDEX,USER_NAME+":(*"+username+"*)").limit(10).vertices()){
            System.out.println(result.getElement().value(USER_NAME));
            list.add((Long) result.getElement().value(USER_ID_KEY));
        }
        return list;
    }

    /**
     *
     * @param userId
     * @return Vertex Object from the graph
     */
    static Vertex getUser(Long userId){
        return graph.query().has(USER_ID_KEY, userId).limit(1).vertices().iterator().next();
    }

    /**
     * returns a list of ids of users friends of given user
     * @param userId: userId
     * @param level: level of deepness
     */
    static List<Object> getFriends(long userId, int level){
        Vertex userNode = getUser(userId);
        return graph.traversal().V(userNode).repeat(__.out(FRIEND_KEY).aggregate("all")).
                times(level).cap("all").unfold().values(USER_ID_KEY).next(Integer.MAX_VALUE);
    }

    /**
     *
     * @param userId
     * @param level
     * @return a list of users ids (only at level: level)
     */
    static List<Object> getMutualFriendsAtLevel(long userId, int level){
        Vertex userNode = getUser(userId);

        return graph.traversal().V(userNode).repeat(__.out(FRIEND_KEY)).
                times(level).values(USER_ID_KEY).next(Integer.MAX_VALUE);
    }

    // not working
    static void updateName(long userId, String newName){

        graph.newTransaction().traversal().V(getUser(userId)).
                property(USER_NAME, newName).iterate();
        graph.tx().commit();
    }

    static void deleteUser(long userId){
        getUser(userId).remove();
    }

    static Random r = new Random();
    static long generateUserId(){
        return r.nextLong();
    }

    static void startConnectionToTitan(){
        // path to config file (it has addresses to backend & indexing engine servers)
        String dataDir = "./config/titan-cassandra-es.properties";
        // start a graph with config file
        graph = TitanFactory.open(dataDir);
    }

    static void setupSchema(){
        // management is used to define schema for the graph itself
        TitanManagement management = graph.openManagement();
        // add an edge to the schema (with multiplicity multi: each vertex could have mutiple edges
        // with label friend going out of it or in it.
        management.makeEdgeLabel("friend").multiplicity(Multiplicity.MULTI).make();
        // define the schema for each node (only name, userId)
        // with cardinality (single): so the key: userId can have only oneValue
        PropertyKey id = management.makePropertyKey(USER_ID_KEY).
                dataType(Long.class).
                cardinality(Cardinality.SINGLE).
                make();
        // same as above
        PropertyKey name = management.makePropertyKey(USER_NAME).
                dataType(String.class).
                cardinality(Cardinality.SINGLE).
                make();
        // build index on the name (to be able to search with name using elasticsearch)
        // could add uniqueness to name by adding .addKey(name).unique().
        management.buildIndex(ID_INDEX, Vertex.class).addKey(id)
                .buildCompositeIndex();
        management.buildIndex(NAME_INDEX, Vertex.class).addKey(name, Mapping.STRING.asParameter())
                .buildMixedIndex("search");
        // commit kol el 3ak elli fat da
        management.commit();
    }

    static void addUserToGraph(long userId, String username) {
        Vertex user = graph.newTransaction().addVertex();
        user.property(USER_ID_KEY, userId);
        user.property(USER_NAME, username);
        user.graph().tx().commit();
    }

    static void clearDatabase(){
        startConnectionToTitan();
        graph.close();
        TitanCleanup.clear(graph);
    }
}
