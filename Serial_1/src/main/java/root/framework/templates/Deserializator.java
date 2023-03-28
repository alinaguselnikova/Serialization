package root.framework.templates;

import javax.json.JsonObject;
import java.util.Map;

public interface Deserializator {
    void deserializeObject(JsonObject deserialized, String id, Map<Integer, Map<String, Integer>> referenceFields, Map<Integer, Object> stubs) throws IllegalAccessException;
    void restoreRefFields(Object target, Map<String, Integer> fields, Map<Integer, Object> stubs) throws IllegalAccessException;
}
