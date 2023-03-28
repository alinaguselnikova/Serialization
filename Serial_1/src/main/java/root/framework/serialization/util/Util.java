package root.framework.serialization.util;

import javax.json.JsonValue;
import java.util.InputMismatchException;

public class Util {
    public static boolean isString(Class <?> cls) {
        return cls.equals(String.class) || cls.equals(char.class)
                || cls.equals(Character.class);
    }

    public static boolean isInt(Class <?> cls) {
        return cls.equals(int.class) || cls.equals(Integer.class)
                || cls.equals(byte.class) || cls.equals(Byte.class)
                || cls.equals(short.class) || cls.equals(Short.class)
                || cls.equals(long.class) || cls.equals(Long.class);

    }

    public static boolean isFloat(Class <?> cls) {
        return cls.equals(double.class) || cls.equals(Double.class)
                || cls.equals(float.class) || cls.equals(Float.class);
    }

    public static boolean isBoolean(Class <?> cls) {
        return cls.equals(boolean.class) || cls.equals(Boolean.class);
    }

    public static Long IntegerValue(String stringElement) {
        return Long.valueOf(stringElement);
    }

    public static Double FloatValue(String stringElement) {
        return Double.valueOf(stringElement);
    }

    public static Boolean BooleanValue(String stringElement) {
        return Boolean.valueOf(stringElement);
    }

    public static Boolean valueToBoolean(JsonValue value) {
        if (value.getValueType() == JsonValue.ValueType.FALSE) {
            return false;
        }
        if (value.getValueType() == JsonValue.ValueType.TRUE) {
            return true;
        }
        throw new InputMismatchException("Not boolean value for boolean argument");
    }
}
