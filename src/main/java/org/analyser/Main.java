package org.analyser;

import org.bytecode.parser.BytecodeParser;
import org.bytecode.parser.ClassFile;
import org.bytecode.parser.ConstantPoolObject;
import org.bytecode.parser.ConstantPoolTags;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


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

    public static List<String> getBytecodeClassDependencies(ClassFile classFile) {
        List<String> dependencies = new ArrayList<>();
        for (ConstantPoolObject curObj : classFile.constantPool) {
            if (curObj.tag == ConstantPoolTags.CONSTANT_Class) {
                ConstantPoolObject nameRef = classFile.constantPool.get(curObj.nameIndex - 1);
                dependencies.add(new String(nameRef.bytes, StandardCharsets.UTF_8));
            }
        }

        return dependencies;
    }

    public static HashSet<String> readJarFile(String path) throws IOException {
        HashSet<String> classFiles = new HashSet<>();
        try {
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) classFiles.add(entry.getName());
            }
            jarFile.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + path);
        }
        System.out.println(classFiles);
        return classFiles;
    }

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {
        // Remember to transform this to a command line input
        //String className = "com.jetbrains.internship2024.ClassA";
        //List<String> jars = new ArrayList<>();
        //jars.add(" ModuleA-1.0.jar");
        //HashSet<String> jarDependencies = new HashSet<>();
        //for (String jar : jars) {
        //    jarDependencies.addAll(readJarFile(jar));
        //}
        //readJarFile( ModuleA-1.0.jar);


        String className = "com.jetbrains.internship2024.SomeAnotherClass";
        Path filePath = getClassPath(className);
        byte[] classData = Files.readAllBytes(filePath);
        BytecodeParser parser = new BytecodeParser(classData);
        ClassFile classFile = parser.parseBytecode();
        List<String> dependencies = getBytecodeClassDependencies(classFile);

        for (String dependency : dependencies) {
            try {
                String d = dependency.replace('/', '.');
                Path dependencyPath = getClassPath(d);
                System.out.println(dependency);
            } catch (Exception e) {
                continue;
            }
        }
    }
}