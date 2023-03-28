package root.framework.templates;

public interface AdapterFactory {
    Serializator getSerializator();
    Deserializator getDeserializator();
}
