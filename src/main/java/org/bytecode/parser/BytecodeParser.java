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
        parseConstantPool(classFile);
        // There is more information that can be parsed from the bytecode, but the constant pool is all I'm interested about
        return classFile;
    }

    private void parseConstantPool(ClassFile classFile) {
        int constantPoolCount = readTwoBytes();
        for (int i = 0; i < constantPoolCount - 1; i++) {
            ConstantPoolTags tag = ConstantPoolTags.getTagByValue(readOneByte());
            ConstantPoolObject curObject = new ConstantPoolObject(tag);
            switch (tag) {
                case CONSTANT_Class, CONSTANT_MethodType:
                    curObject.nameIndex = readTwoBytes();
                    break;
                case CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref, CONSTANT_InvokeDynamic:
                    curObject.classIndex = readTwoBytes();
                    curObject.nameIndex = readTwoBytes();
                    break;
                case CONSTANT_Utf8:
                    int length = readTwoBytes();
                    curObject.bytes = readBytes(length);
                    String s = new String(curObject.bytes, StandardCharsets.UTF_8);
                    break;
                case CONSTANT_Integer, CONSTANT_Float:
                    curObject.bytes = readBytes(4);
                    break;
                case CONSTANT_Long, CONSTANT_Double:
                    curObject.highBytes = readBytes(4);
                    curObject.lowBytes = readBytes(4);
                    i += 1;
                    break;
                case CONSTANT_NameAndType:
                    curObject.nameIndex = readTwoBytes();
                    curObject.stringIndex = readTwoBytes();
                    break;
                case CONSTANT_String:
                    curObject.stringIndex = readTwoBytes();
                    break;
                case CONSTANT_MethodHandle:
                    curObject.referenceKind = readOneByte();
                    curObject.nameIndex = readTwoBytes();
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled constant pool tag: " + tag);
            }
            classFile.constantPool.add(curObject);
            if (tag == ConstantPoolTags.CONSTANT_Double || tag == ConstantPoolTags.CONSTANT_Long)
                classFile.constantPool.add(null);
        }
    }
}
