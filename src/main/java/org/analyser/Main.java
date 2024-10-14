package org.analyser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {
        // Remember to transform this to a command line input
        if (args.length < 2) {
            System.err.println("Usage: java Main <main-class> <jar-file1> [<jar-file2> ...]");
            return;
        }
        String mainClass = args[0];
        List<String> jarPaths = Arrays.asList(args).subList(1, args.length);

        // Step 1: Collect all available .class files from the provided JARs
        Set<String> jarDependencies = JarAnalyser.getJarDependencies(jarPaths);

        // Step 2: Collect all necessary .class files from the main class
        Set<String> mainDependencies = MainClassAnalyser.getMainDependencies(mainClass);

        // Step 3: Check if classpath of jars contain all necessary dependencies necessary for the main class to run
        for (String mainDependency : mainDependencies) {
            if (!jarDependencies.contains(mainDependency + ".class")) {
                System.out.println("False");
                System.out.println("Dependency: " + mainDependency + " not found");
                return;
            }
        }
        System.out.println("True");
    }
}