import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.io.*;
import java.util.*;

public class EndGameScreen extends World 
{
    private static final String HIGHSCORE_FILE = "highscore\\highscore.txt";
    private GreenfootSound gameOverMusic = new GreenfootSound("sounds/GameOver.wav");
    private int countDown;
    private boolean deadCha = false;
    private boolean notDead = false;
    private boolean flipped = false;
    private MusicControl m;
    private static final int TILES_H = 45;
    private static final int TILES_V = 35;
    private static final int BORDER = 22;

    /**
     * Create an EndGameScreen which saves and displays your scores, as well as showing animated Actors
     */
    public EndGameScreen(boolean pause, int score) {
        super(990, 770, 1);
        gameOverMusic.setVolume(30);
        if (pause) {
            stopMusic();
        } else {
            gameOverMusic.playLoop();
        }
        saveScore(score);
        GreenfootImage ts = new GreenfootImage("images/terrain/grass_tile.png");
        setBackground(ts);
        MenuControl mc = new MenuControl(false, false, true);
        addObject(mc, 0, 0);
        createBorder();
        createMC();
        createEndGameScreen();
        createEnemy();
        createCharacter();
        createDeadCharcter();
        removeCharacter0();
        Greenfoot.start();
    }

    /**
     * Add a border of TreeStumps around the edge of the screen
     */
    private void createBorder() {
        int borderMid = BORDER / 2;
        for (int i = 0; i < TILES_H; i++) {
            addObject(new TreeStump(), borderMid + i * BORDER, borderMid);
            addObject(new TreeStump(), borderMid + i * BORDER, getHeight() - borderMid);
        }
        for (int i = 0; i < TILES_V; i++) {
            addObject(new TreeStump(), borderMid, borderMid + i * BORDER);
            addObject(new TreeStump(), getWidth() - borderMid, borderMid + i * BORDER);
        }
    }

    /**
     * Animates the enemies/character on the endscreen, changing their sprites every 50 frames
     */
    public void act() {
        if (countDown > 0) {
            countDown--;
            if (countDown == 99) {
                removeCharacter0();
                createDeadCharcter();
                flipEnemies();
                flipped = true;
            } else if (countDown == 50) {
                removeCharacter1();
                createCharacter();
                flipEnemies();
                flipped = false;
            }
        } else if (countDown == 0) {
            countDown = 100;
        }
    }

    /**
     * Adds a new EndGameScreen object to the world.
     */
    private void createEndGameScreen() {
        UI gameOverScreen = new UI();
        gameOverScreen.setImage(new GreenfootImage("EndGameScreen.png"));
        addObject(gameOverScreen, 495, 385);
    }

    /**
     * Creates the enemies for the page.
     */
    private void createEnemy() {
        addObject(new Walker(this), 850, 120);
        addObject(new Walker(this), 150, 120);
        addObject(new Tank(this), 150, 650);
        addObject(new Tank(this), 850, 650);
    }

    /**
     * Creates the character for the page.
     */
    private void createCharacter() {
        Character c = new Character();
        notDead = true;
        addObject(c, 500, 335);
    }

    /**
     * Removes the character image from the page.
     */
    private void removeCharacter0() {
        Character ch = getObjects(Character.class).get(0);
        if (notDead) {
            removeObject(ch);
        }
    }

    /**
     * Add music control so music can be muted
     */
    private void createMC() {
        m = new MusicControl();
        addObject(m, -300, -100);
        m.getImage().setTransparency(0);
    }

    /**
     * Removes the character image from the page.
     */
    private void removeCharacter1() {
        if (deadCha) {
            Character ch = getObjects(Character.class).get(0);
            removeObject(ch);
        }
    }

    /**
     * Creates an image of the character to look like he is dead.
     */
    private void createDeadCharcter() {
        Character character = new Character();
        deadCha = true;
        character.setImage(new GreenfootImage("character/dead_sprite.png"));
        addObject(character, 490, 345);
    }

    /**
     * Flips the sprites of enemies to give the illusion of movement 
     */
    private void flipEnemies(){
        List<Walker> walkers = getObjects(Walker.class);
        for(int i = 0; i < walkers.size(); i++){
            if(flipped){
                walkers.get(i).setImage("enemy/tankEnemy.png");
            } else {
                walkers.get(i).setImage("enemy/tankEnemyflip.png");
            }
            walkers.get(i).getImage().scale(44,44);
        }
    }

    /**
     * Stops the music playing
     */
    public void stopMusic() {
        gameOverMusic.pause();
    }

    /**
     * Gets the playing status of the music
     */
    public boolean isPlaying() {
        return gameOverMusic.isPlaying();
    }

    /**
     * Starts the music on loop
     */
    public void startMusic() {
        gameOverMusic.playLoop();
    }

    /**
     * Reads the highscores stored in the local file and returns them as a String[10]
     */
    static public String[] readFile() {
        String[] data = new String[10];
        BufferedReader scores = null;
        try {
            FileReader reader = new FileReader(HIGHSCORE_FILE);
            scores = new BufferedReader(reader);
            int lineCount = 0;
            while (scores.ready() && lineCount < 10) {
                data[lineCount] = scores.readLine();
                lineCount++;
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("Could not find highscores file");
            data = null;
        } catch (IOException io) {
            System.out.println("Error reading highscores file");
            data = null;
        } finally {
            if (scores != null) {
                try {
                    scores.close();
                } catch (IOException ioe) {
                    System.out.println("Error reading file");
                }
            }
        }
        return data;
    }

    /**
     * Takes a String[] of scores and a new score to compare, returning what position the new score is placed
     * If not placed, returns -1
     */
    private int compareScore(String[] data, int newScore) {
        int position = -1;
        for (int i = 0; i < data.length; i++) {
            String[] parts = data[i].split(",");
            int score = Integer.parseInt(parts[1]);
            if (newScore > score) {
                return i;
            }
        }
        return position;
    }

    /**
     * Takes a String[] of scores, a new score to add, and the position to add it to
     * Returns the new String[]
     */
    private String[] insertScore(String[] data, String newScore, int position) {
        String[] output = new String[10];
        for (int i = 0; i < data.length - 1; i++) {
            if (i < position) {
                output[i] = data[i];
            } else {
                output[i + 1] = data[i];
            }
        }
        output[position] = newScore;
        return output;
    }

    /**
     * Pops up a message box to the user asking for their name, returning it as a String
     */
    private String askName() {
        String name = Greenfoot.ask("Please Enter Name (10 characters max)");
        //remove commas as they are used to delimit the data
        name = name.replace(",", "");
        if (name.length() > 10) {
            //limit name length to 10 characters
            name = name.substring(0, 10);
        }
        return name;
    }

    /**
     * Goes through the full process of reading the highscores file, comparing the new score against it, displaying the score
     * and saving the updated highscore list to file (if necessar)
     */
    private void saveScore(int score) {
        String name = askName();
        String[] highScores = readFile();
        int position = -1;
        if (highScores != null) {
            position = compareScore(highScores, score);
        }
        displayScore(name, score, position);
        String[] newHighScores = new String[10];
        if (position != -1) {
            String newScore = name + "," + Integer.toString(score) + "," + Long.toString(System.currentTimeMillis());
            newHighScores = insertScore(highScores, newScore, position);
            writeScoresToFile(newHighScores);
        }
    }

    /**
     * Takes a String[] of scores and writes it to the highscores file
     */
    private void writeScoresToFile(String[] scores) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(HIGHSCORE_FILE, false));
            for (int i = 0; i < 10; i++) {
                bw.write(scores[i]);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException ioe) {
            System.out.println("Error opening file");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe) {
                    System.out.println("Error writing file");
                }
            }
        }
    }

    /**
     * Display the name, score, and position of the current run
     */
    private void displayScore(String name, int score, int position) {
        UI currentRun = new UI();
        String message = "Congratulations " + name + ", you scored " + score + "points";
        currentRun.setImage(new GreenfootImage(message, 45, new Color(255, 0, 0), new Color(0, 0, 0, 0), new Color(0, 0, 0)));
        addObject(currentRun, 495, 290);

        UI highScoreInfo = new UI();
        String highScoreMessage = "";
        if (position == -1) {
            highScoreMessage = "You didn't place in the top 10, better luck next time!";
        } else {
            position += 1;
            highScoreMessage = "Well done! You placed " + position;
            if (position == 1) {
                highScoreMessage += "st!";
            } else if (position == 2) {
                highScoreMessage += "nd!";
            } else if (position == 3) {
                highScoreMessage += "rd!";
            } else {
                highScoreMessage += "th!";
            }
        }
        highScoreInfo.setImage(new GreenfootImage(highScoreMessage, 45, new Color(255, 0, 0), new Color(0, 0, 0, 0), new Color(0, 0, 0)));
        addObject(highScoreInfo, 495, 390);
    }
}
