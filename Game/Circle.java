import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Explosion circle used by the boss's area attack, is harmful to the player
 */
public class Circle extends Actor
{
    private int damage = 50;
    
    /**
     * Creates the initial small circle
     */
    public Circle(){
       setImage("circle.png");
       getImage().scale(20, 20);
    }
    
    public void act() 
    {
        Character c = getWorldOfType(MyWorld.class).getCharacter();
        if(isTouching(Character.class) && getImage().getTransparency() != 0){
            c.setKnockbackAngle(c.relativeAngle(getX(), getY()));
            c.takeDamage(damage);
        }
    }    
}
