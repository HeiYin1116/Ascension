import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Rock used as a border around UI/Play area
 */
public class Rock extends ImpassableTerrain
{
    public Rock()
    {
        //Image sourced from: https://onimaru.itch.io/green-valley-map-pack
        setImage(new GreenfootImage("images/terrain/rock.png"));
    }
}
