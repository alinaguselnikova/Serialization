package root.framework.defaults;


import root.framework.templates.AdapterFactory;
import root.framework.templates.Deserializator;
import root.framework.templates.Serializator;

public class DefaultAdapterFactory implements AdapterFactory {
    @Override
    public Serializator getSerializator() {
        return new DefaultSerializator();
    }
    @Override
    public Deserializator getDeserializator(){return new DefaultDeserializator();}
}
