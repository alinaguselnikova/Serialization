package root.framework;

import root.framework.templates.Deserializator;
import root.framework.util.IdGiver;
import root.framework.templates.AdapterFactory;
import root.framework.templates.Serializator;

import javax.json.*;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class SerializationManager {
    private final IdGiver giver;
    private final ArrayDeque<Object> toSerialize;
    private final Serializator serializator;
    private final Deserializator deserializator;
    private final Map<Integer, Map<String, Integer>> referenceFields;
    private final Map<Integer, Object> stubs;

    public SerializationManager(IdGiver giver, AdapterFactory factory) {
        this.giver = giver;
        this.toSerialize = new ArrayDeque<>();
        this.referenceFields = new HashMap<>();
        this.stubs = new HashMap<>();
        this.serializator = factory.getSerializator();
        this.deserializator = factory.getDeserializator();
        serializator.setGiver(giver);
        serializator.setQueue(toSerialize);
    }
    public String serialize(Object o) throws IllegalAccessException {
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
        return res.build().toString();
    }


    public Object deserialize(String jsonString) throws IllegalAccessException {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject object = jsonReader.readObject();
        for (String s : object.keySet()) {
            deserializator.deserializeObject(object.getJsonObject(s), s, referenceFields, stubs);
        }
        for (String s : object.keySet()) {
            if (s.equals("1")) continue;
            Integer id = Integer.parseInt(s);
            deserializator.restoreRefFields(stubs.get(id), referenceFields.get(id), stubs);
        }
        jsonReader.close();
        return stubs.get(0);
    }
}
