package ch.uzh.ifi.hase.soprafs24.externalapi;


import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;


public class DeckOfCardsApi {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URI = "https://www.deckofcardsapi.com/api/deck/";

    //POST request to create a new deck
    public String postDeck() {
        String requestURI = BASE_URI + "new/shuffle/?deck_count=1";

        try {
            HashMap<String, Object> response = restTemplate.postForObject(requestURI, null, HashMap.class);

            //insure correct response
            if (response != null && response.containsKey("success") && (boolean) response.get("success")) {
                return (String) response.get("deck_id");
            } else {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "DeckOfCards API returned an error");
            }

            //catch potential errors
        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(e.getStatusCode(), "Error occurred while making the API request: " + e.getMessage());
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error occurred while communicating with DeckOfCards API: " + e.getMessage());
        }
    }
}