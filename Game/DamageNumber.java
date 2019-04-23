import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Animated damage numbers for when players/enemies are hit
 */
public class DamageNumber extends Actor
{
    private boolean error = false;
    private int duration = 30;
    private int vMovement = 7;
    private int hMovement;

    /**
     * Creates an animated damage number, with differening text depending on whether it is player or enemy damage
     */
    public DamageNumber(int damage, boolean playerDamage)
    {
        hMovement = Greenfoot.getRandomNumber(2) -1; //random between -1, 0, 1
        if(playerDamage){
            setImage(new GreenfootImage(Integer.toString(damage), 30, Color.YELLOW, new Color(0,0,0,0), Color.BLACK));
        } else {
            setImage(new GreenfootImage(Integer.toString(damage), 40, Color.RED, new Color(0,0,0,0)));
        }
    }

    /**
     * Creates an animated error message with the given text
     */
    public DamageNumber(String text, boolean error){
        this.error = error;
        if(error){
            setImage(new GreenfootImage(text, 30, Color.RED, new Color(0,0,0,0)));
        } else {
            hMovement = Greenfoot.getRandomNumber(2) -1; //random between -1, 0, 1
            setImage(new GreenfootImage(text, 30, Color.YELLOW, new Color(0,0,0,0)));
        }
    }

    /**
     * take param of items to check which upgrade was picked up
     */
    public DamageNumber(Items upgrade)
    {
        hMovement = Greenfoot.getRandomNumber(2) -1; //random between -1, 0, 1
        if(upgrade instanceof Items.hpUpgrade){
            setImage(new GreenfootImage("Max Health ++", 30, Color.YELLOW, new Color(0,0,0,0)));
        }
        if(upgrade instanceof Items.GunUpgrade){
            setImage(new GreenfootImage("Range DMG ++", 30, Color.YELLOW, new Color(0,0,0,0)));
        }
        if(upgrade instanceof Items.ROFUpgrade){
            setImage(new GreenfootImage("Rate of Fire ++", 30, Color.YELLOW, new Color(0,0,0,0)));
        }
        if(upgrade instanceof Items.SwordUpgrade){
            setImage(new GreenfootImage("Melee Damage ++", 30, Color.YELLOW, new Color(0,0,0,0)));
        }
    }

    public void act() 
    {
        if(duration > 0){
            setLocation(getX()-hMovement, getY()-vMovement);
            if(duration%2 == 0){
                vMovement--;
            }
            duration--;
        }        
        if(duration == 0 && !error){
            getWorld().removeObject(this);
        }
    }    
}
