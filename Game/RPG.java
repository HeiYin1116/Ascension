import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;

public class RPG extends Actor {
    public int speed = 5;
    private Character character;
    private double angle = 0;
    private Enemy enemy;
    private float smoothX;
    private float smoothY;
    private boolean playerCreated;
    private boolean flipped = false;
    private GreenfootSound rpgSound;
    private MyWorld world;

    /**
     * Creates a bullet, changes properties depending on whether it is character or
     * enemy created
     */
    public RPG(double angle, boolean playerCreated) {
        this.angle = angle;
        this.playerCreated = playerCreated;
        if (playerCreated) {
            setImage("weapons+items/bullet_left.png");
            rpgSound = new GreenfootSound("RPGfire.wav");
            rpgSound.setVolume(30);
            rpgSound.play();
        } 
        else {
            //Created by enemy
            speed = 5;
            setImage("enemy/enemyRPGBullet.png");
            getImage().rotate(180);
        }
        getImage().scale(30, 20);
        getImage().rotate((int) angle);
    }

    /**
     * Act - do whatever the RPG wants to do. This method is called whenever the
     * 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        // check if game is paused
        if (world != null) {
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if (paused) {
                return;
            }
        }
        if (playerCreated) {
            // check for collision with an enemy
            Enemy enemy = (Enemy)getOneIntersectingObject(Enemy.class);
            move(angle);
            if (enemy != null) {
                if (enemy.getHP() > 0) {
                    enemy.takeDamage(character.getRPGDamage());
                    if (getWorld() != null) {
                        getWorld().removeObject(this);
                    }
                }
            } else {
                removeCheck();
            }
        } else {
            // check for collision with character
            character = (Character)getOneIntersectingObject(Character.class);
            move(angle);
            if (character != null) {
                if (character.getHP() > 0) {
                    character.takeDamage(20);
                    character.setKnockbackAngle(character.relativeAngle(getX(), getY()));
                    if (getWorld() != null) {
                        getWorld().removeObject(this);
                    }
                }
            } else {
                removeCheck();
            }
        }
    }

    public void initialiseLocation() {
        smoothX = getX();
        smoothY = getY();
        world = getWorldOfType(MyWorld.class);
        character = world.getCharacter();
    }

    /**
     * Move the bullet speed distance at the angle provided
     */
    public void move(double angle) {
        List<Enemy> enemies = new ArrayList<Enemy>();
        enemies = getWorld().getObjects(Enemy.class);

        if (enemies.size() > 0 && playerCreated) {
            Enemy enemy = (Enemy) enemies.get(0);
            int x = enemy.getX();
            int y = enemy.getY();
            turnTowards(x, y);
            super.move(5);
        } else if (!playerCreated) {
            Character c = getWorldOfType(MyWorld.class).getCharacter();
            int x = c.getX();
            int y = c.getY();
            turnTowards(x, y);
            super.move(5);
        } else {
            double radians = Math.toRadians(angle - 180);
            double vectorX = Math.cos(radians);
            double vectorY = Math.sin(radians);
            smoothX += speed * vectorX;
            smoothY += speed * vectorY;
            setLocation((int) smoothX, (int) smoothY);
        }
    }

    /**
     * Checks if the bullet is intersecting an impassable terrain, and removes it if
     * so
     */
    public void removeCheck() {
        Actor terrain = getOneIntersectingObject(ImpassableTerrain.class);
        if (terrain != null) {
            getWorld().removeObject(this);
        }
    }
}
