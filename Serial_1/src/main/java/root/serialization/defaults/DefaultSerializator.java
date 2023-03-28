package root.serialization.defaults;

import root.serialization.IdGiver;
import root.templates.Serializator;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Queue;

import static root.serialization.util.Util.*;
public class DefaultSerializator implements Serializator {

    private Queue<Object> serializationQueue;
    private IdGiver giver;

    @Override
    public JsonValue serializeObject(Integer ID, Object o) throws IllegalAccessException {
        if (o == null) {
            return JsonValue.NULL;
        }
        Class<?> clazz = o.getClass();
        if (Collection.class.isAssignableFrom(clazz)) {
            return serializeCollection((Collection<?>) o, ID);
        }
        String className = clazz.getName();
        Field[] fields = clazz.getDeclaredFields();
        JsonObjectBuilder jsObj = Json.createObjectBuilder();
        jsObj.add("className", className);
        JsonObjectBuilder primitiveFields = Json.createObjectBuilder();
        JsonObjectBuilder refFields = Json.createObjectBuilder();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (field.get(o) != null) {
                String stringField = field.get(o).toString();
                if (isString(field.getType())) {
                    primitiveFields.add(fieldName, stringField);
                }
                else if (isInt(field.getType())) {
                    primitiveFields.add(fieldName, IntegerValue(stringField));
                }
                else if (isFloat(field.getType())) {
                    primitiveFields.add(fieldName, (FloatValue(stringField)));
                }
                else if (isBoolean(field.getType())) {
                    primitiveFields.add(fieldName, BooleanValue(stringField));
                }
                else {
                    int fieldId = giver.getIDFor(field.get(o));
                    if (fieldId > ID) {
                        serializationQueue.add(field.get(o));
                    }
                    refFields.add(fieldName, fieldId);
                }
            } else {
                primitiveFields.add(fieldName, JsonValue.NULL);
            }
        }
        jsObj.add("fields", primitiveFields);
        jsObj.add("ref", refFields);
        return jsObj.build();
    }

    @Override
    public JsonValue serializeCollection(Collection<?> o, Integer ID) {
        if ( o == null) {
            return JsonValue.NULL;
        }
        JsonObjectBuilder jsColl = Json.createObjectBuilder();
        jsColl.add("ClassName", o.getClass().getName());
        if(o.size() != 0) {
            jsColl.add("Argument Type", o.iterator().next().getClass().getName());
        }
        else jsColl.add("Argument Type", JsonValue.NULL);

        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder refObj = Json.createObjectBuilder();

        for (Object element: o) {
            if (element == null) {
                arrayBuilder.add(JsonValue.NULL);
                continue;
            }
            String stringElement = element.toString();
            if (isString(element.getClass())) {
                arrayBuilder.add(element.toString());
            } else if (isBoolean(element.getClass())) {
                arrayBuilder.add(BooleanValue(stringElement));
            } else if (isInt(element.getClass())){
                arrayBuilder.add(IntegerValue(stringElement));
            } else if (isFloat(element.getClass())){
                arrayBuilder.add(FloatValue(stringElement));
            } else {
                int elementId = giver.getIDFor(element);
                if(elementId > ID && !serializationQueue.contains(element)){
                    serializationQueue.add(element);
                }
                refObj.add(stringElement, elementId);
            }
            arrayBuilder.add(refObj);
        }
        jsColl.add("array", arrayBuilder);
        return jsColl.build();
    }

    public void setGiver(IdGiver giver) {
        this.giver = giver;
    }

    public void setQueue(Queue<Object> serializationQueue) {
        this.serializationQueue = serializationQueue;
    }
}
