package command.commands;

import command.Command;

import java.util.Map;

public class EchoCommand extends Command {
    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        System.out.println("Recompiled");
        return new StringBuffer((String) mapUserData.get("echo"));
    }
}
