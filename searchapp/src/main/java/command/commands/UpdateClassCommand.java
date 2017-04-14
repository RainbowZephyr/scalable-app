package command.commands;

import com.google.gson.Gson;
import command.Command;
import command.CommandClassLoader;
import org.apache.commons.io.FileUtils;
import services.Dispatcher;
import services.Response;
import utility.Constants;
import utility.ResponseCodes;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static utility.Constants.COMMAND_CLASS_BINARY_KEY;
import static utility.Constants.COMMAND_PACKAGE_NAME_KEY;

public class UpdateClassCommand extends Command {
    public StringBuffer execute(Map<String, Object> requestMapData) throws Exception {
        String className = (String) requestMapData.get(Constants.CLASS_NAME);
        String packageName = (String) requestMapData.get(COMMAND_PACKAGE_NAME_KEY);
        String classEncodedBinaries = (String) requestMapData.get(COMMAND_CLASS_BINARY_KEY);
        writeFile(classEncodedBinaries, className, packageName);
        // read binaries and write to directory
        ClassLoader parentClassLoader = CommandClassLoader.class.getClassLoader();
        CommandClassLoader classLoader = new CommandClassLoader(parentClassLoader);
        Class<?> commandClass = classLoader.loadClass(packageName+className);
        Dispatcher.sharedInstance().updateClass(commandClass);
        Response response = new Response(ResponseCodes.STATUS_OK);
        return response.toJson();
    }

    private void writeFile(String classEncodedBinaries, String className, String packageName)
            throws IOException {
        String path = "./searchapp/target/classes/"+packageName.replace('.', File.separatorChar) + className + ".class";
        byte[] decodedBytes = Base64.getDecoder().decode(classEncodedBinaries);
        FileUtils.writeByteArrayToFile(new File(path), decodedBytes);
        System.out.println(path);
    }
}
