package src;

public class Variable {
    public String name;
    public String type;
    public Integer offset;


    public Variable(String name, String type) {
        this.name = name;
        this.type = type;
        this.offset = 0;
    }

}