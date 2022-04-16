package src;
import java.util.ArrayList;

public class Class {
    public String name;
    ArrayList<Method> methods;
    ArrayList<String> fields;
    public Class extending;
    public Integer offset;
    
    public Class(String name) {
        this.name = name;
        this.extending = null;
        this.methods = new ArrayList<Method>();
        this.fields = new ArrayList<String>();
        this.offset = 0;
    }

}