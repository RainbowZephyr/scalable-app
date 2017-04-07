package services;

import command.Command;
import connections.Producer;
import connections.SocketConnectionFactory;
import connections.SocketConnectionToController;
import datastore.DataStoreConnectionFactory;
import exceptions.MultipleResponseException;
import thread_pools.CommandsThreadPool;
import utility.ResponseCodes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static utility.Constants.*;

/* Main Entry Class for Any Of the Independent Applications */

public class Dispatcher {
    private static Dispatcher instance = new Dispatcher();
    private Hashtable<String, Class<?>> _htblCommands, _adminHtblCommands;

    /**
     * Dispatcher is a singleton and therefore its constructor's visibility
     * should not be changed to anything other than private.
     */
    private Dispatcher() {
    }

    public static Dispatcher sharedInstance() {
        return instance;
    }

    /* Handles a request By calling the Appropriate Method (Class With Same Action)
     * Each Executed Command is handled in a separate Thread (thus the thread pool) */
    public void dispatchRequest(RequestHandle requestHandle,
                                ServiceRequest serviceRequest)
            throws IllegalAccessException,
            InstantiationException, ExecutionException,
            InterruptedException, IOException, MultipleResponseException {
        Command cmd;
        String strAction;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RequestHandle.class.getSimpleName(), requestHandle);
        params.put(ServiceRequest.class.getSimpleName(), serviceRequest);
        strAction = serviceRequest.getAction();

        Class<?> innerClass = _htblCommands.get(strAction);
        if (innerClass != null) {
            cmd = (Command) innerClass.newInstance();
            cmd.init(params);
            System.out.println("Dispatcher Caller THREAD ID: " +Thread.currentThread().getId());

            CommandsThreadPool.sharedInstance().getThreadPool().execute(cmd);
        } else {
            Response response = new Response(ResponseCodes.STATUS_NOT_IMPLEMENTED);
            response.addToResponse(APP_ID_KEY, APPLICATION_ID);
            response.addToResponse(RECEIVING_APP_ID_KEY, "Controller");
            requestHandle.send(response.toJson()); // output to the queue
        }
    }

    public void executeControllerCommand(RequestHandle requestHandle,
                                         ServiceRequest serviceRequest)
            throws IllegalAccessException,
            ExecutionException, InterruptedException, InstantiationException,
            IOException, MultipleResponseException {
        Command cmd;
        String strAction;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(RequestHandle.class.getSimpleName(), requestHandle);
        params.put(ServiceRequest.class.getSimpleName(), serviceRequest);
        strAction = serviceRequest.getAction();
        Class<?> innerClass = _adminHtblCommands.get(strAction);
        if (innerClass != null) {
            cmd = (Command) innerClass.newInstance();
            cmd.init(params);
            Thread t = new Thread(cmd);
            t.run();
        } else {
            Response response = new Response(ResponseCodes.STATUS_NOT_IMPLEMENTED);
            response.addToResponse("app_id", APPLICATION_ID);
            response.addToResponse("recieving_app_id", "Controller");
            requestHandle.send(response.toJson()); // output to the queue
        }

    }

    /* Instantiate Commands From The Configuration File & adds to a Hashtable */
    public void loadCommands(Hashtable<String, Class<?>> hashtable, String path)
            throws IOException, ClassNotFoundException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream(path);
        prop.load(in);
        in.close();
        Enumeration enumKeys = prop.propertyNames();
        String strActionName,
                strClassName;

        while (enumKeys.hasMoreElements()) {
            strActionName = (String) enumKeys.nextElement();
            strClassName = (String) prop.get(strActionName);
            Class<?> innerClass = Class.forName(strClassName);
            hashtable.put(strActionName, innerClass);
        }
    }

    /* Instantiate database Thread Pool */
    protected void loadDataStoreConnections() throws IOException, ClassNotFoundException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream("config/data_store.properties");
        prop.load(in);
        in.close();
        Enumeration enumKeys = prop.propertyNames();
        String strClassKey,
                strClassName;

        while (enumKeys.hasMoreElements()) {
            strClassKey = (String) enumKeys.nextElement();
            strClassName = (String) prop.get(strClassKey);
            Class<?> innerClass = Class.forName(strClassName);
            DataStoreConnectionFactory.sharedInstance().
                    registerDataStoreConnection(strClassKey, innerClass);
        }
    }

    public void updateCommandsTable(String key, Class<?> value,
                                    boolean adminCommand) {
        if (adminCommand) {
            _adminHtblCommands.put(key, value);
        } else {
            _htblCommands.put(key, value);
        }
    }

    public void removeCommand(String key,
                              boolean adminCommand) {
        if (adminCommand) {
            _adminHtblCommands.remove(key);
        } else {
            _htblCommands.remove(key);
        }
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

    public void init() throws IOException, ClassNotFoundException {
        _htblCommands = new Hashtable<String, Class<?>>();
        _adminHtblCommands = new Hashtable<String, Class<?>>();
        loadCommands(_htblCommands, "config/commands.properties");
        loadCommands(_adminHtblCommands, "config/admin_commands.properties");
        loadDataStoreConnections();
        loadConnections();
    }

    private void loadConnections() {
        SocketConnectionFactory.sharedInstance().registerConnection(
                SocketConnectionToController.class.getSimpleName(),
                SocketConnectionToController.sharedInstance());

        SocketConnectionFactory.sharedInstance().registerConnection(
                Producer.class.getSimpleName(),
                Producer.sharedInstance()
        );
    }
}
