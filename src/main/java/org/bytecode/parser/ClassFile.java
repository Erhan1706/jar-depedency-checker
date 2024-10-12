package org.bytecode.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassFile {
    int minor;
    int major;
    public List<ConstantPoolObject> constantPool = new ArrayList<>();
}