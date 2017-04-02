package controller;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class Apps {
	public static ArrayList<App> apps;
	
	public static String tcpSend(String ip, int port, int timeout, String content)
    {
        String ipaddress = ip;
        int portnumber = port;
        String modifiedSentence;
        Socket clientSocket;
        try
        {
            clientSocket = new Socket(ipaddress, portnumber);
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes(content + '\n');
            clientSocket.setSoTimeout(timeout);
            modifiedSentence = inFromServer.readLine();
            clientSocket.close();
            outToServer.close();
            inFromServer.close();
        }
        catch (Exception exc)
        {
            modifiedSentence = "";
        }
        return modifiedSentence;
    }

}
