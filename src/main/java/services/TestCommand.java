package services;

import services.Command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TestCommand extends Command {
    public StringBuffer execute(Map<String, Object> mapUserData) throws Exception {
        System.out.println("ReCompiled");
        return new StringBuffer((String) mapUserData.get("echo"));
    }
}
