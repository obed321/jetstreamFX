package worldMap;

import java.util.Random;

public class Game {
    World world;
    String challenge;

    public Game(World world) {
        this.world = world;
        System.out.println("GAME");
        newGame();
    }

    private void newGame() {
        Random random = new Random();
        Country[] challenges = Country.values();
        challenge = challenges[random.nextInt(257)].toString();
        System.out.println("Find: " + challenge);
    }

    public void checkAnswer(String answer){
        if (challenge.equals(answer)){
            System.out.println("Correct!");
        } else {
            System.out.println("Wrong!");
        }
    }



}
