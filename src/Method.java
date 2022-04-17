package src;
import java.util.TreeMap;

public class Method {
    public String name;
    public String returnType;
    public Integer offset;
    TreeMap<String, Variable> variables;
    TreeMap<String, Variable> parameters;

    public Method(String name, String type, String params) {
        this.name = name;
        this.returnType = type;
        this.variables = new TreeMap<String, Variable>();
        if (!params.equals("null")) {
            for (String param : params.split(",")) {
                this.variables.put(param.split(" ")[1] , new Variable(param.split(" ")[0], param.split(" ")[1]));
            }
        } 
        this.parameters = new TreeMap<String, Variable>();
        this.offset = 0;
    }

}