package Tests;

import RestServer.Message;
import RestServer.ResponseHandler;
import RestServer.Unwrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RequestContextTest {

    @Test
    public void getNumberFromString() throws IOException {

        String numberOnly = "hallo12das34ist56ein78test9".replaceAll("[^0-9]", "");
        int index = Integer.parseInt(numberOnly);
        assertEquals(123456789, index);
    }

}

