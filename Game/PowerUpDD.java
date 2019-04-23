import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * PowerUpDD doubles the melee damage of the character for its duration
 */
public class PowerUpDD extends PowerUp
{
    private GreenfootImage DDPU;
    private boolean limit = false;

    /**
     * Creates the PowerUpDD grid in the UI section at the bottom
     */
    public PowerUpDD()
    {
        DDPU = new GreenfootImage("UI/PUDD2_x2.png");
        setImage(DDPU);
    }
    
   /**
     * countDown is subtracted in the superclass, when countDown == 1 it will deactivate the power up
     */
    public void act()
    {
        super.act();
        if (countDown == 1){
            deactivateDD();
        }
    }

    /**
     * Activates the power up, if the limit is false, i.e damage has NOT doubled, it will double the damage and set countDown to 600
     *  and sets limit = true and changes the image indicating the active power up
     * else, it will reset countDown to 600
     */
    public void activateDD()
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        if(!(limit))
        {
            c.doubleDamage();
            countDown = 600;
            limit = true;
        }
        else{
            //coded so character cannot stack damage infinitely, just resets the time if movespeed has already doubled
            countDown = 600;
        }
        setImage(new GreenfootImage("UI/PUDD_x2.png"));
    }

    /**
     * Reverts characters attack back and resets limit = false
     */
    public void deactivateDD()
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        c.revertDamage();
        limit = false;
    }
}
