import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Enemy with a ranged attack, low hp/damage
 */
public class Range extends Enemy
{
    protected static final int speed = 2;
    protected static final int hp = 5;
    protected static final int damage = 2;
    private int fireCooldown = 70;
    private TitleScreen title;
    private GreenfootSound soundEffect;
    private GreenfootImage[] animationR, animationL;
    private int attacking = 25;

    /**
     * Constructor for objects of class Range that takes parameters of speedBonus, hpBonus and damageBonus.
     */
    public Range(int speedBonus, int hpBonus, int damageBonus)
    {
        super(speed+speedBonus, hp+hpBonus, damage+damageBonus);
        points = 150 + 10*hpBonus + 25*damageBonus;
        spriteDown = new GreenfootImage("enemy/rangeEnemy.png");
        spriteDown.scale(81, 63);
        setImage(spriteDown);
        createAttackAnimation();
        super.initialise();
        soundEffect = new GreenfootSound("EnemyShoot.wav");
    }

    /**
     * Constructor for objects of class Range that takes parameters of title, this is used for the title screen.
     */
    public Range(TitleScreen title)
    {
        super(speed, hp, damage);
        spriteDown = new GreenfootImage("enemy/rangeEnemy.png");
        spriteDown.scale(81,63);  
        setImage(spriteDown);
        this.title = title;
    } 

    /**
     * Overridden method that adds the Range attack to the standard movement/contact damage logic from Enemy
     */
    @Override
    public void act()
    {
        super.act();
        if(getWorld() instanceof MyWorld){
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if(paused){
                return;
            }
        }
        if (title instanceof  TitleScreen){
            if(getY() == 441){
               setImage(new GreenfootImage("enemy/rangeEnemy.png"));
               getImage().scale(81, 63);
            }
            if(getX() == 841){
                setImage(new GreenfootImage("enemy/rangeUp.png"));
                getImage().scale(96, 96);
            }
            if(getY() == 165){
                setImage(new GreenfootImage("enemy/rangeEnemyL.png"));
                getImage().scale(81, 63);
            }
            if(getX() == 75){
                setImage(new GreenfootImage("enemy/rangeDown.png"));
                getImage().scale(96, 96);
            }
        }
        else{
            if(fireCooldown < 0 && !stunned){
                isAttacking = true;
                if(attacking%5 == 0 && attacking > 0){
                    if(angle > 270 ||  angle < 90){
                        setImage(animationR[(attacking/5)-1]);
                    }
                    else{
                        setImage(animationL[(attacking/5)-1]);
                    }
                    if(attacking == 5){
                        attack();
                        attacking = 25;
                    }
                }
                attacking--;
            }
            else{
                if(angle > 270 || angle < 90){
                    setImage(spriteDown);
                }
                else{
                    GreenfootImage left = new GreenfootImage("enemy/rangeEnemyL.png");
                    left.scale(81, 63);
                    setImage(left);
                }
                fireCooldown--;
            }
        }
    }

    /**
     * Fires a shot at the character, with some randomness to the cooldown between shots and the angle of fire
     */
    private void attack()
    {
        soundEffect.play();
        MyWorld world = getWorldOfType(MyWorld.class);
        if(world != null){
            int X = getX();
            int Y = getY();
            double angle = world.getRelativeAngle(X, Y);
            int randomCooldown = Greenfoot.getRandomNumber(30)+5;
            int randomAngle = Greenfoot.getRandomNumber(5);

            Bullet bullet = new Bullet(angle-180 + randomAngle, damage);
            world.addObject(bullet, X, Y);
            bullet.initialiseLocation();
            fireCooldown = 70+randomCooldown;
        }
        isAttacking = false;
    }

    /**
     * Returns the variable damage.
     */
    public int getDamage()
    {
        return damage;
    }
    
    /**
     * Returns the ranged damage
     */
    public int getBulletDamage(){
        return damage;
    }
    
    /**
     * Create the images needed for animation and store them in arrays
     */
    private void createAttackAnimation()
    {       
        animationR = new GreenfootImage[5];
        animationL = new GreenfootImage[5];
        for(int i = 0; i < 5; i++){
            animationR[i] = new GreenfootImage("enemy/attack" + (i+1) + ".png");
            animationL[i] = new GreenfootImage("enemy/attackL" + (i+1) + ".png");
            animationR[i].scale(81, 63);
            animationL[i].scale(81, 63);
        }
    }
}
