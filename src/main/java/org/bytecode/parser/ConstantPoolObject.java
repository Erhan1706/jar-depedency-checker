package org.bytecode.parser;

public class ConstantPoolObject {
    public ConstantPoolTags tag;
    public int nameIndex;
    public int classIndex;
    public byte[] bytes;
    public int stringIndex;

    public ConstantPoolObject(ConstantPoolTags tag) {
        this.tag = tag;
    }
}
