package org.bytecode.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassFile {
    int minor;
    int major;
    public List<ConstantPoolObject> constantPool = new ArrayList<>();
    List<String> accessFlags = new ArrayList<>();
    int thisClass;
    int superClass;
    List<Object> interfaces = new ArrayList<>();
    List<Object> fields = new ArrayList<>();
    List<Map<String, Object>> methods;
    List<Map<String, Object>> attributes;
}