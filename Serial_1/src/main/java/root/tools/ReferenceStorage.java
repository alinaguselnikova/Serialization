package root.tools;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class ReferenceStorage {
    private final HashMap<Integer, Object> references;

    public ReferenceStorage() {
        this.references = new HashMap<>();
    }

    public Object getOrCreate(Integer id, Constructor<?> constructor, Object[] constructorArguments) {
        if (references.get(id) != null) {
            return references.get(id);
        }
        try {
            return references.put(id, constructor.newInstance(constructorArguments));
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate object, id = " + id);
        }
    }
}
