import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * InvisibleWall is used to block the UI area from being accessible
 */
public class InvisibleWall extends ImpassableTerrain
{
    public InvisibleWall()
    {
        setImage(new GreenfootImage("images/terrain/treeStump.png"));
        getImage().setTransparency(0);
    }
}
