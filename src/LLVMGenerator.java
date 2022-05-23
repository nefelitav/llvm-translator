package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class LLVMGenerator extends GJDepthFirst<String, Class> {
    SymbolTable table;
    String LLVMCode;
    Integer register;
    Integer label = 0;
    Method currMethod;
    String LLVMCodeAccumulation = "";
    String result = "";
    String lastAllocation = "";
    Integer bitcast = 0;

    public void printResult() {
        System.out.println(this.result);
    }
    
    public String getResult() {
        return this.result;
    }
    
    public String typeInLLVM(String typeInMinijava) {
        switch(typeInMinijava) {
            case "int":
                return "i32";
            case "int[]":
                return "i32*";
            case "boolean":
                return "i1";
            case "boolean[]":
                return "i1*";
            default:
                return "i8*";
        }
    }

    public Integer fieldSize(String fieldType) {
        switch(fieldType) {
            case "int":
                return 4;
            case "boolean":
                return 1;
            default:
                return 8;
        }
    }

    public Integer objectSize(String object) {
        Variable lastField = null;

        for (Map.Entry<String, Variable> entry : this.table.getClass(object).fields.entrySet()) {
            if (lastField == null || entry.getValue().offset.compareTo(lastField.offset) > 0) {
                lastField = entry.getValue();
            }
        }
        Class parentClass = this.table.getClass(object).extending;
        while (parentClass != null && lastField == null) {
            for (Map.Entry<String, Variable> entry : parentClass.fields.entrySet()) {
                if (lastField == null || entry.getValue().offset.compareTo(lastField.offset) > 0) {
                    lastField = entry.getValue();
                }
            }
        }
        if (lastField == null) {
            return 8;
        }
        return lastField.offset + fieldSize(lastField.type) + 8;
    }

    public String isVar(String possibleVar, Class currClass) {
        if (this.currMethod.variables.get(possibleVar) != null) { // first check if local variable, then field
            return this.currMethod.variables.get(possibleVar).type;
        } else if (this.currMethod.parameters.get(possibleVar) != null) {
            return this.currMethod.parameters.get(possibleVar).type;
        } else if (currClass.fields.get(possibleVar) != null) {
            return currClass.fields.get(possibleVar).type;
        } 
        if (currClass.extending != null) {
            Class temp = currClass.extending;
            while(temp != null) {
                if (temp.fields.get(possibleVar) != null) {
                    return temp.fields.get(possibleVar).type;
                }
                temp = temp.extending;
            }
        }
        return null;
    }

    public String Accumulator(String input, Class currClass, String side) {
        if (input.equals("true")) {
            return "1";
        }
        if (input.equals("false")) {
            return "0";
        }
        if (input.chars().allMatch( Character::isDigit )) {
            return input.toString();
        }
        if (currClass.fields.get(input) != null) {
            checkRegisterNumber();
            String fieldType = currClass.fields.get(input).type;
            LLVMCodeAccumulation +=	"\n\t%_" + (this.register)  + " = getelementptr i8, i8* %this, i32 " + (currClass.fields.get(input).offset + 8) + "\n";
            LLVMCodeAccumulation += "\t%_" + (this.register+1) + " = bitcast i8* %_" + (this.register) + " to " + typeInLLVM(fieldType) +"*\n";
            if (side == "right") {
                LLVMCodeAccumulation += "\t%_" + (this.register+2) + " = load " + typeInLLVM(fieldType) +", " + typeInLLVM(fieldType) +"* %_" + (this.register+1) + "\n";
                this.register += 2;
            } else {
                this.register += 1;
            }
            return "%_" + this.register;
        }

        if (currClass.extending != null) {
            Class temp = currClass.extending;
            while(temp != null) {
                if (temp.fields.get(input) != null) {
                    String fieldType = temp.fields.get(input).type;
                    checkRegisterNumber();
                    LLVMCodeAccumulation +=	"\n\t%_" + (this.register)  + " = getelementptr i8, i8* %this, i32 " + (temp.fields.get(input).offset + 8) + "\n"; // offset
                    LLVMCodeAccumulation += "\t%_" + (this.register+1) + " = bitcast i8* %_" + (this.register) + " to " + typeInLLVM(fieldType) +"*\n";
                    if (side == "right") {
                        LLVMCodeAccumulation += "\t%_" + (this.register+2) + " = load " + typeInLLVM(fieldType) +", " + typeInLLVM(fieldType) +"* %_" + (this.register+1) + "\n";
                        this.register += 2;
                    } else {
                        this.register += 1;
                    }
                    return "%_" + this.register;
                }
                temp = temp.extending;
            }
        }
        String type;
        if ((type = isVar(input, currClass)) != null) {
            if (side == "right") {
                checkRegisterNumber();
                LLVMCodeAccumulation += "\t%_" + this.register + " = load " + typeInLLVM(type) + ", " + typeInLLVM(type) + "* %" + input + "\n";
                return "%_" + this.register;
            } else {
                return "%" + input;
            }
        }
        return input;
    }

    public void checkRegisterNumber() {
        if ((this.LLVMCodeAccumulation).contains("%_" + this.register + " ")) {
            this.register++;
        }
    }

    public Integer getMethodsNumber(String className) {
        Set<String> methodNames = this.table.getClass(className).methods.keySet();
        Integer methodNum = this.table.getClass(className).methods.size();
        Integer parentMethodNum = 0;
        if (this.table.getClass(className).extending != null) {
            Set<String> parentMethodNames = this.table.getClass(className).extending.methods.keySet();
            for (String methodName : parentMethodNames) {
                if (methodNames.contains(methodName)) {
                    continue;
                }
                parentMethodNum++;
            }
        }
        Integer allMethodsNum = parentMethodNum + methodNum;
        return allMethodsNum;
    }

    public LLVMGenerator(SymbolTable t) {
        this.table = t;
        this.LLVMCode = "";
        this.register = 0;
        this.label = 0;
        this.currMethod = null;
        Set<String> classNames = t.table.keySet();
        outerloop : for (String className : classNames) {
            Set<String> methodNames = t.table.get(className).methods.keySet();
            Integer methodNum = t.table.get(className).methods.size();
            Integer parentMethodNum = 0;
            if (t.table.get(className).extending != null) {
                Set<String> parentMethodNames = t.table.get(className).extending.methods.keySet();
                for (String methodName : parentMethodNames) {
                    if (methodNames.contains(methodName)) {
                        continue;
                    }
                    parentMethodNum++;
                }
            }
            Integer allMethodsNum = parentMethodNum + methodNum;
            if (methodNames.contains("main")) {
                this.LLVMCode += "@." + className + "_vtable = global [0 x i8*] [";
            } else {
                this.LLVMCode += "@." + className + "_vtable = global [" + allMethodsNum + " x i8*] [";
            }

            Integer i = 0;
            for (String methodName : methodNames) {
                if (methodName == "main") {
                    this.LLVMCode += "]\n\n";
                    continue outerloop;
                }
                if (i == 0) {
                    this.LLVMCode += "\n";
                }
                this.LLVMCode += "\ti8* bitcast ("+typeInLLVM(t.table.get(className).methods.get(methodName).returnType)+" (i8*"; 
                Set<String> methodParams = t.table.get(className).methods.get(methodName).parameters.keySet();
                for (String methodParam : methodParams) {
                    this.LLVMCode += ", " + typeInLLVM(t.table.get(className).methods.get(methodName).parameters.get(methodParam).type);
                }
                
                this.LLVMCode += ")* @" + className + "." + methodName + " to i8*)";
                i++;
                if (i < methodNum) {
                    this.LLVMCode += ",\n";
                }
            }
            int j = 0;
            if (t.table.get(className).extending != null) {
                Set<String> parentMethodNames = t.table.get(className).extending.methods.keySet();
                for (String methodName : parentMethodNames) {
                    if (methodNames.contains(methodName)) {
                        continue;
                    }
                    if (i > 0) {
                        this.LLVMCode += ",";
                    }
                    if (j == 0) {
                        this.LLVMCode += "\n";
                    }

                    this.LLVMCode += "\ti8* bitcast (i32 (i8*"; 
                    Set<String> methodParams = t.table.get(className).extending.methods.get(methodName).parameters.keySet();
                    for (String methodParam : methodParams) {
                        this.LLVMCode += ", " + typeInLLVM(t.table.get(className).extending.methods.get(methodName).parameters.get(methodParam).type);
                    }
                    this.LLVMCode += ")* @" + t.table.get(className).extending.name + "." + methodName + " to i8*)";
                    j++;
                    if (j < parentMethodNum) {
                        this.LLVMCode += ",\n";
                    }
                }
            }
            this.LLVMCode += "\n]\n\n";
        }

        this.LLVMCode += "declare i8* @calloc(i32, i32)\n";
        this.LLVMCode += "declare i32 @printf(i8*, ...)\n";
        this.LLVMCode += "declare void @exit(i32)\n\n";
        this.LLVMCode += "@_cint = constant [4 x i8] c" + "\"%d\\0a\\00\"" + "\n";
        this.LLVMCode += "@_cOOB = constant [15 x i8] c" + "\"Out of bounds\\0a\\00\"" + "\n";
        this.LLVMCode += "@_cNSZ = constant [15 x i8] c" + "\"Negative size\\0a\\00\"" + "\n";
        this.LLVMCode += "\ndefine void @print_int(i32 %i) {\n\t%_str = bitcast [4 x i8]* @_cint to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n\tret void\n}\n\ndefine void @throw_oob() {\n\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n\ndefine void @throw_nsz() {\n\t%_str = bitcast [15 x i8]* @_cNSZ to i8*\n\tcall i32 (i8*, ...) @printf(i8* %_str)\n\tcall void @exit(i32 1)\n\tret void\n}\n";
        this.result += this.LLVMCode;
    }


    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> "public"
    * f4 -> "static"
    * f5 -> "void"
    * f6 -> "main"
    * f7 -> "("
    * f8 -> "String"
    * f9 -> "["
    * f10 -> "]"
    * f11 -> Identifier()
    * f12 -> ")"
    * f13 -> "{"
    * f14 -> ( VarDeclaration() )*
    * f15 -> ( Statement() )*
    * f16 -> "}"
    * f17 -> "}"
    */
   public String visit(MainClass n, Class argu) throws Exception {
        LLVMCodeAccumulation += "define i32 @main() {\n";
        String className = n.f1.accept(this, argu);
        Class classInstance = this.table.getClass(className);
        this.currMethod = classInstance.methods.get("main");
        for (int i = 0; i < n.f14.size(); i++) {
          n.f14.elementAt(i).accept(this, classInstance);
        }
        for (int i = 0; i < n.f15.size(); i++) {
          n.f15.elementAt(i).accept(this, classInstance);
        }
        LLVMCodeAccumulation += "\tret i32 0\n";
        LLVMCodeAccumulation += "\n}\n\n";
        this.result += this.LLVMCodeAccumulation;
        return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
   public String visit(ClassDeclaration n, Class argu) throws Exception {
      String className = n.f1.accept(this, argu);
      Class classInstance = this.table.getClass(className);
      for (int i = 0; i < n.f4.size(); i++) {
        n.f4.elementAt(i).accept(this, classInstance);
      }
      return null;
   }

   /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "extends"
    * f3 -> Identifier()
    * f4 -> "{"
    * f5 -> ( VarDeclaration() )*
    * f6 -> ( MethodDeclaration() )*
    * f7 -> "}"
    */
   public String visit(ClassExtendsDeclaration n, Class argu) throws Exception {
        String parent = n.f3.accept(this, argu);
        String className = n.f1.accept(this, argu);
        Class classInstance = this.table.getClass(className);
        for (int i = 0; i < n.f6.size(); i++) {
            n.f6.elementAt(i).accept(this, classInstance);
        }
        return null;
   }


   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Class argu) throws Exception {
        String fieldType = n.f0.accept(this, argu);
        String fieldName = n.f1.accept(this, argu);
        LLVMCodeAccumulation += "\t%"+ fieldName + " = alloca " + typeInLLVM(fieldType) + "\n";
        return null;
   }

   /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
   public String visit(MethodDeclaration n, Class argu) throws Exception {
        this.register = 0;
        this.LLVMCodeAccumulation = "";
        String methodType = n.f1.accept(this, argu);
        String methodName = n.f2.accept(this, argu);
        this.currMethod = argu.methods.get(methodName);
        String methodParams = n.f4.accept(this, argu);
        String expr = n.f10.accept(this, argu);

        if (methodParams == null) {
            methodParams = "i8* %this";
        }
        LLVMCodeAccumulation += "define "+ typeInLLVM(methodType) +" @"+ argu.name + "." + methodName +"(" + methodParams + ") {\n";
       
        String[] paramsList = methodParams.split(",");
        Integer i = 0;
        for (String param : paramsList) {
            if (i == 0) {
                i++;
                continue;
            }
            LLVMCodeAccumulation += "\t%" + ((param.trim()).split(" ")[1]).split("\\.")[1] + " = alloca " +  (param.trim()).split(" ")[0] + "\n";
            LLVMCodeAccumulation += "\tstore " + (param.trim()).split(" ")[0] + " %." + ((param.trim()).split(" ")[1]).split("\\.")[1] + ", " +  (param.trim()).split(" ")[0] + "* " + "%" + ((param.trim()).split(" ")[1]).split("\\.")[1] + "\n";
            i++;
        }
        
        for (i = 0; i < n.f7.size(); i++) {
           n.f7.elementAt(i).accept(this, argu);
        }
        for (i = 0; i < n.f8.size(); i++) {
           n.f8.elementAt(i).accept(this, argu);
        }

        LLVMCodeAccumulation += "\n";
        String exprToLLVM = Accumulator(expr, argu, "right");
        LLVMCodeAccumulation += "\n\tret " + typeInLLVM(methodType) + " " + exprToLLVM + "\n";
        LLVMCodeAccumulation += "\n}\n\n";
        this.result += this.LLVMCodeAccumulation;
        return null;
   }
    
   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, Class argu) throws Exception {
        String formalParam = n.f0.accept(this, argu);
        String formalParamTail = n.f1.accept(this, argu);

        String code = "";
        if (formalParam != null) {
            if (formalParamTail != null) {
                code += "i8* %this, " + formalParam + formalParamTail;
            } else {
                code += "i8* %this, " + formalParam;
            }
        } 
        return code;
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, Class argu) throws Exception {
        String ret = "";
        for (Node node : n.f0.nodes) {
            ret += node.accept(this, argu);
        }
        return ret;
   }


   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, Class argu) throws Exception {
        return "," + n.f1.accept(this, argu);
   }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, Class argu) throws Exception {
        String paramType = n.f0.accept(this, argu);
        String paramName = n.f1.accept(this, argu);
        return typeInLLVM(paramType) + " %." + paramName;
   }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public String visit(Type n, Class argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, Class argu) throws Exception {
       return "boolean[]";
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(IntegerArrayType n, Class argu) throws Exception {
        return "int[]";
   }

   /**
    * f0 -> "boolean"
    */
   public String visit(BooleanType n, Class argu) throws Exception {
        return "boolean";
   }

   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, Class argu) throws Exception {
        return "int";
   }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, Class argu) throws Exception {
        return n.f0.toString();
    }


   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public String visit(AllocationExpression n, Class argu) throws Exception {
        String type = n.f1.accept(this, argu);
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = call " + typeInLLVM(type) + " @calloc(i32 1, i32 " + objectSize(type) + ")\n";
        LLVMCodeAccumulation += "\t%_" + (this.register + 1) + " = bitcast i8* %_" + (this.register) + " to i8***\n";
        LLVMCodeAccumulation += "\t%_" + (this.register + 2) + " = getelementptr ["+ getMethodsNumber(type) +" x i8*], ["+getMethodsNumber(type)+" x i8*]* @." + type + "_vtable, i32 0, i32 0\n";
        LLVMCodeAccumulation += "\tstore i8** %_" + (this.register + 2) + ", i8*** %_" + (this.register + 1) + "\n";
        this.register += 3;
        this.lastAllocation = type;
		return "%_" + (this.register - 3);
   }

   /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Class argu) throws Exception {
        String identifier = n.f0.accept(this, argu);
        String expr = n.f2.accept(this, argu);
        String exprToLLVM = Accumulator(expr, argu, "right"); 
        String identifierToLLVM = Accumulator(identifier, argu, "left"); 
        LLVMCodeAccumulation += "\tstore " + typeInLLVM(isVar(identifier, argu)) + " " + exprToLLVM + ", " + typeInLLVM(isVar(identifier, argu)) +"* " + identifierToLLVM + "\n";
        return null;
   }

   /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
   public String visit(ArrayAssignmentStatement n, Class argu) throws Exception {
        String identifier = n.f0.accept(this, argu);
        String expr = n.f2.accept(this, argu);
        String expr2 = n.f5.accept(this, argu);
        String identifierToLLVM = Accumulator(identifier, argu, "right"); 
        String exprToLLVM = Accumulator(expr, argu, "right"); 
        String expr2ToLLVM = Accumulator(expr2, argu, "right"); 
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_"+(++this.register)+" = load i32, i32* "+(identifierToLLVM)+"\n";
        LLVMCodeAccumulation += "\t%_"+(++this.register)+" = icmp sge i32 "+exprToLLVM+", 0\n";
        LLVMCodeAccumulation += "\t%_"+(++this.register)+" = icmp slt i32 "+exprToLLVM+", %_"+(this.register-2)+"\n";
        LLVMCodeAccumulation += "\t%_"+(++this.register)+" = and i1 %_"+(this.register-2)+", %_"+(this.register-1)+"\n";
        LLVMCodeAccumulation += "\tbr i1 %_"+(this.register)+", label %oob_ok_"+this.label+", label %oob_err_"+this.label+"\n";
        LLVMCodeAccumulation += "\n\toob_err_"+this.label+":\n";
        LLVMCodeAccumulation += "\tcall void @throw_oob()\n";
        LLVMCodeAccumulation += "\tbr label %oob_ok_"+this.label+"\n";
        LLVMCodeAccumulation += "\n\toob_ok_"+this.label+":\n";
        LLVMCodeAccumulation += "\t%_"+(++this.register)+" = add i32 1, "+exprToLLVM+"\n";
        LLVMCodeAccumulation += "\t%_"+(++this.register)+" = getelementptr i32, i32* "+(identifierToLLVM)+", i32 %_"+(this.register-1)+"\n";
        LLVMCodeAccumulation += "\tstore i32 "+expr2ToLLVM+", i32* %_"+(this.register)+"\n";
        this.label++;
        return null;
   }

   /**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
   public String visit(IfStatement n, Class argu) throws Exception {
        String expr = n.f2.accept(this, argu);
        String exprToLLVM = Accumulator(expr, argu, "right"); 
        Integer currLabel = this.label;

        LLVMCodeAccumulation += "\tbr i1 " + exprToLLVM + ", label %if_then_" + currLabel + ", label %if_else_" + currLabel + "\n\n";

        LLVMCodeAccumulation += "\n\tif_then_" + currLabel + ":\n";
        String stmt = n.f4.accept(this, argu);
        LLVMCodeAccumulation += "\tbr label %if_end_" + currLabel + "\n";

        LLVMCodeAccumulation += "\n\tif_else_" + currLabel + ":\n";
        String elseStmt = n.f6.accept(this, argu);
        LLVMCodeAccumulation += "\tbr label %if_end_" + currLabel + "\n";

        LLVMCodeAccumulation += "\n\tif_end_" + currLabel + ":\n";
        this.label++;
        return null;
   }

   /**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
   public String visit(WhileStatement n, Class argu) throws Exception {
        LLVMCodeAccumulation += "\tbr label %loopstart" + (this.label) + "\n";
        LLVMCodeAccumulation += "\n\tloopstart" + (this.label) + ":\n";
        Integer currLabel = this.label;
        String expr = n.f2.accept(this, argu);
        String exprToLLVM = Accumulator(expr, argu, "right"); 
        LLVMCodeAccumulation += "\tbr i1 " + exprToLLVM + ", label %next"+ currLabel +", label %end"+ currLabel + "\n";
        LLVMCodeAccumulation += "\n\tnext" + currLabel + ":\n";
        n.f4.accept(this, argu);
        LLVMCodeAccumulation += "\tbr label %loopstart" + currLabel + "\n\n";
        LLVMCodeAccumulation += "\n\tend" + currLabel + ":\n";
        this.label++;
        return LLVMCodeAccumulation;
   }

   /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
   public String visit(PrintStatement n, Class argu) throws Exception {
        String expr = n.f2.accept(this, argu);
        String exprToLLVM = Accumulator(expr, argu, "right"); 
        LLVMCodeAccumulation +=  "\tcall void (i32) @print_int(i32 " + exprToLLVM + ")\n";
        this.register++;
        return null;
   }

   /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
   public String visit(AndExpression n, Class argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        String firstToLLVM = Accumulator(first, argu, "right");
        Integer currLabel = this.label;

        LLVMCodeAccumulation += "\tbr i1 " + firstToLLVM + ", label %exp_res_" + (currLabel+1) + ", label %exp_res_" + currLabel + "\n";
        LLVMCodeAccumulation += "\n\texp_res_" + currLabel + ":\n";
        LLVMCodeAccumulation += "\tbr label %exp_res_" + (currLabel+3) + "\n";
        LLVMCodeAccumulation += "\n\texp_res_" + (currLabel+1) + ":\n";
        this.register++;
        String secondToLLVM = Accumulator(second, argu, "right");
        LLVMCodeAccumulation += "\tbr label %exp_res_" + (currLabel+2) + "\n";
        LLVMCodeAccumulation += "\n\texp_res_" + (currLabel+2) + ":\n";
        LLVMCodeAccumulation += "\tbr label %exp_res_" + (currLabel+3) + "\n";
        LLVMCodeAccumulation += "\n\texp_res_" + (currLabel+3) + ":\n";
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = phi i1  [ 0, %exp_res_" + currLabel + " ], [ " + secondToLLVM + ", %exp_res_" + (currLabel+2) + " ]\n";
        this.register++;
        this.label += 3;
        return "%_" + (this.register-1);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public String visit(CompareExpression n, Class argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        String firstToLLVM = Accumulator(first, argu, "right");
        String secondToLLVM = Accumulator(second, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = icmp slt i32 " + firstToLLVM + ", " + secondToLLVM + "\n";
        return "%_" + this.register;

   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public String visit(PlusExpression n, Class argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        String firstToLLVM = Accumulator(first, argu, "right");
        String secondToLLVM = Accumulator(second, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = add i32 " + firstToLLVM + ", " + secondToLLVM + "\n";
        return "%_" + this.register;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public String visit(MinusExpression n, Class argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        String firstToLLVM = Accumulator(first, argu, "right");
        String secondToLLVM = Accumulator(second, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = sub i32 " + firstToLLVM + ", " + secondToLLVM + "\n";
        return "%_" + this.register;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public String visit(TimesExpression n, Class argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        String firstToLLVM = Accumulator(first, argu, "right");
        String secondToLLVM = Accumulator(second, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = mul i32 " + firstToLLVM + ", " + secondToLLVM + "\n";
        return "%_" + this.register;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public String visit(ArrayLookup n, Class argu) throws Exception {
       String expr = n.f0.accept(this, argu);
       String expr2 = n.f2.accept(this, argu);
       String exprToLLVM = Accumulator(expr, argu, "right");
       String expr2ToLLVM = Accumulator(expr2, argu, "right");
       Integer currLabel = this.label;
       checkRegisterNumber();
       LLVMCodeAccumulation += "\t%_"+(this.register)+" = load i32, i32* "+exprToLLVM+"\n";
       LLVMCodeAccumulation += "\t%_"+(++this.register)+" = icmp sge i32 "+expr2ToLLVM+", 0\n";
       LLVMCodeAccumulation += "\t%_"+(++this.register)+" = icmp slt i32 "+expr2ToLLVM+", %_"+(this.register-2)+"\n";
       LLVMCodeAccumulation += "\t%_"+(++this.register)+" = and i1 %_"+(this.register-2)+", %_"+(this.register-1)+"\n";
       LLVMCodeAccumulation += "\tbr i1 %_"+(this.register)+", label %oob_ok_"+currLabel+", label %oob_err_"+currLabel+"\n";
       LLVMCodeAccumulation += "\n\toob_err_"+currLabel+":\n";
       LLVMCodeAccumulation += "\tcall void @throw_oob()\n";
       LLVMCodeAccumulation += "\tbr label %oob_ok_" + currLabel + "\n";

       LLVMCodeAccumulation += "\n\toob_ok_"+currLabel+":\n";
       LLVMCodeAccumulation += "\t%_"+(++this.register)+" = add i32 1, " + expr2ToLLVM + "\n";
       LLVMCodeAccumulation += "\t%_"+(++this.register)+" = getelementptr i32, i32* "+exprToLLVM+", i32 %_"+(this.register-1)+"\n";
       LLVMCodeAccumulation += "\t%_"+(++this.register)+" = load i32, i32* %_"+(this.register-1)+"\n";
       this.register++;
    return "%_" + (this.register-1);
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public String visit(ArrayLength n, Class argu) throws Exception {
        String expr = n.f0.accept(this, argu);
        String exprToLLVM = Accumulator(expr, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = load i32, i32* " + exprToLLVM + "\n";
        return "$_" + (this.register);

   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
   public String visit(MessageSend n, Class argu) throws Exception {
        String expr = n.f0.accept(this, argu);
        String exprToLLVM = Accumulator(expr, argu, "right");
        String method = n.f2.accept(this, argu);
        String args = n.f4.accept(this, argu);
        String argsTypes = "";
        String argsNames = "";

        Integer first = this.register;
        checkRegisterNumber();
        Integer second = this.register + 1;
        if (expr.equals(exprToLLVM)) {
            first = first - 3;
        }

        if (expr.equals("%this")) {
            this.lastAllocation = argu.name;
        }

        if ((this.table.getClass(this.lastAllocation).methods).get(method) == null) {
            if (argu.extending != null) {
                Class temp = argu.extending;
                while(temp != null) {
                    if ((temp.methods).get(method) != null) {
                        this.lastAllocation = temp.name;
                        break;
                    }
                    temp = temp.extending;
                }
            }
        }



        if (isVar(expr, argu) == null ) {
            LLVMCodeAccumulation += "\t%_" + (this.register) + " = bitcast i8* " + expr +" to i8***" + "\n";
        } else {
            LLVMCodeAccumulation += "\t%_" + (this.register) + " = bitcast i8* " + exprToLLVM +" to i8***" + "\n";
        }

        this.register++;
        Integer third = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = load i8**, i8*** %_" + (second-1) + "\n";
        this.register++;
        Integer fourth = this.register;
        

        if (isVar(expr, argu) != null) {
            this.lastAllocation = isVar(expr, argu);
        } 


        Method m = (this.table.getClass(this.lastAllocation).methods).get(method);
        LLVMCodeAccumulation += "\t------------%_" + (this.register) + " = getelementptr i8*, i8** %_" + third +", i32 " +  (m.offset) + "\n";
        this.register++;
        Integer fifth = this.register;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = load i8*, i8** %_" + fourth + "\n";
        this.register++;
        Integer sixth = this.register;
        Integer before = this.register;
        if (args != null) {
            m = (this.table.getClass(this.lastAllocation).methods).get(method);
            Set entrySet = m.parameters.entrySet();
            Iterator it = entrySet.iterator();
            List arguments = Arrays.asList(args.split(","));
            Iterator it2 = arguments.iterator();
            while (it.hasNext() || it2.hasNext()) {
                Map.Entry<String, Variable> entry = (Map.Entry<String, Variable>) it.next();
                String arg = (String) it2.next();
                Variable value = (Variable)entry.getValue();
                // System.out.println(typeInLLVM(value.type) + " " + arg);
                String argToLLVM = Accumulator(arg, argu, "right");
                checkRegisterNumber();
                argsTypes += ", " + typeInLLVM(value.type);
                argsNames += ", " + typeInLLVM(value.type) + " " + argToLLVM;
            }
        }
        Integer diff = this.register - before;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = bitcast i8* %_" + fifth + " to "+typeInLLVM(m.returnType)+" " + "(i8* " + argsTypes + ")*\n";
        this.bitcast = this.register;
        this.register++;
        Integer seventh = this.register - 6 - diff;
        if (expr.equals(exprToLLVM)) {
            seventh = seventh - 2;
        }
        if (expr.equals("%this")) {
            LLVMCodeAccumulation += "\t%_" + (this.register) + " = call "+ typeInLLVM(m.returnType) +" %_" + this.bitcast + "(i8* %this " + argsNames + ")\n";
        } else {
            LLVMCodeAccumulation += "\t%_" + (this.register) + " = call "+ typeInLLVM(m.returnType) +" %_" + this.bitcast + "(i8* " + exprToLLVM + " "+ argsNames + ")\n";
        }
        return "%_" + this.register;
   }

   /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
   public String visit(ExpressionList n, Class argu) throws Exception {
       return n.f0.accept(this, argu) + n.f1.accept(this, argu);
   }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
   public String visit(ExpressionTail n, Class argu) throws Exception {
        String tail = "";
        for (int i = 0; i < n.f0.size(); i++) {
            String param = n.f0.elementAt(i).accept(this, argu);
            tail = tail + param;
        }
        return tail;
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public String visit(ExpressionTerm n, Class argu) throws Exception {
       return "," + n.f1.accept(this, argu);
   }


   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Class argu) throws Exception {
      return n.f0.toString();
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Class argu) throws Exception {
      return n.f0.toString();
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, Class argu) throws Exception {
      return n.f0.toString();
   }


   /**
    * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(BooleanArrayAllocationExpression n, Class argu) throws Exception {
        String expr = n.f3.accept(this, argu); 
        String exprToLLVM = Accumulator(expr, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = add i32 1, " + exprToLLVM + "\n";
        this.register++;
        LLVMCodeAccumulation += "\t%_" + this.register + " = icmp sge i32 %_" + (this.register - 1) +", 1\n";
        LLVMCodeAccumulation += "\tbr i1 %_" + (this.register) + ", label %nsz_ok_"+ this.label+ ", label %nsz_err_"+ this.label+ "\n\n";
        LLVMCodeAccumulation += "\tnsz_err_"+ this.label+ ":\n";
        LLVMCodeAccumulation += "\tcall void @throw_nsz()\n";
        LLVMCodeAccumulation += "\tbr label %nsz_ok_"+ this.label+ "\n\n";
        LLVMCodeAccumulation += "\tnsz_ok_"+ this.label+ ":\n";
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = call i8*" + " @calloc(i32 %_" + (this.register-2) + ", i32 4)\n";
        LLVMCodeAccumulation += "\t%_" + (++this.register) + " = bitcast i8* %_" + (this.register-1) + " to " + typeInLLVM("boolean[]") + "\n";
        this.label++;
        return "%_" + (this.register);
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(IntegerArrayAllocationExpression n, Class argu) throws Exception {
        String expr = n.f3.accept(this, argu); 
        String exprToLLVM = Accumulator(expr, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = add i32 1, " + exprToLLVM + "\n";
        this.register++;
        LLVMCodeAccumulation += "\t%_" + this.register + " = icmp sge i32 %_" + (this.register - 1) +", 1\n";
        LLVMCodeAccumulation += "\tbr i1 %_" + (this.register) + ", label %nsz_ok_"+ this.label+ ", label %nsz_err_"+ this.label+ "\n\n";
        LLVMCodeAccumulation += "\tnsz_err_"+ this.label+ ":\n";
        LLVMCodeAccumulation += "\tcall void @throw_nsz()\n";
        LLVMCodeAccumulation += "\tbr label %nsz_ok_"+ this.label+ "\n\n";
        LLVMCodeAccumulation += "\tnsz_ok_"+ this.label+ ":\n";
        this.register++;
        LLVMCodeAccumulation += "\t%_" + (this.register) + " = call i8*"  + " @calloc(i32 %_" + (this.register-2) + ", i32 4)\n";
        LLVMCodeAccumulation += "\t%_" + (++this.register) + " = bitcast i8* %_" + (this.register-1) + " to " + typeInLLVM("int[]") + "\n";
        this.label++;
        return "%_" + (this.register);
   }



   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
   public String visit(NotExpression n, Class argu) throws Exception {
        String clause = n.f1.accept(this, argu);
        String clauseToLLVM = Accumulator(clause, argu, "right");
        checkRegisterNumber();
        LLVMCodeAccumulation += "\t%_"+ (this.register) +" = xor i1 1, " + clauseToLLVM + "\n";
        return "%_" + this.register;
   }

    /**
    * f0 -> "this"
    */
   public String visit(ThisExpression n, Class argu) throws Exception {
       return "%this";
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public String visit(BracketExpression n, Class argu) throws Exception {
        String expr = n.f1.accept(this, argu);
        return expr;   
   }
}
