import greenfoot.*;

/**
 * EnemySpawner deals with randomly placing enemies around the play area based on the current level, while also
 * conferring bonuses to the enemies stats as the level increases
 */
public class EnemySpawner extends Actor
{
    private MyWorld world;
    private int level;
    private int sublevel;
    private static final int SPAWN_DELAY = 50;
    private int spawnTimer;
    private int remainingEnemies;
    private int[][] spawnData;
    private int[] currentLevel;
    private int enemies = 0;
    
    /**
     * Takes parameters of world and what level the game is on
     * This will spawn a specific enemy pattern depending on what level it's on 
     */
    public EnemySpawner(MyWorld world, int level)
    {
        this.world = world;
        this.level = level;
        //Convert the level to a 0-5 value to be used as an index for spawnData
        sublevel = (level%5 == 0) ? 4 : level%5-1;
        if(enemies < 1){
            spawnTimer = 0;
            enemies++;
        }else{
            spawnTimer = SPAWN_DELAY;
        }
        //data structure for holding spawn information
        spawnData = new int[][]{
            //level, walkers, range, tank, flying, boss
            {1, 1, 0, 1, 1, 0},
            {2, 2, 2, 0, 0, 0},
            {3, 3, 2, 1, 0, 0},
            {4, 2, 3, 3, 0, 0},
            {5, 0, 0, 0, 0, 1},
        };
        currentLevel = spawnData[sublevel];
        //count the number of enemies to spawn
        for(int i = 1; i < currentLevel.length; i++)
        {
            remainingEnemies += currentLevel[i];
        }
    }
    
    /**
     * Spawns the enemy one at a time at random location on the map. 
     * Upgrades on enemies attack and health based on level.
     */
    public void act()
    {
        if(getWorld() instanceof MyWorld){
            boolean paused = getWorldOfType(MyWorld.class).isPaused();
            if(paused){
                return;
            }
        }
        spawnTimer--;
        if(spawnTimer < 0 && remainingEnemies > 0){
            //choose a random enemy to spawn
            boolean spawn = false;
            int enemyType = 0;
            enemyType = 1 + Greenfoot.getRandomNumber(4);
            while(!spawn){
                if(currentLevel[enemyType] > 0){
                    spawn = true;
                } else {
                    //Cycle upwards through the enemy indexes, rolling over at 5
                    enemyType = (enemyType == 4) ? 5 : (enemyType+1)%5;
                }
            }
            int speedBonus = 0;
            //increase hpBonus by 5 on 6th, 11th, 16th etc levels
            int hpBonus = 5 * ((level-1)/5);
            //increase damageBonus by 1 on 2nd, 4th, 6th etc levels
            int damageBonus = level/2;
            world.setEnemyDamageBonus(damageBonus);
            world.setEnemyHealthBonus(hpBonus);
            if(enemyType == 1){
                spawnEnemy(new Walker(speedBonus, hpBonus, damageBonus));
                currentLevel[1]--;
            } else
            if(enemyType == 2){
                spawnEnemy(new Range(speedBonus, hpBonus, damageBonus));
                currentLevel[2]--;
            } else
            if(enemyType == 3){
                spawnEnemy(new Tank(speedBonus, hpBonus, damageBonus));
                currentLevel[3]--;
            } else
            if(enemyType == 4){
                spawnEnemy(new Flying(speedBonus, hpBonus, damageBonus));
                currentLevel[4]--;
            } else
            if(enemyType == 5){
                spawnEnemy(new Boss(speedBonus, hpBonus, damageBonus));
                currentLevel[5]--;
            }
        }
        if(remainingEnemies == 0){
            //Once enemies have been spawned, add a level logic that can then check when they have all been killed
            LevelLogic ll = new LevelLogic(level);
            world.addObject(ll, 0, 0);
            ll.setWall();
            world.removeObject(this);
        }
    }

    /**
     * Spawns an enemy of the provided class at a random location on the edge of the play area
     */
    private void spawnEnemy(Enemy e)
    {
        spawnTimer = SPAWN_DELAY;
        remainingEnemies--;
        //get random positions between 10 and length/height-10 of play area
        int xPos = 25 + Greenfoot.getRandomNumber(940);
        int yPos = 25 + Greenfoot.getRandomNumber(610);
        int spawnLocation = Greenfoot.getRandomNumber(4);
        if(!(e instanceof Boss)){
            if(spawnLocation == 0){
                //top wall
                yPos = 25;
            } else 
            if(spawnLocation == 1){
                //right wall
                xPos = 965;
            } else
            if(spawnLocation == 2){
                //bottom wall
                yPos = 635;
            } else
            if(spawnLocation == 3){
                //left wall
                xPos = 25;
            }
            world.addObject(e, xPos, yPos);
            e.initialiseLocation();
        }else{
            world.addObject(e, 495, 300);
            e.initialiseLocation();
        }
    }
}