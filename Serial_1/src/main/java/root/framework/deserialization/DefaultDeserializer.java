package root.framework.deserialization;

import root.framework.Instantiator;
import root.framework.templates.Deserializator;

import javax.json.*;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static root.framework.serialization.util.Util.*;

public class DefaultDeserializer implements Deserializator {
    private final Map<Integer, Map<String, Integer>> referenceFields = new HashMap<>();
    private final Map<Integer, Object> stubs =  new HashMap<>();
    public Object startFrom(String jsonString) throws IllegalAccessException {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject object = jsonReader.readObject();
        for (String s : object.keySet()) {
            deserializeObject(object.getJsonObject(s), s);
        }
        for (String s : object.keySet()) {
            if (s.equals("1")) continue;
            Integer id = Integer.parseInt(s);
            restoreRefFields(stubs.get(id), referenceFields.get(id));
        }
        jsonReader.close();
        return stubs.get(0);
    }

    public void deserializeObject(JsonObject deserialized, String id) throws IllegalAccessException {
        JsonString classname = deserialized.getJsonString("className");
        Class<?> cls;
        try {
            cls = Class.forName(classname.getString());
        } catch (ClassNotFoundException e) {
            System.out.println("Can't deserialize, no such class: " + classname);
            throw new RuntimeException(e);
        }
        if (Collection.class.isAssignableFrom(cls)) {
            return;
        }
        Object deserializedStub = Instantiator.instantiateClass(cls);
        insertPrimitiveFields(deserializedStub, deserialized.getJsonObject("fields"), cls);
        Map<String, Integer> referenceFieldsMap = getReferenceFieldsMap(deserialized.getJsonObject("ref"));

        referenceFields.put(Integer.parseInt(id), referenceFieldsMap);
        stubs.put(Integer.parseInt(id), deserializedStub);
    }

    public void insertPrimitiveFields(Object stub, JsonObject fields, Class<?> clazz) throws IllegalAccessException {
        System.out.println(fields);
        for (String name : fields.keySet()){
            Field objectField = getField(clazz, name);
            if (objectField != null) {
                objectField.setAccessible(true);
                JsonValue value = fields.getValue("/" + name);
                if (value.getValueType() == JsonValue.ValueType.ARRAY || value.getValueType() == JsonValue.ValueType.OBJECT) {
                    throw new RuntimeException("Non-primitive JSON value in primitive fields part");
                }
                Class<?> objectFieldClass = objectField.getType();
                if (isString(objectFieldClass)) {
                    JsonString string = fields.getJsonString(name);
                    objectField.set(stub, string.getString());
                }
                else if (isBoolean(objectFieldClass)) {
                    Boolean bool = valueToBoolean(value);
                    objectField.set(stub, bool);
                }
                else if (isInt(objectFieldClass)) {
                    JsonNumber number = fields.getJsonNumber(name);
                    objectField.set(stub, number.intValue());
                }
                else if (isFloat(objectFieldClass)) {
                    JsonNumber number = fields.getJsonNumber(name);
                    objectField.set(stub, number.doubleValue());
                }
            }
        }
    }

    private void restoreRefFields(Object target, Map<String, Integer> fields) throws IllegalAccessException {
        for (String fieldName : fields.keySet()) {
            Field field = getField(target.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(target, stubs.get(fields.get(fieldName)));
            }
        }
    }

    private Map<String, Integer> getReferenceFieldsMap(JsonObject refFields) {
        Map<String, Integer> res = new HashMap<>();
        for (String s : refFields.keySet()) {
            res.put(s, refFields.getJsonNumber(s).intValue());
        }
        return res;
    }

    private static Array deserializeArray(LinkedHashMap<String, Object> fieldsMap) {
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

    private static Object convert(Class<?> targetType, String text) {
        if (targetType.getName().equals("int")) {
            return Integer.parseInt(text);
        }
        PropertyEditor editor = PropertyEditorManager.findEditor(targetType);
        editor.setAsText(text);
        return editor.getValue();
    }
}
