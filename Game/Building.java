import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * A simple building structure
 */
public class Building extends ImpassableTerrain
{
    public Building(){
      setImage(new GreenfootImage("terrain/Building.png"));
      getImage().scale(100,100);  
    }
}
