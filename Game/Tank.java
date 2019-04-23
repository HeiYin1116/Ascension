import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Large enemy type. Slow movement
 */
public class Tank extends Enemy
{
    protected static final int SPEED = 1;
    protected static final int HP = 20;
    protected static final int DAMAGE = 10;
    private int frames = 0;
    private GreenfootImage[] animate;
    private GreenfootImage tankFlip;
    /**
     * Constructor for standard Tank, allowing bonuses to be applied to the base stats
     */
    public Tank(int speedBonus, int hpBonus, int damageBonus)
    {
        super(SPEED+speedBonus, HP+hpBonus, DAMAGE+damageBonus);
        points = 200 + 20*hpBonus + 15*damageBonus;
        spriteDown = new GreenfootImage("enemy/EnemySprite.png");
        setImage(spriteDown);
        createWalkingAnimation();
        super.initialise();
    }

    /**
     * Constructor for objects of class Range that takes parameters of title, this is used for the title screen.
     */
    public Tank(TitleScreen title)
    {
        super(2, HP, DAMAGE);
        spriteDown = new GreenfootImage("enemy/EnemySprite.png");  
        createWalkingAnimation();
        setImage(spriteDown);
    } 

    public void act()
    {
        super.act();
        frames = (frames+1)%40;
        setImage(animate[frames/20]);
    }
    

    /**
     * Constructor for objects of class Tank.
     */
    public Tank(EndGameScreen title)
    {
        super(SPEED, HP, DAMAGE);
        spriteDown = new GreenfootImage("enemy/EnemySprite.png");
        //spriteDown.scale(44,44);
        createWalkingAnimation();
        setImage(spriteDown);
    } 

    /**
     * Create the images needed for animation and store them in an array
     */
    public void createWalkingAnimation()
    {
        tankFlip = new GreenfootImage("enemy/EnemySpriteFrontFlip.png");

        animate = new GreenfootImage[] {spriteDown, tankFlip};
    }
}
