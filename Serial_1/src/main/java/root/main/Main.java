package root.main;

import root.framework.SerializationManager;
import root.framework.defaults.DefaultAdapterFactory;
import root.framework.util.IdGiver;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        Example e = new Example("Aline", 0, true);
        Example p = new Example("Don", 200, false);
        e.setRelation(p);
        p.setRelation(e);
        ArrayList<Example>  list = new ArrayList<>();
        {
            list.add(p);
            list.add(p);
        }
        e.setArrayList(list);
        p.setArrayList(list);
        IdGiver giver = new IdGiver();
        DefaultAdapterFactory factory = new DefaultAdapterFactory();
        SerializationManager serializationManager = new SerializationManager(giver, factory);
        String json = serializationManager.serialize(e);
        Object obj = serializationManager.deserialize(json);
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
