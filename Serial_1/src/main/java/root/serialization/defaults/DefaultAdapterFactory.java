package root.serialization.defaults;

import root.templates.AdapterFactory;
import root.templates.Serializator;

public class DefaultAdapterFactory implements AdapterFactory {
    @Override
    public Serializator getSerializator() {
        return new DefaultSerializator();
    }
}
