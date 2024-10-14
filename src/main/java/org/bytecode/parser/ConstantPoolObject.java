package org.bytecode.parser;

/**
 * Represents a single object in the Java Class File Constant Pool.
 */
public class ConstantPoolObject {
    public ConstantPoolTags tag;
    public int nameIndex;
    public int classIndex;
    public byte[] bytes;
    public byte[] highBytes; // For doubles and longs
    public byte[] lowBytes; // For doubles and longs
    public int stringIndex;
    public int referenceKind;


    public ConstantPoolObject(ConstantPoolTags tag) {
        this.tag = tag;
    }
}
