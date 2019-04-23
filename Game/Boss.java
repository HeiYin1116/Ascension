import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Boss enemy that appears every 5th level, having it's own unique attack patterns and logic
 */
public class Boss extends Enemy
{
    protected static final int speed = 1;
    protected static int maxhp = 100;
    protected static final int damage = 10;
    private static final int bulletDamage = 5;
    private static final int rpgDamage = 20;
    private MyWorld world;
    private int Count;
    private int scale = 1;
    private boolean create = false;
    private boolean firstAttackLeft = true;
    private boolean firstAttackRight = true;
    public Circle[] circles;
    public UI[] stars;
    private int x;
    private int y;
    private int i = 1;
    private int j;
    private boolean visible;
    private int wait;
    private GreenfootImage[] animateBoss;
    private int attackType = 0;
    private int maxAttack = 0;
    private int firedBullet = 0;
    private int firedRPG = 0;
    private int count;
    private int animation = 1;
    private int attackDelay = 60;
    private GreenfootSound shootSoundEffect;
    private GreenfootSound bombSoundEffect;
    private GreenfootSound bombExplodeSoundEffect;
    private boolean soundMade = false;

    /**
     * Constructor for standard Boss, allowing bonuses to be applied to the base stats
     */
    public Boss(int speedBonus, int hpBonus, int damageBonus)
    {
        super(speed+speedBonus, maxhp+hpBonus, damage+damageBonus);
        super.setImmunity();
        points = 1000 + 30*hpBonus + 50*damageBonus;
        spriteDown = new GreenfootImage("Boss/BossNormal.png");
        setImage(spriteDown);
        initialiseAttacks();
        setBulletDamage(bulletDamage);
        createWalkingAnimation();
        shootSoundEffect = new GreenfootSound("EnemyShoot.wav");
        bombSoundEffect = new GreenfootSound("BossAttack.wav");
        bombExplodeSoundEffect = new GreenfootSound("BossExplode.wav");
        bombExplodeSoundEffect.setVolume(80);
    }

    /**
     * Calls checkLevel when the Boss is added to the world
     */
    @Override
    protected void addedToWorld(World world){
        checkLevel();
    }

    /**
     * Creates the objects needed for the Boss's attacks
     */
    private void initialiseAttacks()
    {
        circles = new Circle[12];
        stars = new UI[12];
        for(int i = 0; i < 12; i++){
            circles[i] = new Circle();
            stars[i] = new UI();
            stars[i].setImage("star.png");
            stars[i].getImage().scale(20, 20);
        }
        visible = true;
    }

    /**
     * Override the setImage to rescale images to 2x2 their normal size
     */
    @Override
    public void setImage(GreenfootImage image){
        image.scale(image.getWidth()*2,image.getHeight()*2);
        super.setImage(image);
    }

    public void act() 
    {
        // Checks if the game is paused
        if (getWorld() instanceof MyWorld) {
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if (paused) {
                return;
            }
        }
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        super.act();
        if(intersects(c)){
            c.setKnockbackAngle(c.relativeAngle(getX(), getY()));
            c.takeDamage(damage);
        }
        if(Count < 50){
            setAnimation();
            if(animation == 54){
                if(attackType == 0){
                    if(!create){
                        createAttack();
                        create = true;
                        visible = true;
                    }
                    attack();
                    checkHealth();
                }else if(attackType == 1) {
                    setImage(new GreenfootImage("Boss/BossAttack11.png"));
                    fire();
                    firedBullet++;
                    if(firedBullet > 6){
                        checkHealth();
                        firedBullet = 0;
                        if(getHP() > (maxhp/4)){
                            attackType = 0;
                        }
                    }
                }else if(attackType == 2){
                    setImage(new GreenfootImage("Boss/BossAttack11.png"));
                    fireRPG();
                    firedRPG++;
                    if(firedRPG > 6){
                        checkHealth();
                        firedRPG = 0;
                    }
                }else{
                    attackType = 0;
                }
            }
        }

        if(wait > 0){
            wait--;
        }
        Count--;
        if(damageInvuln > 0){
            invulnerability();
        }
    }   

    /**
     * Readies the objects needed for the large area attacks
     */
    public void createAttack(){
        if(attackDelay == 60){ 
            bombSoundEffect.play();
            //create markers
            if(firstAttackLeft){
                createWideMarkers();
            } else {
                createCloseMarkers();
            }
            attackDelay--;
            createAttackLeft();
            createAttackRight();
            createAttackUp();
            createAttackDown();       
        }
    }

    /**
     * Creates the wide attack pattern
     */
    public void createWideMarkers(){
        int x = 150;
        int y = 150;
        for(int i = 0; i < 5; i++){
            world.addObject(stars[i], (i+1)*x+40, y);
            world.addObject(stars[i+5], (i+1)*x+40, 3*y);
        }
        for(int i = 0; i < 2; i++){
            world.addObject(stars[i+10], (i*4+1)*x+40, 2*y);
        }
    }

    /**
     * Creates the close attack pattern
     */
    public void createCloseMarkers(){
        int x = 150;
        int y = 150;
        for(int i = 0; i < 3; i++){
            world.addObject(stars[i], (i+2)*x+40, y);
            world.addObject(stars[i+3], (i+2)*x+40, 3*y);
        }
        for(int i = 0; i < 2; i++){
            world.addObject(stars[i+10], (i*2+2)*x+40, 2*y);
        }
    }

    /**
     * Checks the health of the boss and changes his attack patterns based on that
     */
    public void checkHealth(){
        if(getHP() > (maxhp*3)/4){
            attackType = 0;
        }else if(getHP() <= (maxhp/2) && getHP() > (maxhp/4) && visible == false){
            attackType = (attackType+1)%maxAttack;
        }else if(getHP() <= (maxhp/4) && visible == false){
            attackType = (attackType+1)%maxAttack;
        }
    }

    /**
     * Sets the maximum attack type based on current level
     */
    public void checkLevel(){
        world = getWorldOfType(MyWorld.class);
        if(world.getLevel() <= 5){
            maxAttack = 1;
        }else if(world.getLevel() <= 10){
            maxAttack = 2;
        } else {
            maxAttack = 3;
        }
    }

    /**
     * Creates the left-most explosions
     */
    public void createAttackLeft(){
        x = 150;
        y = 150;
        if(firstAttackLeft){
            i = 1;
            j = 0;
            for(int c = 0; c <= 2; c++){
                circles[c].getImage().setTransparency(0);
                circles[c].getImage().scale(100, 100);
                getWorld().addObject(circles[c], x*i-j+40, y*i);
                i++;
                j += 150;
            }
            firstAttackLeft = false;
        }else{
            i = 2;
            j = 0;
            for(int c = 0; c <= 0; c++){
                circles[c].getImage().setTransparency(0);
                circles[c].getImage().scale(100, 100);
                getWorld().addObject(circles[c], x*i-j+40, y*i);
                i++;
                j += 150;
            }
            firstAttackLeft = true;
        }
    }

    /**
     * Creates the upper explosions
     */
    public void createAttackUp(){
        x = 150;
        y = 150;       
        i = 2;
        j = 150;
        for(int c = 3; c <= 5; c++){
            circles[c].getImage().setTransparency(0);
            circles[c].getImage().scale(100, 100);
            getWorld().addObject(circles[c], x*i+40, y*i-j);
            i++;
            j = j + 150;
        }
    }

    /**
     * Creates the right-hand explosions
     */
    public void createAttackRight(){
        x = 150;
        y = 150;
        if(firstAttackRight){
            i = 1;
            j = 600;
            for(int c = 6; c <= 8; c++){
                circles[c].getImage().setTransparency(0);
                circles[c].getImage().scale(100, 100);
                getWorld().addObject(circles[c], x*i+j+40, y*i);
                i++;
                j -= 150;
            }
            firstAttackRight = false;
        }else{
            i = 2;
            j = 300;
            for(int c = 7; c <= 7; c++){
                circles[c].getImage().setTransparency(0);
                circles[c].getImage().scale(100, 100);
                getWorld().addObject(circles[c], x*i+j+40, y*i);
                i++;
                j -= 150;
            }
            firstAttackRight = true;
        }
    }

    /**
     * Creates the bottom explosions
     */
    public void createAttackDown(){
        x = 150;
        y = 150;       
        i = 2;
        j = 150;
        for(int c = 9; c <= 11; c++){
            circles[c].getImage().setTransparency(0);
            circles[c].getImage().scale(100, 100);
            getWorld().addObject(circles[c], x*i+40, y*i+j);
            i++;
            j -= 150;
        }
    }

    /**
     * Area attack logic for the boss, scales the explosions
     */
    public void attack(){
        if(attackDelay > 0){
            attackDelay--;
            return;
        }
        bombSoundEffect.stop();
        if(!soundMade){
            bombExplodeSoundEffect.play();
            soundMade = true;
        }
        if(Count <  10 && scale <= 150){
            if(wait < 1){
                wait = 100;
            }
            scale = scale + 10;
            for(int s = 0; s <= 11; s++){
                for(int c = 0; c <= 11; c++){
                    circles[c].getImage().scale(20, 20);
                    circles[c].getImage().setTransparency(255);
                    circles[c].getImage().scale(scale, scale);
                }
            }
        }else if(scale > 150 && wait < 1){
            removeAttack();
            soundMade = false;
            Count = 100;
            scale = 1;
            create = false;
            visible = false;
            attackDelay = 60;
            setImage(new GreenfootImage("Boss/BossNormal.png"));
            count = 0;
            animation = 1;
        }
    }

    /**
     * Removes all objects used in the area attack from world
     */
    public void removeAttack(){
        for(int i = 0; i <= 11; i++){
            if(visible == true){
                getWorld().removeObject(circles[i]);
                world.removeObject(stars[i]);
            }
        }
    } 

    /**
     * Fires a normal non-homing shot
     */
    public void fire(){
        shootSoundEffect.play();
        MyWorld world = getWorldOfType(MyWorld.class);
        if(world != null){
            int X = 455;
            int Y = 230;
            double angle = world.getRelativeAngle(X, Y);
            int randomAngle = Greenfoot.getRandomNumber(5);

            Bullet bullet = new Bullet(angle-180 + randomAngle, bulletDamage);
            world.addObject(bullet, X, Y);
            bullet.initialiseLocation();
            Count = 100;
        }
        isAttacking = false;
        count = 0;
        animation = 1;
    }

    /**
     * Fires a homing RPG shot
     */
    public void fireRPG(){
        MyWorld world = getWorldOfType(MyWorld.class);
        if(world != null){
            
            double angle = 0;
            RPG bullet = new RPG(angle, false);
            world.addObject(bullet, 425, 230);
            bullet.initialiseLocation();
            Count = 100;
        }
        isAttacking = false;
        count = 0;
        animation = 1;
    }

    /**
     * Initialises images used for the animation to an array
     */
    public void createWalkingAnimation()
    {            
        animateBoss = new GreenfootImage[11];
        for(int i = 1; i < 12; i++){
            animateBoss[i-1] = new GreenfootImage("Boss/BossAttack"+i+".png");
        }
    }

    /**
     * Sets the current picture based on the animation frame
     */
    public void setAnimation(){
        if (count < 53){
            animation = (animation+1)%240;
            setImage(new GreenfootImage(animateBoss[animation/5]));
            count++;
        }
    }

    /**
     * Flashes the boss if it is invulnerable from taking damage
     */
    private void invulnerability()
    {
        damageInvuln--;
        for(int i = 0; i < 11; i++){
            if(damageInvuln%10 >= 5){
                getImage().setTransparency(50);
            } else
            if(damageInvuln%10 < 5){
                getImage().setTransparency(255);
            }
        }
    }

    /**
     * Overriden method to update the different, larger, HP bar for the boss
     */
    @Override
    protected void updateHP()
    {
        //77f as the max-length bar is 77px long
        float scalePercentage = (77f*hp)/maxhp;
        if(scalePercentage >= 1){
            health.getImage().scale((int)scalePercentage, 13);
            healthOffset = (int)scalePercentage/2 - 47;
            hpText.setImage(new GreenfootImage(Integer.toString(hp), 13, Color.WHITE, new Color(0,0,0,0)));
        }
        int x = getX();
        int y = getY();
        healthbar.setLocation(x, y-70);
        health.setLocation(x+healthOffset, y-70);
        hpText.setLocation(x+40, y-70); 
    }    
}