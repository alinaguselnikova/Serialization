import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, JsonProcessingException {

//   сверху будет класс, который вызывает пользователь
//   и передает имя класса + объект, который нужно сериализовать
// объектная модель (парсинг)
        Example e = new Example();
        Class<?>  clazz = Class.forName(args[0]);
        Field[] fields = clazz.getDeclaredFields();

//        for (Field field : fields) {
//            field.setAccessible(true);
////            out.println(field.getName());
//        }
//
        ObjectMapper mapper = new ObjectMapper();
        String empString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(e);
        out.println(empString);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        stringBuilder.append("\n");

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            Object value = fields[i].get(e);
            stringBuilder.append("\t");
            stringBuilder.append('"');
            stringBuilder.append(fields[i].getName());
            stringBuilder.append('"');
            stringBuilder.append(" : ");
            stringBuilder.append('"');
            stringBuilder.append(value);
            stringBuilder.append('"');
            if (i != fields.length - 1) {
                stringBuilder.append(",");
            }

            stringBuilder.append("\n");
//            out.println(fields[i].getType());
        }
        stringBuilder.append("}");
        out.println(stringBuilder.toString());






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



//        Example e = new Example();
//        serialize(Example.class, e);
    }

//    private static void serialize(Class<?> clazz, Object o) throws IllegalAccessException {
//        Field[] fields = clazz.getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            out.println(field.getName());
        }

//        for (Field f : fields) {
//            Object value = f.get(o);
//            out.println(value);
//        }
//    }

//}
