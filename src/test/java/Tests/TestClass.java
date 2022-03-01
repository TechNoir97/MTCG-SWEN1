package Tests;

import Game.Card;
import Game.Database;
import Game.User;
import RestServer.Unwrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestClass {



    @Test
    public void lineSplitTest(){

        String line="/users/name";


        String[] array = line.split("/",3);
        assertEquals("",array[0]);
        assertEquals("users",array[1]);
        assertEquals("name",array[2]);

    }




    @Test
    public void jsonTest() throws JsonProcessingException {
        String json="[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]";

        ObjectMapper objectMapper = new ObjectMapper();

        List<String> ids = objectMapper.readValue(json,new TypeReference<List<String>>(){});


        assertEquals("845f0dc7-37d0-426e-994e-43fc3ac83c08",ids.get(0));
        assertEquals("99f8f8dc-e25e-4a95-aa2c-782823f36e2a",ids.get(1));

    }


    @Test
    public void jsonTest2() throws JsonProcessingException {

        String json = "[{\"Id\":\"b017ee50-1c14-44e2-bfd6-2c0c5653a37c\", \"Name\":\"WaterGoblin\", \"Damage\": 11.0}, {\"Id\":\"d04b736a-e874-4137-b191-638e0ff3b4e7\", \"Name\":\"Dragon\", \"Damage\": 70.0}, {\"Id\":\"88221cfe-1f84-41b9-8152-8e36c6a354de\", \"Name\":\"WaterSpell\", \"Damage\": 22.0}, {\"Id\":\"1d3f175b-c067-4359-989d-96562bfa382c\", \"Name\":\"Ork\", \"Damage\": 40.0}, {\"Id\":\"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\", \"Name\":\"RegularSpell\", \"Damage\": 28.0}]";


        ObjectMapper objectMapper = new ObjectMapper();

        List<Card> pack = objectMapper.readValue(json, new TypeReference<List<Card>>() {
        });


        assertEquals("b017ee50-1c14-44e2-bfd6-2c0c5653a37c",pack.get(0).Id);
        assertEquals("WaterGoblin",pack.get(0).Name);
        assertEquals("Dragon",pack.get(1).Name);



        for (int i = 0; i < 4; i++) {

            if (pack.get(i).Name.contains("Spell")) {
                pack.get(i).type = "Spell";
            } else {
                pack.get(i).type = "Monster";

            }

        }

        assertEquals("Monster",pack.get(0).type);


    }


}
