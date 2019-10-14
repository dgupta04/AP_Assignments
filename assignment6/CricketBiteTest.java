package assignment6;

import org.junit.*;
import assignment6.*;
import java.io.*;

public class CricketBiteTest {
    @Test(expected = CricketBiteException.class)
    public void testException() throws CricketBiteException{
        System.out.println("Cricket Bite Testing");
        Cricket c = new Cricket(1);
        User u = new User("John", 100, 10);
        c.shakeAction(u);
    }
    @After
    public void afterTest(){
        System.out.println("Cricket Bite done");
    }
}