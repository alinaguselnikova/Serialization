import Framework.Annotations.ConstructorField;

public class Person {
    private int id;
    private String firstName;
    private String lastName;
    public Person(@ConstructorField("id") int id, @ConstructorField("firstName") String firstName, @ConstructorField("lastName") String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
