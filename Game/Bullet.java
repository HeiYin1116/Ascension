import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Bullet projectile that can be created by the character or enemies
 */
public class Bullet extends Actor
{
    public int speed = 10;
    private int damage = 0;
    private Enemy enemy;
    private Character character;
    private double angle = 0;
    private float smoothX;
    private float smoothY;
    private GreenfootSound soundEffect;

    /**
     * Creates a bullet, changes properties depending on whether it is character or enemy created
     * If created by the player, pass 0 as the damage value, otherwise pass the damage of the enemy
     */
    public Bullet(double angle, int damage){
        this.angle = angle;
        this.damage = damage;
        if(damage == 0){
            setImage("weapons+items/bullet_left.png");
            soundEffect = new GreenfootSound("GunShot.mp3");
            soundEffect.setVolume(50);
            soundEffect.play();            
        }
        else {
            speed = 5;
            setImage("enemy/enemyBullet.png");
        }
        getImage().scale(30, 20);
        getImage().rotate((int)angle);
    }    

    /**
     * Initialise the location of the bullet and the character in the current world
     */
    public void initialiseLocation()
    {
        smoothX = getX();
        smoothY = getY();
        character = getWorldOfType(MyWorld.class).getCharacter();
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
        if (damage == 0){
            //check for collision with an enemy
            enemy = (Enemy)getOneIntersectingObject(Enemy.class);
            move(angle);        
            if(enemy != null){
                if(enemy.getHP() > 0){
                    enemy.takeDamage(character.getRangeDamage());
                    if(getWorld() != null){
                        getWorld().removeObject(this);
                    }
                }
            } else {
                removeCheck();
            }   
        }
        else {
            //check for collision with character
            character = (Character)getOneIntersectingObject(Character.class);
            move(angle);        
            if(character!= null){
                if(character.getHP() > 0){
                    character.takeDamage(damage);
                    character.setKnockbackAngle(angle-180);
                    if(getWorld() != null){
                        getWorld().removeObject(this);
                    }
                }
            } else {
                removeCheck();
            }   
        }
    }

    /**
     * Move the bullet speed distance at the angle provided
     */
    public void move(double angle){
        double radians = Math.toRadians(angle-180);
        double vectorX = Math.cos(radians);
        double vectorY = Math.sin(radians);
        smoothX += speed * vectorX;
        smoothY += speed * vectorY;
        setLocation((int)smoothX, (int)smoothY);
    }

    /**
     * Checks if the bullet is intersecting an impassable terrain, and removes it if so
     */
    public void removeCheck(){
        Actor terrain = getOneIntersectingObject(ImpassableTerrain.class); 
        if(terrain != null){
            getWorld().removeObject(this);
        } 
    }
}
