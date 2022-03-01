package Game;

import lombok.Data;
import lombok.Getter;

import java.sql.SQLException;
import java.util.Random;

public class Battle {



    private static Duellist duellist1;
    private static Duellist duellist2;
    @Getter
    private static String battleLog;


    public Battle() throws SQLException {
        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        //retrieve two Duellists
        duellist1=Database.getDuellist();
        duellist2=Database.getDuellist();
        battleLog="";
        Database.closeConnection();

    }

    public static boolean checkIfTwoPlayerReady() throws SQLException {

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        boolean ready=Database.checkIfBattleReady();
        Database.closeConnection();

        return ready;
    }

    public static String startBattle() throws SQLException {

        Random rand = new Random();
        //random= rand.nextInt(upperbound);
        int roundCounter=1;
        battleLog+="Battlelog:\n";
        float dmgCard1;
        float dmgCard2;
        int rand1;
        int rand2;
        String winner;
        if (duellist2.deck.size()==0 || duellist1.deck.size()==0){

            return "one Person didnt pick a deck";
        }
        if (duellist2.deck.size()!=4 || duellist1.deck.size()!=4){

            return "one Person doesnt have a full Deck";
        }



        while(duellist1.deck.size()!=0 && duellist2.deck.size()!=0 && roundCounter<=100){
            rand1=rand.nextInt(duellist1.deck.size());
            rand2=rand.nextInt(duellist2.deck.size());

            //calculate real damage
            dmgCard1=duellist1.deck.get(rand1).getDamage() * checkForMultiplier(duellist1.deck.get(rand1), duellist2.deck.get(rand2));
            dmgCard2=duellist2.deck.get(rand2).getDamage() * checkForMultiplier(duellist2.deck.get(rand2), duellist1.deck.get(rand1));

            battleLog+="\nRound " +roundCounter+":\n";
            battleLog+=duellist1.name + ": " + duellist1.deck.get(rand1).getName()+ "(" +duellist1.deck.get(rand1).getDamage() + "Damage)" + " vs ";
            battleLog+=duellist2.name + ": " + duellist2.deck.get(rand2).getName()+ "(" +duellist2.deck.get(rand2).getDamage() + "Damage)\n";
            battleLog+="-->" + dmgCard1 + " VS " + dmgCard2;

            if(dmgCard1>dmgCard2){
                battleLog+="-->"+duellist1.deck.get(rand1).getName() + " defeats " +duellist2.deck.get(rand2).getName()+ "\n";
                duellist1.deck.add(duellist2.deck.get(rand2));
                duellist2.deck.remove(rand2);
            }else if (dmgCard1<dmgCard2){
                battleLog+="-->"+duellist2.deck.get(rand2).getName() + " defeats " +duellist1.deck.get(rand1).getName()+ "\n";
                duellist2.deck.add(duellist1.deck.get(rand1));
                duellist1.deck.remove(rand1);
            }else{
                battleLog+="-->DRAW\n";
            }
           // battleLog+="listsize1: " + duellist1.deck.size();
           // battleLog+="/listsize2: " + duellist2.deck.size();

            roundCounter++;
        }

        System.out.println(battleLog);

        Database.openConnection("jdbc:postgresql://localhost:5432/mtcg", "admin", "");
        if(duellist1.deck.size()==0){
            Database.transferCards(duellist1.name, duellist2.name);
            Database.changeElo(duellist1.name, 3);
            Database.changeElo(duellist2.name, -5);

        }else if(duellist2.deck.size()==0){
            Database.transferCards(duellist2.name, duellist1.name);
            Database.changeElo(duellist2.name, 3);
            Database.changeElo(duellist1.name, -5);
        }
        Database.closeConnection();

        System.out.println(battleLog);

        return battleLog;



    }


    private static float checkForMultiplier(DeckCard attacker, DeckCard defender){


        if(attacker.getName().contains("Goblin") && defender.getName().contains("Dragon")){
            return 0;
        }
        else if(attacker.getName().contains("Ork") && defender.getName().contains("Wizard")){
            return 0;
        }
        else if(attacker.getName().contains("Knight") && defender.getName().contains("WaterSpell")){
            return 0;
        }
        else if(attacker.getName().contains("Spell") && defender.getName().contains("Kraken")){
            return 0;
        }
        else if(attacker.getName().contains("Dragon") && defender.getName().contains("FireElves")){
            return 0;
        }


        if(!(attacker.getType().equals("Monster") && defender.getType().equals("Monster"))){

            if(attacker.getElement().equals("Water") && defender.getElement().equals("Fire")){
                return 2;
            }
            else if(attacker.getElement().equals("Fire") && defender.getElement().equals("Normal")){
                return 2;
            }
            else if(attacker.getElement().equals("Normal") && defender.getElement().equals("Water")){
                return 2;
            }
            else if(attacker.getElement().equals("Fire") && defender.getElement().equals("Water")){
                return (float) 0.5;
            }
            else if(attacker.getElement().equals("Normal") && defender.getElement().equals("Fire")){
                return (float) 0.5;
            }
            else if(attacker.getElement().equals("Water") && defender.getElement().equals("Normal")){
                return (float) 0.5;
            }

        }

        return 1;
    }


}
