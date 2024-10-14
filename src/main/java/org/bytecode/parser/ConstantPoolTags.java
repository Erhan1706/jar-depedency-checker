package org.bytecode.parser;

/**
 * Enum representing the tags used in the Java Class File Constant Pool.
 */
public enum ConstantPoolTags {
    CONSTANT_Class(7),
    CONSTANT_Fieldref(9),
    CONSTANT_Methodref(10),
    CONSTANT_InterfaceMethodref(11),
    CONSTANT_String(8),
    CONSTANT_Integer(3),
    CONSTANT_Float(4),
    CONSTANT_Long(5),
    CONSTANT_Double(6),
    CONSTANT_NameAndType(12),
    CONSTANT_Utf8(1),
    CONSTANT_MethodHandle(15),
    CONSTANT_MethodType(16),
    CONSTANT_InvokeDynamic(18);

    private final int value;

    ConstantPoolTags(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Retrieves the corresponding {@code ConstantPoolTags} enum for a given integer value
     * @param value the integer value of the constant pool tag
     * @return the {@code ConstantPoolTags} enum corresponding to the value
     * @throws IllegalArgumentException if the value does not correspond to any known constant pool tag
     */
    public static ConstantPoolTags getTagByValue(int value) {
        for (ConstantPoolTags tag : ConstantPoolTags.values()) {
            if (tag.getValue() == value) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Unknown constant pool tag: " + value);
    }
}
