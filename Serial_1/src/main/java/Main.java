import com.fasterxml.jackson.core.JsonProcessingException;

import javax.json.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;



public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, JsonProcessingException, ParseException {
        Example e = new Example();
        // я хотела передавать и название класса функции, но оно не хочет так тоже работать(
        JsonValue jsString = serializeObject(e);
        System.out.println(jsString.toString());

    }

    private static JsonValue serializeObject(Object o) throws IllegalAccessException {
        if (o == null) {
            return JsonValue.NULL;
        }
        Class<?> clazz = o.getClass();
        String className = clazz.getName();
        Field[] fields = clazz.getDeclaredFields();
        JsonObjectBuilder jsObj = Json.createObjectBuilder();
        jsObj.add("className", className);
        if (fields != null) {
            JsonObjectBuilder jsInnerObj = Json.createObjectBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (field.get(o) != null) {
                    if (isString(field)) {
                        jsInnerObj.add(fieldName, field.get(o).toString());
                    }
                    if (isInt(field)) {
                        jsInnerObj.add(fieldName, ((Number) field.get(o)).longValue());
                    }
                    if (isFloat(field)) {
                        jsInnerObj.add(fieldName, ((Number) field.get(o)).doubleValue());
                    }
                    if (isBoolean(field)) {
                        jsInnerObj.add(fieldName, ((Boolean) field.get(o)).booleanValue());
                    }
                } else {
                    jsInnerObj.add(fieldName, JsonValue.NULL);
                }
            }
            jsObj.add("fields", jsInnerObj);
        }
        return jsObj.build();
    }

    private static boolean isString(Field field) throws IllegalAccessException {
        return field.getType().equals(String.class) || field.getType().equals(char.class)
                || field.getType().equals(Character.class);
    }

    private static boolean isInt(Field field) throws IllegalAccessException {
        return field.getType().equals(int.class) || field.getType().equals(Integer.class)
                || field.getType().equals(byte.class) || field.getType().equals(Byte.class)
                || field.getType().equals(short.class) || field.getType().equals(Short.class)
                || field.getType().equals(long.class) || field.getType().equals(Long.class);

    }

    private static boolean isFloat(Field field) throws IllegalAccessException {
        return field.getType().equals(double.class) || field.getType().equals(Double.class)
                || field.getType().equals(float.class) || field.getType().equals(Float.class);
    }

    private static boolean isBoolean(Field field) throws IllegalAccessException {
        return field.getType().equals(boolean.class) || field.getType().equals(Boolean.class);
    }
}


//        ObjectMapper mapper = new ObjectMapper();
//        String empString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(e);
//        out.println(empString);


////
////        for (Field f : fields) {
////            Object value = f.get(o);
//////            out.println(value);
////        }
//



