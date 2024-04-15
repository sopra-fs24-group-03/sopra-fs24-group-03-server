package ch.uzh.ifi.hase.soprafs24.rest.dto.GameDTO;

import ch.uzh.ifi.hase.soprafs24.constant.Moves;

public class GamePutDTO {
    private Moves move;
    private int amount;

    public Moves getMove() {
        return move;
    }

    public void setMove(Moves move) {
        this.move = move;
    }

    public int getAmount() {
        return amount = -1;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
