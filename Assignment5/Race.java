package Assignment5;

import java.util.*;

abstract class ObstacleEncounteredException extends Exception{
    ObstacleEncounteredException(String message){
        super(message);
    }
}

abstract class BiterException extends ObstacleEncounteredException{
    BiterException(String message){
        super(message);
    }
}

class TrampolineException extends ObstacleEncounteredException{
    TrampolineException(int advance){
        super("PingPong! I am a trampoline! You advance by " + advance + " tiles");
    }
}

class WhiteTileException extends ObstacleEncounteredException{
    WhiteTileException(){
        super("This is a white tile! Nothing happens!");
    }
}

class SnakeBiteException extends BiterException{
    SnakeBiteException(int damage){
        super("Hiss! I am a snake! You go back " + damage + " tiles");
    }
}

class CricketBiteException extends BiterException{
    CricketBiteException(int damage){
        super("Chirp! I am a cricket! You go back " + damage + " tiles");
    }
}

class VultureBiteException extends BiterException{
    VultureBiteException(int damage){
        super("Yapping! I am a vulture! You go back " + damage + " tiles!");
    }
}

class GameWinnerException extends Exception{
    GameWinnerException(User u){
        super("Josh wins the game in " + u.getRolls() + " rolls");
    }
}

class NoSixAtStartException extends Exception{
    NoSixAtStartException(String message){
        super(message);
    }
}

class CantMoveFurtherException extends Exception{
    CantMoveFurtherException(){
    }
}

abstract class Tile{
    protected final int type; // 1 - White tile, 2 - Trampoline, 3 - Snake, 4 - Cricket, 5 - Vulture
    protected final int damage;
    protected final int advance;
    Tile(int type, int damage, int advance){
        this.type = type;
        this.damage = damage;
        this.advance = advance;
    }
    public int getType(){
        return this.type;
    }
    public int getDamage(){
        return this.damage;
    }
    public int getAdvance(){
        return this.advance;
    }
    abstract public void shakeAction(User u) throws ObstacleEncounteredException;
}

abstract class Enemy extends Tile{
    Enemy(int type, int damage){
        super(type, damage, 0);
    }
}

class Snake extends Enemy{
    Snake(int damage){
        super(3, damage);
    }
   public void shakeAction(User u) throws SnakeBiteException{
       u.increaseSnake();
       u.decreaseLoc(this.damage);
       throw new SnakeBiteException(this.damage);
   }
}

class Cricket extends Enemy{
    Cricket(int damage){
        super(4, damage);
    }
    @Override
    public void shakeAction(User u) throws CricketBiteException{
        u.increaseCricket();
        u.decreaseLoc(this.damage);
        throw new CricketBiteException(this.damage);
    }
}

class Vulture extends Enemy{
    Vulture(int damage){
        super(5, damage);
    }
    @Override
    public void shakeAction(User u) throws VultureBiteException{
        u.increaseVulture();
        u.decreaseLoc(this.damage);
        throw new VultureBiteException(this.damage);
    }
}

class Trampoline extends Tile{
    Trampoline(int advance){
        super(2, 0 , advance);
    }
    @Override
    public void shakeAction(User u) throws TrampolineException{
        u.increaseTramp();
        u.increaseLoc(this.advance);
        throw new TrampolineException(this.advance);
    }
}

class White extends Tile{
    White(){
        super(1, 0 , 0);
    }
    @Override
    public void shakeAction(User u) throws WhiteTileException{
        u.stay();
        throw new WhiteTileException();
    }
}

class User{
    private final String name;
    private final int totalTiles;
    private boolean hasExitedStart;
    private boolean isWinner;
    private int rolls;
    private int currentLocation;
    private int snakeBites;
    private int cricketBites;
    private int vultureBites;
    private int trampJumps;
    User(String name, int totalTiles){
        this.name = name;
        this.totalTiles = totalTiles;
        this.hasExitedStart = false;
        this.isWinner = false;
        this.rolls = 0;
        this.currentLocation = 1;
        this.snakeBites = 0;
        this.cricketBites = 0;
        this.vultureBites = 0;
        this.trampJumps = 0;
    }
    public void increaseLoc(int advance){
        if(this.currentLocation + advance >= this.totalTiles){
            this.currentLocation = this.totalTiles;
            this.isWinner = true;
            return;
        }
        else{
            this.currentLocation = this.currentLocation + advance;
            if(this.currentLocation == this.totalTiles){
                this.isWinner = true;
            }
        }
    }
    public void rollAdvance(int roll){
        if(this.currentLocation + roll > this.totalTiles){
            return;
        }
        else{
            this.currentLocation = this.currentLocation + roll;
            if(this.currentLocation == this.totalTiles){
                this.isWinner = true;
            }
        }
    } 
    public void decreaseLoc(int damage){
        if(this.currentLocation - damage < 1){
            this.hasExitedStart = false;
            this.currentLocation = 1;
            System.out.println(">>      " + this.name + " moved to Tile-1 as can't go back further");
            return;
        }
        else{
            this.currentLocation = this.currentLocation - damage;
        }
    }
    public void increaseSnake(){
        this.snakeBites++;
    }
    public void increaseCricket(){
        this.cricketBites++;
    }
    public void increaseVulture(){
        this.vultureBites++;
    }
    public void increaseTramp(){
        this.trampJumps++;
    }
    public void stay(){
        return;
    }
    public boolean hasExitedStart(){
        return this.hasExitedStart;
    }
    public int getCurrentLoc(){
        return this.currentLocation;
    }
    public String getUsername(){
        return this.name;
    }
    public void toggleExit(){
        this.hasExitedStart = !(this.hasExitedStart);
    }
    public int getRolls(){
        return this.rolls;
    }
    public void increaseRolls(){
        this.rolls++;
    }
    public int snakeBites(){
        return this.snakeBites;
    }
    public int cricketBites(){
        return this.cricketBites;
    }
    public int vultureBites(){
        return this.vultureBites;
    }
    public int trampJumps(){
        return this.trampJumps;
    }
    public boolean isWinner(){
        return this.isWinner;
    }
}

class Game{
    private final User currentPlayer;
    private final int totalTiles;
    private int totalSnakes;
    private int totalCrickets;
    private int totalVultures;
    private int totalTramps;
    private final int snakeDamage;
    private final int cricketDamage;
    private final int vultureDamage;
    private final int trampAdv;
    private HashMap<Integer, Tile> obstacleTiles;
    private HashMap<Integer, Tile> allTiles;
    Game(int totalTiles){
        Random r = new Random();
        this.snakeDamage = r.nextInt(10) + 1;
        this.cricketDamage = r.nextInt(10) + 1;
        this.vultureDamage = r.nextInt(10) + 1;
        this.trampAdv = r.nextInt(10) + 1;
        this.totalTiles = totalTiles;
        this.totalSnakes = 0;
        this.totalCrickets = 0;
        this.totalVultures = 0;
        this.totalTramps = 0;
        this.obstacleTiles = new HashMap<>();
        this.allTiles = new HashMap<>();
        generateObstacles();
        System.out.println(">> Danger! There are " + this.totalSnakes + ", " + this.totalCrickets + ", " + this.totalVultures + " number of Snakes, Crickets and Vultures respectively on the whole track!");
        System.out.println(">> Each snake, cricket, vulture sets you back by " + this.snakeDamage + ", " + this.cricketDamage + ", " + this.vultureDamage + " tiles respectively");
        System.out.println(">> Good news : There are " + this.totalTramps + " trampolines along your way!");
        System.out.println(">> Good news : Each trampoline helps you advance by " + trampAdv + " tiles");
        String name = null;
        boolean correctName = false;
        while(!correctName){            
            try{
                Scanner s = new Scanner(System.in);
                System.out.println(">> Enter player name");   
                if(s.hasNextLine()){
                    name = s.next();
                    correctName = true;
                    s.close();
                } 
            }
            catch(InputMismatchException i){
                System.out.println("Enter a string only");
                correctName = false;
            }
        }
        this.currentPlayer = new User(name, totalTiles);
    }
    private Tile returnObst(int type){
        Tile reqObst = null;
        if(type == 1){
            reqObst = new White();
        }
        if(type == 2){
            reqObst = new Trampoline(this.trampAdv);
        }
        if(type == 3){
            reqObst = new Snake(this.snakeDamage);
        }
        if(type == 4){
            reqObst = new Cricket(this.cricketDamage);
        }
        if(type == 5){
            reqObst = new Vulture(this.vultureDamage);
        }
        return reqObst;
    }
    private void generateObstacles(){
        Random r = new Random();
        int reqObstacles = (int)Math.floor(totalTiles/5);
        int currentObst = 0;
        while(currentObst < reqObstacles){
            int location = r.nextInt(totalTiles - 1) + 1;
            while(obstacleTiles.keySet().contains(location)){
                location = r.nextInt(totalTiles - 1) + 1;
            }
            int obstacle = r.nextInt(4) + 2;
            Tile putObst = returnObst(obstacle);
            obstacleTiles.put(location, putObst);
            if(obstacle == 2){              
                this.totalTramps++;
            }
            else if(obstacle == 3){
                this.totalSnakes++;
            }
            else if(obstacle == 4){
                this.totalCrickets++;
            }
            else{
                this.totalVultures++;
            }            
            currentObst++;
        }
        int locIndex = 2;
        while(locIndex < totalTiles){
            if(obstacleTiles.keySet().contains(locIndex)){
                allTiles.put(locIndex, obstacleTiles.get(locIndex));
            }
            else{
                allTiles.put(locIndex, returnObst(1));
            }
            locIndex++;
        }       
    }
    private void roll(User currentUser) throws CantMoveFurtherException, GameWinnerException, NoSixAtStartException {
        Random dice = new Random();
        int diceRoll = dice.nextInt(6) + 1;
        currentUser.increaseRolls();
        System.out.print("[Roll - " + currentUser.getRolls() + "] " + currentUser.getUsername() + " rolled " + diceRoll + " at Tile-" + currentUser.getCurrentLoc() + ".");
        if(currentUser.getCurrentLoc() == 1 && !currentUser.hasExitedStart()){
            if(diceRoll == 6){
                System.out.print("You are out of the cage! You get a free roll!");
                currentUser.toggleExit();
                System.out.println();
                diceRoll = dice.nextInt(6) + 1;
                currentUser.increaseRolls();
                System.out.print("[Roll - " + currentUser.getRolls() + "] " + currentUser.getUsername() + " rolled " + diceRoll + " at Tile-" + currentUser.getCurrentLoc() + "." );
                currentUser.rollAdvance(diceRoll);
            }
            else{
                throw new NoSixAtStartException("Oops you need 6 to start");
            }
        }
        else{
            int initialLoc = currentUser.getCurrentLoc();
            currentUser.rollAdvance(diceRoll);
            if(!currentUser.isWinner()){                        
                if(currentUser.getCurrentLoc() == initialLoc){
                    System.out.print(" Landed at Tile-" + currentUser.getCurrentLoc() + '\n');
                    throw new CantMoveFurtherException();
                }
            }
            else{
                System.out.print(" Landed at Tile-" + currentUser.getCurrentLoc());
                System.out.print('\n');
                throw new GameWinnerException(currentUser);
            }
        }
        System.out.print(" Landed at Tile-" + currentUser.getCurrentLoc());
        System.out.print('\n');
        System.out.println(">>      Trying to shake Tile-" + currentUser.getCurrentLoc());
        Tile currTile = this.allTiles.get(currentUser.getCurrentLoc());
        try{
            currTile.shakeAction(currentUser);
        }
        catch(ObstacleEncounteredException o){
            System.out.println(">>      " + o.getMessage());
        }
        finally{
            System.out.println(">>      " + currentPlayer.getUsername() + " moved to Tile-" + currentPlayer.getCurrentLoc()); 
        }
    }
    public void startGame(){
        System.out.println(">> Starting with " + this.currentPlayer.getUsername() + " at Tile-" + this.currentPlayer.getCurrentLoc());
        System.out.println(">> Control transferred to computer for rolling dice for " + this.currentPlayer.getUsername());
        System.out.println(">> Game started!");
        while(!(this.currentPlayer.isWinner())){
            try{
                roll(this.currentPlayer);
            }
            catch(CantMoveFurtherException c){

            }
            catch(NoSixAtStartException n){
                System.out.print(n.getMessage());
                System.out.println();
            }
            catch(GameWinnerException gw){
                System.out.println(">>      " + gw.getMessage());
                System.out.println(">>      Total snake bites = " + currentPlayer.snakeBites());
                System.out.println(">>      Total cricket bites = " + currentPlayer.cricketBites());
                System.out.println(">>      Total vulture bites = " + currentPlayer.vultureBites());
                System.out.println(">>      Total trampoline jumps = " + currentPlayer.trampJumps());
            }
        }
    }
}

public class Race{
    public static void main(String[] args) {
        boolean inp_correct = false;
        int numTiles = 0;
		while(!inp_correct) {
            System.out.println(">> Enter the number of race tiles on the track(length)");
			try{			
                Scanner s = new Scanner(System.in);	
				numTiles = s.nextInt();
				if(numTiles < 50) {
					inp_correct = false;
					System.out.println("Enter a number greater than 50");
                }
                else{
                    inp_correct = true;
                }
            }
            catch (InputMismatchException e){
                System.out.println("Enter an Integer please");
                inp_correct = false;
			}
		}
        System.out.println(">> Setting Up Track");
        Game g = new Game(numTiles);
        g.startGame();

    }
}