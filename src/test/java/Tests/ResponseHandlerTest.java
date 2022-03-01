package Tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ResponseHandlerTest {

    @Test
    public void buildResponse(){

        StringBuilder fullResponse = new StringBuilder();
        fullResponse.append("POST " + "HTTP/1.1 " + "200" +"\r\n");
        fullResponse.append("ContentType: plain/text\r\n");
        fullResponse.append("Content-Length: 7");
        fullResponse.append("\r\n\r\n");
        fullResponse.append("Message");
        fullResponse.append("\r\n\r\n");




        StringBuilder fullRequest = null;
        try (BufferedReader reader = new BufferedReader(new StringReader(fullResponse.toString()))) {
            String message;

            fullRequest = new StringBuilder();
            int contentLength = 0;
            while (true) {
                message = reader.readLine();

                if (message.equals("")) {
                    break;
                }

                fullRequest.append(message);
                fullRequest.append("\r\n");

                if (message.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(message.replaceAll("\\D", ""));
                }

                System.out.println("srv: received: " + message);
            }

            fullRequest.append("\r\n");
            //read body
            for (int i = 0; i < contentLength; i++) {

                char temp = (char) reader.read();
                fullRequest.append(temp);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String request=fullRequest.toString();

        assertEquals("POST HTTP/1.1 200\r\nContentType: plain/text\r\nContent-Length: 7\r\n\r\nMessage",request);

    }
}
