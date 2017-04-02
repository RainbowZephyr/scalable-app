package command;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.zaxxer.hikari.HikariDataSource;
import services.RequestHandle;
import services.ServiceRequest;


public abstract class Command implements Runnable {

    protected ServiceRequest _serviceRequest; // the formatted request
    private RequestHandle _serviceHandle; // handles the response for the user
    protected HikariDataSource _postgresDataSource;  //to get a postgres connection, call getPostgresConnection()

    public void init(HikariDataSource postgresDataSource, RequestHandle serviceHandle,
                     ServiceRequest serviceRequest) {
        _postgresDataSource = postgresDataSource;
        _serviceRequest = serviceRequest;
        _serviceHandle = serviceHandle;
    }

    public void run() {
        try {
            Map<String, Object> map = _serviceRequest.getData();
            StringBuffer strbufResponse = execute(map);
            /**
             * Result of execute will be null in case of a request arriving from the controller.
             */
            if (_serviceHandle != null) {
                _serviceHandle.send(strbufResponse); // sends back the response to the requester
            }
        } catch (Exception exp) {
            System.err.println(exp.toString());
        }
    }

    protected Connection getPostgresConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = _postgresDataSource.getConnection();
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {
            closeConnectionQuietly(connection);
        }
        return connection;
    }

    protected void closeConnectionQuietly(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (Exception exp) {
            // log this...
            exp.printStackTrace();
        }
    }

    public abstract StringBuffer execute(Map<String, Object> requestMapData) throws Exception;
}