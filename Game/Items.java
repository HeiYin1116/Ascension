import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Items deals with creating and placing item drops in the world
 * Contains each item type as an inner class
 */
public class Items extends Actor
{
    private World world;
    private GreenfootSound powerUpSound = new GreenfootSound("PowerUp.mp3");
    private GreenfootSound upgradeSound = new GreenfootSound("Upgrade.mp3");

    public Items()
    {
        //empty constructor for inner classes
    }

    /**
     * Randomly drop an upgrade or powerup when an instance of Items is created
     */
    public Items(Enemy e)
    {
        world = e.getWorld();
        Items item = dropRate();
        if(item != null){
            world.addObject(item, e.getX(), e.getY());
        }
    }

    public void activate(){
        //empty method for subclass use
    }    

    /**
     * Calculates if and what item should drop, and returns it
     */
    public Items dropRate()
    {
        int chance = Greenfoot.getRandomNumber(100)+1;
        Items item = null;
        if(chance <=10){
            item = new Ammo();
        }
        else if(chance <=15){
            item = new RPGAmmo();
        }
        else if(chance <=20){
            item = new DoubleDmg();
        }
        else if (chance <=30){
            item = new DoubleMveSpd();
        }
        else if (chance <=40){
            item = new AddHealth();
        }
        else if (chance <=50){
            item = new Shield();
        }
        else if (chance <=52){
            item = new hpUpgrade();
        }
        else if (chance <=54){
            item = new GunUpgrade();
        }
        else if (chance <=56){
            item = new ROFUpgrade();
        }
        else if(chance <=58){
            item = new SwordUpgrade();
        }
        else {
            //drop nothing
        }
        return item;
    }

    /**
     * PowerUp which doubles you melee damage for a set duration
     */
    public class DoubleDmg extends Items 
    {
        private GreenfootImage DD;

        public DoubleDmg()
        {
            DD = new GreenfootImage("UI/sword_down x2.png");
            setImage(DD);
        }

        public void activate()
        {
            PowerUpDD DD = getWorldOfType(MyWorld.class).getPUDD();
            DD.activateDD();
            powerUpSound.setVolume(40);
            powerUpSound.play();
        }
    }

    /**
     * Pickup that restores ammo to the character
     */
    public class Ammo extends Items
    {
        private GreenfootImage ammo;
        private AmmoCount ammoCount; 
        public Ammo()
        {    
            ammo = new GreenfootImage("weapons+items/ammo.png");
            setImage(ammo);
            getImage().scale(40, 40);
        } 

        public void activate()
        {
            ammoCount = getWorldOfType(MyWorld.class).getAmmoCount();
            ammoCount.gainAmmo();
            ammoCount.changeImage();
            powerUpSound.setVolume(40);
            powerUpSound.play();
        }
    }

    /**
     * Pickup that restores RPG ammo to the player
     */
    public class RPGAmmo extends Items
    {
        private GreenfootImage RPGammo;
        private RPGAmmoCount rpgAmmoCount; 
        public RPGAmmo()
        {        
            RPGammo = new GreenfootImage("weapons+items/RPG_Bullet.jpg");
            setImage(RPGammo);
            getImage().scale(20, 40);            
        } 

        public void activate()
        {
            rpgAmmoCount = getWorldOfType(MyWorld.class).getRPGAmmoCount();
            rpgAmmoCount.gainRPGAmmo();
            rpgAmmoCount.changeRPGImage();
            powerUpSound.setVolume(40);
            powerUpSound.play();
        }
    }

    /**
     * PowerUp that doubles your movement speed for a set duration
     */
    public class DoubleMveSpd extends Items
    {
        private GreenfootImage DMS;

        public DoubleMveSpd(){
            DMS = new GreenfootImage("weapons+items/boots.png");
            setImage(DMS);
            getImage().scale(40, 40);
        }

        public void activate()
        {
            PowerUpBoots pub = getWorldOfType(MyWorld.class).getPUB();
            pub.activateBoots();
            powerUpSound.setVolume(40);
            powerUpSound.play();
        }
    }

    /**
     * PickUp that increases your current health
     */
    public class AddHealth extends Items
    {
        private GreenfootImage AH;

        public AddHealth(){
            AH = new GreenfootImage("weapons+items/Health.png");
            setImage(AH);
            getImage().scale(40, 40);
        }

        public void activate()
        {
            Character c = getWorldOfType(MyWorld.class).getCharacter();
            if(c.getHP() < 100 || c.getHP() + 10 <= 100)
            {
                c.addHealth();
            }
            powerUpSound.setVolume(40);
            powerUpSound.play();
        }
    }

    /**
     * PowerUp that makes you invincible for a set duration
     */
    public class Shield extends Items
    {
        private GreenfootImage SH;

        public Shield(){
            SH = new GreenfootImage("weapons+items/Shield.png");
            setImage(SH);
            getImage().scale(40, 40);
        }

        public void activate()
        {
            ShieldPowerUp spu = getWorldOfType(MyWorld.class).getSPU();
            spu.activateShield();
            powerUpSound.setVolume(40);
            powerUpSound.play();
        }
    }

    /**
     * Upgrade that increases current and max HP
     */
    public class hpUpgrade extends Items
    {
        private GreenfootImage hp, hp2;
        private int animation = 0;
        public hpUpgrade()
        {
            hp = new GreenfootImage("weapons+items/hpUpgrade.png");
            hp2 = new GreenfootImage("weapons+items/hpUpgrade2.png");
            setImage(hp);
            getImage().scale(21,21);
        }

        public void act()
        {
            if(animation%25 == 0){
                if (animation == 50){
                    setImage(hp);
                    getImage().scale(21,21);
                    animation = 0;
                }else{
                    setImage(hp2);
                    getImage().scale(21,21);
                }
            }
            animation++;
        }

        public void activate()
        {
            Character c =  getWorldOfType(MyWorld.class).getCharacter();
            c.boostHp();
            world.addObject(new DamageNumber(this), getX(), getY());
            upgradeSound.setVolume(50);
            upgradeSound.play();
        }
    }

    /**
     * Upgrade that increases the damage of the character's gun
     */
    public class GunUpgrade extends Items
    {
        private GreenfootImage gunUpgrade;

        public GunUpgrade()
        {
            gunUpgrade = new GreenfootImage("weapons+items/GunDmgUpgrade.png");
            setImage(gunUpgrade);
        }

        public void activate()
        {
            Character c =  getWorldOfType(MyWorld.class).getCharacter();
            c.boostRangeDmg();
            world.addObject(new DamageNumber(this), getX(), getY());
            upgradeSound.setVolume(50);
            upgradeSound.play();
        }
    }

    /**
     * Upgrade that reduces the cooldown (increasing firing rate) of the character's gun, and also increases ammo capacity
     */
    public class ROFUpgrade extends Items
    {
        private GreenfootImage gunUpgrade;
        /**
         * takes boolean parameter to see if the item drop is either dmg or ROF upgrade.
         */
        public ROFUpgrade()
        {
            gunUpgrade = new GreenfootImage("weapons+items/ROFUpgrade.png");
            setImage(gunUpgrade);
        }

        public void activate()
        {
            Character c =  getWorldOfType(MyWorld.class).getCharacter();
            c.boostROF();
            world.addObject(new DamageNumber(this), getX(), getY());
            upgradeSound.setVolume(50);
            upgradeSound.play();
        }
    }

    /**
     * Upgrade that increase sword damage of the character
     */
    public class SwordUpgrade extends Items
    {
        private GreenfootImage swordUpgrade;
        /**
         * takes boolean parameter to see if the item drop is either dmg or ROF upgrade.
         */
        public SwordUpgrade()
        {
            swordUpgrade = new GreenfootImage("weapons+items/SD.png");
            setImage(swordUpgrade);
        }

        public void activate()
        {
            Character c =  getWorldOfType(MyWorld.class).getCharacter();
            c.boostSD();
            world.addObject(new DamageNumber(this), getX(), getY());
            upgradeSound.setVolume(50);
            upgradeSound.play();
        }
    }
}
