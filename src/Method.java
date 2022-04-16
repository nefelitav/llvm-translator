package src;
import java.util.ArrayList;

public class Method {
    public String name;
    public String returnType;
    public Class methodOf;
    ArrayList<Variable> variables;
    ArrayList<Variable> parameters;
    
    public Method(String name, String type, Class methodOf) {
        this.name = name;
        this.returnType = type;
        this.methodOf = methodOf;
        this.variables = new ArrayList<Variable>();
        this.parameters = new ArrayList<Variable>();
    }

}