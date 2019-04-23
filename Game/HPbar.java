import greenfoot.*;

/**
 * UI element for the HP Bar, scales and updates the bar as health changes
 */
public class HPbar extends Actor
{
    private GreenfootImage HPbar;
    private int hpLength;
    private Character c;

    /**
     * Creates the HP bar and sets it's image
     */
    public HPbar()
    {
        HPbar = new GreenfootImage("UI/HPbar.png");
        setImage(HPbar);
        hpLength = getImage().getWidth();
    }

    public void act() 
    {
        if(c.getHP() > 1){
            updateCharacterHP();
        }
    }    

    /**
     * Calculate current HP Percentage
     */
    public void updateCharacterHP()
    {
        int hp = c.getHP();
        float maxHP = (float)c.getMaxHP();
        float percentage = hp/maxHP;
        scaleHP(percentage);
    }
    
    /**
     * Redraw the image based on the current HP Percentage
     */
    private void scaleHP(float scale)
    {
        HPbar = new GreenfootImage("UI/HPbar.png");
        HPbar.scale((int)(hpLength*scale), getImage().getHeight());
        setImage(HPbar);
        setLocation(getImage().getWidth()/2+135, 704);
    }

     /**
     * Gets the character in the world.
     */
    public void setCharacter()
    {
        c = getWorldOfType(MyWorld.class).getCharacter();
    }
}
