package serialization;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.json.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;


public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, JsonProcessingException, ParseException {
        Example e = new Example("Aline", 0, true);
        Example p = new Example("Don", 200, false);
        e.setRelation(p);
        p.setRelation(e);
//        System.out.println(startWorking(e));
        ArrayList<Example>  list = new ArrayList<>();
        {
            list.add(e);
            list.add(p);
        }

        e.setArrayList(list);
        p.setArrayList(list);
//        System.out.println(serializeCollection(list))
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
        if (Collection.class.isAssignableFrom(clazz)) {
            return serializeCollection((Collection<?>) o, ID,  serializationQueue, giver);
        }
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
                     String stringField = field.get(o).toString();
                    if (isString(field.getType())) {
                        primitiveFields.add(fieldName, stringField);
                    }
                    else if (isInt(field.getType())) {
                        primitiveFields.add(fieldName, IntegerValue(stringField));
                    }
                    else if (isFloat(field.getType())) {
                        primitiveFields.add(fieldName, (FloatValue(stringField)));
                    }
                    else if (isBoolean(field.getType())) {
                        primitiveFields.add(fieldName, BooleanValue(stringField));
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
        finalString.add(ID.toString(), jsObj);
        return finalString.build();
    }

    private static JsonValue serializeCollection(Collection<?> o, Integer ID, Queue<Object> serializationQueue, IdGiver giver) throws IllegalAccessException {
        if ( o == null) {
            return JsonValue.NULL;
        }
        JsonObjectBuilder jsColl = Json.createObjectBuilder();
        jsColl.add("ClassName", o.getClass().getName());
        if(o.size() != 0) {
            jsColl.add("Argument Type", o.iterator().next().getClass().getName());
        }
        else jsColl.add("Argument Type", JsonValue.NULL);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder refObj = Json.createObjectBuilder();

        for (Object element: o) {
            if (element == null) {
                arrayBuilder.add(JsonValue.NULL);
            }
            String stringElement = element.toString();
            if (isString(element.getClass())) {
                arrayBuilder.add(element.toString());
            } else if (isBoolean(element.getClass())) {
                arrayBuilder.add(BooleanValue(stringElement));
            } else if (isInt(element.getClass())){
                arrayBuilder.add(IntegerValue(stringElement));
            } else if (isFloat(element.getClass())){
                arrayBuilder.add(FloatValue(stringElement));
            } else {
                int elementId = giver.getIDFor(element);
//                if(!serializationQueue.contains(element)){
                if (elementId > ID) {
                    serializationQueue.add(element);
                }
                refObj.add(stringElement, elementId );
            }
                arrayBuilder.add(refObj);
        }
        jsColl.add("array", arrayBuilder);
        JsonObjectBuilder finalString = Json.createObjectBuilder();
        finalString.add(ID.toString(), jsColl.build());
        return finalString.build();
    }

    private static boolean isString(Class <?> cls) throws IllegalAccessException {
        return cls.equals(String.class) || cls.equals(char.class)
                || cls.equals(Character.class);
    }

    private static boolean isInt(Class <?> cls) throws IllegalAccessException {
        return cls.equals(int.class) || cls.equals(Integer.class)
                || cls.equals(byte.class) || cls.equals(Byte.class)
                || cls.equals(short.class) || cls.equals(Short.class)
                || cls.equals(long.class) || cls.equals(Long.class);

    }

    private static boolean isFloat(Class <?> cls) throws IllegalAccessException {
        return cls.equals(double.class) || cls.equals(Double.class)
                || cls.equals(float.class) || cls.equals(Float.class);
    }

    private static boolean isBoolean(Class <?> cls) throws IllegalAccessException {
        return cls.equals(boolean.class) || cls.equals(Boolean.class);
    }

    private static Long IntegerValue(String stringElement) {
        return Long.valueOf(stringElement);
    }

    private static Double FloatValue(String stringElement) {
        return Double.valueOf(stringElement);
    }

    private static Boolean BooleanValue(String stringElement) {
        return Boolean.valueOf(stringElement);
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

