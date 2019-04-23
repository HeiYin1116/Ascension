import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.HashMap;

/**
 * A playable level in the game world, a new instance of this class is created for each level the game progresses through
 */
public class MyWorld extends World
{
    private static final GreenfootSound MUSIC = new GreenfootSound("sounds/Main.wav");
    private static final int TILES_H = 45;
    private static final int TILES_V = 35;
    private static final int BORDER = 22;
    private int level;
    private HashMap<String, Actor> gameState;
    private Character character;
    private MeleeWeapon weapon;
    private InvisibleWall iw;
    private HPbar bar;
    private MusicControl m;
    private AmmoCount ammoCount;
    private RPGAmmoCount rpgAmmoCount;
    private PowerUpBoots pub;
    private PowerUpDD DD;
    private Fade fade;
    private Circle circle;
    private ShieldPowerUp SPU;
    private LakeCreator lake;
    private boolean paused = false;
    private boolean mPaused = false;
    private BuildingCreator bk;
    private int enemyDamageBonus;
    private int enemyHealthBonus;
    private StatScreen ss;
    private ScoreCounter sc;
    
    /**
     * Initial constructor called on game start, initialises the UI and game state
     */
    public MyWorld()
    {
        super(TILES_H*BORDER, TILES_V*BORDER, 1);
        setBackground("terrain/grass_tile.png");
        startMusic();
        level = 1;
        createBorder();
        lake = createRandomLake();
        createABuilding();
        createMeleeWeapon();
        createCharacter();
        createEnemies(level);
        createUI();  
        createMC();
        createFade();
        createScoreCounter();
        setPaintOrder(Fade.class, ScoreCounter.class, StatScreenImage.class, StatScreen.class, Character.class, MeleeWeapon.class);
        addStatScreen();
        Greenfoot.start();
    }

    /**
     * Constructor for 2nd and beyond levels, requires being passed an initialised gameState
     */
    public MyWorld(int level, HashMap<String, Actor> gameState, boolean pause)
    {
        super(TILES_H*BORDER, TILES_V*BORDER, 1);
        setBackground("terrain/grass_tile.png");
        mPaused = pause;
        if(pause == false){
            unPause();
        }
        this.gameState = gameState;
        this.level = level;
        if(level%2 == 0){
            createRockBorder();
        } else {
            createBorder();
        }

        if(level%5 == 0){
            createBossTerrain();
        }else{
            lake = createRandomLake();
            createABuilding();
        }
        createFade();
        createMC();

        //redraw the healthbar
        UI healthBarBackground = new UI();
        healthBarBackground.setImage(new GreenfootImage("UI/emptyhpbar.png"));
        addObject(healthBarBackground, 212,703);
        
        restoreState();
        setPaintOrder(Fade.class, ScoreCounter.class, StatScreenImage.class, StatScreen.class, Character.class, MeleeWeapon.class, Boss.class);
        createEnemies(level);
    }

    /**
     * Take each actor from the gameState and assign it back to the relevant instance variable, then re-add it to the world
     */
    public void restoreState()
    {
        iw = (InvisibleWall)gameState.get("iw");
        bar = (HPbar)gameState.get("bar");
        ammoCount = (AmmoCount)gameState.get("ammoCount");
        rpgAmmoCount = (RPGAmmoCount)gameState.get("rpgAmmoCount");
        pub = (PowerUpBoots)gameState.get("pub");
        weapon = (MeleeWeapon)gameState.get("weapon");
        character = (Character)gameState.get("character");
        DD = (PowerUpDD)gameState.get("DD");
        SPU = (ShieldPowerUp)gameState.get("SPU");
        ss = (StatScreen)gameState.get("ss");
        sc = (ScoreCounter)gameState.get("sc");

        addObject(iw, 495, 704);
        addObject(bar, 257, 704);
        addObject(ammoCount, 555,704);
        addObject(rpgAmmoCount, 440,704);
        addObject(pub, 800, 704);
        addObject(weapon, 50, 500);
        addObject(character, 30, character.getY());
        addObject(DD, 921,704);
        addObject(SPU, 670,704);
        addObject(ss, 495, 385);
        addObject(sc, 890, 36);
        ss.setWorld();
        character.initialiseLocation();
        character.setWorld();
    }

    /**
     * Stores the gameState and passes it to a new MyWorld constructor to create a new level
     */
    public void nextLevel(int level, boolean paused)
    {
        //calculate and store password
        String data = ss.calculateBinaryGamestate();
        String password = ss.createPassword(data);
        ss.setPassword(password);
        gameState = new HashMap<String, Actor>();
        gameState.put("bar", bar);
        gameState.put("ammoCount", ammoCount);
        gameState.put("pub", pub);
        gameState.put("DD", DD);
        gameState.put("SPU", SPU);
        gameState.put("iw", iw);
        gameState.put("weapon", weapon);
        gameState.put("character", character);
        gameState.put("rpgAmmoCount", rpgAmmoCount);
        gameState.put("ss", ss);
        gameState.put("sc", sc);
        MyWorld test = new MyWorld(level, gameState, paused);
        Greenfoot.setWorld(test);
    }

    /**
     * Creates and spawns the character in the centre of the play area
     */
    private void createCharacter()
    {
        character = new Character();
        int xPos = TILES_H*BORDER/2;
        int yPos = TILES_V*BORDER/2;
        addObject(character, xPos, yPos);
        int offset = 20;
        //if character is spawning inside a building/lake, randomly reposition them within a 
        //20 radius area, growing the potential radius on each failed placement
        while(character.intersectingShootable()){
            character.setLocation(xPos-offset+Greenfoot.getRandomNumber(offset*2), yPos-offset+Greenfoot.getRandomNumber(offset*2));
            offset++;
        }
        character.initialiseLocation();
        character.setWorld();
    }

    /**
     * Creates new enemies for the game.
     */
    public void createEnemies(int level)
    {
        EnemySpawner es = new EnemySpawner(this, level);
        addObject(es, 0, 0);
    }

    /**
     * Creates the melee weapon and adds it into the world.
     */
    private void createMeleeWeapon()
    {
        weapon = new MeleeWeapon();
        addObject(weapon, -300, -100);
        weapon.getImage().setTransparency(0);
    }

    /**
     * Create music control so music can be muted
     */
    private void createMC(){
        m = new MusicControl();
        addObject(m, -300, -100);
        m.getImage().setTransparency(0);
    }

    /**
     * Creates a buildingCreator and adds it into the world.
     */
    public void createBuildingCreator(){
        bk = new BuildingCreator();
    }

    /**
     * Creates the UI elements at the bottom of the screen and add them to the world
     */
    private void createUI()
    {
        UI healthBarBackground = new UI();
        healthBarBackground.setImage(new GreenfootImage("UI/emptyhpbar.png"));
        addObject(healthBarBackground, 212,703);

        bar = new HPbar();
        addObject(bar, 257,704);
        bar.setCharacter();

        ammoCount = new AmmoCount();
        addObject(ammoCount, 555,704);

        rpgAmmoCount = new RPGAmmoCount();
        addObject(rpgAmmoCount, 440,704);

        pub = new PowerUpBoots();
        addObject(pub, 800,704);

        DD = new PowerUpDD();
        addObject(DD, 921,704);

        SPU = new ShieldPowerUp();
        addObject(SPU, 670,704);

        //Invisible Wall stops enemies/characters from entering the UI area at the bottom
        iw = new InvisibleWall();
        iw.getImage().scale(TILES_H*BORDER, 6*BORDER);
        addObject(iw, 495, 704);
    }

    /**
     * Creates the fade and adds it into the world with a transparency of zero.
     */
    private void createFade(){
        fade = new Fade();
        addObject(fade, 495, 385);
        fade.getImage().setTransparency(0);
    }

    /**
     * Creates border of treestumps around the screen and separating the play area from the UI
     */
    private void createBorder()
    {
        //Need to generalise this to take any actor as a parameter
        int borderMid = BORDER/2;
        for(int i = 0; i < TILES_H; i++){
            addObject(new TreeStump(), borderMid+i*BORDER, borderMid);
            addObject(new TreeStump(), borderMid+i*BORDER, getHeight()-borderMid-110);
            addObject(new TreeStump(), borderMid+i*BORDER, getHeight()-borderMid);
        }
        for(int i = 0; i < TILES_V; i++){
            addObject(new TreeStump(), borderMid, borderMid+i*BORDER);
            addObject(new TreeStump(), getWidth()-borderMid, borderMid+i*BORDER);
        }
    }

    /**
     * Creates border of rocks around the screen and seperating the play area from the UI
     */
    private void createRockBorder()
    {
        int borderMid = BORDER/2;
        for(int i = 0; i < TILES_H; i++){
            addObject(new Rock(), borderMid+i*BORDER, borderMid);
            addObject(new Rock(), borderMid+i*BORDER, getHeight()-borderMid-110);
            addObject(new Rock(), borderMid+i*BORDER, getHeight()-borderMid);
        }
        for(int i = 0; i < TILES_V; i++){
            addObject(new Rock(), borderMid, borderMid+i*BORDER);
            addObject(new Rock(), getWidth()-borderMid, borderMid+i*BORDER);
        }
    }

    /**
     * Creates a special terrain for the boss fight
     */
    private void createBossTerrain(){
        int borderMid = BORDER/2;
        for(int i = 0; i < 5; i++){
            addObject(new Rock(), borderMid+i*BORDER+720, borderMid+100);
            addObject(new Rock(), borderMid+i*BORDER+150, borderMid+100);
            addObject(new Rock(), borderMid+i*BORDER+150, borderMid+480);
            addObject(new Rock(), borderMid+i*BORDER+720, borderMid+480);
        }

        for(int i = 0; i < 5; i++){
            addObject(new Rock(), borderMid+830, borderMid+i*BORDER+100);
            addObject(new Rock(), borderMid+830, borderMid+i*BORDER+392);
            addObject(new Rock(), borderMid+129, borderMid+i*BORDER+100);
            addObject(new Rock(), borderMid+129, borderMid+i*BORDER+392);
        }
    }

    /**
     * Gets relative angle of the character.
     */
    public double getRelativeAngle(int x, int y)
    {
        return getCharacter().relativeAngle(x, y);
    }

    /**
     * Gets character.
     */
    public Character getCharacter()
    {
        return character;
    }

    /**
     * Gets the fade objects.
     */
    public Fade getFade(){
        return fade;
    }

    /**
     * Returns the lake creator.
     */
    public LakeCreator getLakeCreator()
    {
        return lake;
    }

    /**
     * Basic left-to-right screenshake values to pass to the moveAll method
     */
    public void shake(int n)
    {
        int[] xmoves = {-5, 9, -8, 7, -6, 5, -4, 3, -2, 1};
        int[] ymoves = {-1, 1, -1, 1, -1, 1, -1, 1, -1, 1};
        moveAll(xmoves[n], ymoves[n]);
    }

    /**
     * Moves all terrain objects by the dx/dy values, this method is to be called by the act() method of another object to produce frame-by-frame shake
     */
    public void moveAll(int dx, int dy)
    {
        //Changes the background image to simulate shaking of the ground
        //Grass image sourced from https://onimaru.itch.io/green-valley-map-pack
        if(dy == -1){
            setBackground("terrain/grass2.png");
        } else {
            setBackground("terrain/grass_tile.png");
        }
        for(Actor a : getObjects(ImpassableTerrain.class)){
            a.setLocation(a.getX()+dx, a.getY()+dy);
        }
    }

    /**
     * Gets HP bar.
     */
    public HPbar getHPbar()
    {
        return bar;
    }

    /**
     * Gets ammoCount.
     */
    public AmmoCount getAmmoCount()
    {
        return ammoCount;
    }

    /**
     * Gets ammoCount.
     */
    public RPGAmmoCount getRPGAmmoCount()
    {
        return rpgAmmoCount;
    }

    /**
     * Gets the power up boots.
     */
    public PowerUpBoots getPUB()
    {
        return pub;
    }

    /**
     * Gets the power up double damage.
     */
    public PowerUpDD getPUDD()
    {
        return DD;
    }

    /**
     * Gets the shield power up.
     */
    public ShieldPowerUp getSPU()
    {
        return SPU;
    }

    /**
     * Creates a random lake of width/height 5-15 and x/y starting pos of {100-700, 100-400}
     */
    private LakeCreator createRandomLake()
    {
        int xpos = 100 + Greenfoot.getRandomNumber(600);
        int ypos = 100 + Greenfoot.getRandomNumber(300);
        int width = 5 + Greenfoot.getRandomNumber(10);
        int height = 5 + Greenfoot.getRandomNumber(10);

        //leave 100px minimum on the borders
        if(xpos + width*30 > 890){
            xpos = 890 - width*30;
        }
        if(ypos + height*30 > 550){
            ypos = 550 - height*30;
        }
        LakeCreator lake = new LakeCreator(this, xpos, ypos, width, height);

        return lake;
    }

    /**
     * Creates a building in a random location
     * Must be called after the lake is created as BuildingCreator ensures that the building is not
     *  placed on an existing lake
     */
    public void createABuilding(){
        bk = new BuildingCreator();
        addObject(bk, 0, 0);
        boolean buildingCreated = false;
        while(!buildingCreated){
            if(bk.building() == true){
                addObject(new Building(), bk.getbX(), bk.getbY());
                buildingCreated = true;
            }
        }
    }

    /**
     * Returns the lake.
     */
    public LakeCreator getLake()
    {
        return lake;
    }

    /**
     * Returns the boolean paused.
     */
    public boolean isPaused()
    {
        return paused;
    }

    /**
     * Sets the boolean paused.
     */
    public void setPause(boolean paused)
    {
        this.paused = paused;
    }

    /**
     * Returns the boolean paused.
     */
    public boolean isMPaused()
    {
        return mPaused;
    }

    /**
     * Sets the boolean paused.
     */
    public void setMPause(boolean paused)
    {
        this.mPaused = paused;
    }

    /**
     * Adds a new stat screen to the world and sets its position.
     */
    private void addStatScreen()
    {
        ss = new StatScreen();
        addObject(ss, 495, 385);
        ss.setWorld();        
        ss.getImage().setTransparency(0);
    }

    /**
     * Returns the variable level.
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Sets the enemy damage bonus.
     */
    public void setEnemyDamageBonus(int i)
    {
        enemyDamageBonus = i;
    }

    /**
     * Returns the enemy damage bonus.
     */
    public int getEnemyDamageBonus()
    {
        return enemyDamageBonus;
    }

    /**
     * Sets the variable enemy heath bonus.
     */
    public void setEnemyHealthBonus(int i)
    {
        enemyHealthBonus = i;
    }

    /**
     * Returns the variable enemy health bonus.
     */
    public int getEnemyHealthBonus()
    {
        return enemyHealthBonus;
    }

    /**
     * Starts the music
     */
    public void startMusic(){
        MUSIC.playLoop();
        MUSIC.setVolume(30);
    }

    /**
     * Resumes the music
     */
    public void unPause(){
        MUSIC.play();
        MUSIC.setVolume(30);
    }

    /**
     * Stops the music
     */
    public void stopMusic(){
        MUSIC.pause();
    }

    /**
     * Gets the playing status of the music
     */
    public boolean isPlaying(){
        return MUSIC.isPlaying();
    }

    /**
     * Loads a gamestate from a 56 (or 60) bit binary string created by the password system in the StatScreen
     * Returns the level of the gamestate for loading
     */
    public int loadGameState(String gamestate)
    {
        int level = Integer.parseInt(gamestate.substring(0, 8), 2)+1;
        int ammo = Integer.parseInt(gamestate.substring(8, 20), 2);
        int health = Integer.parseInt(gamestate.substring(20, 32), 2);
        int maxHp = Integer.parseInt(gamestate.substring(32, 38), 2)*50+100;
        int meleeDamage = 5+Integer.parseInt(gamestate.substring(38, 44), 2);
        int rangeDamage = 3+2*Integer.parseInt(gamestate.substring(44, 50), 2);
        int cooldown = Integer.parseInt(gamestate.substring(50, 56), 2)+20;

        ammoCount.setAmmo(ammo);
        character.setHP(health);
        character.setMaxHP(maxHp);
        character.setMeleeDamage(meleeDamage);
        character.setRangeDamage(rangeDamage);
        character.setFireCooldown(cooldown);
        return level;
    }

    /**
     * Creates the scoreCounter UI element at the top left of the screen
     */
    private void createScoreCounter(){
        sc = new ScoreCounter();
        addObject(sc, 890, 36);
    }

    /**
     * Returns the score counter
     */
    public ScoreCounter getScoreCounter(){
        return sc;
    }    
}