package root;

import com.fasterxml.jackson.core.JsonProcessingException;
import root.Example;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.lang.reflect.Field;



public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, JsonProcessingException {
        // объект, который мы передаем для десериализации
        Example e = new Example();
        // я хотела передавать и название класса функции, но оно не хочет так тоже работать(
        JsonValue jsString = serializeObject(e);
        System.out.println(jsString.toString());

    }
    private static JsonValue serializeObject(Object o) throws IllegalAccessException {
        if (o == null) {
            return JsonValue.NULL;
        }
        Class <?> clazz = o.getClass();
        String className = clazz.getName();
        Field[] fields = clazz.getDeclaredFields();
        JsonObjectBuilder jsObj = Json.createObjectBuilder();
        jsObj.add("className", className);
        if (fields != null){
            JsonObjectBuilder jsInnerObj = Json.createObjectBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
//                if(isPrimitive(field.getType()){
                    if(field.get(o) != null){
                        jsInnerObj.add(fieldName, field.get(o).toString());
                }
                    else {
                        jsInnerObj.add(fieldName,JsonValue.NULL);
                }
//                }
            }
            jsObj.add("fields", jsInnerObj);
        }
        return jsObj.build();
    }
//    private static boolean isPrimitive(Class<?> cls) {
//        return cls.isPrimitive() || cls.equals(Integer.class) ||
//                cls.equals(String.class) || cls.equals(Double.class) ||
//                cls.equals(Boolean.class) || cls.equals(Byte.class) ||
//                cls.equals(Short.class) || cls.equals(Long.class) ||
//                cls.equals(Float.class);
//    }

}




//        Class<?> clazz = Class.forName(args[0]);
//        Field[] fields = clazz.getDeclaredFields();

//        for (Field field : fields) {
//            field.setAccessible(true);
//            out.println(field.getName());
//        }
//    }

//        ObjectMapper mapper = new ObjectMapper();
//        String empString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(e);
//        out.println(empString);


    // для дерганья аннотаций (сырой, допишем)

//        out.format("Annotations:%n");
//        Annotation[] ann = c.getAnnotations();
//        if (ann.length != 0) {
//            for (Annotation a : ann)
//                out.format("  %s%n", a.toString());
//            out.format("%n");
//        } else {
//            out.format("  -- No Annotations --%n%n");
//        }


//        root.Example e = new root.Example();
//        serialize(root.Example.class, e);
//    }

//    private static String serialize(Class<?> clazz, Object o) throws IllegalAccessException {
////        Field[] fields = clazz.getDeclaredFields();
////        for (Field field : fields) {
////            field.setAccessible(true);
//////            out.println(field.getName());
////        }
////
////        for (Field f : fields) {
////            Object value = f.get(o);
//////            out.println(value);
////        }
//
//        return (serializeObject(o).toString());
//    }



