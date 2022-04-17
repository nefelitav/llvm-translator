package src;
import java.util.*;

public class SymbolTable {
    TreeMap<String, Class> table;

    public SymbolTable() {
        this.table = new TreeMap<String, Class>();
    }

    public void addClass(String className, Class classInstance) {
        this.table.put(className, classInstance);
    }

    public Class getClass(String className) {
        return this.table.get(className);
    }

    public void printTable() {
        for (Map.Entry<String, Class> entry : this.table.entrySet()) {
            String className = entry.getKey();
            Class classInstance = entry.getValue();
            for (Map.Entry<String, Variable> field : classInstance.fields.entrySet()) {

                String fieldName = field.getKey();
                Variable fieldInstance = field.getValue();
                Integer fieldOffset = fieldInstance.offset;
                System.out.println(className + "." + fieldName + " : " + fieldOffset);
            }

            for (Map.Entry<String, Method> method : classInstance.methods.entrySet()) {
                String methodName = method.getKey();
                Method methodInstance = method.getValue();
                Integer methodOffset = methodInstance.offset;
                System.out.println(className + "." + methodName + " : " + methodOffset);
            }

        }
    }

}