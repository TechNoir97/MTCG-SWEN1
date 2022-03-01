package Tests;

import Game.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JasonTranslation {

    @Test
    public void JasonTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}";

        User user = objectMapper.readValue(json, User.class);
        assertEquals("daniel",user.getPassword());
        assertEquals("kienboec",user.getUsername());

    }

}
