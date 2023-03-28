package root.framework.templates;

import root.framework.serialization.IdGiver;

import javax.json.JsonValue;
import java.util.Collection;
import java.util.Queue;

public interface Serializator {

    JsonValue serializeObject(Integer ID, Object o) throws IllegalAccessException;
    JsonValue serializeCollection(Collection<?> o, Integer ID);

    void setQueue(Queue<Object> queue);
    void setGiver(IdGiver giver);
}
