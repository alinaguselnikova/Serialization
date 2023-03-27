package serialization;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, JsonProcessingException, ParseException {
        Example e = new Example("Aline", 0);
        Example p = new Example("Don", 200);
        e.setRelation(p);
        p.setRelation(e);
        System.out.println(startWorking(e));
    }

    private static List<JsonValue> startWorking(Object o) throws IllegalAccessException {
        if ( o == null) {
            return null;
        }
        ArrayDeque<Object> toSerialize = new ArrayDeque<>();
        List<JsonValue> res = new ArrayList<>();
        toSerialize.add(o);
        IdGiver IdExample = new IdGiver();
        while (!toSerialize.isEmpty()) {
            Object obj = toSerialize.pop();
            Integer currentId = IdExample.getIDFor(obj);
            JsonValue serializedObject = serializeObject(currentId, obj, toSerialize, IdExample);
            res.add(serializedObject);
        }
        return res;
    }

    private static JsonValue serializeObject(Integer ID, Object o, Queue<Object> serializationQueue, IdGiver giver) throws IllegalAccessException {
        if (o == null) {
            return JsonValue.NULL;
        }
        Class<?> clazz = o.getClass();
        String className = clazz.getName();
        Field[] fields = clazz.getDeclaredFields();
        JsonObjectBuilder jsObj = Json.createObjectBuilder();
        jsObj.add("className", className);
        if (fields != null) {
            JsonObjectBuilder primitiveFields = Json.createObjectBuilder();
            JsonObjectBuilder refFields = Json.createObjectBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (field.get(o) != null) {
                    if (isString(field)) {
                        primitiveFields.add(fieldName, field.get(o).toString());
                    }
                    else if (isInt(field)) {
                        primitiveFields.add(fieldName, ((Number) field.get(o)).longValue());
                    }
                    else if (isFloat(field)) {
                        primitiveFields.add(fieldName, ((Number) field.get(o)).doubleValue());
                    }
                    else if (isBoolean(field)) {
                        primitiveFields.add(fieldName, (Boolean) field.get(o));
                    }
                    else {
                        int fieldId = giver.getIDFor(field.get(o));
                        if (fieldId > ID) {
                            serializationQueue.add(field.get(o));
                        }
                        refFields.add(fieldName, fieldId);
                    }
                } else {
                    primitiveFields.add(fieldName, JsonValue.NULL);
                }
            }
            jsObj.add("fields", primitiveFields);
            jsObj.add("ref", refFields);
        }
        JsonObjectBuilder finalString = Json.createObjectBuilder();
        finalString.add(ID.toString(),jsObj);


        return finalString.build();
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



