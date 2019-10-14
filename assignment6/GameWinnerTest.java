package assignment6;

import org.junit.*;
import assignment6.*;
import java.io.*;

public class GameWinnerTest {
    @Test(expected = GameWinnerException.class)
    public void testException() throws GameWinnerException{
        System.out.println("Testing Game Winner");
        Game g = new Game(100, 90);
        g.startGame();
    }
    @After
    public void afterTest(){
        System.out.println("Game Winner done");
    }
}