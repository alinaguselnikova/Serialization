package root.serialization;

import java.util.HashMap;


public class IdGiver {
        private final HashMap<Object, Integer> references;
        private Integer id;

        public IdGiver() {
            this.references = new HashMap<>();
            id=0;
        }

        public Integer getIDFor(Object object) {
            if (references.containsKey(object)) {
                return references.get(object);
            }
            references.put(object, id);
            return id++;
        }
    }

