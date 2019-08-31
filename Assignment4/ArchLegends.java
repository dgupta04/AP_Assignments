package Assignment4;

// import java.math.BigDecimal;
import java.util.*;
import javafx.util.Pair;

class CompareSidekick implements Comparator<Sidekick>{
    @Override
    public int compare(Sidekick s1, Sidekick s2){
        return s1.getXP() - s2.getXP();
    }
}

class Character implements Cloneable{
    protected int currentLocation;
    protected float HP;
    protected int maxHP;
    protected int currentLevel;  
    Character(int maxHP, int currentLevel){
        this.maxHP = maxHP;
        this.HP = maxHP;
        this.currentLevel = currentLevel;
    }
    public float returnHP(){
        return this.HP;
    }
    public int returnMaxHP(){
        return this.maxHP;
    }
    public int getLevel(){
        return this.currentLevel;
    }    
    public Character clone(){
        try{
            Character retChar = (Character)super.clone();
            retChar.currentLocation = this.currentLocation;
            retChar.maxHP = this.maxHP;
            retChar.HP = this.HP;
            retChar.currentLevel = this.currentLevel;
            return retChar;
        }
        catch(CloneNotSupportedException e){
            return null;
        }        
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
    public float attack(Hero h){
        Random gen = new Random();
        float mean = this.HP/4.0f;
        float variance = mean;
        float damage = (float)Math.abs((int)Math.round((double)mean/2 + ((double)variance/2)*gen.nextGaussian()));
        return damage;
    }
    public void decreaseHP(float damage){
        if(Float.compare(damage , this.HP) > 0){
            this.HP = 0.0f;
            return;
        }
        this.HP = this.HP - damage;
    }
}

abstract class Hero extends Character{
    protected final int type; // 1 - Warrior , 2 - Mage , 3 - Thief , 4 - Healer
    protected final String typeString;   
    protected float XP;  
    protected int attackAttribute;
    protected int defenseAttribute;
    protected int totalMoves;
    protected int numSuperpowers;
    protected int numMovesAfterSuperpower;
    protected boolean hasSuperpower;
    protected boolean powerActivated;
    protected boolean hasSidekick;
    protected ArrayList<Sidekick> allSidekicks;
    protected ArrayList<Minion> clonedMinions;
    protected Sidekick inUse;
    protected boolean usingSidekick;
    Hero(int type, String typeString, int HP, int attackAttribute, int currentLevel){
        super(HP, currentLevel);
        this.type = type;
        this.XP = 0.0f;
        this.attackAttribute = attackAttribute;
        this.typeString = typeString;
        this.totalMoves = 0;
        this.numSuperpowers = 0;
        this.numMovesAfterSuperpower = 0;
        this.powerActivated = false;
        this.hasSidekick = false;
        this.allSidekicks = new ArrayList<>();
        this.inUse = null;
        this.usingSidekick = false;
        this.clonedMinions = null;
    }
    protected void checkSidekick(){
        if(allSidekicks.size() == 0){
            this.hasSidekick = false;
        }
    }
    public boolean isUsingSidekick(){
        return this.usingSidekick;
    }
    private void sortSidekick(){
        Comparator<Sidekick> comparer = new CompareSidekick();
        Comparator<Sidekick> mainComparer = comparer.reversed();
        Collections.sort(allSidekicks, mainComparer);
    }
    public void addSidekick(Sidekick toAdd){
        allSidekicks.add(toAdd);
        sortSidekick();
    }
    public void setLocation(int location){
        this.currentLocation = location;
    }
    public float returnHP(){
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
    public boolean hasSidekick(){
        return this.hasSidekick;
    }   
    public void useSidekick(Monster m, Scanner s){
        if(allSidekicks.size() == 0){
            System.out.println("No sidekicks available. All dead.");
            return;
        }
        else{
            Sidekick current = allSidekicks.get(0);
            System.out.println("You have a sidekick " + current.getTypeString() + ". Attack of sidekick is " + Float.toString(current.getAttack()));               
            if(current.canClone()){
                System.out.println("Enter c to use cloning, press f to fight without cloning. Enter correct input!");
                String clone = s.next();
                if(clone.equals("c")){
                    current.toggleClone();
                    current.toggleUsing();
                    System.out.println("Cloning done");
                }
            }
            if(m.getLevel() == 2 && current.getTypeString().equals("Knight")){
                current.setDefense(5);
            }
            this.inUse = current;
            this.usingSidekick = true;            
        }
    }
    protected void standardAttack(Monster m, int attackTotal){
        System.out.println("You attacked and inflicted " + attackTotal + " damage to monster");
        m.decreaseHP(attackTotal);
        if(this.usingSidekick){
            if(inUse.getTypeString().equals("Minion")){
                if(inUse.usingClone()){
                    if(this.clonedMinions == null){
                        this.clonedMinions = new ArrayList<>();
                        this.clonedMinions.add((Minion)inUse);
                        for(int i=0 ; i<3; i++){
                            Minion tobeCloned = (Minion)inUse;
                            Minion cloned = tobeCloned.clone();
                            this.clonedMinions.add(cloned);
                        }                        
                    }
                    for(Minion minion: clonedMinions){
                        System.out.println("Sidekick attacked and inflicted " + minion.getAttack() + " damage to the monster");
                        m.decreaseHP(minion.getAttack());
                        System.out.println("Sidekick's HP : " + minion.returnHP() + "/" + minion.returnMaxHP());
                    }
                }
                else{
                    System.out.println("Sidekick attacked and inflicted " + inUse.getAttack() + " damage to the monster");
                    m.decreaseHP(inUse.getAttack()); 
                }
            }
            else{
                System.out.println("Sidekick attacked and inflicted " + inUse.getAttack() + " damage to the monster");
                m.decreaseHP(inUse.getAttack()); 
            }
        }
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        System.out.println("Monster attack!");
        float damageByMonster = m.attack(this);
        if(Float.compare(damageByMonster, this.HP)>0){
            damageByMonster = this.HP;
        }
        System.out.println("The monster attacked and inflicted " + damageByMonster + " to you");
        this.HP = this.HP - damageByMonster;        
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP  + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        if(this.usingSidekick){
            if(!(inUse.usingClone())){
                inUse.decreaseHP(1.5f*damageByMonster);
                if(inUse.returnHP() < 0.5f){
                    System.out.println("Sidekick died. Not available anymore.");
                    allSidekicks.remove(0);
                    inUse = null;
                    this.usingSidekick = false;
                }
                else{
                    System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                }
            }
            else{
                boolean allDead = false;
                for(Minion minion: clonedMinions){
                    minion.decreaseHP(1.5f*damageByMonster);
                    if(minion.returnHP() < 0.5f){
                        allDead = true;
                    }
                    else{
                        System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                    }
                }
                if(allDead){
                    System.out.println("Sidekick died. Clones also dead.");
                    allSidekicks.remove(0);
                    this.clonedMinions = null;
                    inUse = null;
                    this.usingSidekick = false;
                }
            }
        }
        checkSidekick();
        this.totalMoves++;
    }
    protected void standardDefense(Monster m, int defenseTotal){
        float damageByMonster = m.attack(this);
        float reducedDamage;
        if(this.usingSidekick){
            if(inUse.getTypeString().equals("Knight")){
                System.out.println("Sidekick reduced damage by " + inUse.returnDefense());
                reducedDamage = damageByMonster - (float)defenseTotal - inUse.returnDefense();
            }
            else{
                reducedDamage = damageByMonster - (float)defenseTotal;    
            }
        }
        else{
            reducedDamage = damageByMonster - (float)defenseTotal;    
        }     
        if(Float.compare(reducedDamage, 0) < 0){
            reducedDamage = 0.0f;
        }
        if(Float.compare(reducedDamage, this.HP) > 0){
            this.HP = 0;
            System.out.println("Monster attack!");
            System.out.println("The monster attacked and inflicted " + reducedDamage + " to you");
            System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
            if(this.usingSidekick){
                if(inUse.usingClone()){
                    boolean allDead = false;
                    for(Minion minion: clonedMinions){
                        minion.decreaseHP(1.5f*reducedDamage);
                        if(minion.returnHP() < 0.5f){
                            allDead = true;
                        }
                        else{
                            System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                        }
                    }
                    if(allDead){
                        System.out.println("Sidekick died. Clones also dead.");
                        allSidekicks.remove(0);
                        this.clonedMinions = null;
                        inUse = null;
                        this.usingSidekick = false;
                    }
                }
                else{
                    inUse.decreaseHP(1.5f*damageByMonster);
                    if(inUse.returnHP() < 0.5f){
                        System.out.println("Sidekick died. Not available anymore.");
                        allSidekicks.remove(0);
                        inUse = null;
                        this.usingSidekick = false;
                    }
                    else{
                        System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                    }
                }
            }        
        }
        else{
            System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());        
            System.out.println("Monster attack!");
            System.out.println("The monster attacked and inflicted " + reducedDamage + " to you");
            this.HP = this.HP - reducedDamage;
            System.out.println("Your HP: " + this.HP + "/" + this.maxHP + "Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
            if(this.usingSidekick){
                if(inUse.usingClone()){
                    boolean allDead = false;
                    for(Minion minion: clonedMinions){
                        minion.decreaseHP(1.5f*reducedDamage);
                        if(minion.returnHP() < 0.5f){
                            allDead = true;
                        }
                        else{
                            System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                        }
                    }
                    if(allDead){
                        System.out.println("Sidekick died. Clones also dead.");
                        allSidekicks.remove(0);
                        this.clonedMinions = null;
                        inUse = null;
                        this.usingSidekick = false;
                    }
                }
                else{
                    inUse.decreaseHP(1.5f*damageByMonster);
                    if(inUse.returnHP() < 0.5f){
                        System.out.println("Sidekick died. Not available anymore.");
                        allSidekicks.remove(0);
                        inUse = null;
                        this.usingSidekick = false;
                    }
                    else{
                        System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                    }
                }        
            }
        }
        checkSidekick();
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
            return 1;
        }
        else if(Float.compare(this.HP, 0.5f) < 0){
            System.out.println("You lost to the monster! Restart game!");
            return -1;
        }
        return 0;
    }
    private boolean successfulUpgradeOrAddition(Scanner s){
        System.out.println("Do you want a sidekick? Type yes to buy or no to check for level upgrade. Please enter correct input");
        System.out.println("Current XP is " + this.XP);
        String wantSidekick = s.next();
        wantSidekick = wantSidekick.toLowerCase();
        if(wantSidekick.equals("yes")){
            System.out.println("Choose sidekick:");
            System.out.println("1) Minion");
            System.out.println("2) Knight");
            int chosenSidekick = s.nextInt();
            while(chosenSidekick < 1 || chosenSidekick > 2){
                System.out.println("For god's sake enter the correct input");
                System.out.println("Choose sidekick:");
                System.out.println("1) Minion");
                System.out.println("2) Knight");
                chosenSidekick = s.nextInt();
            }
            System.out.println("XP willing to spend (please enter a float value): ");
            float willingXP = s.nextFloat();
            if(Float.compare((float)this.XP, willingXP) < 0){
                System.out.println("XP willing is greater than available. Aborting");
                return false;
            }
            else{
                if(chosenSidekick == 1){
                    while(Float.compare(willingXP, 5.0f) < 0){
                        System.out.println("Min XP required is 5.0");
                        System.out.println("XP willing to spend (please enter a float value): ");
                        willingXP = s.nextFloat();
                    }
                    Sidekick toAdd = new Minion(willingXP);
                    addSidekick(toAdd);
                    this.XP = this.XP - willingXP;
                    this.hasSidekick = true;
                    System.out.println("Bought sidekick : " + toAdd.getTypeString());
                    System.out.println("XP of sidekick: " + Float.toString((float)toAdd.getXP()));
                    System.out.println("Attack of sidekick is " + Float.toString((float)toAdd.getAttack()));
                    return true;
                }
                else{
                    while(Float.compare(willingXP, 8.0f) < 0){
                        System.out.println("Min XP required is 8.0");
                        System.out.println("XP willing to spend (please enter a float value): ");
                        willingXP = s.nextFloat();
                    }
                    Sidekick toAdd = new Knight(willingXP);
                    addSidekick(toAdd);
                    this.XP = this.XP - willingXP;
                    this.hasSidekick = true;
                    System.out.println("Bought sidekick : " + toAdd.getTypeString());
                    System.out.println("XP of sidekick: " + Float.toString((float)toAdd.getXP()));
                    System.out.println("Attack of sidekick is " + Float.toString((float)toAdd.getAttack()));
                    return true;
                }
            }           
        }
        else{
            if(this.XP >= 20.0f && this.XP < 60.0f){
                System.out.println("Promoted to level 2!");
                this.maxHP = 150;
            }
            else if(this.XP >= 60.0f && this.XP < 120.0f){
                System.out.println("Promoted to level 3!");
                this.maxHP = 200;
            }
            else if(this.XP >= 120.0f){
                System.out.println("Promoted to level 4!");
                this.maxHP = 250;
            }
            return true;
        }        
    }
    public void victoryAddtion(Monster m, Scanner s){
        System.out.println("Defeated monster!");
        System.out.println(m.getLevel()*20 + " XP awarded!");
        System.out.println("Fight won! Move to next location");
        resetAttributes();
        float XPGained = (float)(m.getLevel()*20);
        if(this.usingSidekick){
            inUse.upgradeParams(XPGained);
            if(inUse.usingClone()){
                clonedMinions = null;
            }
            this.usingSidekick = false;
            inUse = null;            
        }
        this.XP = this.XP + XPGained;
        boolean doneAddition = successfulUpgradeOrAddition(s);
        while(!doneAddition){
            doneAddition = successfulUpgradeOrAddition(s);
        }
    }
    public boolean checkHealth(){
        return (Float.compare(this.HP, 0.5f) < 0);
    }
    abstract public void attack(Monster m);
    abstract public void specialAttack(Monster m);
    abstract public void defense(Monster m);
    abstract protected void specialDefense(Monster m);
}

class Sidekick extends Character implements Cloneable{
    protected int type;
    protected String typeString;
    protected int XP;
    protected float minXPDonate;
    protected float attackAttribute;
    protected float initialAttack;
    protected boolean canClone;  
    protected boolean usingClone;
    protected int addDefense;
    Sidekick(int maxHP, int currentLevel, String typeString, int type, float minXPDonate){
        super(maxHP, currentLevel);
        this.HP = 100.0f;
        this.type = type;
        this.typeString = typeString;
        this.XP = 0;
        this.minXPDonate = minXPDonate;
    }
    public Sidekick clone(){
        Sidekick clone = (Sidekick)super.clone();
        clone.type = this.type;
        clone.typeString = this.typeString;
        clone.XP = this.XP;
        clone.attackAttribute = this.attackAttribute;
        return clone;
    }
    public String getTypeString(){
        return this.typeString;
    }
    public float getAttack(){
        return this.attackAttribute;
    }
    public float getMinXPDonate(){
        return this.minXPDonate;
    }
    public int getXP(){
        return this.XP;
    }
    public float returnDefense(){
        return this.addDefense;
    }
    public void upgradeParams(float XPGained){
        int addXP = (int)XPGained;
        this.XP = this.XP + (addXP/10);
        this.HP = 100.0f;
        this.attackAttribute = (this.initialAttack + (float)(addXP/5));
        if(this.usingClone){
            this.usingClone = false;
        }
    }
    public int getType(){
        return this.type;
    }
    public boolean usingClone(){
        return this.usingClone;
    }
    public void toggleUsing(){
        this.usingClone = !(this.usingClone);
    }
    public boolean canClone(){
        return this.canClone;
    }
    public void toggleClone(){
        this.canClone = !(this.canClone);
    }
    public void decreaseHP(float monsterDamage){
        if(Float.compare(monsterDamage, this.HP) > 0){
            this.HP = 0.0f;
            return;
        }
        this.HP = this.HP - monsterDamage;
    }
    public void setDefense(int addDefense){
        this.addDefense = addDefense;
    }
}

class Minion extends Sidekick implements Cloneable{      
    Minion(float XPDonated){
        super(100, 1, "Minion", 1, 5.0f);
        this.canClone = true;
        this.initialAttack = 0.5f*(XPDonated - minXPDonate) + 1.0f;
        this.attackAttribute = this.initialAttack;
        this.usingClone = false;
        this.addDefense = 0;
    }
    @Override
    public boolean equals(Object minionComp){
        if(minionComp != null && getClass() == minionComp.getClass()){
            Minion compareMin = (Minion)minionComp;
            return type == compareMin.type;
        }
        else{
            return false;
        }
    }   
    public Minion clone(){
        Minion cloned = (Minion) super.clone();
        cloned.canClone = false;
        cloned.initialAttack = this.initialAttack;
        cloned.attackAttribute = this.attackAttribute;
        cloned.usingClone = true;
        cloned.addDefense = 0;
        return cloned;
    }
}

class Knight extends Sidekick implements Cloneable{
    Knight(float XPDonated){
        super(100, 1, "Knight", 2, 2.0f);
        this.initialAttack = 0.5f*(XPDonated - minXPDonate) + 2.0f;
        this.attackAttribute = this.initialAttack;
        this.canClone = false;
        this.usingClone = false;
        this.addDefense = 0;
    }
    @Override
    public boolean equals(Object knightComp){
        if(knightComp != null && getClass() == knightComp.getClass()){
            Knight compareKni = (Knight)knightComp;
            return type == compareKni.type;
        }
        else{
            return false;
        }
    }
    public int getDefense(){
        return this.addDefense;
    }
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
        m.decreaseHP((float)stolenHPMonster);
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        if(this.usingSidekick){
            if(inUse.getTypeString().equals("Minion")){
                if(inUse.usingClone()){
                    if(this.clonedMinions == null){
                        this.clonedMinions = new ArrayList<>();
                        this.clonedMinions.add((Minion)inUse);
                        for(int i=0 ; i<3; i++){
                            Minion tobeCloned = (Minion)inUse;
                            Minion cloned = tobeCloned.clone();
                            this.clonedMinions.add(cloned);
                        }                        
                    }
                    for(Minion minion: clonedMinions){
                        System.out.println("Sidekick attacked and inflicted " + minion.getAttack() + " damage to the monster");
                        m.decreaseHP(minion.getAttack());
                        System.out.println("Sidekick's HP : " + minion.returnHP() + "/" + minion.returnMaxHP());
                    }
                }
                else{
                    System.out.println("Sidekick attacked and inflicted " + inUse.getAttack() + " damage to the monster");
                    m.decreaseHP(inUse.getAttack()); 
                }
            }
            else{
                System.out.println("Sidekick attacked and inflicted " + inUse.getAttack() + " damage to the monster");
                m.decreaseHP(inUse.getAttack()); 
            }
        }
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        System.out.println("Monster attack!");
        float damageByMonster = m.attack(this);
        if(Float.compare(damageByMonster, this.HP)>0){
            damageByMonster = this.HP;
        }
        System.out.println("The monster attacked and inflicted " + damageByMonster + " to you");
        this.HP = this.HP - damageByMonster;        
        System.out.println("Your HP: " + this.HP + "/" + this.maxHP  + ", Monster's HP: " + m.returnHP() + "/" + m.returnMaxHP());
        if(this.usingSidekick){
            if(!(inUse.usingClone())){
                inUse.decreaseHP(1.5f*damageByMonster);
                if(inUse.returnHP() < 0.5f){
                    System.out.println("Sidekick died. Not available anymore.");
                    allSidekicks.remove(0);
                    inUse = null;
                    this.usingSidekick = false;
                }
                else{
                    System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                }
            }
            else{
                boolean allDead = false;
                for(Minion minion: clonedMinions){
                    minion.decreaseHP(1.5f*damageByMonster);
                    if(minion.returnHP() < 0.5f){
                        allDead = true;
                    }
                    else{
                        System.out.println("Sidekick's HP: " + inUse.returnHP() + "/" + inUse.returnMaxHP());
                    }
                }
                if(allDead){
                    System.out.println("Sidekick died. Clones also dead.");
                    allSidekicks.remove(0);
                    this.clonedMinions = null;
                    inUse = null;
                    this.usingSidekick = false;
                }
            }
        }
        checkSidekick();
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
        m.decreaseHP((float)stolenHPMonster);
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
        float increasedHP = this.HP + (float)selfHPIncrease;
        if(Float.compare(increasedHP , this.maxHP) > 0){
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
        float increasedHP = this.HP + (float)selfHPIncrease;
        if(Float.compare(increasedHP,this.maxHP) > 0){
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
    public float attack(Hero h){
        Random prob = new Random();
        double attackProb = prob.nextDouble();        
        if(attackProb > 0.1){ 
            Random gen = new Random();
            float mean = this.HP/4;
            float variance = mean;
            int damage = (int)Math.floor((double)mean/2 + ((double)variance/2)*gen.nextGaussian());
            return (float)damage;
        }
        else{
            return (float)(h.returnHP()/2);
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
        if(goodGuy.hasSidekick() && !goodGuy.isUsingSidekick()){
            System.out.println("Type yes if you want to use sidekick. Else type no. Please please enter proper input");
            String useOrNot = s.next();
            useOrNot = useOrNot.toLowerCase();
            if(useOrNot.equals("yes")){
                goodGuy.useSidekick(badGuy, s);
            }
        }
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
                goodGuy.victoryAddtion(badGuy, s);
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