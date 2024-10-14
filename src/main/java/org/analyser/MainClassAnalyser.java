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

    public static Path getClassPath(String className) throws ClassNotFoundException, IOException, URISyntaxException {
        Class<?> clazz = Class.forName(className);
        String resourceName = clazz.getSimpleName() + ".class";
        URL resourceUrl = clazz.getResource(resourceName);
        if (resourceUrl == null) {
            //throw new IOException("Could not find class file for " + className);
            System.out.println("Resource not found: " + resourceName);
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

    public static Set<String> temp(String curPath) throws IOException, URISyntaxException, ClassNotFoundException {
        Path filePath = getClassPath(curPath);
        if (filePath == null) {
            return null;
        }
        byte[] classData = Files.readAllBytes(filePath);
        BytecodeParser parser = new BytecodeParser(classData);
        ClassFile classFile = parser.parseBytecode();
        Set<String> curDependencies = getBytecodeClassDependencies(classFile);
        // Filter out core packages like java/lang/object or java/io/PrintStream
        curDependencies = curDependencies.stream().filter(x -> !x.startsWith("java/")).collect(Collectors.toSet());
        return curDependencies;
    }

    public static Set<String> getMainDependencies(String mainClass) throws IOException, URISyntaxException, ClassNotFoundException {
        Set<String> dependencies = new HashSet<>();
        Queue<String> toCheck = new LinkedList<>();
        toCheck.add(mainClass);
        Set<String> checked = new HashSet<>();

        while (!toCheck.isEmpty()) {
            String current = toCheck.poll();
            if (checked.contains(current)) continue;
            checked.add(current);
            Set<String> curDependencies = temp(current); // Rename this
            if (curDependencies == null) {
                dependencies.remove(current.replace('.','/'));
                continue;
            }
            dependencies.addAll(curDependencies);
            for (String dependency: curDependencies) {
                String dependencyPackage = dependency.replace('/','.');
                if (!checked.contains(dependencyPackage))  toCheck.add(dependencyPackage);
            }
        }
        System.out.println(dependencies);
        return dependencies;
    }
}
