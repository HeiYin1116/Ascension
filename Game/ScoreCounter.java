import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Dynamically keeps track of the score during game play
 */
public class ScoreCounter extends Actor
{
    private int score = 0;
    private int instantScore = 0;
    
    /**
     * Creates a score counter starts at 0, setting the font size and color. Located at the top right of the game
     */
    public ScoreCounter()
    {
        setImage(new GreenfootImage("00000000",45,new Color(255, 255, 0), new Color(0,0,0,0), new Color(0,0,0)));
    }

    /**
     * Takes parameter of int points when an enemy dies. Instantly adds the score up behind the scene
     */
    public void addInstantScore(int points){
        instantScore += points;
    }

    /**
     * Takes parameter of int points when an enemy dies. End of the animation of the score adding up during game play
     */
    public void addPoints(int points){
        score += points;
        updateScore();
    }

    /**
     * Visual update of the score on the top right. 
     */
    private void updateScore(){
        String scoreString = Integer.toString(score);
        String scoreText = "00000000".substring(scoreString.length()) + score;
        setImage(new GreenfootImage(scoreText,45,new Color(255, 255, 0), new Color(0,0,0,0), new Color(0,0,0)));
    }  
    
    /**
     * returns variable instantScore
     */
    public int getScore()
    {
        return instantScore;
    }
}
