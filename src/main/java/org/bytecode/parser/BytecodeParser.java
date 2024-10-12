package org.bytecode.parser;

import java.nio.charset.StandardCharsets;

public class BytecodeParser {

    private int pointer = 0;
    final private byte[] bytes;

    public BytecodeParser(byte[] bytes) {
        this.bytes = bytes;
    }

    private int readOneByte() {
        return bytes[pointer++] & 0xFF;
    }

    private int readTwoBytes() {
        int byte1 = readOneByte();
        int byte2 = readOneByte();
        return (byte1 << 8) | byte2;
    }

    private int readFourBytes() {
        int byte1 = readOneByte();
        int byte2 = readOneByte();
        int byte3 = readOneByte();
        int byte4 = readOneByte();
        return (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4;
    }

    private byte[] readBytes(int numBytes) {
        byte[] result = new byte[numBytes];
        System.arraycopy(bytes, pointer, result, 0, numBytes);
        pointer += numBytes;
        return result;
    }

    public ClassFile parseBytecode() {
        ClassFile classFile = new ClassFile();
        readFourBytes(); // skip magic number
        classFile.minor = readTwoBytes();
        classFile.major = readTwoBytes();
        int constantPoolCount = readTwoBytes();
        for (int i = 0; i < constantPoolCount - 1; i++) {
            ConstantPoolTags tag = ConstantPoolTags.getTagByValue(readOneByte());
            ConstantPoolObject curObject = new ConstantPoolObject(tag);
            switch (tag) {
                case CONSTANT_Class:
                    curObject.nameIndex = readTwoBytes();
                    System.out.println("Name: " + curObject.nameIndex);
                    break;
                case CONSTANT_Fieldref, CONSTANT_Methodref:
                    curObject.classIndex = readTwoBytes();
                    curObject.nameIndex = readTwoBytes();
                    break;
                case CONSTANT_Utf8:
                    int length = readTwoBytes();
                    byte[] utf8Bytes = readBytes(length);
                    curObject.bytes = utf8Bytes;
                    String utf8String = new String(utf8Bytes, StandardCharsets.UTF_8);
                    //System.out.println("UTF-8 String: " + utf8String);
                    break;
                case CONSTANT_NameAndType:
                    curObject.nameIndex = readTwoBytes();
                    curObject.stringIndex = readTwoBytes();
                    break;
                case CONSTANT_String:
                    curObject.stringIndex = readTwoBytes();
                    break;
                case CONSTANT_InterfaceMethodref:
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled constant pool tag: " + tag);
                    //break;
            }
            classFile.constantPool.add(curObject);
        }
        System.out.println(classFile.constantPool);

        return classFile;
    }
}
