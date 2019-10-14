package assignment6;

import org.junit.*;
import assignment6.*;
import java.io.*;

public class TrampolineJumpTest {
    @Test(expected = TrampolineException.class)
    public void testException() throws TrampolineException{
        System.out.println("Testing Trampoline Jump");
        Trampoline t = new Trampoline(6);
        User u = new User("John", 100, 10);
        t.shakeAction(u);
    }
    @After
    public void afterTest(){
        System.out.println("Trampoline Test Successful");
    }
}