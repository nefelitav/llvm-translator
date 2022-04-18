package src;
import java.util.*;

public class SymbolTable {
    LinkedHashMap<String, Class> table;
    public Integer fieldOffset;
    public Integer methodOffset;
    public Integer fieldOffsetNext;
    public Integer methodOffsetNext;

    public SymbolTable() {
        this.table = new LinkedHashMap<String, Class>();
        this.fieldOffset = 0;
        this.methodOffset = 0;
        this.fieldOffsetNext = 0;
        this.methodOffsetNext = 0;
    }

    public void addClass(String className, Class classInstance) {
        this.table.put(className, classInstance);
    }

    public Class getClass(String className) {
        return this.table.get(className);
    }

    public void printTable() {
        int i = 0;
        for (Map.Entry<String, Class> entry : this.table.entrySet()) {
            String className = entry.getKey();
            Class classInstance = entry.getValue();
            if (i != 0) {
                System.out.println("------ Class " + className + " ------");
                System.out.println("--- Variables ---");
            }
            for (Map.Entry<String, Variable> field : classInstance.fields.entrySet()) {
                String fieldName = field.getKey();
                Variable fieldInstance = field.getValue();
                Integer fieldOffset = fieldInstance.offset;
                System.out.println(className + "." + fieldName + " : " + fieldOffset);
            }
            if (i != 0) {
                System.out.println("--- Methods ---");
            }
            for (Map.Entry<String, Method> method : classInstance.methods.entrySet()) {
                String methodName = method.getKey();
                Method methodInstance = method.getValue();
                if (classInstance.extending != null && classInstance.extending.methods.get(methodName) != null) {
                    continue;
                } 
                Integer methodOffset = methodInstance.offset;
                System.out.println(className + "." + methodName + " : " + methodOffset);
            }
            i = i + 1;
        }
    }

}