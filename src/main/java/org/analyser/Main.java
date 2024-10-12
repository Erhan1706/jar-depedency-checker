package org.analyser;

import org.bytecode.parser.ClassFile;
import org.bytecode.parser.ConstantPoolObject;
import org.bytecode.parser.ConstantPoolTags;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
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

        // Step 1: Collect all available classes from JAR files
        Set<String> jarDependencies = JarAnalyser.getJarDependencies(jarPaths);

        // Step 2: Collect all necessary class files from the main class
        Set<String> mainDependencies = MainClassAnalyser.getMainDependencies(mainClass);


//        for (String dependency : dependencies) {
//            try {
//                String d = dependency.replace('/', '.');
//                Path dependencyPath = MainClassAnalyser.getClassPath(d);
//                System.out.println(dependency);
//            } catch (Exception e) {
//                continue;
//            }
//        }
    }
}