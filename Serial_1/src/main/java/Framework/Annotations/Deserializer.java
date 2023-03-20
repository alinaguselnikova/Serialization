package Framework.Annotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

public class Deserializer {
    public static void main(String[] args) throws JsonProcessingException, IllegalAccessException {
        String json = "{ \"ClassName\" : \"Person\", \"fields\" : { \"id\" : 12, \"firstName\" : \"Aline\", \"lastName\" : \"Grace\" } }";
        Object obj = deserializeObject(json);
        System.out.println(obj);
        printObject(obj);

    }

    public static Object deserializeObject(String jsonString) throws JsonProcessingException, IllegalAccessException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> objectMap = objectMapper.readValue(jsonString, new TypeReference<>() {
        });
        //Определяем имя класса
        String className = objectMap.get("ClassName").toString();
        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't deserialize, no such class: " + className);
            throw new RuntimeException(e);
        }

        //Обрабатываем либо как объект, либо как массив
        if (objectMap.containsKey("fields")) {
            return deserializeSingleObject(cls, (LinkedHashMap<String, Object>) objectMap.get("fields"));
        } else if (objectMap.containsKey("array")) {
            return deserializeArray(cls, (LinkedHashMap<String, Object>) objectMap.get("array"));
        }
        return null;
    }

    private static Object deserializeSingleObject(Class<?> cls, LinkedHashMap<String, Object> fieldsMap) throws IllegalAccessException {

        Object[] constructors = Arrays.stream(cls.getConstructors()).toArray();

        //Случай если конструктор один:
        if (constructors.length == 1) {
            Constructor<?> constructor = (Constructor<?>) constructors[0];
            Annotation[][] anArr = constructor.getParameterAnnotations();
            ArrayList<String> constructionFields = new ArrayList<>();
            ArrayList<Object> constrValues = new ArrayList<>();
            HashMap<String, Object> constructionMap = new HashMap<>(); //needs casting
            //Достанем имена всех полей, которые нужны для конструктора
            for (Annotation[] ans : anArr) {
                for (Annotation an : ans) {
                    if (an.annotationType().equals(ConstructorField.class)) {
                        String pName = ((ConstructorField) an).value();
                        constructionFields.add(pName);
                        if (fieldsMap.containsKey(pName)) {
                            Object pVal = fieldsMap.get(pName);
                            constrValues.add(pVal);
                            constructionMap.put(pName, pVal);
                        } else {
                            System.out.println("Deserialization warning: can't find parameter key in JSON: " + pName);
                        }
                    }
                }
            }


            //Преобразование типов полей, которые передаются в конструктор
            Class<?>[] constrFieldsTypes = constructor.getParameterTypes();
            for (int i = 0; i < constrFieldsTypes.length; i++) {
                if (constrFieldsTypes[i].equals(constrValues.get(i).getClass()))
                    continue;
                try {
                    constrValues.set(i, convert(constrFieldsTypes[i], constrValues.get(i).toString()));
                } catch (ClassCastException e) {
                    System.out.println("Deserialization warning! Can't cast value of type "
                            + constrValues.get(i).getClass().getName()
                            + " to type " + constrFieldsTypes[i].getName());
                    constrValues.set(i, null);
                }
            }
            //Преобразование типов остальных полей
            for (String key : fieldsMap.keySet()) {
                if (!constructionFields.contains(key)) {
                    Field field = getField(cls, key);
                    assert field != null;
                    field.setAccessible(true);
                    if (field.getType().equals(fieldsMap.get(key).getClass())) {
                        continue;
                    }
                    try {
                        fieldsMap.put(key, convert(field.getType(), (String) fieldsMap.get(key)));
                    } catch (ClassCastException e) {
                        System.out.println("Deserialization warning! Can't cast value of type "
                                + fieldsMap.get(key).getClass().getName()
                                + " to type " + field.getType().getName());
                    }
                }
            }

            Object res;
            //Создаем объект через найденный конструктор
            try {
                res = constructor.newInstance(constrValues.toArray());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                //throw new RuntimeException(e);
                e.printStackTrace();
                return null;
            }

            //Добавляем остальные объявленные поля
            for (String key : constructionFields) {
                Field f = getField(cls, key);
                assert f != null;
                f.setAccessible(true);
                f.set(res, fieldsMap.get(key));
            }
            return res;
        }
        return null;
    }

    private static Array deserializeArray(Class<?> cls, LinkedHashMap<String, Object> fieldsMap) {
        return null;
    }

    private static Field getField(Class<?> cls, String fieldName) {
        for (Field f : cls.getDeclaredFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    public static Object convert(Class<?> targetType, String text) {
        if (targetType.getName().equals("int")) {
            return Integer.parseInt(text);
        }
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }

    private static void printObject(Object obj) {
        Class<?> cls = obj.getClass();
        ArrayList<Field> fields =
                new ArrayList<Field>(Arrays.asList(obj.getClass().getDeclaredFields()));
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
