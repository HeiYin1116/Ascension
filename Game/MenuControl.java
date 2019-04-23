import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Title/Gameover screen control Class.
 */
public class MenuControl extends Actor
{
    private boolean start;
    private boolean intro;
    private boolean buttonHeld = false; 
    private DamageNumber error;
    /**
     * Create a Menu Control, start is whether the TitleScreen has been passed, 
     * intro is whether it's at the start or end of a game
     */
    public MenuControl(boolean start, boolean intro, boolean buttonHeld)
    {
        getImage().setTransparency(0);
        this.start = start;
        this.intro = intro;
        this.buttonHeld = buttonHeld;
    }

    public void act() 
    {
        if(buttonHeld){
            if(!Greenfoot.isKeyDown("space")){
                buttonHeld = false;
            }
        }

        if(!buttonHeld && start){             
            if(Greenfoot.isKeyDown("space")){

                MyWorld world = new MyWorld();
                Greenfoot.setWorld(world);
                getWorld().removeObject(this);
                intro = false;
                start = true;
                buttonHeld = true;
            }
        }else if(!buttonHeld && intro){
            if(Greenfoot.isKeyDown("space")){
                IntroScreen world = new IntroScreen();
                Greenfoot.setWorld(world);
                getWorld().removeObject(this);
                intro = true;
                start = false;
                buttonHeld = true;
            } else if(Greenfoot.isKeyDown("l")){
                StatScreen ss = new StatScreen();
                String password = Greenfoot.ask("Please enter a password (10 characters)");
                String gamestate = ss.decryptPassword(password);                
                if(gamestate.length() != 60){
                    TitleScreen ts = getWorldOfType(TitleScreen.class);
                    if(error != null){
                        ts.removeObject(error);
                    }
                    error = new DamageNumber(gamestate, true);
                    ts.addObject(error, 495, 550);
                } else {
                    MyWorld world = new MyWorld();
                    int level = world.loadGameState(gamestate);
                    world.nextLevel(level, false);
                }
            }
        }else{
            if(Greenfoot.isKeyDown("space") && !(buttonHeld)){
                getWorldOfType(EndGameScreen.class).stopMusic();
                TitleScreen world = new TitleScreen();
                Greenfoot.setWorld(world);
                getWorld().removeObject(this);
                intro = false;
                start = false;
                buttonHeld = true;
            }
        }
    }  
    
    /**
     * Removes any password error text
     */
    public void removeError()
    {
        if(error != null) {
            TitleScreen ts = getWorldOfType(TitleScreen.class);
            ts.removeObject(error);
        }
    }
}

