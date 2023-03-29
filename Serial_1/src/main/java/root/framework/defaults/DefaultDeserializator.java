package root.framework.defaults;

import root.framework.templates.Deserializator;
import root.framework.util.Instantiator;

import javax.json.*;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static root.framework.util.Util.*;

public class DefaultDeserializator implements Deserializator {


    public void deserializeObject(JsonObject deserialized, String id, Map<Integer, Map<String, Integer>> referenceFields, Map<Integer, Object> stubs) throws IllegalAccessException {
        JsonString classname = deserialized.getJsonString("className");
        Class<?> cls;
        try {
            cls = Class.forName(classname.getString());
        } catch (ClassNotFoundException e) {
            System.out.println("Can't deserialize, no such class: " + classname);
            throw new RuntimeException(e);
        }
        if (Collection.class.isAssignableFrom(cls)) {
            JsonString argname = deserialized.getJsonString("Argument Type");
            JsonArray array = deserialized.getJsonArray("array");
            Class<?> argType;
            try {
                argType = Class.forName(argname.getString());
            } catch (ClassNotFoundException e) {
                System.out.println("Can't deserialize, no such class: " + classname);
                throw new RuntimeException(e);
            }
            Map<String, Integer> refFieldsMap = deserializeCollection(cls,argType,array,id,stubs);
            referenceFields.put(Integer.parseInt(id), refFieldsMap);
        }
        else {
            Object deserializedStub = Instantiator.instantiateClass(cls);
            insertPrimitiveFields(deserializedStub, deserialized.getJsonObject("fields"), cls);
            Map<String, Integer> referenceFieldsMap = getReferenceFieldsMap(deserialized.getJsonObject("ref"));
            referenceFields.put(Integer.parseInt(id), referenceFieldsMap);
            stubs.put(Integer.parseInt(id), deserializedStub);
        }
    }

    private void insertPrimitiveFields(Object stub, JsonObject fields, Class<?> clazz) throws IllegalAccessException {
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

    private Map<String, Integer> deserializeCollection(Class<?> cls, Class<?> objCls, JsonArray array, String id, Map<Integer, Object> stubs) {
        Collection<Object> collection = (Collection<Object>) Instantiator.instantiateClass(cls);
        Map<String, Integer> res = new HashMap<>();
        if (collection == null) throw new RuntimeException();
        if (isString(objCls) || isBoolean(objCls) || isInt(objCls) || isFloat(objCls)) {
            for (Object o : array) {
                collection.add(convert(objCls, (String)o));
            }
        } else {
            for (JsonValue ref : array) {
                JsonObject objRef = ref.asJsonObject();
                JsonNumber refid = objRef.getJsonNumber(objRef.keySet().stream().toList().get(0));
                String name = (String) (objRef.keySet().toArray())[0];
                res.put(name, refid.intValue());
            }
        }
        stubs.put(Integer.parseInt(id), collection);
        return res;
    }

    public void restoreRefFields(Object target, Map<String, Integer> fields, Map<Integer, Object> stubs) throws IllegalAccessException {
        if (Collection.class.isAssignableFrom(target.getClass())) {
            Collection<Object> collection = (Collection<Object>) target;
            for (Integer i : fields.values()) {
                collection.add(stubs.get(i));
            }
        }
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
