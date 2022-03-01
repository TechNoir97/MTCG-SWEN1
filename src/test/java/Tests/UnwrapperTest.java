package Tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UnwrapperTest {

    @Test
    public void splitTest() {

        String request = "POST / HTTP/1.1\r\nHost: localhost:8080\r\nContent-Length: 7\r\nAccep: */*\r\n\r\nMessage";
        Scanner scanner = new Scanner(request);

        StringBuilder newHeader = new StringBuilder();

        String line;
        //removes first line, to be testet later
        line = scanner.nextLine();

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if (line.equals("")) {
                break;
            }

            newHeader.append(line + "\r\n");
        }
        String header = newHeader.toString();
        assertEquals("Host: localhost:8080\r\nContent-Length: 7\r\nAccep: */*\r\n", header);

        StringBuilder newBody = new StringBuilder();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            if (line.equals("")) {
                break;
            }
            newBody.append(line);
        }
        String body = newBody.toString();
        assertEquals("Message", body);

        scanner.close();

    }

    @Test
    public void retrieveFirstLineTest() {
        String[] array = "POST / HTTP/1.1".split(" ", 3);
        assertEquals("POST", array[0]);
        assertEquals("/", array[1]);
        assertEquals("HTTP/1.1", array[2]);
    }

    @Test
    public void addToHashmap(){

        Map<String, String> hashMap = new HashMap<>();
        String headerInfo=" Content-Length: 7 ".trim();
        String[] stringArray = headerInfo.split(":", 2);
        hashMap.put(stringArray[0], stringArray[1]);

        for(Map.Entry<String,String> entry : hashMap.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            assertEquals("Content-Length 7",key+value);
        }


    }


}





/*



    private void addToHashmap(String line){

        //remove whitespaces
        line.trim();
        //line.replaceAll("\\s+", "");
        String[] stringArray = line.split(": ", 2);
        hashMap.put(stringArray[0],stringArray[1]);
        //hashMap.forEach((key, value) -> System.out.println(key + ": " + value));

    }*/