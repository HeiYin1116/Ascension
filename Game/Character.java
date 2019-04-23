import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * The main character of the game. Contains the majority of game logic with regards to player input
 */
public class Character extends Actor
{
    private MyWorld world;
    private int speed = 4;
    private float D = 2.828f; //Diagonal speed, (sqrt2)*4
    private static final int SPRITE_W = 32;
    private static final int SPRITE_H = 32;
    private String up, left, down, right;
    private GreenfootImage[] downAnimation, leftAnimation, upAnimation, rightAnimation;
    private GreenfootImage downStand, downStep1, downStep2, leftStand, leftStep1, leftStep2, upStand, upStep1, upStep2, rightStand, rightStep1, rightStep2;
    private float smoothX, smoothY; 
    private boolean stunned = false;
    private boolean shield = false;
    private boolean dead = false;
    private int remainingKnockback;
    private double knockbackAngle;
    private boolean shakeWorld = false;
    private int shake = 0;
    private int fireCooldown = 20;
    private int currentFireCooldown = 20;
    private int cursorX, cursorY;
    private int maxHP = 100;
    private int currentHP = 100;
    private int damage = 5;
    private int damageMultiplier = 1;
    private int rangeDamage = 3;
    private int rpgDamage = 20;
    private boolean isFaded = false;
    private int damageInvuln = 0;
    private int animation = 0;
    private int stationary = 0;
    private GreenfootSound[] soundEffects;
    
    /**
     * Creates a standard character and initialises it's sprites and sound effects
     */
    public Character()
    {
        up = "w";
        left = "a";
        down = "s";
        right = "d";
        createSprites();
        createSoundEffects();
    }

    public void act()
    {
        //check if game is paused
        if(getWorld() instanceof MyWorld){
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if(paused){
                return;
            }
        }
        if(currentHP < 1){
            if(isFaded){
                MyWorld gameworld = getWorldOfType(MyWorld.class);
                EndGameScreen world = new EndGameScreen(gameworld.isMPaused(), gameworld.getScoreCounter().getScore());
                Greenfoot.setWorld(world);
                getWorld().removeObject(this);
            }
            dead = true;
        }
        if(!dead){
            if (getWorld().getClass() == MyWorld.class) {
                //Character is part of a normal level, do standard game logic
                if(!stunned){
                    boolean moved = smoothBoundedMovement();
                    if(moved){
                        animation = (animation+1)%40;
                        stationary = 0;
                    } else {
                        stationary++;
                        if(stationary == 5){
                            animation = (animation/20)*20; //set back to standing (but keep which step is next)
                        }
                    }
                    faceMouse();
                    fire();
                    fireRPG();
                    pickUp();
                    if(damageInvuln > 0){
                        invulnerability();
                    }
                } else {
                    knockback(remainingKnockback);
                }
                if(shakeWorld){
                    world.shake(shake);
                    shake += 1;
                    if(shake > 9){
                        shakeWorld = false;
                        shake = 0;
                    }
                }
                currentFireCooldown--;
            }
            else{
                //movement for titlescreen
                animation = (animation+1)%40;
                if(getY() == 441){
                    setLocation(getX() + 2, getY());
                    setImage(rightAnimation[animation/10]);
                }
                if(getX() == 841){
                    setLocation(getX(), getY() - 2);
                    setImage(upAnimation[animation/10]);
                }
                if(getY() == 165){
                    setLocation(getX() - 2, getY());
                    setImage(leftAnimation[animation/10]);
                }
                if(getX() == 75){
                    setLocation(getX(), getY() + 2);
                    setImage(downAnimation[animation/10]);
                }
            }
        }
    }
    
    /**
     * Initialises references to each of the sound effects used
     */
    public void createSoundEffects(){
        soundEffects = new GreenfootSound[3];
        for(int i = 0; i < 3; i++){
            soundEffects[i] = new GreenfootSound("TakingDamage/damage" + (i+1) + ".wav");
            soundEffects[i].setVolume(35);
        }
    }

    /**
     * Game logic for firing a bullet
     * Checks if the key is pressed, whether there is ammo available, and creates the shot if everything is correct
     */
    public void fire(){
        AmmoCount ammoCount = world.getAmmoCount();
        if(Greenfoot.isKeyDown("space") && ammoCount.getAmmo() > 0){
            if(currentFireCooldown > 0){
                //do nothing, weapon on cooldown
            }else{
                ammoCount.removeOneAmmo();
                MouseInfo mouse = Greenfoot.getMouseInfo();
                double angle = 0;
                int X = getX();
                int Y = getY();
                if(mouse != null){
                    cursorX = mouse.getX();
                    cursorY = mouse.getY();
                }
                angle = relativeAngle(cursorX, cursorY);
                Bullet bullet = new Bullet(angle, 0);
                world.addObject(bullet, X, Y);
                bullet.initialiseLocation();
                currentFireCooldown = fireCooldown;
            }
        }
    }

    /**
     * Game logic for firing the RPG
     * Checks if the key is pressed, whether there is ammo available, and creates the shot if everything is correct
     */
    public void fireRPG(){
        RPGAmmoCount rpgAmmoCount = world.getRPGAmmoCount();
        if(Greenfoot.isKeyDown("r") && rpgAmmoCount.getRPGAmmo() > 0){
            if(currentFireCooldown > 0){
                //do nothing, weapon on cooldown
            }else{
                rpgAmmoCount.removeOneRPGAmmo();
                MouseInfo mouse = Greenfoot.getMouseInfo();
                double angle = 0;
                int X = getX();
                int Y = getY();
                if(mouse != null){
                    cursorX = mouse.getX();
                    cursorY = mouse.getY();
                }
                angle = relativeAngle(cursorX, cursorY);
                RPG bullet = new RPG(angle, true);
                world.addObject(bullet, X, Y);
                bullet.initialiseLocation();
                currentFireCooldown = fireCooldown;
            }
        }
    }

    /**
     * Sets the current knockback angle of the player
     */
    public void setKnockbackAngle(double angle)
    {
        knockbackAngle = angle;
    }

    /**
     * Check if the player can currently be damaged, and if so damages them and knocks them back, playing a hurt sound
     */
    public void takeDamage(int damage)
    {
        if(stunned != true && shield != true && damageInvuln == 0){
            world.addObject(new DamageNumber(damage, false), getX(), getY());
            stunned = true;
            currentHP = currentHP - damage;
            remainingKnockback = 100;
            damageInvuln = 90;
            int sound = Greenfoot.getRandomNumber(3);
            soundEffects[sound].play();
        }
    }

    /**
     * Logic for moving the character back forcefully after taking damage
     */
    private void knockback(int distance)
    {
        double angle = Math.toRadians(knockbackAngle-180);
        if(distance == 0){
            //knockback has finished
            stunned = false;
            return;
        }

        //get normalised vectors of the angle
        double vectorX = Math.cos(angle);
        double vectorY = Math.sin(angle);

        //calculate movement distance in each axis
        double dx = distance * vectorX * -1;
        double dy = distance * vectorY * -1;

        int worldHeight = world.getHeight();
        int worldWidth = world.getWidth();

        //get height/width of character normalised to the direction of travel (+/-)
        double boundingWidth = (dx/Math.abs(dx)*SPRITE_W/2)+dx;
        double boundingHeight = (dy/Math.abs(dy)*SPRITE_H/2)+dy;

        //check for going out of bounds on x-axis
        List<Shootable> horizontal = getObjectsAtOffset((int)boundingWidth, 0, Shootable.class);
        if(horizontal.size()>0 || (smoothX + boundingWidth) < 0 || (smoothX + boundingWidth) > worldWidth){
            dx = 0;
        }

        //check for going out of bounds on y-axis
        List<Shootable> vertical = getObjectsAtOffset(0, (int)boundingHeight, Shootable.class);
        if(vertical.size()>0 || (smoothY + boundingHeight) < 0 || (smoothY + boundingHeight) > worldHeight){
            dy = 0;
        }

        smoothX += (float)dx;
        smoothY += (float)dy;
        setLocation((int)smoothX, (int)smoothY);
        remainingKnockback = (int)(remainingKnockback/(2));
    }

    /**
     * Initialises the character's location for float movement purposes
     */
    public void initialiseLocation()
    {
        smoothX = getX();
        smoothY = getY();
    }

    /**
     * Change character sprite to face in the same cardinal direction as the mouse cursor
     */
    private void faceMouse()
    {
        MouseInfo cursor = Greenfoot.getMouseInfo();
        if(cursor!=null){
            int cursorX = cursor.getX();
            int cursorY = cursor.getY();
            double angle = relativeAngle(cursorX, cursorY);
            if(angle > 315 || angle <= 45){
                setImage(leftAnimation[animation/10]);
            } else
            if(angle > 45 && angle <= 135){
                setImage(upAnimation[animation/10]);
            } else
            if(angle > 135 && angle <= 225){
                setImage(rightAnimation[animation/10]);
            } else
            if(angle > 225 && angle <= 315){
                setImage(downAnimation[animation/10]);
            }
        }
    }

    /**
     * Takes x/y co-ordinates of an actor and returns the angle between the character and actor
     * @return angle in degrees, from 0 (directly left), clockwise through 360
     */
    public double relativeAngle(int x, int y)
    {
        int characterX = getX();
        int characterY = getY();
        double angle = Math.atan2(y-characterY, x-characterX);
        angle = Math.toDegrees(angle) + 180; //Covert to degrees, change from +-180 to 0-360
        return angle;
    }

    /**
     * All movement logic, checks for keypresses, ensures character doesn't move through objects,
     * uses floats to store a smooth movement which is converted to int for setLocation
     * @return whether the character moved (used for animation)
     */
    private boolean smoothBoundedMovement()
    {
        boolean leftPressed = Greenfoot.isKeyDown(left);
        boolean upPressed = Greenfoot.isKeyDown(up);
        boolean rightPressed = Greenfoot.isKeyDown(right);
        boolean downPressed = Greenfoot.isKeyDown(down);

        //cancel opposing movement
        if(leftPressed && rightPressed){
            leftPressed = false;
            rightPressed = false;
        }
        if(upPressed && downPressed){
            upPressed = false;
            downPressed = false;
        }
        float dx = 0;
        float dy = 0;
        if(leftPressed){
            if(upPressed){
                dx -= D;
                dy -= D;
            }
            else if(downPressed){
                dx -= D;
                dy += D;
            }
            else {
                dx -= speed;
            }
        }
        else if(rightPressed)
        {
            if(upPressed){
                dx += D;
                dy -= D;
            }
            else if(downPressed){
                dx += D;
                dy += D;
            }
            else {
                dx += speed;
            }
        }
        else if(upPressed)
        {
            dy -= speed;
        }
        else if(downPressed)
        {
            dy += speed;
        }

        //get height/width of character normalised to the direction of travel (+/-)
        float boundingWidth = (dx/Math.abs(dx)*SPRITE_W/2)+dx;
        float boundingHeight = (dy/Math.abs(dy)*SPRITE_H/2)+dy;

        //check for going out of bounds on x-axis
        List<Shootable> horizontal = getObjectsAtOffset((int)boundingWidth, 0, Shootable.class);
        if(horizontal.size()>0){
            dx = 0;
        }
        //check for going out of bounds on y-axis
        List<Shootable> vertical = getObjectsAtOffset(0, (int)boundingHeight, Shootable.class);
        if(vertical.size()>0){
            dy = 0;
        }
        smoothX += dx;
        smoothY += dy;
        setLocation((int)smoothX, (int)smoothY);
        return (Math.abs(dx) > 0 || Math.abs(dy) > 0);
    }

    /**
     * Creates the animation pictures and put them in the animation arrays
     */
    private void createSprites()
    {
        String folder = "character/";
        downStand = new GreenfootImage(folder+"down_stand.png");
        downStep1 = new GreenfootImage(folder+"down_step1.png");
        downStep2 = new GreenfootImage(folder+"down_step2.png");
        downAnimation = new GreenfootImage[]{downStand, downStep1, downStand, downStep2};
        leftStand = new GreenfootImage(folder+"left_stand.png");
        leftStep1 = new GreenfootImage(folder+"left_step1.png");
        leftStep2 = new GreenfootImage(folder+"left_step2.png");
        leftAnimation = new GreenfootImage[]{leftStand, leftStep1, leftStand, leftStep2};
        upStand = new GreenfootImage(folder+"up_stand.png");
        upStep1 = new GreenfootImage(folder+"up_step1.png");
        upStep2 = new GreenfootImage(folder+"up_step2.png");
        upAnimation = new GreenfootImage[]{upStand, upStep1, upStand, upStep2};
        rightStand = new GreenfootImage(folder+"right_stand.png");
        rightStep1 = new GreenfootImage(folder+"right_step1.png");
        rightStep2 = new GreenfootImage(folder+"right_step2.png");
        rightAnimation = new GreenfootImage[]{rightStand, rightStep1, rightStand, rightStep2};        
        setImage(downStand);
    }

    /**
     * Returns the current HP
     */
    public int getHP()
    {
        return currentHP;
    }

    /**
     * Returns the maximum HP
     */
    public int getMaxHP()
    {
        return maxHP;
    }

    /**
     * Check if there are any items in range of the character, if so picks them up and activates them
     */
    public void pickUp()
    {
        List<Items> items = getObjectsInRange(30, Items.class);  
        for(int i = 0; i < items.size(); i++){
            items.get(i).activate();
            world.removeObject(items.get(i));
        }
    }

    /**
     * Flashes the character sprite every 5 frames while invulnerable
     */
    private void invulnerability()
    {
        damageInvuln--;
        for(int i = 0; i < 4; i++){
            if(damageInvuln%10 == 5){
                upAnimation[i].setTransparency(50);
                leftAnimation[i].setTransparency(50);
                downAnimation[i].setTransparency(50);
                rightAnimation[i].setTransparency(50);
            } else
            if(damageInvuln%10 == 0){
                upAnimation[i].setTransparency(255);
                leftAnimation[i].setTransparency(255);
                downAnimation[i].setTransparency(255);
                rightAnimation[i].setTransparency(255);
            }
        }
    }

    public void shakeWorld()
    {
        shakeWorld = true;
    }

    /**
     * Sets the current world that the character resides in
     */
    public void setWorld()
    {
        world = getWorldOfType(MyWorld.class);
    }
    
    public int getUnmodifiedMeleeDamage()
    {
        return damage;
    }

    public int getMeleeDamage()
    {
        return damage*damageMultiplier;
    }

    public int getRangedDamage()
    {
        return rangeDamage;
    }

    public float getRateOfFire()
    {
        return 60f/fireCooldown;
    }

    public int getFireCooldown()
    {
        return fireCooldown;
    }

    public int getSpeed()
    {
        return speed;
    }

    /**
     * Applies the doubleSpeed powerup
     */
    public void doubleSpeed()
    {
        speed = speed*2;
        D = D*2;
    }

    /**
     * Removes the doubleSpeed powerup
     */
    public void revertSpeed()
    {
        speed = speed/2;
        D = D/2;
    }

    /**
     * Applies the doubleDamage powerup
     */
    public void doubleDamage()
    {
        damageMultiplier = 2;
    }

    /**
     * Removes the doubleSpeed powerup
     */
    public void revertDamage()
    {
        damageMultiplier = 1;
    }

    /**
     * Increases current health by 10%
     */
    public void addHealth()
    {
        currentHP = currentHP + maxHP/10;
        if(currentHP > maxHP){
            currentHP = maxHP;
        }
    }
    
    /**
     * Activates the invicibility shield
     */
    public void shield()
    {
        shield = true;
    }

    /**
     * Removes the invincibility shield
     */
    public void deShield()
    {
        shield = false;
    }

    public boolean isShielded()
    {
        return shield;
    }

    public boolean isDead(){
        return dead;
    }

    public void setFaded(){
        isFaded = true;
    }

    public boolean isFaded(){
        return isFaded;
    }

    public int getRangeDamage()
    {
        return rangeDamage;
    }

    public int getRPGDamage(){
        return rpgDamage;
    }

    /**
     * Returns whether the character is currently intersecting an impassible object
     */
    public boolean intersectingShootable()
    {
        return getOneIntersectingObject(Shootable.class)!=null;
    }

    /**
     * Increases current and Max HP from a powerup
     */
    public void boostHp()
    {
        maxHP += 50;
        currentHP += 50; 
    }

    /**
     * Boosts ranged damage from the powerup
     */
    public void boostRangeDmg()
    {
        rangeDamage += 2;
    }

    /**
     * Increases rate of fire from the powerup
     */
    public void boostROF()
    {
        fireCooldown--;
        AmmoCount ammoCount = world.getAmmoCount();
        ammoCount.increaseMaxAmmo();
    }

    /**
     * Increases melee damage from the powerup
     */
    public void boostSD(){
        damage++;
    }

    public void setHP(int HP)
    {
        this.currentHP = HP;
    }

    public void setMaxHP(int maxHP)
    {
        this.maxHP = maxHP;
    }

    public void setMeleeDamage(int damage)
    {
        this.damage = damage;
    }

    public void setRangeDamage(int damage)
    {
        rangeDamage = damage;
    }

    public void setFireCooldown(int cooldown)
    {
        fireCooldown = cooldown;
    }
}
