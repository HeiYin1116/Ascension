import greenfoot.*;

/**
 * Basic Enemy type, medium speed/hp/damage
 */
public class Walker extends Enemy
{
    protected static final int speed = 2;
    protected static final int hp = 10;
    protected static final int damage = 5;
    private GreenfootImage walker, walkerR2, walkerR3, walkerL, walkerL2, walkerL3;
    private GreenfootImage[] animateR, animateL;
    private int frames = 0;
    /**
     * Constructor for objects of class Walker that takes parameters of speedBonus, hpBonus and damageBonus.
     */
    public Walker(int speedBonus, int hpBonus, int damageBonus)
    {
        super(speed+speedBonus, hp+hpBonus, damage+damageBonus);
        points = 125 + 10*hpBonus + 10*damageBonus;
        spriteDown = new GreenfootImage("enemy/tankEnemy.png");
        spriteDown.scale(39, 45);
        setImage(spriteDown);
        createWalkingAnimation();
        super.initialise();
    }

    /**
     * Constructor for objects of class Walker that takes parameters of title, this is used for the title screen.
     */
    public Walker(TitleScreen title)
    {
        super(speed, hp, damage);

        spriteDown = new GreenfootImage("enemy/tankEnemy.png");
        spriteDown.scale(45,39);  
        createWalkingAnimation();
        setImage(spriteDown);
        super.initialise();
    } 

    /**
     * Constructor used for the animated enemy on the endgame screen
     */
    public Walker(EndGameScreen title)
    {
        super(speed, hp, damage);
        spriteDown = new GreenfootImage("enemy/tankEnemy.png");
        spriteDown.scale(45,39);  
        setImage(spriteDown);
    }  

    public void act()
    {
        super.act();
        if(getWorld() instanceof TitleScreen){
            frames = (frames+1)%30;
            if(getY() == 441){
                setImage(animateR[(frames/10)]);                   
            }
            else{
                setImage(animateL[(frames/10)]);             
            } 
        }
        else if(getWorld() instanceof MyWorld){
            if (frames == 30){
                if(angle > 270 ||  angle < 90){
                    setImage(animateR[0]);                
                }
                else{
                    setImage(animateL[0]);                
                }
                frames = 0;
            }else{
                if(angle > 270 ||  angle < 90){
                    setImage(animateR[(frames/10)]);     
                }
                else{
                    setImage(animateL[(frames/10)]);    
                }
            }
            frames++;
        }
    }

    /**
     * Create the images needed for animation and store them in arrays
     */
    public void createWalkingAnimation()
    {
        walker = new GreenfootImage("enemy/walkerR1.png");
        walkerR2 = new GreenfootImage("enemy/walkerR2.png");
        walkerR3 = new GreenfootImage("enemy/walkerR3.png");
        walkerL = new GreenfootImage("enemy/walkerL1.png");
        walkerL2 = new GreenfootImage("enemy/walkerL2.png");
        walkerL3 = new GreenfootImage("enemy/walkerL3.png");

        walker.scale(39, 42);
        walkerR2.scale(36, 45);
        walkerR3.scale(39, 39);
        walkerL.scale(39, 42);
        walkerL2.scale(36, 45);
        walkerL3.scale(39, 39);

        animateR = new GreenfootImage[] {walker, walkerR2, walkerR3};
        animateL = new GreenfootImage[] {walkerL, walkerL2, walkerL3};
    }
}
