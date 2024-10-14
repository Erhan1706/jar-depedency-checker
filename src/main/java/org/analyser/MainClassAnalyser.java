package org.analyser;

import org.bytecode.parser.BytecodeParser;
import org.bytecode.parser.ClassFile;
import org.bytecode.parser.ConstantPoolObject;
import org.bytecode.parser.ConstantPoolTags;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MainClassAnalyser {

    /**
     * Gets the file system path to the compiled class file for the specified class name.
     * @param className the class name
     * @return the {@code Path} to the .class file, or {@code null} if the class file is not found
     * @throws ClassNotFoundException  if the .class file cannot be located
     * @throws IOException if the url scheme is not supported
     * @throws URISyntaxException if the class file's URI is invalid
     */
    public static Path getClassPath(String className) throws ClassNotFoundException, IOException, URISyntaxException {
        Class<?> clazz = Class.forName(className);
        String resourceName = clazz.getSimpleName() + ".class";
        URL resourceUrl = clazz.getResource(resourceName);
        if (resourceUrl == null) {
            return null;
        }

        URI uri = resourceUrl.toURI();
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            // Class file is in the regular file system
            return Paths.get(uri);
        } else if (scheme.equals("jar")) {
            // Class file is inside a JAR
            try {
                // Try to get existing filesystem for the JAR
                FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                // If filesystem doesn't exist, create new one
                Map<String, String> env = new HashMap<>();
                env.put("create", "true");
                FileSystems.newFileSystem(uri, env);
            }
            return Paths.get(uri);
        } else {
            throw new IOException("Unsupported resource URL scheme: " + scheme);
        }
    }

    /**
     * Iterates through all the constant pool entries, finds all class references and returns them.
     * @param classFile the {@code ClassFile} object to extract dependencies from
     * @return a set of strings representing all the .class files the current classFile depends on
     */
    public static Set<String> getBytecodeClassDependencies(ClassFile classFile) {
        Set<String> dependencies = new HashSet<>();
        for (ConstantPoolObject curObj : classFile.constantPool) {
            if (curObj != null && curObj.tag == ConstantPoolTags.CONSTANT_Class) {
                ConstantPoolObject nameRef = classFile.constantPool.get(curObj.nameIndex - 1);
                dependencies.add(new String(nameRef.bytes, StandardCharsets.UTF_8));
            }
        }
        return dependencies;
    }

    /**
     * Looks for the class path of the specified class and retrieves all its dependencies by parsing its bytecode.
     *
     * @param curPath path of the class to analyze
     * @return a set of strings representing all the .class files the current classFile depends on
     * @throws IOException if an I/O error occurs while reading class files
     * @throws URISyntaxException if the class file's URI is invalid
     * @throws ClassNotFoundException if the class cannot be located
     */
    public static Set<String> getClassDependencies(String curPath) throws IOException, URISyntaxException, ClassNotFoundException {
        Path filePath = getClassPath(curPath);
        if (filePath == null) {
            return null;
        }
        byte[] classData = Files.readAllBytes(filePath);
        BytecodeParser parser = new BytecodeParser(classData);
        // Parse the bytecode of the current file
        ClassFile classFile = parser.parseBytecode();
        // Get all the dependencies from the bytecode constant pool
        Set<String> curDependencies = getBytecodeClassDependencies(classFile);
        // Filter out core packages like java/lang/object or java/io/PrintStream
        curDependencies = curDependencies.stream().filter(x -> !x.startsWith("java/")).collect(Collectors.toSet());
        return curDependencies;
    }

    /**
     * Recursively collects all class dependencies for a given main class, following its references to other classes.
     * The method uses breadth-first search to find all dependencies.
     *
     * @param mainClass the initial main class passed as input to the program
     * @return all the .class files the main class depends on
     * @throws IOException if an I/O error occurs while reading class files
     * @throws URISyntaxException if the class file's URI is invalid
     * @throws ClassNotFoundException if the class cannot be located
     */
    public static Set<String> getMainDependencies(String mainClass) throws IOException, URISyntaxException, ClassNotFoundException {
        Set<String> dependencies = new HashSet<>();
        Queue<String> toCheck = new LinkedList<>(); // holds all classes that still need to be checked for dependencies
        toCheck.add(mainClass);
        Set<String> checked = new HashSet<>(); // holds all classes that have already been checked
        // BFS
        while (!toCheck.isEmpty()) {
            String current = toCheck.poll();
            if (checked.contains(current)) continue;
            checked.add(current);
            Set<String> curDependencies = getClassDependencies(current);
            if (curDependencies == null) {
                // If can't find classpath then remove from list of dependencies (mostly for array class references like [C)
                dependencies.remove(current.replace('.','/'));
                continue;
            }
            dependencies.addAll(curDependencies);
            for (String dependency: curDependencies) {
                String dependencyPackage = dependency.replace('/','.');
                if (!checked.contains(dependencyPackage))  toCheck.add(dependencyPackage);
            }
        }
        return dependencies;
    }
}
