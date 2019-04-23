import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * BuildingCreator is used to find a valid spot to randomly place the building, this is done in the MyWorld construction
 */
public class BuildingCreator extends Actor
{
    int x;
    int y;

    /**
     * Creates an invisible object
     */
    public BuildingCreator(){
        getImage().setTransparency(0);
    }

    /**
     * Tests a random location on the map to see if it is a valid position for a building and doesn't overlap with the lake
     * x/y coordinates are stored on each attempt so once this method returns true, the valid x/y co-ordinate can be accessed
     */
    public boolean building(){
        x = 100 + Greenfoot.getRandomNumber(780);
        y = 100 + Greenfoot.getRandomNumber(460);
        LakeCreator lk = getWorldOfType(MyWorld.class).getLakeCreator();
        if(x > lk.getLeft()-70 && x < lk.getRight()+30){
            if(y > lk.getTop()-70 && y < lk.getBottom()+30){
                return false;
            }
        }
        return true;
    }
    
    public int getbX(){
        return x;
    }

    public int getbY(){
        return y;
    }
}
