package command.commands;

import command.Command;
import command.CommandClassLoader;
import services.Dispatcher;
import utility.Constants;

import java.util.Map;

/**
 * Created by Ahmed Abdelbadie on 4/2/17.
 */
public class UpdateClassCommand extends Command {
    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        String className = (String) mapUserData.get(Constants.CLASS_NAME);
        ClassLoader parentClassLoader = CommandClassLoader.class.getClassLoader();
        CommandClassLoader classLoader = new CommandClassLoader(parentClassLoader);

        Class<?> commandClass = classLoader.loadClass(className);

        Dispatcher.sharedInstance().updateClass(commandClass);
        return null;
    }
}
