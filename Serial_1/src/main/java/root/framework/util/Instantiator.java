package root.framework.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Instantiator {

    public static Object instantiateClass(Class<?> clazz){
        Object[] constructors = Arrays.stream(clazz.getConstructors()).toArray();
        for (Object o : constructors) {
            Constructor<?> c = (Constructor<?>) o;
            if (c.getParameterCount() == 0) {
                try {
                    return c.newInstance();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        throw new RuntimeException("Cannot find default constructor");
    }
}
