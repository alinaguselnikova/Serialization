package root.framework.deserialization;

import root.framework.Instantiator;
import root.framework.templates.Deserializator;

import javax.json.*;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

    public Class<?> getClassFromString(JsonString className) {
        Class<?> cls;
        try {
            cls = Class.forName(className.getString());
        } catch (ClassNotFoundException e) {
            System.out.println("Can't deserialize, no such class: " + className);
            throw new RuntimeException(e);
        }
        return cls;
    }

    public void deserializeObject(JsonObject deserialized, String id) throws IllegalAccessException {
        Class<?> cls = getClassFromString(deserialized.getJsonString("className"));
        if (Collection.class.isAssignableFrom(cls)) {
            deserializeCollection(cls, getClassFromString(deserialized.getJsonString("Argument Type")), deserialized.getJsonArray("array"), id);
        } else {
            Object deserializedStub = Instantiator.instantiateClass(cls);
            insertPrimitiveFields(deserializedStub, deserialized.getJsonObject("fields"), cls);
            Map<String, Integer> referenceFieldsMap = getReferenceFieldsMap(deserialized.getJsonObject("ref"));
            referenceFields.put(Integer.parseInt(id), referenceFieldsMap);
            stubs.put(Integer.parseInt(id), deserializedStub);
        }
    }

    private void deserializeCollection(Class<?> cls, Class<?> objCls, JsonArray array, String id) {
        Collection<Object> collection = (Collection<Object>) Instantiator.instantiateClass(cls);
        if (collection == null) throw new RuntimeException();
        if (isString(objCls) || isBoolean(objCls) || isInt(objCls) || isFloat(objCls)) {
            for (Object o : array) {
                collection.add(convert(objCls, (String)o));
            }
        } else {
            for (JsonValue ref : array) {
                JsonObject objRef = ref.asJsonObject();
                JsonValue refid = objRef.get(objRef.keySet().stream().toList().get(0));
                if (refid == null) {
                    collection.add(null);
                    continue;
                }
                collection.add(stubs.get(Integer.parseInt(refid.toString())));
            }
        }
        stubs.put(Integer.parseInt(id), collection);
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
