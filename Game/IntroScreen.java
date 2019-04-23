import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class IntroScreen extends World
{
    private static final int TILES_H = 45;
    private static final int TILES_V = 35;
    private static final int BORDER = 22;
    
    /**
     * Creates an informational screen that shows the controls of the game
     */
    public IntroScreen()
    {    
        super(990, 770, 1); 
        GreenfootImage ts = new GreenfootImage("images/terrain/grass_tile.png");
        setBackground(ts);
        MenuControl mc = new MenuControl(true, false, true);
        addObject(mc, 0, 0);
        createIntroScreen();
        createBorder();
        Greenfoot.start();
    }

    /**
     * Creates the grass and tree border.
     */
    private void createBorder()
    {  
        int borderMid = BORDER/2;
        for(int i = 0; i < TILES_H; i++){
            addObject(new TreeStump(), borderMid+i*BORDER, borderMid);
            addObject(new TreeStump(), borderMid+i*BORDER, getHeight()-borderMid);
        }
        for(int i = 0; i < TILES_V; i++){
            addObject(new TreeStump(), borderMid, borderMid+i*BORDER);
            addObject(new TreeStump(), getWidth()-borderMid, borderMid+i*BORDER);
        }
    }

    /**
     * Adds a new EndGameScreen object to the world.
     */
    private void createIntroScreen()
    {
        UI gameOverScreen = new UI();
        gameOverScreen.setImage(new GreenfootImage("IntroPage.png"));
        addObject(gameOverScreen, 495, 385);    
    }

}
