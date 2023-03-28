package root.framework;

import root.framework.deserialization.DefaultDeserializer;
import root.framework.serialization.defaults.DefaultSerializator;
import root.framework.templates.AdapterFactory;
import root.framework.templates.Deserializator;
import root.framework.templates.Serializator;

public class DefaultAdapterFactory implements AdapterFactory {
    @Override
    public Serializator getSerializator() {
        return new DefaultSerializator();
    }
    public Deserializator getDeserializator(){return new DefaultDeserializer();}
}
