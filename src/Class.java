package src;
import java.util.*;

public class Class {
    public String name;
    LinkedHashMap<String, Method> methods;
    LinkedHashMap<String, Variable> fields;
    public Class extending;

    public Class(String name) {
        this.name = name;
        this.extending = null;
        this.methods = new LinkedHashMap<String, Method>();
        this.fields = new LinkedHashMap<String, Variable>();
    }

    public void addField(String fieldType, String fieldName, SymbolTable table) throws Exception {
        if (this.fields.get(fieldName) != null) {
            throw new Exception("Field is already declared");
        }
        Integer offset = 0;
        switch (fieldType) {
            case "int":
                offset = 4;
                break;
            case "boolean":
                offset = 1;
                break;
            case "int[]":
                offset = 8;
                break;
            case "boolean[]":
                offset = 8;
                break;
            default:
                if (table.getClass(fieldType) != null || fieldType.equals(this.name)) { // maybe its a pointer to a previously declared class or to the same class
                    offset = 8;
                } else {
                    throw new Exception("Not a valid type");
                }
        }

        Variable field = new Variable(fieldName, fieldType);            // create variable
        this.fields.put(fieldName, field);                          // link this variable to this class
        table.fieldOffset = table.fieldOffset + table.fieldOffsetNext; // increment to get current field's position
        field.offset = table.fieldOffset;                       
        table.fieldOffsetNext = offset;                              // next field's position
    }

    public void addMethod(String methodType, String methodName, String methodParams, String methodVars, SymbolTable table) throws Exception {
        
        if (this.methods.get(methodName) != null) {
            throw new Exception("Method is already declared");
        }
        Integer offset = 8;

        if (!methodType.equals("int") && !methodType.equals("boolean") && !methodType.equals("int[]") && !methodType.equals("boolean[]") && table.getClass(methodType) == null && !methodType.equals(this.name)) {
            if (!(methodType.equals("void") && methodName.equals("main")))
            {
                throw new Exception("Not a valid type");
            }
        }
        Method method = new Method(methodName, methodType, methodParams, methodVars);                // create Method
        this.methods.put(methodName, method);                          // link this method to this class
        if (methodName.equals("main")) {
            return;
        }
        if (this.extending != null && this.extending.methods.get(methodName) != null) {
            if (!methodType.equals(this.extending.methods.get(methodName).returnType)) {
                throw new Exception("Method cant have different return type from the parent one.");
            }

            for (Map.Entry<String, Variable> entry : (this.extending.methods.get(methodName).parameters).entrySet()) {
                String key = entry.getKey();
                Variable value = entry.getValue();
                for (Map.Entry<String, Variable> entry2 : (method.parameters).entrySet()) {
                    String key2 = entry2.getKey();
                    Variable value2 = entry2.getValue();
                    if (!((value.type).equals(value2.type))) {
                        throw new Exception("Method cant have different parameters from the parent one.");
                    }
                }
            }
  
        } else {
            table.methodOffset = table.methodOffset + table.methodOffsetNext; // increment to get current method's position
            method.offset = table.methodOffset;                       
            table.methodOffsetNext = offset;                              // next method's position
        }
    }
}