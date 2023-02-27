import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        // сверху будет класс, который вызывает пользователь
        // и передает имя класса + объект, который нужно сериализовать
        Class<?> exam = Class.forName(args[0]);
        Field[] fields = exam.getDeclaredFields();
        for (Field field : fields) {
//            out.println(field.getName());
//            out.println(exam.getDeclaredField("name"));
        }



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
    }
}
