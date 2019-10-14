package assignment6;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import assignment6.*;
import java.io.*;

public class SerializeTest {
    private static Game toTest;
    @Before
    public void beforeTest(){
        System.out.println("Beginning Serialize Test");
        String[] args = {"hello"};
        Race.main(args);
        toTest = Race.getCurrentGame();
    }    
    @Test
    public void testException() throws ClassNotFoundException{
        ObjectInputStream readGame = null;
        try{
            readGame = new ObjectInputStream(new FileInputStream(toTest.getCurrentUser().getUsername() + ".txt"));
            Game testGame = (Game) readGame.readObject();
            assertEquals(testGame, toTest);
        }
        catch(IOException i){
            System.out.println(i.getMessage());
        }
    }
    @After
    public void afterTest(){
        System.out.println("Serialize test successful");
    }
}