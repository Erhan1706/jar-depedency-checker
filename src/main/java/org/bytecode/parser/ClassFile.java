package org.bytecode.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the Java Virtual Machine .class file format
 */
public class ClassFile {
    int minor;
    int major;
    public List<ConstantPoolObject> constantPool = new ArrayList<>();
}