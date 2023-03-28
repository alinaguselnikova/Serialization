package root.main;

import java.util.ArrayList;
import java.util.Objects;

public class Example {
    private final String name;
    private final Integer age;
    private final Boolean isTrue;

    private ArrayList<Example> list;
    private Example relation;

    public Example(String name, Integer age, Boolean isTrue) {
        this.relation = null;
        this.age = age;
        this.name = name;
        this.isTrue = isTrue;
        this.list = null;
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
        if (!Objects.equals(this.isTrue, other.isTrue)) {
            return false;
        }
        if (!Objects.equals(this.list, other.list)) {
            return false;
        }
        if (!Objects.equals(this.relation, other.relation)) {
            return false;
        }
        return this.age.equals(other.age);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + age;
        hashCode = 31 * hashCode + isTrue.hashCode();
//        hashCode = 31 * hashCode + list.hashCode();
//        hashCode = 31 * hashCode + relation.hashCode();
        return hashCode;
    }

    public void setRelation(Example relation) {
        this.relation = relation;
    }

    public void setArrayList(ArrayList<Example> list){
        this.list = list;
    }

}
