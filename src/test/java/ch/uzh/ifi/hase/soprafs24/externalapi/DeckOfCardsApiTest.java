package ch.uzh.ifi.hase.soprafs24.externalapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class DeckOfCardsApiTest {
    MockRestServiceServer mockServer;

    RestTemplate restTemplate = new RestTemplate();

    DeckOfCardsApi api = new DeckOfCardsApi(restTemplate);

    @BeforeEach
    public void setUp() {
        //Setup mock server
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockServer = MockRestServiceServer.createServer(gateway);
    }

    @Test
    public void postDeck_success(){
        //setup
        mockServer.expect(once(), requestTo("https://www.deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1"))
                .andRespond(withSuccess("{\"success\": true, \"deck_id\": \"id\", \"shuffled\": true, \"remaining\": 52}", MediaType.APPLICATION_JSON));

        //method call
        String result = api.postDeck();

        //verification
        mockServer.verify();
        assertEquals("id", result);
    }

    @Test
    public void postDeck_unexpectedResponse(){
        //setup
        mockServer.expect(once(), requestTo("https://www.deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1"))
                .andRespond(withSuccess("{\"success\": false}", MediaType.APPLICATION_JSON));


        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> {
            api.postDeck();
        });

        //verification
        mockServer.verify();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }


    @Test
    public void postDeck_failedAPICall(){
        mockServer.expect(once(), requestTo("https://www.deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1"))
                .andRespond(withServerError());

        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> {
            api.postDeck();
        });

        //verification
        mockServer.verify();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    public void drawCards_success(){
        //setup
        mockServer.expect(once(), requestTo("https://www.deckofcardsapi.com/api/deck/id/draw/?count=2"))
                .andRespond(withSuccess("{" +
                        "\"success\": true," +
                        "\"deck_id\": \"kxozasf3edqu\"," +
                        "\"cards\": [" +
                        "{" +
                        "\"code\": \"6H\"," +
                        "\"image\": \"https://deckofcardsapi.com/static/img/6H.png\"," +
                        "\"images\": {" +
                        "\"svg\": \"https://deckofcardsapi.com/static/img/6H.svg\"," +
                        "\"png\": \"https://deckofcardsapi.com/static/img/6H.png\"" +
                        "}," +
                        "\"value\": \"6\"," +
                        "\"suit\": \"HEARTS\"" +
                        "}," +
                        "{" +
                        "\"code\": \"5S\"," +
                        "\"image\": \"https://deckofcardsapi.com/static/img/5S.png\"," +
                        "\"images\": {" +
                        "\"svg\": \"https://deckofcardsapi.com/static/img/5S.svg\"," +
                        "\"png\": \"https://deckofcardsapi.com/static/img/5S.png\"" +
                        "}," +
                        "\"value\": \"5\"," +
                        "\"suit\": \"SPADES\"" +
                        "}" +
                        "]," +
                        "\"remaining\": 50" +
                        "}", MediaType.APPLICATION_JSON));

        //method call
        List<Card> result = api.drawCards("id", 2);

        //verification
        mockServer.verify();
        assertEquals("6H", result.get(0).getCode());
        assertEquals("https://deckofcardsapi.com/static/img/6H.png", result.get(0).getImage());

        assertEquals("5S", result.get(1).getCode());
        assertEquals("https://deckofcardsapi.com/static/img/5S.png", result.get(1).getImage());
    }

    @Test
    public void drawCards_failedAPICall(){
        mockServer.expect(once(), requestTo("https://www.deckofcardsapi.com/api/deck/id/draw/?count=2"))
                .andRespond(withServerError());

        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> {
            api.drawCards("id", 2);

        });

        //verification
        mockServer.verify();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    public void drawCards_unexpectedResponse(){
        mockServer.expect(once(), requestTo("https://www.deckofcardsapi.com/api/deck/id/draw/?count=2"))
                .andRespond(withSuccess("{\"success\": false}", MediaType.APPLICATION_JSON));

        //method call
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> {
            api.drawCards("id", 2);

        });

        //verification
        mockServer.verify();
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }
}
