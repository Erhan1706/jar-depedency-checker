import org.analyser.Main;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTests {

    @Test
    void ClassBIncorrect() throws IOException, URISyntaxException, ClassNotFoundException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"com.jetbrains.internship2024.ClassB", "./build/libs/ModuleB-1.0.jar"};
        Main.main(args);
        assertTrue(outContent.toString().contains("False"));
    }

    @Test
    void ClassBCorrect() throws IOException, URISyntaxException, ClassNotFoundException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"com.jetbrains.internship2024.ClassB", "./build/libs/ModuleB-1.0.jar", "./build/libs/ModuleA-1.0.jar"};
        Main.main(args);
        assertTrue(outContent.toString().contains("True"));
    }

    @Test
    void ClassACorrect() throws IOException, URISyntaxException, ClassNotFoundException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"com.jetbrains.internship2024.ClassA", "./build/libs/ModuleA-1.0.jar"};
        Main.main(args);
        assertTrue(outContent.toString().contains("True"));
    }

    @Test
    void SomeAnotherClassIncorrect() throws IOException, URISyntaxException, ClassNotFoundException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"com.jetbrains.internship2024.SomeAnotherClass", "./build/libs/ModuleA-1.0.jar"};
        Main.main(args);
        assertTrue(outContent.toString().contains("False"));
    }

    @Test
    void SomeAnotherClassCorrect() throws IOException, URISyntaxException, ClassNotFoundException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"com.jetbrains.internship2024.SomeAnotherClass", "./build/libs/ModuleA-1.0.jar", "./build/libs/commons-io-2.16.1.jar"};
        Main.main(args);
        assertTrue(outContent.toString().contains("True"));
    }

    @Test
    void ClassB1Correct() throws IOException, URISyntaxException, ClassNotFoundException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"com.jetbrains.internship2024.ClassB1", "./build/libs/ModuleB-1.0.jar"};
        Main.main(args);
        assertTrue(outContent.toString().contains("True"));
    }

    @Test
    void ClassB1Incorrect() throws IOException, URISyntaxException, ClassNotFoundException {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] args = {"com.jetbrains.internship2024.ClassB1", "./build/libs/ModuleA-1.0.jar"};
        Main.main(args);
        assertTrue(outContent.toString().contains("False"));
    }
}


