import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        // сверху будет класс, который вызывает пользователь
        // и передает имя класса + объект, который нужно сериализовать
//        Class<?> exam = Class.forName(args[0]);
//        Field[] fields = exam.getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            out.println(field.getName());
//        }



        // для дерганья аннотаций (сырой, допишем)

//          out.format("Annotations:%n");
//        Annotation[] ann = c.getAnnotations();
//        if (ann.length != 0) {
//            for (Annotation a : ann)
//                out.format("  %s%n", a.toString());
//            out.format("%n");
//        } else {
//            out.format("  -- No Annotations --%n%n");
//        }

        //как вытащить значения из всего этого массива...
        // почему блять не работает даже getDeclaredField с названием сука поля
//        Example example = new Example();
//        for (Field f : fields) {
//            Object value = f.get(example);
//            out.println(value);
//        }
        Example e = new Example();
        serialize(Example.class, e);
    }

    private static void serialize(Class<?> clazz, Object o) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            out.println(field.getName());
        }

        for (Field f : fields) {
            Object value = f.get(o);
            out.println(value);
        }
    }
}
