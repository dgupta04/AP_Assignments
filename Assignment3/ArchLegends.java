package Assignment3;

import java.util.*;
import javafx.util.Pair;

abstract class Character{
    protected int currentLocation;
    protected int HP;
    protected int maxHP;
    protected int currentLevel;  
    Character(int maxHP, int currentLevel){
        this.maxHP = maxHP;
        this.HP = maxHP;
        this.currentLevel = currentLevel;
    }    
}

abstract class Monster extends Character{
    protected final int type; // 1 - Goblin, 2 - Zombie, 3 - Fiend, 4 - Lionfang
    protected final String typeString;
    Monster(int type, String typeString, int currentLocation, int HP, int currentLevel){
        super(HP, currentLevel);
        this.typeString = typeString;
        this.type = type; 
    }
    public void setLocation(int location){
        this.currentLocation = location;
    }
    public int attack(Hero h){
        Random gen = new Random();
        int mean = this.HP/4;
        int variance = mean;
        int damage = Math.abs((int)Math.round((double)mean/2 + ((double)variance/2)*gen.nextGaussian()));
        return damage;
    }
    public void decreaseHP(int damage){
        if(damage > this.HP){
            this.HP = 0;
            return;
        }
        this.HP = this.HP - damage;
    }
    public int returnHP(){
        return this.HP;
    }
    public int returnMaxHP(){
        return this.maxHP;
    }
    public int getLevel(){
        return this.currentLevel;
    }
}

abstract class Hero extends Character{
    protected final int type; // 1 - Warrior , 2 - Mage , 3 - Thief , 4 - Healer
    protected final String typeString;   
    protected int XP;  
    protected int attackAttribute;
    protected int defenseAttribute;
    protected int totalMoves;
    protected int numSuperpowers;
    protected int numMovesAfterSuperpower;
    protected boolean hasSuperpower;
    protected boolean powerActivated;
    Hero(int type, String typeString, int HP, int attackAttribute, int currentLevel){
        super(HP, currentLevel);
        this.type = type;
        this.XP = 0;
        this.attackAttribute = attackAttribute;
        this.typeString = typeString;
        this.totalMoves = 0;
        this.numSuperpowers = 0;
        this.numMovesAfterSuperpower = 0;
        this.powerActivated = false;
    }
    public void setLocation(int location){
        this.currentLocation = location;
    }
    public int returnHP(){
        return this.HP;
    }
    public String getType(){
        return this.typeString;
    }
    protected void resetAttributes(){
        this.HP = this.maxHP;
        this.totalMoves = 0;
        this.numSuperpowers = 0;
        this.hasSuperpower = false;
        this.powerActivated = false;
    }
    public void restoreHealth(){
        resetAttributes();
    }
    protected void makeSuperPowerAvailable(){
        if(totalMoves % 4 == 3){
            numSuperpowers++;
            hasSuperpower = true;
        }
    }
    public void addAndCheckSuperpower(){
        if(totalMoves % 4 == 3){
            numSuperpowers++;
            hasSuperpower = true;
        }
        if(numSuperpowers == 0){
            hasSuperpower = false;
        }
    }    
    protected void standardAttack(Monster m, int attackTotal){
        System.out.println("You attacked and inflicted " + attackTotal + " damage to monster");
        m.decreaseHP(attackTotal);
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        System.out.println("Monster attack!");
        int damageByMonster = m.attack(this);
        if(damageByMonster > this.HP){
            damageByMonster = this.HP;
        }
        System.out.println("The monster attacked and inflicted " + damageByMonster + " to you");
        this.HP = this.HP - damageByMonster;
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP  + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        this.totalMoves++;
    }
    protected void standardDefense(Monster m, int defenseTotal){
        int damageByMonster = m.attack(this);
        int reducedDamage = damageByMonster - defenseTotal;
        if(reducedDamage < 0){
            reducedDamage = 0;
        }
        if(reducedDamage > this.HP){
            this.HP = 0;
            System.out.println("Monster attack!");
            System.out.println("The monster attacked and inflicted " + reducedDamage + " to you");
            System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());        
        }
        else{
            System.out.println("Monster attack reduced by " + reducedDamage);
            System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());        
            System.out.println("Monster attack!");
            System.out.println("The monster attacked and inflicted " + reducedDamage + " to you");
            this.HP = this.HP - reducedDamage;
            System.out.println("Your HP: " + this.HP + "/100, Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        }
        this.totalMoves++;
    }
    public boolean hasSuperpower(){
        return this.hasSuperpower;
    }
    public boolean powerActivated(){
        return this.powerActivated;
    }
    public int checkVictory(Monster m){
        if(m.returnHP() == 0){
            System.out.println("Defeated monster!");
            resetAttributes();
            this.XP = this.XP += m.getLevel()*20;
            if(this.XP >= 20 && this.XP < 60){
                System.out.println("Promoted to level 2!");
                this.maxHP = 150;
            }
            else if(this.XP >= 60 && this.XP < 120){
                System.out.println("Promoted to level 3!");
                this.maxHP = 200;
            }
            else if(this.XP >= 120){
                System.out.println("Promoted to level 4!");
                this.maxHP = 250;
            }
            return 1;
        }
        else if(this.HP == 0){
            System.out.println("You lost to the monster! Restart game!");
            return -1;
        }
        return 0;
    }
    public boolean checkHealth(){
        return this.HP == 0;
    }
    abstract public void attack(Monster m);
    abstract public void specialAttack(Monster m);
    abstract public void defense(Monster m);
    abstract protected void specialDefense(Monster m);
}

class Warrior extends Hero{
    private final int boostedAttack = 5;
    private final int boostedDefense = 5;
    Warrior(){
        super(1, "Warrior", 100, 10, 1);
        this.defenseAttribute = 3;
    }
    @Override
    public void attack(Monster m){
        if(this.hasSuperpower && powerActivated){
            specialAttack(m);
            this.numMovesAfterSuperpower++;
            if(this.numMovesAfterSuperpower == 3){
                powerActivated = false;
                this.numSuperpowers--;
                if(this.numSuperpowers == 0){
                    this.hasSuperpower = false;
                }
                this.numMovesAfterSuperpower = 0;
            }
        }
        else{
            standardAttack(m, this.attackAttribute);
        }
        makeSuperPowerAvailable();
    }
    @Override
    public void specialAttack(Monster m){
        powerActivated = true;
        int totalDamage = this.attackAttribute + this.boostedAttack;
        System.out.println("Using special power for attack!");
        System.out.println("You inflicted additional " + boostedAttack + " damage to the monster, total damage: " + totalDamage);
        standardAttack(m, totalDamage);
    }
    @Override
    public void defense(Monster m){
        if(this.hasSuperpower && powerActivated){
            specialDefense(m);
            this.numMovesAfterSuperpower++;
            if(this.numMovesAfterSuperpower == 3){
                powerActivated = false;
                this.numSuperpowers--;
                if(this.numSuperpowers == 0){
                    this.hasSuperpower = false;
                }
                this.numMovesAfterSuperpower = 0;
            }
        }
        else{
            standardDefense(m, this.defenseAttribute);
        }
        makeSuperPowerAvailable();
    }
    @Override
    protected void specialDefense(Monster m){
        powerActivated = true;
        System.out.println("Using special power for defense!");
        System.out.println("Monster damage reduced by additional" + this.boostedDefense + " HP");
        standardDefense(m, this.defenseAttribute + boostedDefense);
    }
}

class Mage extends Hero{
   Mage(){
        super(2, "Mage", 100, 5, 1);
        this.defenseAttribute = 5;
    }
    @Override
    public void attack(Monster m){
        if(this.hasSuperpower && powerActivated){
            specialAttack(m);
            this.numMovesAfterSuperpower++;
            System.out.println(this.numMovesAfterSuperpower);
            if(this.numMovesAfterSuperpower == 3){
                powerActivated = false;
                this.numSuperpowers--;
                if(this.numSuperpowers == 0){
                    this.hasSuperpower = false;
                }
                this.numMovesAfterSuperpower = 0;
            }
        }
        else{
            standardAttack(m, this.attackAttribute);
        }
        makeSuperPowerAvailable();
    }
    @Override
    public void specialAttack(Monster m){
        powerActivated = true;
        int totalDamage = (int)(this.attackAttribute + m.returnHP()*0.05);
        System.out.println("Using special power for attack!");
        System.out.println("You inflicted additional " + (int)(m.returnHP()*0.05) + "damage to the monster, total damage: " + totalDamage);
        standardAttack(m, totalDamage);
    }
    @Override
    public void defense(Monster m){
        if(this.hasSuperpower && powerActivated){
            specialDefense(m);
            this.numMovesAfterSuperpower++;
            if(this.numMovesAfterSuperpower == 3){
                powerActivated = false;
                this.numSuperpowers--;
                if(this.numSuperpowers == 0){
                    this.hasSuperpower = false;
                }
                this.numMovesAfterSuperpower = 0;
            }
        }
        else{
            standardDefense(m, this.defenseAttribute);
        }
        makeSuperPowerAvailable();
    }
    @Override
    protected void specialDefense(Monster m){
        powerActivated = true;
        System.out.println("Using special power for defense!");
        standardDefense(m, this.defenseAttribute);
        m.decreaseHP((int)(m.returnHP()*0.05));
    }
}

class Thief extends Hero{
    Thief(){
        super(3, "Thief", 100, 6, 1);
        this.defenseAttribute = 4;
    }
    @Override
    public void attack(Monster m){
        standardAttack(m, this.attackAttribute);            
        makeSuperPowerAvailable();
    }
    @Override
    public void specialAttack(Monster m){
        this.numSuperpowers--;
        if(this.numSuperpowers <= 0){
            this.hasSuperpower = false;
        }
        System.out.println("Using special power for attack!");
        int stolenHPMonster = (int)(m.returnHP()*0.3);
        System.out.println("Stole " + stolenHPMonster + " HP from Monster");
        m.decreaseHP(stolenHPMonster);
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        System.out.println("Monster attack!");
        int damageByMonster = m.attack(this);
        if(damageByMonster > this.HP){
            damageByMonster = this.HP;
        }
        System.out.println("The monster attacked and inflicted " + damageByMonster + " to you");
        this.HP = this.HP - damageByMonster;
        System.out.println("Your HP: " + this.HP + "/100, Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        this.totalMoves++;
        powerActivated = false;
        makeSuperPowerAvailable();
    }
    @Override
    public void defense(Monster m){
        if(this.hasSuperpower && powerActivated){
            specialDefense(m);
            this.numSuperpowers--;
            powerActivated = false;
            if(this.numSuperpowers == 0){
                this.hasSuperpower = false;
            }
        }
        else{
            standardDefense(m, this.defenseAttribute);            
        }
        makeSuperPowerAvailable();
    }
    @Override
    protected void specialDefense(Monster m){
        powerActivated = true;
        System.out.println("Using special power for defense!");
        int stolenHPMonster = (int)(m.returnHP()*0.3);
        System.out.println("Stole " + stolenHPMonster + " HP from Monster");
        m.decreaseHP(stolenHPMonster);
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        standardDefense(m, this.defenseAttribute);
        powerActivated = false;
        makeSuperPowerAvailable();                
    }
}

class Healer extends Hero{
    Healer(){
        super(4, "Healer", 100, 4, 1);
        this.defenseAttribute = 8;
    }
    @Override
    public void attack(Monster m){
        if(this.hasSuperpower && powerActivated){
            specialAttack(m);
            this.numMovesAfterSuperpower++;
            if(this.numMovesAfterSuperpower == 3){
                powerActivated = false;
                this.numSuperpowers--;
                if(this.numSuperpowers == 0){
                    this.hasSuperpower = false;
                }
                this.numMovesAfterSuperpower = 0;
            }
        }
        else{
            standardAttack(m, this.attackAttribute);
        }
        makeSuperPowerAvailable();
    }
    @Override
    public void specialAttack(Monster m){
        powerActivated = true;
        System.out.println("Using special power for attack!");
        standardAttack(m, this.attackAttribute);
        int selfHPIncrease = (int)(this.HP*0.05);
        System.out.println("Increased your HP by " + selfHPIncrease);
        int increasedHP = this.HP + selfHPIncrease;
        if(increasedHP > this.maxHP){
            this.HP = this.maxHP;
        }
        else{
            this.HP = increasedHP;
        }
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
    }
    @Override
    public void defense(Monster m){
        if(this.hasSuperpower){
            specialDefense(m);
            this.numMovesAfterSuperpower++;
            if(this.numMovesAfterSuperpower == 3){
                powerActivated = false;
                this.numSuperpowers--;
                if(this.numSuperpowers == 0){
                    this.hasSuperpower = false;
                }
                this.numMovesAfterSuperpower = 0;
            }
        }
        else{
            standardDefense(m, this.defenseAttribute);
        }
        makeSuperPowerAvailable();
    }
    @Override
    protected void specialDefense(Monster m){
        powerActivated = true;
        System.out.println("Using special power for defense!");
        standardDefense(m, this.defenseAttribute);
        int selfHPIncrease = (int)(this.HP*0.05);
        System.out.println("Increased your HP by " + selfHPIncrease);
        int increasedHP = this.HP + selfHPIncrease;
        if(increasedHP > this.maxHP){
            this.HP = this.maxHP;
        }
        else{
            this.HP = increasedHP;
        }
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
    }
}

class Goblin extends Monster{
    Goblin(){
        super(1, "Goblin", 1, 100, 1);
    }
}

class Zombie extends Monster{
    Zombie(){
        super(2, "Zombie", 1, 150, 2);
    }
}

class Fiend extends Monster{
    Fiend(){
        super(3, "Fiend", 1, 200, 3);
    }
}

class Lionfang extends Monster{
    Lionfang(){
        super(4, "Lionfang", 1, 250, 4);
    }
    @Override
    public int attack(Hero h){
        Random prob = new Random();
        double attackProb = prob.nextDouble();        
        if(attackProb > 0.1){ 
            Random gen = new Random();
            int mean = this.HP/4;
            int variance = mean;
            int damage = (int)Math.floor((double)mean/2 + ((double)variance/2)*gen.nextGaussian());
            return damage;
        }
        else{
            return (h.returnHP()/2);
        }
    }
}

class User{
    private final String name;
    private Hero currentHero;
    private int currentLocation;
    private int pathTraversed;
    User(String name, Hero currentHero){
        this.name = name;
        this.currentHero = currentHero;
        this.currentLocation = 0; // 0 is assumed the starting location of every user
        this.pathTraversed = 0;
    }
    public Hero getHero(){
        return this.currentHero;
    }
    public void setHero(Hero newHero){
        this.currentHero = newHero;
    }
    public String getName(){
        return this.name;
    }
    public int getLocation(){
        return this.currentLocation;
    }
    public void setLocation(int currentLocation){
        this.currentLocation = currentLocation;
    }
    public int getPathTraversed(){
        return this.pathTraversed;
    }
    public void setPathTraversed(int currentPathTraversed){
        this.pathTraversed = currentPathTraversed;
    }

}

class Battle{
    private Hero goodGuy;
    private Monster badGuy;
    private GameInstance currentInstance;
    private Scanner s;
    Battle(Hero goodGuy, Monster badGuy, GameInstance currentInstance, Scanner s){
        this.goodGuy = goodGuy;
        this.badGuy = badGuy;
        this.currentInstance = currentInstance;
        this.s = s;
    }
    public int startBattle(){
        while(goodGuy.checkVictory(badGuy) != -1){
            if(goodGuy.checkVictory(badGuy) == 0){
                if(goodGuy.hasSuperpower() && !(goodGuy.powerActivated())){
                    currentInstance.generateFightMenu();                   
                    System.out.println("3) Special Power");
                    int fightMode = s.nextInt();
                    while(fightMode < 1 || fightMode > 3){
                        System.out.println("How many times can you give invalid input! Try again!");
                        currentInstance.generateFightMenu();
                        System.out.println("3) Special Power");
                        fightMode = s.nextInt();
                    }
                    if(fightMode == 1){
                        System.out.println("You chose to attack");
                        goodGuy.attack(badGuy);
                    }
                    else if(fightMode == 2){
                        System.out.println("You chose to defend");
                        goodGuy.defense(badGuy);
                    }
                    else{
                        System.out.println("You activated special power");
                        if(!goodGuy.getType().equals("Thief")){
                            goodGuy.addAndCheckSuperpower();
                        }
                        goodGuy.specialAttack(badGuy);
                    }
                }
                // else if(goodGuy.hasSuperpower()){
                //     currentInstance.generateFightMenu();
                //     int fightMode = s.nextInt();                    
                //     while(fightMode < 1 || fightMode > 2){
                //         System.out.println("How many times can you give invalid input! Try again!");
                //         currentInstance.generateFightMenu();
                //         fightMode = s.nextInt();
                //     }
                //     if(fightMode == 1){
                //         System.out.println("You chose to attack");
                //         goodGuy.attack(badGuy);
                //     }
                //     else if(fightMode == 2){
                //         System.out.println("You chose to defend");
                //         goodGuy.defense(badGuy);
                //     }
                    
                // }
                else{
                    currentInstance.generateFightMenu();
                    int fightMode = s.nextInt();
                    while(fightMode < 1 || fightMode > 2){
                        System.out.println("How many times can you give invalid input! Try again!");
                        currentInstance.generateFightMenu();
                        fightMode = s.nextInt();
                    }
                    if(fightMode == 1){
                        System.out.println("You chose to attack");
                        goodGuy.attack(badGuy);
                    }
                    else if(fightMode == 2){
                        System.out.println("You chose to defend");
                        goodGuy.defense(badGuy);
                    }
                }
            }
            else{
                return 1;
            }
        }
        return -1;
    }
}

class GameInstance{
    private ArrayList<User> allUsers = new ArrayList<>();
    private HashMap<Integer , ArrayList<Integer>> locationByLevel = new HashMap<>();
    private ArrayList<Integer> allLocations;
    GameInstance(){
        allLocations = new ArrayList<>();
        for(int i = 1; i < 5; i++){
            locationByLevel.put(i, new ArrayList<>());
            for(int j = 0; j < 3; j++){
                Random newLoc = new Random();
                int addLoc = newLoc.nextInt(50) + 1;
                while(allLocations.contains(addLoc)){
                    addLoc = newLoc.nextInt(50) + 1;
                } 
                allLocations.add(addLoc);
                locationByLevel.get(i).add(addLoc);
            }
        }
    }
    public Pair <ArrayList<Integer> , Integer> getRandomLocations(int pathTraversed, int currentLocation){
        if(pathTraversed < 4){
            Random levelSeclector = new Random();
            int level = levelSeclector.nextInt(3) + 1;
            while(locationByLevel.get(level).contains(currentLocation)){
                level = levelSeclector.nextInt(3) + 1;
            }            
            return new Pair(locationByLevel.get(level), level);
        }
        else{
            return new Pair(locationByLevel.get(4), 4);
        }
    }
    public int getLevel(int location){
        for(Integer i: locationByLevel.keySet()){
            if(locationByLevel.get(i).contains(location)){
                return i;
            }
        }
        return 0;
    }
    public boolean addNewUser(String name, Hero currentHero){
        for(User i : allUsers){
            if(i.getName().equals(name)){
                System.out.println("User already exists, choose new name");
                return false;
            }
        }
        User addUser = new User(name, currentHero);
        allUsers.add(addUser);
        System.out.println("User created. Username: " + name + " Hero: " + currentHero.getType() + ". Login again to start playing.");
        return true;
    }
    public User loginUser(String name){
        User loggedIn = null;
        for(User i : allUsers){
            if(i.getName().equals(name)){
                loggedIn = i;
                return loggedIn;
            }
        }
        System.out.println("No such user found, try again.");
        return loggedIn;
    }
    public Hero generateHero(int type){
        Hero newHero = null;
        if(type == 1){
            newHero = new Warrior();
        }
        else if(type == 2){
            newHero = new Mage();
        }
        else if(type == 3){
            newHero = new Thief();
        }
        else if(type == 4){
            newHero = new Healer();
        }
        return newHero;        
    }
    public Monster generateMonster(int type){
        Monster newMonster = null;
        if(type == 1){
            newMonster = new Goblin();
        }
        else if(type == 2){
            newMonster = new Zombie();
        }
        else if(type == 3){
            newMonster = new Fiend();
        }
        else if(type == 4){
            newMonster = new Lionfang();
        }
        return newMonster;        
    }
    public void generateMainMenu(){
        System.out.println("Welcome to ArchLegends");
        System.out.println("Choose your option");
        System.out.println("1) Create new User");
        System.out.println("2) Login as existing User");
        System.out.println("3) Exit");
    }
    public void generateHeroMenu(){
        System.out.println("Choose your hero");
        System.out.println("1) Warrior");
        System.out.println("2) Mage");
        System.out.println("3) Thief");
        System.out.println("4) Healer");
    }
    public void generateFightMenu(){
        System.out.println("1) Attack");
        System.out.println("2) Defend");
    }
}

class Game{
    private GameInstance g;
    private User currentUser;
    private int pathTraversed;
    Game(){
        g = new GameInstance();
    }
    public void startGame(){
        Scanner s = new Scanner(System.in);
        g.generateMainMenu();
        int opt = s.nextInt();
        while(opt != 3){
            while(opt > 3 || opt < 1){
                System.out.println("Invalid option, try again");
                g.generateMainMenu();
                opt = s.nextInt();
            }
            if(opt == 1){
                System.out.println("Enter username");
                String name = s.next();
                g.generateHeroMenu();
                int heroType = s.nextInt();
                while(heroType < 1 || heroType > 4){
                    System.out.println("Invalid option, try again");
                    g.generateHeroMenu();
                    heroType = s.nextInt();
                }
                Hero currentHero = g.generateHero(heroType);
                while(!(g.addNewUser(name, currentHero))){
                    System.out.println("Enter username");
                    name = s.next();
                    g.generateHeroMenu();
                    heroType = s.nextInt();
                    while(heroType < 1 || heroType > 4){
                        System.out.println("Invalid option, try again");
                        g.generateHeroMenu();
                        heroType = s.nextInt();
                    }
                }
                g.generateMainMenu();
                opt = s.nextInt();
            }
            if(opt == 2){
                System.out.println("Enter username");
                String login = s.next();                
                while(g.loginUser(login) == null){
                    System.out.println("Enter username");
                    login = s.next();
                }
                currentUser = g.loginUser(login);
                System.out.println("Welcome to Archlegends, " + currentUser.getName());
                int monsterLevelCurrent = 0;                   
                while(!currentUser.getHero().checkHealth()){                                     
                    if(currentUser.getLocation() == 0){
                        System.out.println("You are at the starting location. Choose path");
                        Pair<ArrayList<Integer>, Integer> availableLocations = g.getRandomLocations(pathTraversed, currentUser.getLocation());
                        int k = 1;
                        for(Integer i : availableLocations.getKey()){
                            System.out.println(k + ") Go to location " + i);
                            k++;
                        }
                        System.out.println("Enter -1 to exit");
                        int locationChosen = s.nextInt();
                        if(locationChosen == -1){
                            currentUser.setPathTraversed(pathTraversed);
                            break;
                        }
                        while(locationChosen < -1 || locationChosen > 3){
                            System.out.println("Invalid input. Choose again");
                            k = 1;
                            for(Integer i : availableLocations.getKey()){
                                System.out.println(k + ") Go to location " + i);
                                k++;
                            }
                            System.out.println("Enter -1 to exit");
                            locationChosen = s.nextInt();
                            if(locationChosen == -1){
                                currentUser.setPathTraversed(pathTraversed);
                                break;
                            }
                        }
                        monsterLevelCurrent = availableLocations.getValue();
                        System.out.println("You are at location " + availableLocations.getKey().get(locationChosen - 1));
                        currentUser.setLocation(availableLocations.getKey().get(locationChosen - 1));
                        System.out.println("You are battling a level " + availableLocations.getValue() + " monster");
                        Battle currentBattle = new Battle(currentUser.getHero(), g.generateMonster(availableLocations.getValue()), g, s);
                        int battleResult = currentBattle.startBattle();
                        if(battleResult == -1){
                            break;
                        }
                        else{
                            pathTraversed++;
                            currentUser.getHero().restoreHealth();
                            continue;
                        }
                    }
                    else{
                        System.out.println("You are at location " + currentUser.getLocation() + ". Choose path");
                        Pair<ArrayList<Integer>, Integer> availableLocations = g.getRandomLocations(pathTraversed, currentUser.getLocation());
                        int k = 1;
                        for(Integer i : availableLocations.getKey()){
                            System.out.println(k + ") Go to location " + i);
                            k++;
                        }
                        System.out.println("4) Go back");
                        System.out.println("Enter -1 to exit");
                        int locationChosen = s.nextInt();
                        if(locationChosen == -1){
                            currentUser.setPathTraversed(pathTraversed);
                            break;
                        }
                        while(locationChosen < -1 || locationChosen > 4){
                            System.out.println("Invalid input. Choose again");
                            k = 1;
                            for(Integer i : availableLocations.getKey()){
                                System.out.println(k + ") Go to location " + i);
                                k++;
                            }
                            System.out.println("4) Go back");
                            System.out.println("Enter -1 to exit");
                            locationChosen = s.nextInt();
                            if(locationChosen == -1){
                                currentUser.setPathTraversed(pathTraversed);
                                break;
                            }
                        }
                        if(locationChosen == 4){
                            pathTraversed--;
                            System.out.println("You are at location " + currentUser.getLocation());
                            System.out.println("You are battling a level " + monsterLevelCurrent + " monster");
                            Battle currentBattle = new Battle(currentUser.getHero(), g.generateMonster(monsterLevelCurrent), g, s);
                            int battleResult = currentBattle.startBattle();
                            if(battleResult == -1){
                                break;
                            }
                            else{
                                pathTraversed++;
                                currentUser.getHero().restoreHealth();
                                continue;
                            }
                        }
                        else{
                            System.out.println("You are at location " + availableLocations.getKey().get(locationChosen - 1));
                            currentUser.setLocation(availableLocations.getKey().get(locationChosen - 1));
                            System.out.println("You are battling a level " + availableLocations.getValue() + " monster");
                            Battle currentBattle = new Battle(currentUser.getHero(), g.generateMonster(availableLocations.getValue()), g, s);
                            int battleResult = currentBattle.startBattle();
                            if(battleResult == -1){
                                break;
                            }
                            else{
                                pathTraversed++;
                                currentUser.getHero().restoreHealth();
                                continue;
                            }
                        }
                    }
                }
                g.generateMainMenu();
                opt = s.nextInt();
            }   
        }
    }
}

public class ArchLegends{
    public static void main(String[] args) {
        Game g = new Game();
        g.startGame();
    }
}