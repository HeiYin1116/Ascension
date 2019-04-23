import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Animates the points going towards the ScoreCounter on the top right
 */
public class PointNumber extends Actor
{
    private float xMovement, yMovement;
    private float xPos, yPos;
    private int duration = 60;
    boolean reachedX = false;
    boolean reachedY = false;
    private int points;
    private boolean scoreAdded = false;
    
    /**
     * Takes parameters of point value of the dead enemy, and the enmies x and y location
     * Creates the PointNumber at the dead enemy's location
     */
    public PointNumber(int points, int x, int y)
    {
        xPos = x;
        yPos = y;
        this.points = points;
        setImage(new GreenfootImage(Integer.toString(points), 40, Color.YELLOW, new Color(0,0,0,0)));
        xMovement = (930f-x)/duration;
        yMovement = (70f-y)/duration;
    }

    /**
     * Animation of the points flying towards the ScoreCounter object on the top right, removing itself when it reaches the boarder of the ScoreCounter 
     */
    public void act() 
    {
        if(!scoreAdded){
            ScoreCounter sc = getWorldOfType(MyWorld.class).getScoreCounter();
            sc.addInstantScore(points);
            scoreAdded = true;
        }
        xPos += xMovement;
        yPos += yMovement;
        if(getX() >= 930){
            reachedX = true;
        }
        if(getY() <= 70){
            reachedY = true;
        }
        setLocation((int)xPos, (int)yPos);
        if(reachedX && reachedY){
            ScoreCounter sc = getWorldOfType(MyWorld.class).getScoreCounter();
            sc.addPoints(points);
            getWorld().removeObject(this);
        }
    }  
}