package root.framework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Instantiator {

    public static Object instantiateClass(Class<?> clazz){
        Object[] constructors = Arrays.stream(clazz.getConstructors()).toArray();
        Constructor<?> constructor = (Constructor<?>) constructors[0];
        try {
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
