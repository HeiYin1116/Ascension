import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * PowerUp controls giving and removing buffs from PowerUps to the character class, based on interally-held timers
 */
public class PowerUp extends Actor
{
    protected int countDown;
    protected String type;

 
    public void act() 
    {
        //check if game is paused
        if(getWorld() instanceof MyWorld){
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if(paused){
                return;
            }
        }
        if(countDown > 0){
            countDown--;
            if(countDown < 180){
                //flash sprites if powerup has < 3 seconds remaining
                if(countDown % 5 != 0){
                    if (this instanceof PowerUpBoots){
                        setImage(new GreenfootImage("UI/PUboots.png"));
                    }
                    if (this instanceof PowerUpDD){
                        setImage(new GreenfootImage("UI/PUDD_x2.png"));
                    }
                    if (this instanceof ShieldPowerUp){
                        setImage(new GreenfootImage("UI/ShieldGrid.png"));
                    }                     
                }
                else{
                    if (this instanceof PowerUpBoots){
                        setImage(new GreenfootImage("UI/PUboots3.png"));
                    }
                    if (this instanceof PowerUpDD){
                        setImage(new GreenfootImage("UI/PUDD2_x2.png"));
                    }
                    if (this instanceof ShieldPowerUp){
                        setImage(new GreenfootImage("UI/No_ShieldGrid.png"));
                    }
                }
            }
        }
    }
}    