import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * StatScreen shows an overview of the current game status, while also acting as a pause function for the game
 */
public class StatScreen extends Actor
{
    private MyWorld world;
    private StatScreenImage characterModel, 
    currentLevelText, maxHealthText, meleeDamageText, rangedDamageText, rateOfFireText, 
    levelValueText, healthValueText, meleeValueText, rangeValueText, rateOfFireValueText, 
    enemyDamageText, enemyHealthText, 
    enemyDamageValue, enemyHealthValue,
    passWordText, passWordValue;
    private StatScreenImage[] textImages;
    private String[] text;
    private int[][] textPosition;
    private boolean buttonHeld = false;   
    private String password;

    
    /**
     * Create the StatScreen and it's controller. This listens for the C keypress and makes the screen visible
     */
    public StatScreen()
    {        
        setImage(new GreenfootImage("images/StatScreen.png"));
        //Each of these arrays is laid out to match the onscreen appearance, each row here relating to a column on screen
        textImages = new StatScreenImage[]{currentLevelText, maxHealthText, meleeDamageText, rangedDamageText, rateOfFireText, 
            levelValueText, healthValueText, meleeValueText, rangeValueText, rateOfFireValueText, 
            enemyDamageText, enemyHealthText, 
            enemyDamageValue, enemyHealthValue,
            passWordText, passWordValue};
        text = new String[]  {"Current Level", "Max Health", "Melee Damage", "Ranged Damage", "Rate of Fire", 
            "0", "0", "0", "0", "0", 
            "Enemy Bonus Damage", "Enemy Bonus Health", 
            "0", "0",
            "Password(End of Level)", " " };
        textPosition = new int[][]{{500, 100},{500, 180},{500, 260},{500, 340},{500, 420},
            {720, 100},{720, 180},{720, 260},{720, 340},{720, 420},
            {250, 550},{250, 600},
            {500, 550},{500, 600},
            {250,650},{600,650}
        };
    }

    /**
     * Act - do whatever the StatScreen wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        //Check that the button has not been held down, stops the character screen toggling on and off every frame
        if(buttonHeld){
            if(!Greenfoot.isKeyDown("c")){
                buttonHeld = false;
            }
        }
        if(!buttonHeld && Greenfoot.isKeyDown("c")){
            if(world != null && !world.isPaused()){
                //open the menu
                world.setPause(true);
                getImage().setTransparency(200);

                buttonHeld = true;
                //String state = calculateBinaryGamestate();
                //password = createPassword(state);

                pauseMenu();
            } else
            if(world != null && world.isPaused()){
                //close the menu
                world.setPause(false);
                getImage().setTransparency(0);
                removeMenu();
                buttonHeld = true;
            }
        }
    }

    /**
     * Finds the current gamestate and returns a 60-character binary string representation of it
     */
    public String calculateBinaryGamestate()
    {
        String gamestate = "";

        //first 8 bits are level
        String level = Integer.toBinaryString(world.getLevel());
        gamestate += "00000000".substring(level.length())+level;

        //12 bits for current ammo
        String ammo = Integer.toBinaryString(world.getAmmoCount().getAmmo());
        gamestate += "000000000000".substring(ammo.length())+ammo;

        //12 bits for current health
        String health = Integer.toBinaryString(world.getCharacter().getHP());
        gamestate += "000000000000".substring(health.length())+health;

        //6 bits for max health upgrades (count)
        int maxHp = (world.getCharacter().getMaxHP()-100)/50;
        String hUpgrades = Integer.toBinaryString(maxHp);
        gamestate += "000000".substring(hUpgrades.length())+hUpgrades;

        //6 bits for melee damage upgrades (count)
        //currently no upgrades
        gamestate += "000000";

        //6 bits for ranged damage upgrades (count)
        int rangeDamage = (world.getCharacter().getRangeDamage()-3)/2;
        String rUpgrades = Integer.toBinaryString(rangeDamage);
        gamestate += "000000".substring(rUpgrades.length())+rUpgrades;

        //6 bits for rate of fire upgrades (count)
        int rateOfFire = 20-world.getCharacter().getFireCooldown();
        if(rateOfFire < 0){
            rateOfFire = 0;
        }
        String rofUpgrades = Integer.toBinaryString(rateOfFire);
        gamestate += "000000".substring(rofUpgrades.length())+rofUpgrades;

        //checksum, treat the current gamestate as a set of 4-bit values, add them all up and then take the last 4 bits
        int fourbits = 0;
        for(int i = 0; i < 14; i++){
            fourbits += Integer.parseInt(gamestate.substring(i*4, i*4+4), 2);
        }
        String checksum = Integer.toBinaryString(fourbits);
        if(checksum.length() > 4){
            checksum = checksum.substring(checksum.length()-4);
        } else if(checksum.length() < 4){
            checksum = "0000".substring(checksum.length())+checksum;
        }
        gamestate += checksum;
        return gamestate;
    }
    
    /**
     * Takes a 60 character binary string and creates (and returns) a 10 character obfuscated password from it
     */
    public String createPassword(String gamestate)
    {
        //interleve four quarters of the gamestate
        String encrypted = "";
        for(int i = 0; i < 15; i++){
            encrypted += gamestate.charAt(i);
            encrypted += gamestate.charAt(i+15);
            encrypted += gamestate.charAt(i+30);
            encrypted += gamestate.charAt(i+45);
        }

        //flip every other bit
        char[] split = encrypted.toCharArray();
        for(int i = 0; i < 60; i++){
            if(i%2 != 0){
                if(split[i] == '0'){
                    split[i] = '1';
                } else {
                    split[i] = '0';
                }
            }
        }
        String flipped = String.valueOf(split);

        //take each 6bit section and treat it as a character
        String passwordCharacter = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?";
        String password = "";
        for(int i = 0; i < 10; i++){
            int n = Integer.parseInt(flipped.substring(i*6, i*6+6), 2);
            //shift each character 6 spots further
            n = (n+(i*6))%63;
            //every other character goes right to left
            if(i%2 == 0){
                password += passwordCharacter.charAt(n);
            } else {
                password += passwordCharacter.charAt(63-n);
            }
        }
        return password;
    }

    /**
     * Takes a 10 character password and returns the decoded 60 character binary string gamestate
     */
    public String decryptPassword(String password)
    {
        if(password.length() > 10){
            return "Password too long";
        }
        if(password.length() < 10){
            return "Password too short";
        }
        //get each character back to it's 6bit string
        String passwordCharacter = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?";
        String flipped = "";
        for(int i = 0; i < 10; i++){
            int n = passwordCharacter.indexOf(password.charAt(i));
            //undo right-to-left reading on every other character
            if(i%2 != 0){
                n = 63-n;
            }
            //undo shifting each character 6 spots further
            n = Math.floorMod(n-(i*6), 63);
            //re-pad to 6bit and append
            String unpadded = Integer.toBinaryString(n);
            flipped += "000000".substring(unpadded.length())+unpadded;
        }

        //(un)flip every other bit
        char[] split = flipped.toCharArray();
        for(int i = 0; i < 60; i++){
            if(i%2 != 0){
                if(split[i] == '0'){
                    split[i] = '1';
                } else {
                    split[i] = '0';
                }
            }
        }
        String encrypted = String.valueOf(split);

        //reorder the interleved bits
        String firstQ = "", secondQ = "", thirdQ = "", fourthQ = "";
        for(int i = 0; i < 15; i++){
            firstQ += encrypted.charAt(i*4);
            secondQ += encrypted.charAt(i*4+1);
            thirdQ += encrypted.charAt(i*4+2);
            fourthQ += encrypted.charAt(i*4+3);
        }

        String gamestate = firstQ + secondQ + thirdQ + fourthQ;
        //verify checksum
        int fourbits = 0;
        for(int i = 0; i < 14; i++){
            fourbits += Integer.parseInt(gamestate.substring(i*4, i*4+4), 2);
        }
        String checksum = Integer.toBinaryString(fourbits);
        if(checksum.length() > 4){
            checksum = checksum.substring(checksum.length()-4);
        } else if(checksum.length() < 4){
            checksum = "0000".substring(checksum.length())+checksum;
        }
        if(!gamestate.substring(56).equals(checksum)){
            return "Password is invalid";
        }
        return gamestate;
    }

    /**
     * Sets the world.
     */
    public void setWorld()
    {
        world = getWorldOfType(MyWorld.class);
    }

    /**
     * Calls the method to draw all elements on the StatScreen
     */
    private void pauseMenu()
    {
        drawCharacter();
        updateValues();
        drawText();
    }

    /**
     * Creates a new character to go over the pause menu.
     */
    private void drawCharacter()
    {
        characterModel = new StatScreenImage();
        characterModel.setImage("images/character/down_stand.png");
        characterModel.getImage().scale(256,256);
        world.addObject(characterModel, 200, 250);
    }

    /**
     * Gets the current game stats from the world and character classes and replaces the current values in the String array
     */
    private void updateValues()
    {
        Character c = world.getCharacter();
        text[5] = Integer.toString(world.getLevel());
        text[6] = Integer.toString(c.getMaxHP());
        text[7] = Integer.toString(c.getMeleeDamage());
        text[8] = Integer.toString(c.getRangedDamage());
        text[9] = String.format("%.2f", c.getRateOfFire()) + "/s";
        text[12] = Integer.toString(world.getEnemyDamageBonus());
        text[13] = Integer.toString(world.getEnemyHealthBonus());
        String previousLevel = Integer.toString(world.getLevel()-1);
        if(previousLevel.equals("0")){
            text[14] = "";
        } else {
            text[14] = "Password(end of level " + Integer.toString(world.getLevel()-1)+ ")";
            text[15] = password;
        }
    }

    /**
     * Creates, draws, and places each text element based on the data in the arrays
     */
    private void drawText()
    {
        Font f = new Font("Arial Black", false, false, 20);
        for(int i = 0; i < textImages.length; i++){
            textImages[i] = new StatScreenImage();
            textImages[i].setImage(new GreenfootImage(text[i], 45, new Color(82, 184, 205), new Color(0,0,0,0)));
            textImages[i].getImage().setFont(f);
            world.addObject(textImages[i], textPosition[i][0], textPosition[i][1]);
        }
    }    

    /**
     * Removes the pause menu from the world.
     */
    private void removeMenu()
    {
        world.removeObject(characterModel);
        for(int i = 0; i < textImages.length; i++){
            world.removeObject(textImages[i]);
        }
    }

    /**
     * Sets the current password. This is called (and therefore the game is 'saved') at the end of each level
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
}