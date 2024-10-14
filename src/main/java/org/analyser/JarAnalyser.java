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

    /**
     * Reads the contents of a JAR file and collects all .class files within it.
     *
     * @param path the file path of the JAR to be read
     * @return a set of .class files found in the provided JAR
     * @throws IOException if an I/O error occurs or the JAR file paths are invalid
     */
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
        return classFiles;
    }

    /**
     * Aggregates the .class files from a list of JAR's.
     *
     * @param jarPaths a list of file paths to JAR files
     * @return a set of .class (bytecode) files found across all the provided JAR files
     * @throws IOException if an I/O error occurs or the JAR file paths are invalid
     */
    public static Set<String> getJarDependencies(List<String> jarPaths) throws IOException {
        Set<String> jarDependencies = new HashSet<>();
        for (String jarPath : jarPaths) {
            jarDependencies.addAll(readJarFile(jarPath));
        }
        return jarDependencies;
    }
}
