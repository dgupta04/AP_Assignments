package assignment6;

import org.junit.*;
import assignment6.*;
import java.io.*;

public class WhiteTileTest {
    @Test(expected = WhiteTileException.class)
    public void testException() throws WhiteTileException{
        System.out.println("Testing White Tile Exception");
        White w = new White();
        User u = new User("John", 100, 10);
        w.shakeAction(u);
    }
    @After
    public void afterTest(){
        System.out.println("Tested White Tile");
    }
}