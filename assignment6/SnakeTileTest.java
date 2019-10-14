package assignment6;

import org.junit.*;
import assignment6.*;
import java.io.*;

public class SnakeTileTest {
    @Test(expected = SnakeBiteException.class)
    public void testException() throws SnakeBiteException{
        System.out.println("Testing Snake Bite");
        Snake s = new Snake(2);
        User u = new User("John", 100, 10);
        s.shakeAction(u);
    }
    @After
    public void afterTest(){
        System.out.println("Snake Bite done");
    }
}