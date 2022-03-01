package Game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.List;

public class Card {

    public String Name;

    public String Id;

    public Integer Damage;


    @JsonIgnore
    public String element;
    @JsonIgnore
    public String type;



    public static void createPackage(String json) throws JsonProcessingException, SQLException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<Card> pack= objectMapper.readValue(json, new TypeReference<List<Card>>(){});
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");

        for(int i= 0; i<5; i++){
            if(pack.get(i).Name.contains("Spell")){
                pack.get(i).type="Spell";
            }else{
                pack.get(i).type="Monster";
            }

            if(pack.get(i).Name.contains("Fire")){
                pack.get(i).element="Fire";
            }else if(pack.get(i).Name.contains("Water")){
                pack.get(i).element="Water";
            }else{
                pack.get(i).element="Normal";
            }
        }

            Database.addPackToDatabase(pack);


        Database.closeConnection();


    }


    public static String buyPack(String token) throws SQLException {
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.buyPack(token);
        Database.closeConnection();

        return returnMsg;
    }


    public static String seeOwnedCards(String token) throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.ownedCards(token);
        Database.closeConnection();
        return returnMsg;

    }
    public static String seeDeck(String token) throws SQLException {
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        
        String returnMsg=Database.getDeck(token);
        Database.closeConnection();
        return returnMsg;
    }


    public static String configureDeck(String token,String body) throws JsonProcessingException, SQLException {

        ObjectMapper objectMapper = new ObjectMapper();
        String returnMsg="Error,not enough cards";
        List<String> ids = objectMapper.readValue(body,new TypeReference<List<String>>(){});

        if(ids.size()==4){
            Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
            returnMsg=Database.addCardsToDeck(token, ids);
            Database.closeConnection();

        }

        return returnMsg;

    }


    /*public static String offerCard(String token, String json) throws JsonProcessingException, SQLException {

        ObjectMapper objectMapper = new ObjectMapper();
        TradeOffer card = objectMapper.readValue(json, TradeOffer.class);
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.offerCard(token,card);

        Database.closeConnection();
        return returnMsg;




    }

    public static String seeOffers(String token) throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.seeOffers(token);

        Database.closeConnection();
        return returnMsg;
    }

    public static String removeOffer(String token, String card) throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.deleteOffer(token,card);

        Database.closeConnection();
        return returnMsg;
    }

    public static String acceptOffer(String token, String id) throws SQLException {
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.acceptOffer(token,id);

        Database.closeConnection();
        return returnMsg;

    }
*/
    public static String salvageCard(String token, String json) throws JsonProcessingException, SQLException {

        ObjectMapper objectMapper = new ObjectMapper();
        String returnMsg="wrong amount of cards";
        List<String> ids = objectMapper.readValue(json,new TypeReference<List<String>>(){});

        if(ids.size()==4){
            Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
            returnMsg=Database.salvageCards(token, ids);
            Database.closeConnection();

        }

        return returnMsg;
    }
}
