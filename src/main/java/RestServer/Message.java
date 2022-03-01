package RestServer;

import java.util.Map;

public class Message {

    Map<String,String> myHeader;
    String myBody;

    Message(Map<String,String> header, String body)
    {
        myHeader=header;
        myBody=body;

    }
}
