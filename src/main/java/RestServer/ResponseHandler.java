package RestServer;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ResponseHandler {


    private Socket client;
    public ResponseHandler(Socket listener) {

        client=listener;

    }

    public void sendResponse(String message, int code) throws IOException {

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

                writer.write(buildResponse(message,code));
                writer.flush();

    }
    private String buildResponse(String message, int code){

        StringBuilder fullResponse = new StringBuilder();
        fullResponse.append("HTTP/1.1 " + code +"\r\n");
        fullResponse.append("ContentType: plain/text\r\n");
        fullResponse.append("Content-Length: " + message.length());
        fullResponse.append("\r\n\r\n");
        fullResponse.append(message);
        fullResponse.append("\r\n\r\n");

        return fullResponse.toString();
    }



}
