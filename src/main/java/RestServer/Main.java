package RestServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main implements Runnable {

    private static ServerSocket listener = null;

    public static void main(String[] args) {
        System.out.println("start server");

        try {
            listener = new ServerSocket(10001, 5);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        ListOfMessages messageList= new ListOfMessages();

        Runtime.getRuntime().addShutdownHook(new Thread(new Main()));

        try {
            while (true) {
                Socket client = listener.accept();


                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
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

                //handle request

                RequestContext requestContext=new RequestContext(request, messageList, client);
                requestContext.requestHandler();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listener = null;
        System.out.println("close server");
    }
}