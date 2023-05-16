package GameManagement;

import collision.AABB;
import entity.*;
import gameplay.Click;
import graphics.Camera;
import graphics.Window;
import org.joml.Vector2f;
import world.World;
import main.main;
import world.EndScreen;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class GameManager {
    public int lives = 3;
    public int score = 0;

    public String finalScore = "";
    public String finalTime = "";
    public String finalHighScore = "";
    public String finalHighScoreTime = "";
    public int maxEntities;
    public World world;
    public Camera camera;
    public Window window;
    Random rand = new Random();

    //percent for each square to spawn
    int greenPercent = 0;
    int redPercent = 0;
    int orangePercent = 0;
    int icePercent = 0;
    public boolean iceCooldown; //tracks when ice is able to spawn again
    double iceDropStart = 0; //time when ice spawns
    int heartPercent = 0;
    double heartDropStart = 0; //time when heart spawns
    int frenzyPercent = 0;
    public boolean frenzyTime = false; //tracks when frenzyCofig is being used
    public double frenzyStartTime = 0; //time when frenzy spawns
    public double frenzyEndTime = 0; //time when frenzy can spawn again
    int pinkPercent = 0;
    int goldPercent = 0;

    public double speedModifier = 10; //modifier added onto the random speed calculated

    public double tempSpeedModifier = 0; //temp speedModifier that saves speedModifier when ice halves it

    public double speedModTimeStart = 1; //time when speedModifier increased
    public double speedModTimeEnd = 100; //next time when speedModifier increases

    //method always running to decide when and what square should spawn
    public void update() {

        if (speedModifier <= tempSpeedModifier){ // if the speedModifier has not reached where it was before it was halved by ice
            speedModifier = iceSpeedModIncreaser(speedModifier); //increases speedModifier faster
            iceCooldown = false; //prevents ice from spawning when speedModifier is lowered so it doesn't get permanently lowered
        } else{
            if(speedModifier <=30) { // caps speedModifier at 30
                speedModifier = speedModIncreaser(speedModifier); //gradually increases speedModifier
            } else {
                speedModifier = 30; //sets speedModifier to 30 incase it jumps past it
            }
            iceCooldown = true; //allows ice to spawn again
        }
        if (lives <= 0) { //records the final score and sets high score if applicable
            finalScore += score;
            finalTime += (int) main.getTime_passed();
            AttemptToWriteHighscore(score, (float) main.getTime_passed(), "highs");
            EndScreen.canRun = true; //runs game over screen
            World.canRun = false; //stops running gameplay
        }else {
            for (int i = world.totalEntities(); i < maxEntities; i++) { //spawns squares until it reaches the max number of squares
                int randSpeed = (int) speedModifier + rand.nextInt(10); //sets speed of squares using speedModifier and a random
                int dropPercent = 1 + rand.nextInt(100); //generates a number 1-100 that determines which square spawns
                if (frenzyTime){ //lasts for 5 seconds after frenzy square is hit
                    maxEntities = 10; //increases max squares to 10
                    frenzyEndTime = main.getTime_passed(); //gets the current time
                    frenzyConfig(randSpeed, dropPercent); //uses different config to only spawn green and gold squares
                    if ((frenzyEndTime - frenzyStartTime) >= 5){ //if 5 or more seconds hae passed since frenzy started it ends
                        frenzyTime = false; //ends the frenzy event
                    }
                } else {
                    maxEntities = 5; //resets max squares to 5
                    if (main.getTime_passed() < 30) { //config for first 30 seconds
                        easyConfig(randSpeed, dropPercent);
                    } else if (main.getTime_passed() < 90) { //config for next 60 seconds
                        midConfig(randSpeed, dropPercent);
                    } else { //config for after 90 seconds
                        hardConfig(randSpeed, dropPercent);
                    }
                }
            }
        }

        Click(window); //calls click method to see if a square has been clicked
        DisappearEntities(); //removes entities that move off the screen

    }

    //easy configuration that only spawns green, orange, and red squares
    public void easyConfig(int inSpeed, int inDrop){
        int randSpeed = inSpeed;
        int dropPercent = inDrop;
        greenPercent = 85;
        redPercent = 90;
        orangePercent = 100;
        if (dropPercent <= greenPercent) {
            InstantiateGreenSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= redPercent) {
            InstantiateRedSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else {
            InstantiateOrangeSquare(RandomStartingPositionOfSquare(), randSpeed);
        }
    }

    //middle configuration that only spawns all squares based on their percentages
    public void midConfig(int inSpeed, int inDrop){
        int randSpeed = inSpeed;
        int dropPercent = inDrop;
        greenPercent = 65;
        redPercent = 80;
        orangePercent = 91;
        pinkPercent = 94;
        goldPercent = 97;
        heartPercent = 98;
        frenzyPercent = 99;
        icePercent = 100;
        if (dropPercent <= greenPercent) {
            InstantiateGreenSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= redPercent) {
            InstantiateRedSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= orangePercent){
            InstantiateOrangeSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= pinkPercent){
            InstantiatePinkSquare(RandomStartingPositionOfSquare(),randSpeed+10);
        } else if (dropPercent <= goldPercent) {
            InstantiateGoldSquare(RandomStartingPositionOfSquare(), randSpeed + 15);
        } else if (dropPercent <= heartPercent) {
            InstantiateHeartSquare(RandomStartingPositionOfSquare(), randSpeed + 10);
        } else if (dropPercent <= frenzyPercent) {
            InstantiateFrenzySquare(RandomStartingPositionOfSquare(), randSpeed);
        } else {
            InstantiateIceSquare(RandomStartingPositionOfSquare(), randSpeed);
        }
    }

    //hard configuration that only spawns all squares but less friendly squares
    public void hardConfig(int inSpeed, int inDrop){
        int randSpeed = inSpeed;
        int dropPercent = inDrop;
        greenPercent = 60;
        redPercent = 75;
        orangePercent = 83;
        pinkPercent = 89;
        goldPercent = 95;
        frenzyPercent = 96;
        icePercent = 98;
        heartPercent = 100;
        if (dropPercent <= greenPercent) {
            InstantiateGreenSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= redPercent) {
            InstantiateRedSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= orangePercent){
            InstantiateOrangeSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= pinkPercent){
            InstantiatePinkSquare(RandomStartingPositionOfSquare(),randSpeed + 10);
        } else if (dropPercent <= goldPercent){
            InstantiateGoldSquare(RandomStartingPositionOfSquare(), randSpeed + 15);
        } else if (dropPercent <= frenzyPercent) {
            InstantiateFrenzySquare(RandomStartingPositionOfSquare(), randSpeed);
        } else if (dropPercent <= icePercent){
            InstantiateIceSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else {
            InstantiateHeartSquare(RandomStartingPositionOfSquare(), randSpeed + 10);
        }
    }

    //config for after a frenzy square is hit that only spawns green and gold
    public void frenzyConfig(int inSpeed, int inDrop){
        int randSpeed = inSpeed;
        int dropPercent = inDrop;
        greenPercent = 90;
        goldPercent = 100;
        if (dropPercent <= greenPercent){
            InstantiateGreenSquare(RandomStartingPositionOfSquare(), randSpeed);
        } else {
            InstantiateGoldSquare(RandomStartingPositionOfSquare(), 30);
        }
    }

    //class constructor
    public GameManager(Camera camera, World world, Window window, int lives, int startingScore) {
        this.lives = lives;
        this.score = startingScore;
        this.world = world;
        this.camera = camera;
        this.window = window;
        maxEntities = 5;
    }

    //methods that spawn each square by creating an object from their class
    public void InstantiateGreenSquare(Vector2f position, float speed) {
        GreenSquare greenSquare = new GreenSquare(new Transform(position.x, position.y));
        greenSquare.speed = speed;
        world.CreateEntity(greenSquare);
    }

    public void InstantiateRedSquare(Vector2f position, float speed) {
        RedSquare redSquare = new RedSquare(new Transform(position.x, position.y));
        redSquare.speed = speed;
        world.CreateEntity(redSquare);
    }

    public void InstantiateOrangeSquare(Vector2f position, float speed) {
        OrangeSquare orangeSquare = new OrangeSquare(new Transform(position.x, position.y));
        orangeSquare.speed = speed;
        world.CreateEntity(orangeSquare);
    }

    public void InstantiatePinkSquare(Vector2f position, float speed) {
        PinkSquare pinkSquare = new PinkSquare(new Transform(position.x, position.y));
        pinkSquare.speed = speed;
        world.CreateEntity(pinkSquare);
    }

    public void InstantiateGoldSquare(Vector2f position, float speed) {
        GoldSquare goldSquare = new GoldSquare(new Transform(position.x, position.y));
        goldSquare.speed = speed;
        world.CreateEntity(goldSquare);
    }

    public void InstantiateIceSquare(Vector2f position, float speed) {
        if ((main.getTime_passed() - iceDropStart) >= 3) { //three seconds after last ice spawned
            if (iceCooldown) { //cooldown for when ice can spawn
                IceSquare iceSquare = new IceSquare(new Transform(position.x, position.y));
                iceSquare.speed = speed;
                world.CreateEntity(iceSquare);
                iceDropStart = main.getTime_passed(); //gets time when the ice spawns
            }
        }
    }

    public void InstantiateFrenzySquare(Vector2f position, float speed) {
        FrenzySquare frenzySquare = new FrenzySquare(new Transform(position.x, position.y));
        frenzySquare.speed = speed;
        world.CreateEntity(frenzySquare);
    }
    public void InstantiateHeartSquare(Vector2f position, float speed) {
        if ((main.getTime_passed() - heartDropStart) >= 3) { //three seconds after last heart spawns
            if (lives <= 2 && lives > 0) { //is less than 3 and more than 0
                HeartSquareish heartSquare = new HeartSquareish(new Transform(position.x, position.y));
                heartSquare.speed = speed;
                world.CreateEntity(heartSquare);
            }
        }
    }

    //constantly increases speedModifier by .2 every second
    public double speedModIncreaser(double speedMod){
        double modifier = speedMod;
        speedModTimeEnd = main.getTime_passed();
        if ((speedModTimeEnd - speedModTimeStart) >= 1){
            modifier += .2;
            System.out.println("Modifier: " + (int) modifier + "\nTime: " + (int) main.getTime_passed());
            speedModTimeStart = speedModTimeEnd;
        }
        return modifier;
    }

    //exponentially increases speedModifier to where it was before the ice was hit over a span of 10 seconds
    public double iceSpeedModIncreaser(double speedMod){
        double modifier = speedMod;
        speedModTimeEnd = main.getTime_passed();
        if ((speedModTimeEnd - speedModTimeStart) >= 1){
            modifier += (modifier * .116);
            System.out.println("Modifier: " + (int) modifier + "\nTime: " + (int) main.getTime_passed());
            speedModTimeStart = speedModTimeEnd;
        }
        return modifier;
    }





    //random start posisiton of a square
    public Vector2f RandomStartingPositionOfSquare() {
        return new Vector2f(DetermineRandomXOfStartingPosition(11, 47, new Random(), 1000000), -5);
    }

    //gets the x value of the starting position
    public float DetermineRandomXOfStartingPosition(float min, float max, Random random, float scalar) {
        int minimum = (int) (min * scalar);
        int maximum = (int) (max * scalar);

        return ((random.nextInt(maximum - minimum) + minimum) / scalar);
    }

    //registers when the mouse is being clicked and detects when an entity is clicked
    public void Click(Window window) {
        if (Click.clicked) {
            Vector2f positionOfClick = Click.ConvertClickPositionToRealCoordinates(camera, Click.position, world.getScale());

            for (int i = 0; i < world.totalEntities(); i++) {
                if (world.getCountingUpEntity(i).getBoundingBox().getCollision(new AABB(positionOfClick, new Vector2f(window.GetCursorWidth()/32f, window.GetCursorHeight()/32f))).isIntersecting) {
                    Clicked(world.getCountingUpEntity(i), i);
                }
            }
        }
    }

    //deletes entity if it passes off the screen and causes penalty is that square causes a penalty to happen when dropped
    void DisappearEntities() {
        for (int i = 0; i < world.totalEntities(); i++) {
            if (world.getCountingUpEntity(i).getPosition().y < -42)
                NotClicked(world.getCountingUpEntity(i), i);
        }
    }

    //deletes the square that is clicked
    public void Clicked(Entity entity, int index) {
        entity.DestroySquare(this);
        world.RemoveEntity(index);

        System.out.println("SCORE : " + score);
    }

    //deletes the square that is dropped
    public void NotClicked(Entity entity, int index) {
        entity.NoClickDestroy(this);
        world.RemoveEntity(index);
    }

    // Controls Writing for High Score
    public void AttemptToWriteHighscore(int score, float time, String location) {
        String[] data = getFileInfo(location);
        int currentHighScore = Integer.parseInt(data[0]);
        float currentHighTime = Float.parseFloat(data[1]);

        finalHighScore = currentHighScore + "";
        finalHighScoreTime = (int) currentHighTime + "";

        if (score > currentHighScore || (score == currentHighScore && time < currentHighTime)) {
            System.out.println("NEW HIGH SCORE");
            WriteFile(location, score + "\n"+ time);

            finalHighScore = score + "";
            finalHighScoreTime = (int) time + "";
        }
    }

    //gets high score from file
    public String[] getFileInfo(String location) {
        String[] data = new String[2];
        try {
            File myObj = new File("scores/" + location);
            Scanner read = new Scanner(myObj);
            int i = 0;
            while (read.hasNextLine()) {
                data[i] = read.nextLine();
                i++;
            }
            read.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error Loading File");
            e.printStackTrace();
        }
        return data;
    }

    //writes high score into file
    public void WriteFile(String location, String data) {
        try {
            FileWriter myWriter = new FileWriter("scores/" + location);
            myWriter.write(data);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("Error Writing to File");
            e.printStackTrace();
        }
    }
}
