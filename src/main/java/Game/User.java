package Game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.sql.SQLException;

public class User {



    @Getter
    public String Username;
    @Getter
    public String Password;

    @JsonIgnore
    private static String token;




    public static boolean addUserToDatabase(String json) throws JsonProcessingException, SQLException {

        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(json, User.class);
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        if(Database.checkIfUserExists(user.Username)){
            return false;
        }

        Database.insertUser(user.Username, user.Password);
        Database.closeConnection();

        return true;

    }

    public static String UserLogin(String json) throws SQLException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(json, User.class);
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        token="error";
        if(Database.checkPassword(user.Username, user.Password)){
             token= Database.getTokenWithUsername(user.Username);
        }
        Database.closeConnection();
        return token;

    }



    public static String getUserData(String name, String token) throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.getUserData(name,token);
        Database.closeConnection();
        return returnMsg;
    }

    public static String changeUserData(String name, String token, String json) throws SQLException, JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserChanges changes = objectMapper.readValue(json, UserChanges.class);

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.changeUserData(name,token,changes);
        Database.closeConnection();
        return returnMsg;
    }

    public static String getStats(String token) throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.getStats(token);
        Database.closeConnection();
        return returnMsg;


    }


    public static String getScoreboard(String token) throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.getScoreboard(token);
        Database.closeConnection();
        return returnMsg;
    }


    public static String addToBattle(String token) throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        String returnMsg=Database.addToBattle(token);
        Database.closeConnection();
        return returnMsg;

    }
}
