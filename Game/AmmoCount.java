import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * UI Element for the Ammo Counter, stores and draws the current ammo count of the character
 */
public class AmmoCount extends Actor
{
    private GreenfootImage grid;
    private int ammo = 10;
    private int maxAmmo = 100;

    public AmmoCount()
    {
        createGrid();
    }


    /**
     * Creates the grid to go around the gun and the number of bullets.
     */
    private void createGrid()
    {
        grid = new GreenfootImage("UI/UIgrid.png");
        setImage(grid);
        changeImage();
    }

    /**
     * Creates the numbers and adds them to the world for the ammo count.
     */
    public void ammoCount(int ammo)
    {
        Font font = getImage().getFont();
        font = font.deriveFont(18.0f);
        getImage().setFont(font);
        String text = Integer.toString(ammo);
        String maxA = Integer.toString(maxAmmo);
        if(ammo < 10){
            //add a leading zero
            getImage().drawString("0" + text +"/" + maxA, 28, 90);
        }
        else{
            getImage().drawString(text +"/" + maxA, 28, 90);
        }
    }
    
    /**
     * Changes the images of the gun if you have bullets or no bullets.
     */
    public void changeImage()
    {
        if(ammo == 0)
        {
            //"Disabled" image when out of ammo
            setImage(new GreenfootImage("UI/UIgrid.png"));
            ammoCount(0);
        }
        else {
            //"Active" image when shots are available
            setImage(new GreenfootImage("UI/UIgrid2.png"));
            ammoCount(ammo);
        }
    }

    /**
     * Returns the amount of ammo you have.
     */
    public int getAmmo()
    {
        return ammo;
    }

    /**
     * Change the amount of ammo you have and the image if you have no ammo.
     */
    public void removeOneAmmo()
    {
        if(ammo > 0){
            ammo--;
            changeImage();
        }
    }

    /**
     * Increases the ammount to ammo you have until you get to the max 100.
     */
    public void gainAmmo()
    {
        if(ammo < maxAmmo){
            ammo = ammo + maxAmmo/10;
        } else {
            ammo = maxAmmo;
        }
    }
    
    public void setAmmo(int ammo)
    {
        this.ammo = ammo;
        changeImage();
    }
    
    public void increaseMaxAmmo()
    {
        maxAmmo += 20;
        changeImage();
    }
}
