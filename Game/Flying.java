import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Small enemy that can fly over all obstacles, medium hp, low damage/speed
 */
public class Flying extends Enemy
{
    protected static final int speed = 2;
    protected static final int hp = 10;
    protected static final int damage = 2;
    private GreenfootImage flying2, flying3;
    private GreenfootImage flyingAnimation[]; 
    private int frames = 0;
    
    /**
     * Constructor for objects of class Flying that takes parameters of speedBonus, hpBonus and damageBonus.
     */
    public Flying(int speedBonus, int hpBonus, int damageBonus)
    {
        super(speed+speedBonus, hp+hpBonus, damage+damageBonus);
        points = 150 + 10*hpBonus + 15*damageBonus;
        spriteDown = new GreenfootImage("enemy/flying.png");
        spriteDown.scale(44, 44);
        setImage(spriteDown);
        super.initialise();
        createAnimation();
    }

    public void act()
    {
        super.act();
        if (frames == 40){
            setImage(flyingAnimation[0]);
            frames = 0;
        }
        setImage(flyingAnimation[(frames/10)]);     
        frames++;
    }

    /**
     * Create the images needed for animation and store them in an array
     */
    private void createAnimation()
    {
        flying2 = new GreenfootImage("enemy/flying2.png");
        flying3 = new GreenfootImage("enemy/flying3.png");

        flying2.scale(56, 36);
        flying3.scale(64, 40);

        flyingAnimation = new GreenfootImage[] {spriteDown, flying2, flying3, flying2};
    }
}
