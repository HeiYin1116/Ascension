import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * PowerUpBoots double the movement speed of the character for its duration
 */
public class PowerUpBoots extends PowerUp
{
    private GreenfootImage DMSPU;

    /**
     * Creates the PowerUpBoots grid in the UI section at the bottom
     */
    public PowerUpBoots()
    {
        DMSPU = new GreenfootImage("UI/PUboots3.png");
        setImage(DMSPU);        
    }

    /**
     * countDown is subtracted in the superclass, when countDown == 1 it will deactivate the power up
     */
    public void act()
    {
        super.act();
        if (countDown == 1){
            deactivateBoots();
        }
    }

    /**
     * Activates the power up, if character speed is at its base value of 4 it will double it and sets the countDown to 600(10s)
     *  changes the image indicating the active power up
     * else, just resets the countDown to 600
     */
    public void activateBoots()
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        if(c.getSpeed() == 4)
        {
            c.doubleSpeed();
            countDown = 600;
        }
        else{
            //coded so character cannot stack movement speed infinitely, just resets the time if movespeed has already doubled
            countDown =600; 
        }
        setImage(new GreenfootImage("UI/PUboots.png"));
    }

    /**
     * Reverts character speed back to its base
     */
    public void deactivateBoots()
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        c.revertSpeed();
    }
}
