import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Enemy superclass which contains the majority of the enemy logic for movement/attacking/death animations
 */
public abstract class Enemy extends Actor {
    protected int speed;
    protected int hp;
    protected int maxhp;
    protected int damage;
    protected int bulletDamage;
    private int rpgDamage;
    private int remainingKnockback;
    protected GreenfootImage spriteUp, spriteLeft, spriteDown, spriteRight, spriteWhite;
    private int spriteW;
    private int spriteH;
    private float smoothX, smoothY;
    protected boolean stunned = false;
    private boolean firedBullet = false;
    private boolean firedRPG = false;
    protected double angle = 0;
    private int spawnImmunity = 20;
    protected UI healthbar;
    protected UI health;
    protected UI hpText;
    protected int healthOffset = -7;
    protected boolean isAttacking = false;
    public int damageInvuln = 0;
    protected int points = 0;
    private GreenfootSound hitSound;
    private GreenfootSound deathSound;

    /**
     * Each subclass has its own unique speed/hp/damage values to pass through
     */
    public Enemy(int speed, int hp, int damage) {
        this.speed = speed;
        this.hp = hp;
        this.maxhp = hp;
        this.damage = damage;
        hitSound = new GreenfootSound("EnemyHit.mp3");
        hitSound.setVolume(60);
        deathSound = new GreenfootSound("EnemyDeath.wav");
        deathSound.setVolume(80);
        createSprites();
    }

    /**
     * Sets up the sprites and values based on those sprites, as each enemy subclass has it's own sprite
     */
    protected void initialise() {
        spriteH = getImage().getHeight();
        spriteW = getImage().getWidth();
        GreenfootImage sprite = new GreenfootImage(spriteDown);
        sprite.scale(1, 1);
        setImage(sprite);

        // Creates a white version of the sprite to show when an enemy takes damage
        // Paints a new image with white where the current sprite is non-transparent
        GreenfootImage white = new GreenfootImage(spriteDown);
        white.setColor(Color.WHITE);

        for (int i = 0; i < white.getWidth(); i++) {
            for (int j = 0; j < white.getHeight(); j++) {
                if (white.getColorAt(i, j).getAlpha() == 0) {
                    // do nothing
                } else {
                    white.setColorAt(i, j, Color.WHITE);
                }
            }
        }
        spriteWhite = white;
    }

    /**
     * Act - do whatever the Enemy wants to do. This method is called whenever the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() {
        // Checks if the game is paused
        if (getWorld() instanceof MyWorld) {
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if (paused) {
                return;
            }
        }

        if (getWorld().getClass() == TitleScreen.class) {
            // If the enemy exists on the TitleScreen, do the movement animation
            if (getY() == 441) {
                setLocation(getX() + speed, getY());
            }
            if (getX() == 841) {
                setLocation(getX(), getY() - speed);
            }
            if (getY() == 165) {
                setLocation(getX() - speed, getY());
            }
            if (getX() == 75) {
                setLocation(getX(), getY() + speed);
            }
        } else if (getWorld().getClass() == MyWorld.class) {
            // if the enemy exists in a normal level, do it's standard actions
            int xPos = getX();
            int yPos = getY();
            if (spawnImmunity > 0) {
                GreenfootImage newSprite = new GreenfootImage(spriteDown);
                // scale image from 14.5(%) to 100(%) as the spawn immunity goes from 20 to 0,
                // in 4.5% steps
                double scale = (4.5 * (20 - spawnImmunity) + 10) / 100;
                newSprite.scale((int) (spriteW * scale), (int) (spriteH * scale));
                setImage(newSprite);
                spawnImmunity--;
            } else if (spawnImmunity == 0) {
                if (this instanceof Boss) {
                    getWorld().addObject(healthbar, xPos, yPos - 70);
                    getWorld().addObject(health, xPos - 10, yPos - 70);
                    getWorld().addObject(hpText, xPos + 40, yPos - 70);
                } else {
                    // When spawning has finished add the healthbar
                    getWorld().addObject(healthbar, xPos, yPos - 30);
                    getWorld().addObject(health, xPos - 10, yPos - 30);
                    getWorld().addObject(hpText, xPos + 10, yPos - 30);
                }
                spawnImmunity--;
            } else if (!stunned) {
                angle = getWorldOfType(MyWorld.class).getRelativeAngle(xPos, yPos);
                if (!isAttacking && !(this instanceof Boss)) {
                    moveTowardsCharacter(angle);
                }
                checkCharacterCollision();
                if (hp < 1) {
                    dropItem();
                    die();

                }
            } else {
                if (!(this instanceof Boss)) {
                    knockback(angle, remainingKnockback);
                }
            }
        }
    }

    /**
     * Allows the enemy to move towards the character.
     */
    private void moveTowardsCharacter(double angle) {
        double radians = Math.toRadians(angle - 180);
        // get normalised vectors of angle
        double vectorX = Math.cos(radians);
        double vectorY = Math.sin(radians);
        double dX = speed * -vectorX;
        double dY = speed * -vectorY;

        double boundingWidth = (dX / Math.abs(dX) * spriteW / 2) + dX;
        double boundingHeight = (dY / Math.abs(dY) * spriteH / 2) + dY;

        // stop enemy walking over unpathable objects
        if (!(this instanceof Flying)) {
            if (!getObjectsAtOffset((int) boundingWidth, 0, Shootable.class).isEmpty()) {
                if (!(dY == 0)) {
                    // make the enemy move at fullspeed against an object
                    dY += (dY / Math.abs(dY)) * Math.abs(dX);
                }
                dX = 0;
            }
            if (!getObjectsAtOffset(0, (int) boundingHeight, Shootable.class).isEmpty()) {
                if (!(dX == 0)) {
                    // make the enemy move at fullspeed against an object
                    dX += (dX / Math.abs(dX)) * Math.abs(dY);
                }
                dY = 0;
            }
        }
        // cap the movement speed
        if (Math.abs(dX) > speed) {
            dX = speed * (dX / Math.abs(dX));
        }
        if (Math.abs(dY) > speed) {
            dY = speed * (dY / Math.abs(dY));
        }

        smoothX += dX;
        smoothY += dY;
        setLocation((int) smoothX, (int) smoothY);
    }

    /**
     * Makes the enemy take damage and get knockedback by the character.
     */
    public void takeDamage(int damage) {
        if (spawnImmunity < 1) {
            if (this instanceof Boss) {
                if (damageInvuln > 0) {
                    return;
                }
                damageInvuln = 90;
            } else {
                stunned = true;
                remainingKnockback = 51;
                setImage(spriteWhite);
            }
            hitSound.play();
            hp -= damage;
            if (hp > -1) {
                getWorldOfType(MyWorld.class).addObject(new DamageNumber(damage, true), getX(), getY());
            }
            updateHP();
            // damageInvuln = 120;
        }
    }

    /**
     * Propels the enemy backwards after taking a hit
     */
    private void knockback(double angle, int distance) {
        if (distance < 8) {
            setImage(spriteDown);
        }
        angle = Math.toRadians(angle - 180);
        if (distance == 0) {
            stunned = false;
            return;
        }

        // get normalised vectors of the angle
        double vectorX = Math.cos(angle);
        double vectorY = Math.sin(angle);

        // calculate movement distance in each axis
        double dx = distance * vectorX;
        double dy = distance * vectorY;

        int worldHeight = getWorld().getHeight();
        int worldWidth = getWorld().getWidth();

        int width = getImage().getWidth();
        int height = getImage().getHeight();

        // get height/width of enemy normalised to the direction of travel (+/-)
        double boundingWidth = (dx / Math.abs(dx) * width / 2) + dx;
        double boundingHeight = (dy / Math.abs(dy) * height / 2) + dy;

        // check for going out of bounds on x-axis
        List<Shootable> horizontal = getObjectsAtOffset((int) boundingWidth, 0, Shootable.class);
        if (horizontal.size() > 0 || (smoothX + boundingWidth) < 0 || (smoothX + boundingWidth) > worldWidth) {
            dx = 0;
        }

        // check for going out of bounds on y-axis
        List<Shootable> vertical = getObjectsAtOffset(0, (int) boundingHeight, Shootable.class);
        if (vertical.size() > 0 || (smoothY + boundingHeight) < 0 || (smoothY + boundingHeight) > worldHeight) {
            dy = 0;
        }

        smoothX += (float) dx;
        smoothY += (float) dy;
        setLocation((int) smoothX, (int) smoothY);
        remainingKnockback = (int) (remainingKnockback / (1.3));
    }

    /**
     * Sets the smoothX and Y.
     */
    public void initialiseLocation() {
        // initialise smoothing floats to starting values
        smoothX = getX();
        smoothY = getY();
    }

    /**
     * Deals damage to the character if they come in contact
     */
    private void checkCharacterCollision() {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        if (intersects(c)) {
            c.setKnockbackAngle(c.relativeAngle(getX(), getY()));
            c.takeDamage(damage);
        }
    }

    /**
     * Sets all of the images of the health bar.
     */
    private void createSprites() {
        if (this instanceof Boss) {
            healthbar = new UI();
            healthbar.setImage(new GreenfootImage("Boss/bossemptyhpbar.png"));
            health = new UI();
            health.setImage(new GreenfootImage("Boss/EnemyHPbar.png"));
            hpText = new UI();
            hpText.setImage(new GreenfootImage(Integer.toString(hp), 13, Color.WHITE, new Color(0, 0, 0, 0)));
        } else {
            healthbar = new UI();
            healthbar.setImage(new GreenfootImage("enemy/healthbar.png"));
            health = new UI();
            health.setImage(new GreenfootImage("enemy/health.png"));
            hpText = new UI();
            hpText.setImage(new GreenfootImage(Integer.toString(hp), 13, Color.WHITE, new Color(0, 0, 0, 0)));
        }
    }

    /**
     * Returns the variable damage.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Creates 4 smaller EnemyParts from the current sprite, adds them to the world and then removes this enemy
     */
    public void die() {
        deathSound.play();
        
        PointNumber pn = new PointNumber(points, getX(), getY());
        getWorld().addObject(pn, getX(), getY());

        int w = getImage().getWidth() / 2;
        int h = getImage().getHeight() / 2;
        GreenfootImage[] parts = new GreenfootImage[4];
        int[] xDirection = { -1, -1, 1, 1 };
        int[] yDirection = { -1, 1, -1, 1 };

        // draw the 4 enemy parts based on the current sprite
        for (int i = 0; i < 4; i++) {
            parts[i] = new GreenfootImage(w, h);
            int x = i / 2;
            int y = i % 2;
            parts[i].drawImage(getImage(), -w * x, -h * y);
            EnemyPart ep = new EnemyPart(parts[i], xDirection[i], yDirection[i]);
            getWorld().addObject(ep, getX() + x * xDirection[i], getY() + y * yDirection[i]);
        }

        if (this instanceof Boss) {
            ((Boss) this).removeAttack();
        }

        getWorld().removeObject(healthbar);
        getWorld().removeObject(health);
        getWorld().removeObject(hpText);
        getWorld().removeObject(this);
    }

    /**
     * Creates a new item class.
     */
    public void dropItem() {
        new Items(this);
    }

    /**
     * Returns the HP of the enemy.
     */
    public int getHP() {
        return hp;
    }

    /**
     * Overridden method to move the HPbar elements any time the enemy is moved
     */
    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        healthbar.setLocation(x, y - 30);
        health.setLocation(x + healthOffset, y - 31);
        hpText.setLocation(x + 10, y - 29);
    }

    /**
     * Updates the enemies HP.
     */
    protected void updateHP() {
        // 19f as the max-length bar is 19px long
        float scalePercentage = (19f * hp) / maxhp;
        if (scalePercentage >= 1) {
            health.getImage().scale((int) scalePercentage, 3);
            healthOffset = (int) scalePercentage / 2 - 17;
            hpText.setImage(new GreenfootImage(Integer.toString(hp), 13, Color.WHITE, new Color(0, 0, 0, 0)));
        }
    }

    /**
     * Removes damage immunity from the enemy
     */
    public void setImmunity() {
        spawnImmunity = 0;
    }

    public void setBulletDamage(int damage) {
        bulletDamage = damage;
    }

    public void setRPGDamage(int damage) {
        rpgDamage = damage;
    }

    public int getBulletDamage() {
        return bulletDamage;
    }

    public int getRPGDamage() {
        return rpgDamage;
    }

    // public void setFiredBullet(boolean fired) {
        // firedBullet = fired;
    // }

    // public void setFiredRPG(boolean fired) {
        // firedRPG = fired;
    // }

    // public boolean getFiredBullet() {
        // return firedBullet;
    // }

    // public boolean getFiredRPG() {
        // return firedRPG;
    // }

    /**
     * Parts of an enemy used in their death animation
     */
    public class EnemyPart extends Actor {
        private int xDirection, yDirection;
        private int remainingVelocity;
        private boolean active;

        /**
         * Constructor for objects of class EnemyPart.
         */
        public EnemyPart(GreenfootImage sprite, int xDirection, int yDirection) {
            setImage(sprite);
            this.xDirection = xDirection;
            this.yDirection = yDirection;
            remainingVelocity = 20;
            active = true;
        }

        /**
         * Act - do whatever the EnemyPart wants to do. This method is called whenever the 'Act' or 'Run' button gets pressed in the environment.
         */
        public void act() {
            if (active) {
                // travel outwards in the direction provided by the dying enemy
                setLocation(getX() + xDirection * remainingVelocity, getY() + yDirection * remainingVelocity);
                GreenfootImage sprite = getImage();
                // scale image down as it travels
                int newWidth = (int) (sprite.getWidth() * 0.95);
                int newHeight = (int) (sprite.getHeight() * 0.95);
                if (newWidth == 0 || newHeight == 0) {
                    active = false;
                    return;
                }
                sprite.scale(newWidth, newHeight);
                remainingVelocity = (int) (remainingVelocity / 1.5);
            } else {
                getWorld().removeObject(this);
            }
        }
    }
}