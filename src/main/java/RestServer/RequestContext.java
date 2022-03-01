package RestServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Game.Battle;
import Game.Card;
import Game.Database;
import Game.User;

import javax.swing.plaf.synth.SynthDesktopIconUI;

public class RequestContext {


    String request;

    ListOfMessages list;
    private static Socket client;

    public RequestContext(String fullRequest, ListOfMessages messageList, Socket listener) {
        request=fullRequest;
        list= messageList;
        client= listener;
    }

    public void requestHandler() throws IOException, SQLException {


        Unwrapper unwrapper= new Unwrapper(request);
        ResponseHandler responseHandler=new ResponseHandler(client);

        switch(unwrapper.getVerb()) {
            case "POST":
                String temp=unwrapper.getLocation();


                switch(temp){


                    case "/users":
                        if(User.addUserToDatabase(unwrapper.getBody())){

                            responseHandler.sendResponse("Added User", 201);
                        }else{
                            responseHandler.sendResponse("User could not be added", 400);
                        }

                        break;


                    case "/sessions":
                        String token=User.UserLogin(unwrapper.getBody());
                        if(token!= "error"){

                            responseHandler.sendResponse("Succesfully logged in, Token:"+token, 201);
                        }else{
                            responseHandler.sendResponse("Username or password are wrong", 400);
                        }

                        break;

                    case "/packages":
                        if(unwrapper.Authorization.equals(Database.getAdminToken())) {
                            Card.createPackage(unwrapper.getBody());
                            responseHandler.sendResponse("Added Package", 201);
                        }else{responseHandler.sendResponse("no permission", 400);}


                        break;

                    case "/transactions/packages":
                        responseHandler.sendResponse(Card.buyPack(unwrapper.Authorization), 201);
                        break;


                    case "/battles":
                        if(unwrapper.Authorization=="no") {

                            responseHandler.sendResponse("no Token", 400);
                        }else{
                            User.addToBattle(unwrapper.Authorization);
                            boolean startBattle= Battle.checkIfTwoPlayerReady();
                            if(startBattle){
                                Battle battle=new Battle();
                                responseHandler.sendResponse(battle.startBattle(), 201);
                            }

                        }
                        break;
                    case "/tradings":
                        System.out.println("Trade not yet implemented");
                        /*if(unwrapper.Authorization=="no"){

                            responseHandler.sendResponse("no Token", 400);
                        }else{
                            responseHandler.sendResponse(Card.offerCard(unwrapper.Authorization,unwrapper.getBody()), 201);
                        }*/




                        break;


                }

                if(temp.startsWith("/tradings/")) {

                    /*if(unwrapper.Authorization=="no"){

                        responseHandler.sendResponse("no Token", 400);
                    }else{
                        String[] array = temp.split("/",3);
                        String cardName=array[2];
                        responseHandler.sendResponse(Card.acceptOffer(unwrapper.Authorization,cardName), 201);
                    }*/
                    System.out.println("Trade not yet implemented");
                }

                break;
            case "GET":
                temp=unwrapper.getLocation();


                switch(temp){

                    case "/cards":

                        if(unwrapper.Authorization=="no"){

                            responseHandler.sendResponse("no Token", 400);
                        }else{
                            responseHandler.sendResponse(Card.seeOwnedCards(unwrapper.Authorization), 201);
                        }
                    case "/deck":
                        if(unwrapper.Authorization=="no") {

                            responseHandler.sendResponse("no Token", 400);
                        }else{
                            responseHandler.sendResponse(Card.seeDeck(unwrapper.Authorization), 201);

                        }
                        break;
                    case "/stats":
                        if(unwrapper.Authorization=="no") {

                            responseHandler.sendResponse("no Token", 400);
                        }else{
                            responseHandler.sendResponse(User.getStats(unwrapper.Authorization), 201);

                        }
                        break;
                    case "/score":
                        if(unwrapper.Authorization=="no") {

                            responseHandler.sendResponse("no Token", 400);
                        }else{
                            responseHandler.sendResponse(User.getScoreboard(unwrapper.Authorization), 201);

                        }
                        break;
                    case "/tradings":
                        /*if(unwrapper.Authorization=="no"){

                            responseHandler.sendResponse("no Token", 400);
                        }else{
                            responseHandler.sendResponse(Card.seeOffers(unwrapper.Authorization), 201);

                        }*/
                        System.out.println("Trade not yet implemented");



                        break;



                }


                if(temp.startsWith("/users/")){
                    String[] array = temp.split("/",3);
                    String name=array[2];
                    responseHandler.sendResponse(User.getUserData(name,unwrapper.Authorization),201);

                }

                //Code from first Project

               /* if(temp.equals("/")){
                    responseHandler.sendResponse("ERROR",404);
                }

                else if(temp.equals("/messages")){
                    responseHandler.sendResponse(list.getAllMessages(),200);

                }
                else if(temp.startsWith("/messages/")){

                    String numberOnly= temp.replaceAll("[^0-9]", "");
                    int index= Integer.parseInt(numberOnly);

                    responseHandler.sendResponse(list.getMessage(index),200);
                }else{responseHandler.sendResponse("ERROR",404); }
                */
                break;


            case "PUT":
                temp=unwrapper.getLocation();

                switch(temp){

                    case "/deck":

                    if(unwrapper.Authorization=="no") {

                        responseHandler.sendResponse("no Token", 404);
                    }else{
                        responseHandler.sendResponse(Card.configureDeck(unwrapper.Authorization, unwrapper.getBody()), 201);

                    }
                    break;


                }

                if(temp.startsWith("/users/")){
                    String[] array = temp.split("/",3);
                    String name=array[2];
                    responseHandler.sendResponse(User.changeUserData(name,unwrapper.Authorization, unwrapper.getBody()),201);

                }


                /*

                String changeTemp=unwrapper.getLocation();
                if(changeTemp.startsWith("/messages/")){

                    String numberOnly= changeTemp.replaceAll("[^0-9]", "");
                    int index= Integer.parseInt(numberOnly);

                    Message changeMessage= new Message(unwrapper.getHashMap(), unwrapper.getBody());
                    if(list.updateMessage(index,changeMessage)){
                        responseHandler.sendResponse("Message "+numberOnly+" changed",200);
                    }else{ responseHandler.sendResponse("ERROR",404); }
                }else{responseHandler.sendResponse("ERROR",405);}

                 */

                break;
            case "DELETE":
                temp=unwrapper.getLocation();

                if(temp.startsWith("/tradings/")) {

                    /*if(unwrapper.Authorization=="no"){

                        responseHandler.sendResponse("no Token", 400);
                    }else{
                        String[] array = temp.split("/",3);
                        String cardName=array[2];
                        responseHandler.sendResponse(Card.removeOffer(unwrapper.Authorization,cardName), 201);
                    }*/
                    System.out.println("Trade not yet implemented");
                }

                if(temp.equals("/salvage")){


                    if(unwrapper.Authorization=="no") {

                        responseHandler.sendResponse("no Token", 404);
                    }else{
                        responseHandler.sendResponse(Card.salvageCard(unwrapper.Authorization, unwrapper.getBody()), 201);

                    }

                }

                /*
                String delTemp=unwrapper.getLocation();
                if(delTemp.startsWith("/messages/")){
                    delTemp= delTemp.replaceAll("[^0-9]", "");
                    int index= Integer.parseInt(delTemp);
                    if(list.removeMessage(index)){
                        responseHandler.sendResponse("Message "+delTemp+" deleted",200);
                    }else{responseHandler.sendResponse("ERROR",404);}

                }else{responseHandler.sendResponse("ERROR",405);}

                 */

                break;
            default:
                // code block
        }

    }
}
