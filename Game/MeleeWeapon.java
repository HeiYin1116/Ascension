import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

/**
 * Sword weapon for the character
 */
public class MeleeWeapon extends Actor
{
    private int fadeTime = 0;
    private static final int DURATION = 20;
    private static final int COOLDOWN = -20;
    private int swipeAngle = 0;
    private GreenfootSound[] soundEffects;
    
    /**
     * Creates a sword weapon with the logic needed to check if it should be swung, and whether it's hitting enemies
     */
    public MeleeWeapon(){
        setUpSoundEffects();
    }

    public void act() 
    {
        //check if the game is paused
        if(getWorld() instanceof MyWorld){
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if(paused){
                return;
            }
        }

        fadeTime--;
        if(fadeTime > 0){
            attack();
            rotate();
            centreOnCharacter();
        } else if(fadeTime == 0){
            getImage().setTransparency(0);
        } else if(fadeTime < COOLDOWN){
            checkKeys();
        }
    }

    /**
     * Rotates the sword around the character, both moving it and changing the image
     */
    public void rotate()
    {
        //150 = total swing length (in degrees)
        swipeAngle -= (int)(150/DURATION);
        GreenfootImage newSword = new GreenfootImage("weapons+items/sword.png");
        newSword.rotate(swipeAngle-180);
        setImage(newSword);
    }

    /**
     * Keeps the sword centred on the character's location
     */
    public void centreOnCharacter()
    {
        //-225 due to combination of sword image being diagonal, sword starting 75degrees clockwise and the baseline angle being straight left
        double radians = Math.toRadians(swipeAngle-225);

        //normalise angle in to x/y vectors
        double vectorX = Math.cos(radians);
        double vectorY = Math.sin(radians);
        Character chara = getWorldOfType(MyWorld.class).getCharacter();
        int x = chara.getX();
        int y = chara.getY();
        setLocation(x+(int)(vectorX*40), y+(int)(vectorY*40));
    }

    /**
     * Checks if the sword-swing button has been pressed, attacking if it has been
     */
    public void checkKeys()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if(mouse!=null){
            if(Greenfoot.mousePressed(null) && mouse.getButton() == 1){
                melee(mouse.getX(), mouse.getY());
            }
        }
    }

    /**
     * Initialises the soundEffects from their files
     */
    public void setUpSoundEffects()
    {
        soundEffects = new GreenfootSound[3];
        soundEffects[0] = new GreenfootSound("Sword/sword1.wav");
        soundEffects[1] = new GreenfootSound("Sword/sword2.wav");
        soundEffects[2] = new GreenfootSound("Sword/sword3.wav");
    }

    /**
     * Swings sword in direction of coordinates given
     */
    public void melee(int x, int y)
    {
        int sound = Greenfoot.getRandomNumber(3);
        soundEffects[sound].play();
        
        fadeTime = DURATION;
        Character chara;
        double angle = 0;

        chara = getWorldOfType(MyWorld.class).getCharacter();
        angle = chara.relativeAngle(x, y);
        swipeAngle = (int)angle+120;
        GreenfootImage newSword = new GreenfootImage("weapons+items/sword.png");
        newSword.rotate(swipeAngle-180);
        centreOnCharacter();
        setImage(newSword);
        getImage().setTransparency(255);
    }

    /**
     * Checks if enemies have been hit by sword and damages them if so
     */
    public void attack(){
        MyWorld world = getWorldOfType(MyWorld.class);
        Character c = world.getCharacter();
        List<Enemy> enemies = getIntersectingObjects(Enemy.class);
        if(enemies.size()>0){
            for(Object e: enemies){
                ((Enemy)e).takeDamage(c.getMeleeDamage());
                world.getCharacter().shakeWorld();
            }
        }
    }
}
