package src;
import java.util.HashMap;

public class SymbolTable {
    
    HashMap<String, HashMap<String, Integer>> classMembersType;
    Integer offset;
    

    public SymbolTable() {
        this.classMembersType = new HashMap<String, HashMap<String, Integer>>();
        this.offset = 0;
    }

}