package services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.*;
/* Main Entry Class for Any Of the Independent Applications */

import com.zaxxer.hikari.HikariDataSource;
import command.Command;
import command.CommandClassLoader;

public class Dispatcher {
    private static Dispatcher instance = new Dispatcher();
    private Hashtable<String, Class<?>> _htblCommands;
    private ThreadPoolExecutor _threadPoolCmds;
    private HikariDataSource _postgresDataSource;
    private final int poolSize = 5;
    private final int postgresPoolSize = 5;

    /**
     * Dispatcher is a singleton and therefore its constructor's visibility
     * should not be changed to anything other than private.
     */
    private Dispatcher() {
    }

    public static Dispatcher sharedInstance() {
        return instance;
    }

    public ThreadPoolExecutor get_threadPoolCmds() {
        return _threadPoolCmds;
    }

    /* Handles a request By calling the Appropriate Method (Class With Same Action)
     * Each Executed Command is handled in a separate Thread (thus the thread pool) */
    public void dispatchRequest(RequestHandle requestHandle,
                                ServiceRequest serviceRequest)
            throws IllegalAccessException, InstantiationException {
        Command cmd;
        String strAction;
        strAction = serviceRequest.getAction();

        Class<?> innerClass = _htblCommands.get(strAction);
        cmd = (Command) innerClass.newInstance();
        cmd.init(_postgresDataSource, requestHandle, serviceRequest);
        _threadPoolCmds.execute(cmd);
    }

    /* Instantiate Commands From The Configuration File & adds to a Hashtable */
    public void loadCommands() throws IOException, ClassNotFoundException {
        _htblCommands = new Hashtable<String, Class<?>>();
        Properties prop = new Properties();
        InputStream in = new FileInputStream("config/commands.properties");
        prop.load(in);
        in.close();
        Enumeration enumKeys = prop.propertyNames();
        String strActionName,
                strClassName;

        while (enumKeys.hasMoreElements()) {
            strActionName = (String) enumKeys.nextElement();
            strClassName = (String) prop.get(strActionName);
            Class<?> innerClass = Class.forName(strClassName);
            _htblCommands.put(strActionName, innerClass);
        }
    }

    /* Instantiate database Thread Pool */
    protected void loadHikari(String strAddress, int nPort,
                              String strDBName,
                              String strUserName, String strPassword) {

        _postgresDataSource = new HikariDataSource();
        _postgresDataSource.setJdbcUrl("jdbc:postgresql://" + strAddress + ":" + nPort + "/" + strDBName);
        _postgresDataSource.setUsername(strUserName);
        _postgresDataSource.setPassword(strPassword);
        _postgresDataSource.setMaximumPoolSize(postgresPoolSize);
    }

    /* Instantiate the Thread Pool */
    public void loadThreadPool() {
        _threadPoolCmds = new ThreadPoolExecutor(poolSize, poolSize, 0,
                TimeUnit.NANOSECONDS,
                new LinkedBlockingDeque<Runnable>());
    }

    public void updateCommandsTable(String key, Class<?> value) {
        _htblCommands.put(key, value);
    }

    public void removeCommand(String key, Class<?> value) {
        _htblCommands.remove(key);
    }

    public void updateClass(Class<?> value) {
        String commandName = "";
        for (String key : _htblCommands.keySet()) {
            Class<?> commandClass = _htblCommands.get(key);
            if (commandClass.getName().equals(value)) {
                commandName = key;
                break;
            }
        }

        if (commandName.length() > 0) {
            _htblCommands.put(commandName, value);
        }
    }

    public void init(String postgresAddress, int postgresPort,
                     String postgresDBName,
                     String postgresUserName,
                     String postgresPassword) throws Exception {
        loadThreadPool();
        loadCommands();
        loadHikari(postgresAddress, postgresPort, postgresDBName, postgresUserName, postgresPassword);
    }
}
