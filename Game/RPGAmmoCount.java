import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class RPGAmmoCount here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RPGAmmoCount extends Actor
{
    private int rpgAmmo = 10;
    private GreenfootImage grid;
    
    /**
     * Creates the UI element which keeps track of current RPG Ammo
     */
    public RPGAmmoCount()
    {
        createGrid();
    }

    /**
     * Creates the grid to go around the gun and the number of bullets.
     */
    private void createGrid()
    {
        grid = new GreenfootImage("weapons+items/rpg_full.png");
        setImage(grid);
        changeRPGImage();
    }

    /**
     * Changes the images of the gun if you have bullets or no bullets.
     */
    public void changeRPGImage()
    {
        if(rpgAmmo == 0)
        {
            //"Disabled" image when out of ammo
            setImage(new GreenfootImage("weapons+items/rpg_empty.png"));
            rpgAmmoCounter(0);
        }
        else {
            //"Active" image when shots are available
            setImage(new GreenfootImage("weapons+items/rpg_full.png"));
            rpgAmmoCounter(rpgAmmo);
        }
    }

    /**
     * Creates the numbers and adds them to the world for the ammo count.
     */
    public void rpgAmmoCounter(int ammo)
    {
        Font font = getImage().getFont();
        font = font.deriveFont(18.0f);
        getImage().setFont(font);
        String text = Integer.toString(ammo);
        if(ammo < 10){
            //add a leading zero
            getImage().drawString("0" + text +"/100", 28, 90);
        }
        else{
            getImage().drawString(text +"/100", 28, 90);
        }
    }

    /**
     * Change the amount of ammo you have and the image if you have no ammo.
     */
    public void removeOneRPGAmmo()
    {
        if(rpgAmmo > 0){
            rpgAmmo--;
            changeRPGImage();
        }
    }
    
    /**
     * Returns the amount of ammo you have.
     */
    public int getRPGAmmo()
    {
        return rpgAmmo;
    }

    /**
     * Increases the ammount to ammo you have until you get to the max 100.
     */
    public void gainRPGAmmo()
    {
        if(rpgAmmo < 90){
            rpgAmmo += 2;
        } else {
            rpgAmmo = 100;
        }
    }
}
