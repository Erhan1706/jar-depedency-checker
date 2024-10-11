package org.analyser;

import org.bytecode.parser.BytecodeParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;


public class Main {

    public static Path getClassPath(String className) throws ClassNotFoundException, IOException, URISyntaxException {
        Class<?> clazz = Class.forName(className);
        String resourceName = clazz.getSimpleName() + ".class";
        URL resourceUrl = clazz.getResource(resourceName);

        if (resourceUrl == null) {
            throw new IOException("Could not find class file for " + className);
        }

        URI uri = resourceUrl.toURI();
        String scheme = uri.getScheme();

        // Look into what this code does later

        if ("file".equals(scheme)) {
            // Class file is in the regular file system
            return Paths.get(uri);
        } else if ("jar".equals(scheme)) {
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

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {
        // Remember to transform this to a command line input
        String className = "com.jetbrains.internship2024.ClassB";
        Path filePath = getClassPath(className);
        byte[] classData = Files.readAllBytes(filePath);
        BytecodeParser parser = new BytecodeParser(classData);
        parser.parseBytecode();
    }
}