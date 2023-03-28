package root.serialization;

import root.templates.AdapterFactory;
import root.templates.Serializator;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.ArrayDeque;

public class SerializationManager {
    private final IdGiver giver;
    private final ArrayDeque<Object> toSerialize;
    private final Serializator serializator;

    public SerializationManager(IdGiver giver, AdapterFactory factory) {
        this.giver = giver;
        this.toSerialize = new ArrayDeque<>();
        this.serializator = factory.getSerializator();
        serializator.setGiver(giver);
        serializator.setQueue(toSerialize);
    }
    public JsonValue startWorking(Object o) throws IllegalAccessException {
        if ( o == null) {
            return null;
        }
        JsonObjectBuilder res = Json.createObjectBuilder();
        toSerialize.add(o);
        while (!toSerialize.isEmpty()) {
            Object obj = toSerialize.pop();
            Integer currentId = giver.getIDFor(obj);
            JsonValue serializedObject = serializator.serializeObject(currentId, obj);
            res.add(currentId.toString(), serializedObject);
        }
        return res.build();
    }
}
