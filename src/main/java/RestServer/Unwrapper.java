package RestServer;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Unwrapper {


    @Getter
    String header;

    @Getter
    String body;

    @Getter
    String verb;

    @Getter
    String location;

    @Getter
    String version;

    @Getter
    private Map<String, String> hashMap;

    @Getter
    String Authorization;




    private String request;

    public Unwrapper(String fullRequest){
        location="empty";
        request=fullRequest;
        hashMap= new HashMap<>();
        unwrap();


    }

    private void split(){

        Scanner scanner = new Scanner(request);

        StringBuilder newHeader = new StringBuilder();

        String line;

        line = scanner.nextLine();
        retrieveFirstLine(line);
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            if(line.equals("")){break;}
            if(line.startsWith("Authorization")){
                String[] array = line.split(": ",2);
                Authorization=array[1];
                System.out.println("Auth: " + Authorization);
            }


            newHeader.append(line);
            addToHashmap(line);

        }
        header=newHeader.toString();
        if(Authorization==null)
            Authorization="no";
        //hashMap.forEach((key, value) -> System.out.println(key + ":" + value));

        StringBuilder newBody = new StringBuilder();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();

            if (line.equals("")) {
                break;
            }
           newBody.append(line);
        }
        body= newBody.toString();
        System.out.println("body: " + body);

        scanner.close();

    }
    private void unwrap(){

        split();

    }

    private void retrieveFirstLine(String line){
        String[] array = line.split(" ",3);
        verb = array[0];
        location=array[1];
        version=array[2];
    }

    private void addToHashmap(String line){

        //remove whitespaces
        line.trim();
        //line.replaceAll("\\s+", "");
        String[] stringArray = line.split(": ", 2);
        hashMap.put(stringArray[0],stringArray[1]);
        //hashMap.forEach((key, value) -> System.out.println(key + ": " + value));

    }




}
