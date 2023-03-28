package root.main;

import root.framework.deserialization.DefaultDeserializer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {
//    public static void main(String[] args) throws IllegalAccessException {
//        Example e = new Example("Aline", 0, true);
//        Example p = new Example("Don", 200, false);
//        e.setRelation(p);
//        p.setRelation(e);
//        ArrayList<Example>  list = new ArrayList<>();
//        {
//            list.add(p);
//            list.add(p);
//        }
//        e.setArrayList(list);
//        p.setArrayList(list);
//        IdGiver giver = new IdGiver();
//        DefaultAdapterFactory factory = new DefaultAdapterFactory();
//        SerializationManager serializationManager = new SerializationManager(giver, factory);
//        System.out.println(serializationManager.startWorking(e));
//    }
    public static void main(String[] args) throws IllegalAccessException {
        String json = """
                {
                  "0": {
                    "className":"root.main.Example",
                    "fields":{"name":"Aline","age":0,"isTrue":true},
                    "ref":{"list":1,"relation":2}
                  },
                  "1":{
                    "className":"java.util.ArrayList",
                    "Argument Type":"root.main.Example",
                    "array":[{"root.main.Example@3f2e1cf":2},{"root.main.Example@3f2e1cf":2}]
                  },
                  "2":{
                    "className":"root.main.Example",
                    "fields":{"name":"Don","age":200,"isTrue":false},
                    "ref":{"list":1,"relation":0}
                  }
                }""";
        Object obj = new DefaultDeserializer().startFrom(json);
//        System.out.println(obj);
        printObject(obj);
    }
    private static void printObject(Object obj) {
        ArrayList<Field> fields =
                new ArrayList<>(Arrays.asList(obj.getClass().getDeclaredFields()));
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                System.out.println(f.getName() + " = " + f.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
