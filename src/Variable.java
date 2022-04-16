package src;
import java.util.ArrayList;

public class Variable {
    public String name;
    public String type;
    public Method variableOf;

    public Variable(String name, String type, Method variableOf) {
        this.name = name;
        this.type = type;
        this.variableOf = variableOf;
    }

}