package services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.*;
/* Main Entry Class for Any Of the Independent Applications */

public class Dispatcher {
    private static Dispatcher instance = new Dispatcher();
    private Hashtable<String, Class<?>> _htblCommands;
    private ThreadPoolExecutor _threadPoolCmds;
    private final int poolSize = 5;

    private Dispatcher() {} // no one should be able to instantiate it

    ThreadPoolExecutor get_threadPoolCmds() {
        return _threadPoolCmds;
    }

    public static Dispatcher sharedInstance() {
        return instance;
    } // only one instance available

    /* Handles a request By calling the Appropriate Method (Class With Same Action)
     * Each Executed Command is handled in a separate Thread (thus the thread pool) */
    public void dispatchRequest(RequestHandle requestHandle,
                                ServiceRequest serviceRequest )
            throws IllegalAccessException, InstantiationException {
        Command	cmd;
        String	strAction;
        strAction = serviceRequest.getAction( );

        Class<?> innerClass = _htblCommands.get( strAction );
        cmd = (Command) innerClass.newInstance();
        cmd.init( requestHandle, serviceRequest );
        _threadPoolCmds.execute( cmd );
    }

    /* Loads Commands From The Configuration File & adds to a Hashtable */
    public void loadCommands( ) throws IOException, ClassNotFoundException {
        _htblCommands   = new Hashtable<String, Class<?>>();
        Properties prop = new Properties();
        InputStream in  = new FileInputStream("config/commands.properties");
        prop.load( in );
        in.close( );
        Enumeration enumKeys = prop.propertyNames( );
        String  strActionName,
                strClassName;

        while( enumKeys.hasMoreElements() ){
            strActionName = (String)enumKeys.nextElement();
            strClassName  = (String)prop.get(strActionName);
            Class<?> innerClass = Class.forName(strClassName);
            _htblCommands.put(strActionName, innerClass);
        }
    }

    /* Instantiate the Thread Pool */
    void loadThreadPool(){
        _threadPoolCmds = new ThreadPoolExecutor(poolSize, poolSize, 0,
                TimeUnit.NANOSECONDS,
                new LinkedBlockingDeque<Runnable>());
    }

    public void addCommand(String key, Class<?> value){
        _htblCommands.put(key, value);
    }

    public void updateCommand(String key, Class<?> value){
        _htblCommands.put(key, value);
    }

    public void removeCommand(String key, Class<?> value){
        _htblCommands.remove(key);
    }

    public void updateClass(String key, Class<?> value) throws ClassNotFoundException {
    }

    public void init() throws Exception{
        loadThreadPool();
        loadCommands();
    }
}
