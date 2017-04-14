import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by abdoo on 4/15/17.
 */
public class TestClassWrite {

    public static void main(String[]args) throws IOException {
        File file = new File("./searchapp/EchoCommand.class");
        byte[] encodedBytes = Base64.getEncoder().encode(FileUtils.readFileToByteArray(file));
        System.out.println(new String(encodedBytes));
    }
}
