package Tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MainTest {


    @Test
    public void readerTest() throws IOException {

        BufferedReader reader = new BufferedReader(new StringReader("POST / HTTP/1.1\r\n" + "Host: localhost:8080\r\n"+ "Content-Length: 7\r\n" + "Accep: */*\r\n\r\n" + "Message" +"\r\n\r\n"));
        String message;

        StringBuilder fullRequest = new StringBuilder();
        int contentLength=0;
        while (true) {
            message = reader.readLine();

            if(message.equals("")){break;}

            fullRequest.append(message);
            fullRequest.append("\r\n");

            if(message.startsWith("Content-Length:")){
                contentLength = Integer.parseInt(message.replaceAll("\\D", ""));
            }

            System.out.println("srv: received: " + message);
        }

        fullRequest.append("\r\n");
        //read body
        for (int i = 0; i < contentLength; i++) {

            char temp = (char)reader.read();
            fullRequest.append(temp);

        }

        String request=fullRequest.toString();

        assertEquals("POST / HTTP/1.1\r\nHost: localhost:8080\r\nContent-Length: 7\r\nAccep: */*\r\n\r\nMessage",request);
    }
}
