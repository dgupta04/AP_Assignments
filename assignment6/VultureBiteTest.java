package assignment6;

import org.junit.*;
import assignment6.*;
import java.io.*;

public class VultureBiteTest {
    @Test(expected = VultureBiteException.class)
    public void testException() throws VultureBiteException{
        System.out.println("Testing Vulture Bite");
        Vulture v = new Vulture(5);
        User u = new User("John", 100, 10);
        v.shakeAction(u);
    }
    @After
    public void afterTest(){
        System.out.println("Vulture Bite Test Successful");
    }
}