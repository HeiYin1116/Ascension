import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Actor which controls the currently playing music, allowing the user to pause it
 */
public class MusicControl extends Actor
{
    private MyWorld world;
    private EndGameScreen egsWorld;
    private boolean buttonHeld = false; 

    public void act() 
    {
        //Check that the button is not being held so music doesn't toggle every frame
        if(buttonHeld){
            if(!Greenfoot.isKeyDown("m")){
                buttonHeld = false;
            }
        }

        if(getWorld().getClass() == MyWorld.class){
            world = getWorldOfType(MyWorld.class);
            if(!buttonHeld && Greenfoot.isKeyDown("m")){
                if(world != null && getWorldOfType(MyWorld.class).isPlaying() == true){
                    //Stop the music
                    getWorldOfType(MyWorld.class).stopMusic();
                    world.setMPause(true);
                    buttonHeld = true;
                } else if(world != null && getWorldOfType(MyWorld.class).isPlaying() == false){
                    //Start the music
                    getWorldOfType(MyWorld.class).startMusic();
                    world.setMPause(false);
                    buttonHeld = true;
                }
            }
        }else if(getWorld().getClass() == EndGameScreen.class){
            egsWorld = getWorldOfType(EndGameScreen.class);
            if(!buttonHeld && Greenfoot.isKeyDown("m")){
                if(egsWorld != null && getWorldOfType(EndGameScreen.class).isPlaying() == true){
                    //Stop the music
                    getWorldOfType(EndGameScreen.class).stopMusic();
                    buttonHeld = true;
                } else if(egsWorld != null && getWorldOfType(EndGameScreen.class).isPlaying() == false){
                    //Start the music
                    getWorldOfType(EndGameScreen.class).startMusic();
                    buttonHeld = true;
                }
            }
        }
    }    
}
