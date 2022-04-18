package src;
import java.util.LinkedHashMap;

public class Method {
    public String name;
    public String returnType;
    public Integer offset;
    LinkedHashMap<String, Variable> variables;
    LinkedHashMap<String, Variable> parameters;

    public Method(String name, String type, String params, String vars) throws Exception {
        this.name = name;
        this.returnType = type;
        this.parameters = new LinkedHashMap<String, Variable>();
        if (!params.equals("null") && !params.isEmpty()) {
            for (String param : params.split(",")) {
                if (this.parameters.get(param.split(" ")[1]) != null) {
                    throw new Exception("Parameter is already declared");
                }
                this.parameters.put(param.split(" ")[1] , new Variable(param.split(" ")[1], param.split(" ")[0]));
            }
        } 
        this.variables = new LinkedHashMap<String, Variable>();
        if (!vars.equals("null") && !vars.isEmpty()) {
            for (String var : vars.split(",")) {
                if (this.variables.get(var.split(" ")[1]) != null) {
                    throw new Exception("Local variable is already declared");
                }
                if (this.parameters.get(var.split(" ")[1]) != null) {
                    throw new Exception("Local variable has same name with formal parameter");
                }

                this.variables.put(var.split(" ")[1] , new Variable(var.split(" ")[1], var.split(" ")[0]));
            }
        } 
        this.offset = 0;
    }

}