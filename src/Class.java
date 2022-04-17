package src;
import java.util.TreeMap;

public class Class {
    public String name;
    TreeMap<String, Method> methods;
    TreeMap<String, Variable> fields;
    public Class extending;
    public Integer fieldOffset;
    public Integer methodOffset;
    public Integer fieldOffsetNext;
    public Integer methodOffsetNext;

    public Class(String name) {
        this.name = name;
        this.extending = null;
        this.methods = new TreeMap<String, Method>();
        this.fields = new TreeMap<String, Variable>();
        this.fieldOffset = 0;
        this.methodOffset = 0;
        this.fieldOffsetNext = 0;
        this.methodOffsetNext = 0;
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
        this.fieldOffset = this.fieldOffset + this.fieldOffsetNext; // increment to get current field's position
        field.offset = this.fieldOffset;                       
        this.fieldOffsetNext = offset;                              // next field's position
    }

    public void addMethod(String methodType, String methodName, String methodParams, SymbolTable table) throws Exception {
        if (this.methods.get(methodName) != null) {
            throw new Exception("Method is already declared");
        }
        Integer offset = 8;

        if (!methodType.equals("int") && !methodType.equals("boolean") && !methodType.equals("int[]") && !methodType.equals("boolean[]") && table.getClass(methodType) == null && !methodType.equals(this.name)) {
            throw new Exception("Not a valid type");
        }
        Method method = new Method(methodName, methodType, methodParams);                // create Method
        this.methods.put(methodName, method);                          // link this variable to this class
        if (this.extending != null && this.extending.methods.get(methodName) != null) {
            if (this.extending.methods.get(methodName).returnType != methodType) {
                throw new Exception("Method cant have different return type from the parent one.");
            }
            if (!(this.extending.methods.get(methodName).parameters).equals(method.parameters)) {
                throw new Exception("Method cant have different parameters.");
            }
        } else {
            this.methodOffset = this.methodOffset + this.methodOffsetNext; // increment to get current method's position
            method.offset = this.methodOffset;                       
            this.methodOffsetNext = offset;                              // next method's position
        }
    }
}