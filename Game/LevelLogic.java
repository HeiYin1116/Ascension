import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;

/**
 * LevelLogic keeps track of when to open the exit to the next level, opens it by removing the wall on 
 * the right hand side, and then calls the world's NextLevel method when the character travels through the opening
 */
public class LevelLogic extends Actor
{
    private int level;
    private boolean levelComplete = false;
    private int destroyDelay = 5;
    private int destroyCount = 0;
    private MyWorld world;
    private ArrayList<Actor> rightWall;
    private GreenfootSound explosion;

    /**
     * Creates a LevelLogic instance which presides over a single level
     */
    public LevelLogic(int level)
    {
        this.level = level;
        rightWall = new ArrayList<Actor>();
        getImage().setTransparency(0);
        explosion = new GreenfootSound("WallExplode.wav");
        explosion.setVolume(70);
    }

    /**
     * Find the terrain pieces in the right hand wall and store reference to them in an ArrayList
     */
    public void setWall()
    {
        world = getWorldOfType(MyWorld.class);
        for(Actor a : world.getObjects(Shootable.class)){
            if(a.getX() > 900 && a.getY() > 180 && a.getY() < 480){
                rightWall.add(a);                    
            }
        }
    }

    /**
     * Checks if there are enemies left. If no enemies remain remove one section of wall every 5 frames
     * until every piece has been removed
     * Once the wall is removed, start checking if the character has passed through to start the next level
     */
    public void act() 
    {       
        List<Enemy> enemies = world.getObjects(Enemy.class);
        if(enemies.isEmpty() && !levelComplete && destroyDelay <= 0){
            world.removeObject(rightWall.get(destroyCount));
            explosion.play();
            destroyCount++;
            destroyDelay = 5;
        } else
        if(destroyCount == rightWall.size()){
            levelComplete = true;
        }
        destroyDelay--;
        Character c = world.getObjects(Character.class).get(0);
        if(levelComplete && c.getX() > 960){
            world.nextLevel(level+1, world.isMPaused());
            world.removeObject(this);
        }
    }
}