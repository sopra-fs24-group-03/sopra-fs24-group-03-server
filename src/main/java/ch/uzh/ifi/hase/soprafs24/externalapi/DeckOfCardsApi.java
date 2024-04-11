package ch.uzh.ifi.hase.soprafs24.externalapi;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DeckOfCardsApi {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URI = "https://www.deckofcardsapi.com/api/deck";

    //POST request to create a new deck
    public String postDeck() {
        String requestURI = BASE_URI + "/new/shuffle/?deck_count=1";

        try {
            //make API request
            HashMap<String, Object> response = restTemplate.postForObject(requestURI, null, HashMap.class);

            //insure correct response
            if (response != null && response.containsKey("success") && (boolean) response.get("success")) {
                return (String) response.get("deck_id");
            } else {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "DeckOfCards API returned an error");
            }

        //catch potential errors
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error occurred while communicating with DeckOfCards API: " + e.getMessage());
        }
    }


    //Draw cards from a deck, give id and amount of cards drawn
    public List<HashMap<String, String>> drawCards(String id, int amount){

        //Setup the URL
        String requestURI = String.format("%s/%s/draw/?count=%d", BASE_URI, id, amount);

        try {
            //make API request
            String response = restTemplate.getForObject(requestURI, String.class);

            //extract cards from response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode cardNode = jsonNode.get("cards");


            List<HashMap<String, String>> cardList = new ArrayList<>();

            //extract card code and card image from cards and add to the cardList
            for (JsonNode card : cardNode) {
                String code = card.get("code").asText();
                String image = card.get("image").asText();

                // Create a HashMap for code and image
                HashMap<String, String> cardMap = new HashMap<>();
                cardMap.put("code", code);
                cardMap.put("image", image);

                // Add the HashMap to the List
                cardList.add(cardMap);
            }

            return cardList;

        //catch processing error, in case response is not in expected format
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Unexpected API response");

        //catch error, in case API is not working
        } catch (RestClientException e) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Error occurred while communicating with DeckOfCards API: " + e.getMessage());
    }

    }
}