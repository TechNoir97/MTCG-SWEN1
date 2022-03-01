package Game;



import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Database {

    private static Connection _connection;


    public static void openConnection(String url, String user, String password) throws SQLException {
        _connection = DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection() throws SQLException {
        _connection.close();
        _connection = null;
    }


    public static boolean checkIfUserExists(String username) throws SQLException {
        PreparedStatement statement = _connection.prepareStatement("""
            select Username from "Data".player 
            where(Username= ?)
            """);

        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        boolean entry=true;
        if (!resultSet.next()){
            entry=false;
        }
        return entry;

    }

    public static boolean checkPassword(String username,String password) throws SQLException {
        PreparedStatement statement = _connection.prepareStatement("""
            select Username from "Data".player 
            where(Username= ? and Password= ?)
            """);

        statement.setString(1, username);
        statement.setString(2, password);


        ResultSet resultSet = statement.executeQuery();
        boolean entry=true;
        if (!resultSet.next()){
            entry=false;
        }
        return entry;

    }


    public static void insertUser(String username, String password) {
        String token="Basic "+username+"-mtcgToken";
        try {

            _connection
                    .createStatement();


            PreparedStatement statement = _connection.prepareStatement("""
            insert into "Data".player 
            (Username, Password, token ) 
            values(?, ?, ?)
            """);

                statement.setString(1, username);
                statement.setString(2, password);
                statement.setString(3, token);
                statement.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void addPackToDatabase(List<Card> pack){

        try {
            int packID=getCurrentPackID()+1;

            _connection
                    .createStatement();


            PreparedStatement statement = _connection.prepareStatement("""
            insert into "Data".cards 
            (cardid, name, damage, element, type, owner, packid) 
            values(?, ?, ?, ?, ?, ?, ?)
            """);

            for(var item : pack){
                statement.setString(1, item.Id);
                statement.setString(2, item.Name);
                statement.setInt(3, item.Damage);
                statement.setString(4, item.element);
                statement.setString(5, item.type);
                statement.setString(6, "admin");
                statement.setInt(7, packID);

                statement.execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static int getCurrentPackID() throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
            select max(packid) from "Data".cards 
            """);

        ResultSet resultSet = statement.executeQuery();
        int id =0;
        while(resultSet.next()){

             id=resultSet.getInt(1);

        }
        return id;

    }

    private static int getOldestPackID() throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
            select min(packid) from "Data".cards 
            where owner= ?
            """);

        statement.setString(1,"admin");
        ResultSet resultSet = statement.executeQuery();
        int id = 0;
        while(resultSet.next()){

            id=resultSet.getInt(1);
        }
        System.out.println(id);
        return id;

    }



    public static String buyPack(String token) throws SQLException {

        int packID = getOldestPackID();

        if(packID == 0){

            return "no more packs available";

        }

        String owner= getUsernameFromToken(token);
        String returnMsg="Error";

        try {
            int coins=checkCoins(owner);

            if(coins>=5){

                _connection
                        .createStatement();

                PreparedStatement statement = _connection.prepareStatement("""
            update "Data".cards 
            set owner = ?
            where packid = ?
            """);

                statement.setString(1,owner);
                statement.setInt(2, packID);
                statement.execute();

            }else{
                return "not enough coins";
            }

        changeCoins(owner, 5);
            returnMsg="Successfully bought a Pack";

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return returnMsg;

    }

    public static int checkCoins(String username) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
            select coins from "Data".player 
            where username = ?
            """);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        int coins=0;
        while(resultSet.next()){

            coins=resultSet.getInt(1);
        }
        return coins;

    }
    public static void changeCoins(String owner, int coins) throws SQLException {

        _connection
                .createStatement();

        PreparedStatement statement = _connection.prepareStatement("""
            update "Data".player 
            set coins = coins -?
            where username = ?
            """);

        statement.setInt(1,coins);
        statement.setString(2, owner);

        statement.execute();

    }

    public static boolean checkIfPacksLeft(){


            return true;

    }

    public static String getUsernameFromToken(String token) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
            select username from "Data".player 
            where token = ?
            """);
        statement.setString(1, token);
        ResultSet resultSet = statement.executeQuery();
        String owner="";
        while(resultSet.next()){

            owner=resultSet.getString(1);
        }
        return owner;

    }

    public static String getTokenWithUsername(String username) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
            select token from "Data".player 
            where username = ?
            """);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        String owner="";
        while(resultSet.next()){

            owner=resultSet.getString(1);
        }
        return owner;

    }

    public static String ownedCards(String token) throws SQLException {

        String owner=getUsernameFromToken(token);
        if(owner==""){
            return "No Cards found for that user";
        }
        String returnMsg="Owned Cards:\n";

        PreparedStatement statement = _connection.prepareStatement("""
        select cardid, name, damage, element, type from "Data".cards
        where owner= ?
        """);
        statement.setString(1, owner);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){

            returnMsg += "ID: " + resultSet.getString(1);
            returnMsg += ", Name: " + resultSet.getString(2);
            returnMsg += ", Damage: " + resultSet.getInt(3);
            returnMsg += ", Element: " + resultSet.getString(4);
            returnMsg += ", Type: " + resultSet.getString(5) + "\n";

        }

        return returnMsg;
    }

    public static String getDeck(String token) throws SQLException {

        String owner=getUsernameFromToken(token);

        if(owner==""){
            return "No Deck found for that user";
        }
        String returnMsg="Cards in Deck:\n";
        PreparedStatement statement = _connection.prepareStatement("""
         select cardid, name, damage, element, type from "Data".cards
         where (owner= ? and indeck= 1)
         """);
        statement.setString(1, owner);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){

            returnMsg += "ID: " + resultSet.getString(1);
            returnMsg += ", Name: " + resultSet.getString(2);
            returnMsg += ", Damage: " + resultSet.getInt(3);
            returnMsg += ", Element: " + resultSet.getString(4);
            returnMsg += ", Type: " + resultSet.getString(5) + "\n";

        }
        return returnMsg;


    }

    private static int checkIfCardsAreOwned(String owner, String cardid) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
         select count(*) from "Data".cards
         where (owner= ? and cardid= ?)
         """);
        statement.setString(1, owner);
        statement.setString(2, cardid);
        ResultSet resultSet = statement.executeQuery();
        int returnMsg=0;
        while(resultSet.next()){

            returnMsg = resultSet.getInt(1);

        }
        return returnMsg;

    }


    public static String addCardsToDeck(String token, List<String> deck) throws SQLException {

        String owner=getUsernameFromToken(token);
        if(owner==""){
            return "Error, User doesnt own these cards";
        }
        for(int i=0;i< deck.size();i++){

            if(checkIfCardsAreOwned(owner,deck.get(i))==0){
                return "Error, User doesnt own these cards";
            }

        }

        removeCardsFromDecks(owner);

        try {

            _connection
                    .createStatement();


            PreparedStatement statement = _connection.prepareStatement("""
            update "Data".cards 
            set indeck = 1
            where (owner = ? and cardid = ? and price = 0)
            """);


            Iterator iterator = deck.iterator();
            while(iterator.hasNext()) {

                System.out.println(owner);
                statement.setString(1, owner);
                statement.setString(2, String.valueOf(iterator.next()));

                statement.execute();
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return "Success";
    }

    public static void removeCardsFromDecks(String owner) throws SQLException {

        _connection
                .createStatement();

        PreparedStatement statement = _connection.prepareStatement("""
            update "Data".cards 
            set indeck = 0
            where owner = ?
            """);

        statement.setString(1, owner);

        statement.execute();

    }


    public static String getUserData(String name, String token) throws SQLException {

        String username=getUsernameFromToken(token);
        if(!username.equals(name)){

            return "no permission";
        }
        String returnMsg="";

        PreparedStatement statement = _connection.prepareStatement("""
         select Name, bio, image from "Data".player
         where token = ?
         """);
        statement.setString(1, token);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){


            returnMsg += "Name: \n" + resultSet.getString(1);
            returnMsg += "\nBio: \n" + resultSet.getString(2);
            returnMsg += "\nImage: \n" + resultSet.getString(3);



        }
        return returnMsg;

    }

    public static String changeUserData(String name, String token, UserChanges changes) throws SQLException {

        String username=getUsernameFromToken(token);
        if(!username.equals(name)){

            return "no permission";
        }
        String returnMsg="";

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".player 
         set Name = ?, Bio = ?, Image=?
         where token = ?
         """);
        statement.setString(1, changes.Name);
        statement.setString(2, changes.Bio);
        statement.setString(3, changes.Image);
        statement.setString(4, token);


        statement.execute();
        return "Information updated";

    }


    public static String getStats(String token) throws SQLException {


        String username=getUsernameFromToken(token);
        if(username.equals("")){

            return "no user found";
        }
        String returnMsg=("Stats for User: "+username);

        PreparedStatement statement = _connection.prepareStatement("""
         select elo,games, wins, losses from "Data".player
         where username = ?
         """);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){


            returnMsg += "\nElo: " + resultSet.getInt(1);
            returnMsg += "\nGames: " + resultSet.getInt(2);
            returnMsg += "\nWins: " + resultSet.getInt(3);
            returnMsg += "\nLosses: " + resultSet.getInt(4);

        }
        return returnMsg;

    }

    public static String getScoreboard(String token) throws SQLException {

        String username=getUsernameFromToken(token);
        if(username.equals("")){

            return "permisson";
        }
        String returnMsg=("Scoreboard:\n");

        PreparedStatement statement = _connection.prepareStatement("""
         select Name, elo, wins, losses from "Data".player
         order by elo
         """);
        int counter=0;
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){

            counter++;
            returnMsg += counter + "." +" Name: " + resultSet.getString(1);
            returnMsg += " Elo: " + resultSet.getInt(2);
            returnMsg += " W/L: " + resultSet.getInt(3);
            returnMsg += "/" + resultSet.getInt(4);

            returnMsg += "\n";

        }
        return returnMsg;


    }

    public static String addToBattle(String token) throws SQLException {

        String username=getUsernameFromToken(token);
        if(username.equals("")){

            return "no permission";
        }
        String returnMsg="";

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".player 
         set battle= ?
         where token = ?
         """);
        statement.setInt(1, 1);
        statement.setString(2, token);


        statement.execute();
        return "added to battle";

    }

    public static boolean checkIfBattleReady() throws SQLException {


        PreparedStatement statement = _connection.prepareStatement("""
         select count(battle) from "Data".player
         where battle = 1
         """);
        int counter=0;
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){

            counter=resultSet.getInt(1);

        }

        if(counter>=2){
            return true;
        }else{
            return false;
        }
    }

    public static Duellist getDuellist() throws SQLException {

        Duellist duellist = new Duellist();
        String duellistName=getNameOfDuellist();

        duellist.name=duellistName;

        duellist.deck = new ArrayList<>();
        PreparedStatement statement = _connection.prepareStatement("""
        select  cardid, name, damage, element, type
        from "Data".cards
        where (owner = ? and indeck = 1)
        """);
        statement.setString(1, duellistName);
        ResultSet resultSet = statement.executeQuery();
        List<DeckCard> cards = new ArrayList<>();
        while(resultSet.next()){
            DeckCard deckcard = new DeckCard(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getInt(3),
                    resultSet.getString(4),
                    resultSet.getString(5));


            cards.add(deckcard);
        }
        duellist.deck = new ArrayList<>();
        duellist.deck=cards;

        setBattleToFalse(duellistName);



        return duellist;

    }

    private static void setBattleToFalse(String name) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".player 
         set battle= ?
         where username = ?
         """);
        statement.setInt(1, 0);
        statement.setString(2, name);
        statement.execute();

    }

    private static String getNameOfDuellist() throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
         select username from "Data".player
         where battle = 1
         """);
        String name="should not have this name";
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){

            name=resultSet.getString(1);

        }
        return name;
    }

    public static String getAdminToken() throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");


        PreparedStatement statement = _connection.prepareStatement("""
         select token from "Data".player
         where username= ?
         """);
        String token="";
        statement.setString(1, "admin");
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){

            token=resultSet.getString(1);

        }

        Database.closeConnection();


        return token;
    }

    public static void transferCards(String winner, String loser) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".cards
         set owner= ?, indeck = 0
         where (owner = ? and indeck = 1)
         """);
        statement.setString(1, winner);
        statement.setString(2, loser);
        statement.execute();
    }

    public static void changeElo(String name, int i) throws SQLException {

        int win=0;
        int loss=0;
        if(i>0){
            win=1;
        }
        if(i<0){
            loss=1;
        }

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".player
         set elo= elo + ?, games= games+1, wins=wins + ?, losses= losses + ?
         where username = ?
         """);
        statement.setInt(1, i);

        statement.setInt(2, win);
        statement.setInt(3, loss);
        statement.setString(4, name);
        statement.execute();


    }

    /*public static String offerCard(String token, TradeOffer card) throws SQLException {

        String owner=getUsernameFromToken(token);
        if(checkIfCardsAreOwned(owner,card.Id)==0){
            return "Error, User doesnt own this cards";
        }
        if(checkIfCardInDeck(card.Id)==0){
            return "Error, Card currently in deck";
        }

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".cards
         set price=  ?
         where cardid = ?
         """);
        statement.setInt(1, card.Price);
        statement.setString(2, card.Id);
        statement.execute();
        return ("Card offered for " + card.Price + " Gold");



    }*/

    private static int checkIfCardInDeck(String id) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
         select count(*) from "Data".cards
         where (cardid= ? and indeck= 0)
         """);
        statement.setString(1, id);
        ResultSet resultSet = statement.executeQuery();
        int returnMsg=0;
        while(resultSet.next()){

            returnMsg = resultSet.getInt(1);

        }
        return returnMsg;

    }


    /*public static String seeOffers(String token) throws SQLException {

        String owner=getUsernameFromToken(token);
        if(owner==""){
            return "No Deck found for that user";
        }
        String returnMsg="Offers:\n";

        PreparedStatement statement = _connection.prepareStatement("""
         select cardid, name, damage, element, type, price from "Data".cards
         where (price > 0)
         """);

        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()){

            returnMsg += "ID: " + resultSet.getString(1);
            returnMsg += ", Name: " + resultSet.getString(2);
            returnMsg += ", Damage: " + resultSet.getInt(3);
            returnMsg += ", Element: " + resultSet.getString(4);
            returnMsg += ", Type: " + resultSet.getString(5) ;
            returnMsg += ", Price: " + resultSet.getInt(6)+ "\n";

        }
        return returnMsg;

    }*/

    /*public static String deleteOffer(String token, String card) throws SQLException {

        String owner=getUsernameFromToken(token);
        if(checkIfCardsAreOwned(owner,card)==0){
            return "Error, User doesnt own this cards";
        }

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".cards
         set price=  0
         where cardid = ?
         """);

        statement.setString(1, card);
        statement.execute();
        return ("Card remove from Trade");
    }

    public static String acceptOffer(String token, String id) throws SQLException {

        String owner=getUsernameFromToken(token);
        String cardOwner=getCardOwner(id);
        if(checkIfCardsAreOwned(owner,id)==1){
            return "You cant buy your own card";
        }


        int money=checkCoins(owner);
        int price=getCardPrice(id);

        if(money<price){

            return "Not enough money";
        }

        PreparedStatement statement = _connection.prepareStatement("""
         update "Data".cards
         set price=  0, owner= ? 
         where cardid = ?
         """);
        statement.setString(1, owner);
        statement.setString(2, id);
        statement.execute();
        changeCoins(owner,price);
        changeCoins(cardOwner,-price);

        return ("Card bought for " + price + " Gold");

    }*/

    /*private static String getCardOwner(String id) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
         select owner from "Data".cards
         where cardid= ? 
         """);
        statement.setString(1, id);
        ResultSet resultSet = statement.executeQuery();
        String returnMsg="";
        while(resultSet.next()){

            returnMsg = resultSet.getString(1);

        }
        return returnMsg;
    }*/

    /*public static int getCardPrice(String id) throws SQLException {

        PreparedStatement statement = _connection.prepareStatement("""
         select price from "Data".cards
         where cardid= ? 
         """);
        statement.setString(1, id);
        ResultSet resultSet = statement.executeQuery();
        int returnMsg=0;
        while(resultSet.next()){

            returnMsg = resultSet.getInt(1);

        }
        return returnMsg;

    }*/


    public static String salvageCards(String token, List<String> ids) throws SQLException {

        String owner=getUsernameFromToken(token);
        if(owner==""){
            System.out.println(owner);
            return "Error, User doesnt own these cards";
        }
        for(int i=0;i< ids.size();i++){

            if(checkIfCardsAreOwned(owner,ids.get(i))==0){

                return ("Error, User doesnt own these cards---->" + (i+1));
            }

        }
        for(int i=0;i< ids.size();i++){

            if(checkIfCardInDeck(ids.get(i))==0){
                return "One or more cards are currently in a deck";
            }

        }

        try {

            _connection
                    .createStatement();


            PreparedStatement statement = _connection.prepareStatement("""
            delete from "Data".cards 
            where  cardid = ?
            """);


            Iterator iterator = ids.iterator();
            while(iterator.hasNext()) {

                statement.setString(1, String.valueOf(iterator.next()));

                statement.execute();
            }
            changeCoins(owner,1);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "added 1 Coin\n";
    }



}