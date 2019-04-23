import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * ShieldPowerUp which provides the character with invulnerability for its duration
 */
public class ShieldPowerUp extends PowerUp
{
    private GreenfootImage DMSPU;
    

    /**
     * Creates the ShieldPowerUp grid in the UI section at the bottom
     */
    public ShieldPowerUp()
    {
        DMSPU = new GreenfootImage("UI/No_ShieldGrid.png");
        setImage(DMSPU);
    }

    /**
     * countDown is subtracted in the superclass, when countDown == 1 it will deactivate the power up
     */
    public void act() 
    {
        super.act();
        if(countDown == 1)
        {
            deactivateShield();
        }
    }    

    /**
     * Activates the power up and sets the countdown to 600 and changes the image indicating the power up is active
     */
    public void activateShield()
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        c.shield();
        //10 second duration
        countDown = 600;
        setImage(new GreenfootImage("UI/ShieldGrid.png"));
    }  

    /**
     * Deactivates the power up
     */
    public void deactivateShield()
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        c.deShield();
    }
}
