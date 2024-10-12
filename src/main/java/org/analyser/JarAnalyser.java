package org.analyser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarAnalyser {

    public static Set<String> readJarFile(String path) throws IOException {
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
        //System.out.println(classFiles);
        return classFiles;
    }

    public static Set<String> getJarDependencies(List<String> jarPaths) throws IOException {
        Set<String> jarDependencies = new HashSet<>();
        for (String jarPath : jarPaths) {
            jarDependencies.addAll(readJarFile(jarPath));
        }
        System.out.println(jarDependencies);
        return jarDependencies;
    }
}
