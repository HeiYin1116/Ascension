import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class TitleScreen extends World
{
    private static final int TILES_H = 45;
    private static final int TILES_V = 35;
    private static final int BORDER = 22;
    private MenuControl mc;
    /**
     * Create a TitleScreen with animated Actors and keyboard menu controls
     */
    public TitleScreen()
    {    
        super(990, 770, 1); 
        GreenfootImage ts = new GreenfootImage("images/terrain/grass_tile.png");
        setBackground(ts);
        mc = new MenuControl(false, true, true);
        addObject(mc, 0, 0);
        createBorder();
        createTitle();
        createEnemy();
        createCharacter();
        createHighScoreList();
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
     * Sets the images for the title page.
     */
    private void createTitle()
    {
        //"Survival" in large text
        UI title = new UI();
        title.setImage(new GreenfootImage("images/gamename.png"));
        addObject(title, 270, 360);        

        //"Ascension" subtitle
        UI mainTitle = new UI();
        mainTitle.setImage(new GreenfootImage("images/gamename2.png"));
        addObject(mainTitle, 394, 281);

        //"Press start to continue" text
        UI startTitle = new UI();
        startTitle.setImage(new GreenfootImage("images/startTitle.png"));
        addObject(startTitle, 495, 678);
    }

    /**
     * Creates the enemies for the title page.
     */
    private void createEnemy()
    {
        addObject(new Walker(this), 275, 441);
        addObject(new Walker(this), 225, 441);
        addObject(new Range(this), 175, 441);
        addObject(new Tank(this), 75, 441);
    }
    
    /**
     * Creates the character for the title page.
     */
    private void createCharacter()
    {
        Character character = new Character();
        addObject(character, 401, 441);
    }
    
    /**
     * Create a highscore Actor which listens for the H keypress
     */
    private void createHighScoreList(){
        HighScores hs = new HighScores();
        addObject(hs, 495, 385);
        hs.setWorld();
        hs.getImage().setTransparency(0);
    }
    
    /**
     * Gets the MenuControl
     */
    public MenuControl getMenuControl() {
        return mc;
    }
}
