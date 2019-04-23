import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Fades the screen to black when character dies.
 */
public class Fade extends Actor
{
    private int transVal = 25;

    /**
     * Fades the screen bit by bit on every frame until blackscreen
     */
    public void act() 
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        if(c.isDead()){ 
            getWorldOfType(MyWorld.class).stopMusic();
            while(transVal != 255){
                if(transVal > 255){
                    transVal = 255;
                }else{
                    getImage().setTransparency(transVal);
                    transVal = transVal + 10;
                    Greenfoot.delay(3);
                    c.isFaded();
                }
            }
        }   
    }
}
