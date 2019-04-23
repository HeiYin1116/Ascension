import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Highscores screen accessible from the start screen
 */
public class HighScores extends Actor
{
    private boolean buttonHeld = false;
    private TitleScreen world;
    private boolean open = false;
    private String[] scores;
    private StatScreenImage[] images;
    private StatScreenImage error;

    /**
     * Creates and prepares the HighScores screen, as well as the controller that listens for the H button press
     */
    public HighScores()
    {
        setImage(new GreenfootImage("images/StatScreen.png"));
        getScores();
        if(scores != null) {
            prepareList();
        }
    }

    public void act() 
    {
        //Check that the button has not been held down, stops the character screen toggling on and off every frame
        if(buttonHeld){
            if(!Greenfoot.isKeyDown("h")){
                buttonHeld = false;
            }
        }
        if(!buttonHeld && Greenfoot.isKeyDown("h")){
            if(world != null && !open){
                //open the screen
                getImage().setTransparency(230);
                buttonHeld = true;
                open = true;
                drawList();
            } else
            if(world != null && open){
                //close the screen
                getImage().setTransparency(0);
                removeList();
                buttonHeld = true;
                open = false;
            }
        }
    }

    /**
     * Set the current world that the HighScores screen exists within
     */
    public void setWorld()
    {
        world = getWorldOfType(TitleScreen.class);
    }
    
    /**
     * Gets the scores from the file saved to the local drive
     */
    private void getScores()
    {
        String[] data = EndGameScreen.readFile();
        if(data == null) {
            return;
        }
        scores = new String[20];
        for(int i = 0; i < 10; i++) {
            String[] parts = data[i].split(",");
            scores[i*2] = parts[0];
            scores[i*2+1] = parts[1];
        }
    }
    
    /**
     * Prepares all the images and text needed for the high score screen
     */
    private void prepareList()
    {
        images = new StatScreenImage[22];
        for(int i = 0; i < 22; i++) {
            images[i] = new StatScreenImage();
        }
        //headers
        images[0].setImage(new GreenfootImage("Name", 45, new Color(82, 184, 205), new Color(0,0,0,0)));
        images[1].setImage(new GreenfootImage("Score", 45, new Color(82, 184, 205), new Color(0,0,0,0)));
        //1st place, gold
        images[2].setImage(new GreenfootImage(scores[0], 90, new Color(255, 215, 0), new Color(0,0,0,0)));
        images[3].setImage(new GreenfootImage(scores[1], 90, new Color(255, 215, 0), new Color(0,0,0,0)));
        //2nd place, silver
        images[4].setImage(new GreenfootImage(scores[2], 75, new Color(192, 192, 192), new Color(0,0,0,0)));
        images[5].setImage(new GreenfootImage(scores[3], 75, new Color(192, 192, 192), new Color(0,0,0,0)));
        //3rd place, bronze
        images[6].setImage(new GreenfootImage(scores[4], 60, new Color(205, 127, 50), new Color(0,0,0,0)));
        images[7].setImage(new GreenfootImage(scores[5], 60, new Color(205, 127, 50), new Color(0,0,0,0)));
        
        //4th through 10th place, standard blue
        for(int i = 8; i < 22; i++) {
            images[i].setImage(new GreenfootImage(scores[i-2], 45, new Color(82, 184, 205), new Color(0,0,0,0)));
        }
    }
    
    /**
     * Draws all the elements of the highscore screen
     */
    private void drawList() {
        world.getMenuControl().removeError();
        if(images == null) {
            error = new StatScreenImage();
            String errorMessage = "Could not open the highscores file";
            error.setImage(new GreenfootImage(errorMessage, 60, new Color(255, 0, 0), new Color(0,0,0,0)));
            world.addObject(error, 485, 330);
            return;
        }
        int heightOffset = 100;
        for(int i = 0; i < 11; i++) {
            heightOffset += 20 + images[i*2+1].getImage().getHeight()/2;
            world.addObject(images[i*2], 360, heightOffset);
            int width = images[i*2+1].getImage().getWidth();
            world.addObject(images[i*2+1], 700-width/2, heightOffset);            
        }
    }
    
    /**
     * Removes all elements of the highscore screen
     */
    private void removeList()
    {
        if(error != null) {
            world.removeObject(error);
        } else {
            for(int i = 0; i < 22; i++) {
                world.removeObject(images[i]);
            }
        }
    }
}
