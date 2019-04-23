import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Creates a lake made of individual 30x30 tiles, effectively storing the numerous tiles as a single objects which you can find the boundaries of
 */
public class LakeCreator extends Actor
{
    private int leftX;
    private int topY;
    private int rightX;
    private int bottomY;

    /**
     * Creates a lake in a given world to the given dimensions in the given x/y position
     */
    public LakeCreator(World w, int xpos, int ypos, int width, int height)
    {
        leftX = xpos;
        topY = ypos;
        rightX = xpos + width*30;
        bottomY = ypos + height*30;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                w.addObject(new Lake(), xpos+30*i, ypos+30*j);
            }
        }
    }

    /**
     * Gets the left x position of the lake.
     */
    public int getLeft(){
        return leftX;
    }

    /**
     * Gets the top y position of the lake.
     */
    public int getTop(){
        return topY;
    }

    /**
     * Gets the right x position of the lake.
     */
    public int getRight(){
        return rightX;
    }

    /**
     * Gets the bottom y position of the lake.
     */
    public int getBottom(){
        return bottomY;
    }
}
