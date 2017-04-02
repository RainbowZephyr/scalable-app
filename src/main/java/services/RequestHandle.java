package services;

public class RequestHandle {
    private StringBuffer stringBuffer;
    public void send(StringBuffer stringBuffer){
        this.stringBuffer = stringBuffer;
        System.out.println(stringBuffer);
        // some how send this to its destination (should be put in the corresponding queue)
    }

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }
}