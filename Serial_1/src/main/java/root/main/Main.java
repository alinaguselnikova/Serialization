package root.main;

import root.serialization.defaults.DefaultAdapterFactory;
import root.serialization.IdGiver;
import root.serialization.SerializationManager;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) throws IllegalAccessException {
        Example e = new Example("Aline", 0, true);
        Example p = new Example("Don", 200, false);
        e.setRelation(p);
        p.setRelation(e);
        ArrayList<Example>  list = new ArrayList<>();
        {
            list.add(e);
            list.add(p);
        }
        e.setArrayList(list);
        p.setArrayList(list);
        IdGiver giver = new IdGiver();
        DefaultAdapterFactory factory = new DefaultAdapterFactory();
        SerializationManager serializationManager = new SerializationManager(giver, factory);
        System.out.println(serializationManager.startWorking(e));
    }
}
