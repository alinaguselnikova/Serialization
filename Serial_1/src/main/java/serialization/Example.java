package serialization;

import java.util.Objects;

public class Example {
    private final String name;
    private final Integer age;
    private Example relation;

    public Example(String name, Integer age) {
        this.relation = null;
        this.age = age;
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final Example other = (Example) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }

        return this.age.equals(other.age);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + age;
        return hashCode;
    }

    public Example getRelation() {
        return relation;
    }

    public void setRelation(Example relation) {
        this.relation = relation;
    }

    public Integer getAge() {
        return age;
    }
}

