package src;
import java.util.*;
import java.util.Map.*;

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
                offset = 8;
        }

        Variable field = new Variable(fieldName, fieldType);            // create variable
        this.fields.put(fieldName, field);                          // link this variable to this class

        if ((this.fields).size() == 1 && this.extending == null) {
            table.fieldOffset = 0; // increment to get current field's position
            field.offset = 0;                       
            table.fieldOffsetNext = offset;                              // next field's position
        } else {
            table.fieldOffset = table.fieldOffset + table.fieldOffsetNext; // increment to get current field's position
            field.offset = table.fieldOffset;                       
            table.fieldOffsetNext = offset;                              // next field's position
        }
    }

    public void addMethod(String methodType, String methodName, String methodParams, String methodVars, SymbolTable table) throws Exception {
        
        if (this.methods.get(methodName) != null) {
            throw new Exception("Method is already declared");
        }
        Integer offset = 8;

        Method method = new Method(methodName, methodType, methodParams, methodVars);                // create Method
        this.methods.put(methodName, method);                          // link this method to this class
        if (methodName.equals("main")) {
            return;
        }
        if (this.extending != null && this.extending.methods.get(methodName) != null) {
            if (!methodType.equals(this.extending.methods.get(methodName).returnType)) {
                throw new Exception("Method cant have different return type from the parent one.");
            }

            Set entrySet = (this.extending.methods.get(methodName).parameters).entrySet();
            Iterator it = entrySet.iterator();
            Set entrySet2 = (method.parameters).entrySet();
            Iterator it2 = entrySet2.iterator();

            while (it.hasNext() || it2.hasNext()) {
                Map.Entry<String, Variable> entry = (Map.Entry<String, Variable>) it.next();
                Map.Entry<String, Variable> entry2 = (Map.Entry<String, Variable>) it2.next();

                String key = (String)entry.getKey();
                Variable value = (Variable)entry.getValue();
                String key2 = (String)entry2.getKey();
                Variable value2 = (Variable)entry2.getValue();

                // System.out.println(key + " " + key2);
                // System.out.println(value.type + " " + value2.type);

                if (!key.equals(key2) || !(value.type).equals(value2.type)) {
                    throw new Exception("Method cant have different parameters from the parent one.");
                }

            }

        } else {
            if ((this.methods).size() == 1) { // first method of class
                if (this.extending != null && this.extending.methods.get(methodName) == null) {
                    table.methodOffset = table.methodOffset + table.methodOffsetNext; // increment to get current method's position
                    method.offset = table.methodOffset;                       
                    table.methodOffsetNext = offset;                              // next method's position
                } else {
                    table.methodOffset = 0; 
                    method.offset = 0;                       
                    table.methodOffsetNext = offset;                              // next method's position
                }  
            } else {
                table.methodOffset = table.methodOffset + table.methodOffsetNext; // increment to get current method's position
                method.offset = table.methodOffset;                       
                table.methodOffsetNext = offset;                              // next method's position
            }
            
        }
    }
}