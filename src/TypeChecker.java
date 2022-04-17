package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class TypeChecker extends GJDepthFirst<String, Object> {
    SymbolTable table;
    Method currMethod;

    public TypeChecker(SymbolTable table) {
        this.table = table;
        this.currMethod = null;
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
    public String visit(MainClass n, Object argu) throws Exception {
        super.visit(n, argu);
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
    public String visit(ClassDeclaration n, Object argu) throws Exception {
        String className = n.f1.accept(this, argu);
        Class classInstance = this.table.getClass(className);
        for (int i = 0; i < n.f3.size(); i++) {
            String field = n.f3.elementAt(i).accept(this, classInstance);
        }
        for (int i = 0; i < n.f4.size(); i++) {
            String method = n.f4.elementAt(i).accept(this, classInstance);
        }
        super.visit(n, argu);
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
    public String visit(ClassExtendsDeclaration n, Object argu) throws Exception {
        String parent = n.f3.accept(this, argu);
        String className = n.f1.accept(this, argu);
        for (int i = 0; i < n.f5.size(); i++) {
            String field = n.f5.elementAt(i).accept(this, className);
        }
        for (int i = 0; i < n.f6.size(); i++) {
            String method = n.f6.elementAt(i).accept(this, className);
        }
        return null;
    }

   /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
   public String visit(VarDeclaration n, Object argu) throws Exception {
        String fieldType = n.f0.accept(this, argu);
        String fieldName = n.f1.accept(this, argu);
        return fieldType + " " + fieldName;
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
   public String visit(MethodDeclaration n, Object argu) throws Exception {
        String methodType = n.f1.accept(this, argu);
        String methodName = n.f2.accept(this, argu);
        String methodParams = n.f4.accept(this, argu);
        String returnType = n.f10.accept(this, argu);
        if (returnType != argu.methods.get(methodName).returnType) {
            throw new Exception("Return type is different from Method type");
        }
        return methodType + " " + methodName + ":" + methodParams;
   }
    
   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, Object argu) throws Exception {
        String formalParam = n.f0.accept(this, argu);
        String formalParamTail = n.f1.accept(this, argu);
        return formalParam + " " + formalParamTail;
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, Object argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> ","
    * f1 -> FormalParameter()
    */
   public String visit(FormalParameterTerm n, Object argu) throws Exception {
        return "," + n.f1.accept(this, argu);
   }
   /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
   public String visit(FormalParameter n, Object argu) throws Exception {
        String paramType = n.f0.accept(this, argu);
        String paramName = n.f1.accept(this, argu);
        return paramType + " " + paramName;
   }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
   public String visit(Type n, Object argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> "boolean"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(BooleanArrayType n, Object argu) throws Exception {
       return "boolean[]";
   }

   /**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
   public String visit(IntegerArrayType n, Object argu) throws Exception {
        return "int[]";
   }

   /**
    * f0 -> "boolean"
    */
   public String visit(BooleanType n, Object argu) throws Exception {
        return "boolean";
   }

   /**
    * f0 -> "int"
    */
   public String visit(IntegerType n, Object argu) throws Exception {
        return "int";
   }

    /**
    * f0 -> <IDENTIFIER>
    */
    public String visit(Identifier n, Object argu) throws Exception {
        return n.f0.toString();
    }



//    /**
//     * f0 -> Block()
//     *       | AssignmentStatement()
//     *       | ArrayAssignmentStatement()
//     *       | IfStatement()
//     *       | WhileStatement()
//     *       | PrintStatement()
//     */
//    public String visit(Statement n, Object argu) throws Exception {
//       return n.f0.accept(this, argu);
//    }

//    /**
//     * f0 -> "{"
//     * f1 -> ( Statement() )*
//     * f2 -> "}"
//     */
//    public String visit(Block n, Object argu) throws Exception {
//    }

//    /**
//     * f0 -> Identifier()
//     * f1 -> "="
//     * f2 -> Expression()
//     * f3 -> ";"
//     */
//    public String visit(AssignmentStatement n, Object argu) throws Exception {
//    }

//    /**
//     * f0 -> Identifier()
//     * f1 -> "["
//     * f2 -> Expression()
//     * f3 -> "]"
//     * f4 -> "="
//     * f5 -> Expression()
//     * f6 -> ";"
//     */
//    public String visit(ArrayAssignmentStatement n, Object argu) throws Exception {
//    }

//    /**
//     * f0 -> "if"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     * f5 -> "else"
//     * f6 -> Statement()
//     */
//    public String visit(IfStatement n, Object argu) throws Exception {
//    }

//    /**
//     * f0 -> "while"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     */
//    public String visit(WhileStatement n, Object argu) throws Exception {
//    }

//    /**
//     * f0 -> "System.out.println"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> ";"
//     */
//    public String visit(PrintStatement n, Object argu) throws Exception {
//    }

   /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
   public String visit(AndExpression n, Object argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        if (first == second == "boolean") {
            return "boolean";
        } else {
            throw new Exception("Cannot use logical operators on non boolean data");
        }
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
   public String visit(CompareExpression n, Object argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        if (first == second == "int") {
            return "int";
        } else {
            if (argu.fields.get(expr).type != "int") {
                if (currMethod.variables.get(expr).type != "int") {
                    throw new Exception("Cannot use comparison operators on non integers");
                }
            }
        }
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
   public String visit(PlusExpression n, Object argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        if (first == second == "int") {
            return "int";
        } else {
            if (argu.fields.get(expr).type != "int") {
                if (currMethod.variables.get(expr).type != "int") {
                    throw new Exception("Cannot use arithmetic operators on non integers");
                }
            }
        }
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
   public String visit(MinusExpression n, Object argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        if (first == second == "int") {
            return "int";
        } else {
            if (argu.fields.get(expr).type != "int") {
                if (currMethod.variables.get(expr).type != "int") {
                    throw new Exception("Cannot use arithmetic operators on non integers");
                }
            }
        }
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
   public String visit(TimesExpression n, Object argu) throws Exception {
        String first = n.f0.accept(this, argu);
        String second = n.f2.accept(this, argu);
        if (first == second == "int") {
            return "int";
        } else {
            if (argu.fields.get(expr).type != "int") {
                if (currMethod.variables.get(expr).type != "int") {
                    throw new Exception("Cannot use arithmetic operators on non integers");
                }
            }
        }
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
   public String visit(ArrayLookup n, Object argu) throws Exception {
       String expr = n.f0.accept(this, argu);
       String type = "";
        if (expr != "int[]") {
            if (argu.fields.get(expr).type != "int[]") {
                if (currMethod.variables.get(expr).type != "int[]") {
                    throw new Exception("Cannot index a non array object");
                }
            } else {
                type = "int";
            }
        } else {
            type = "int";
        }

        if (expr != "boolean[]") {
            if (argu.fields.get(expr).type != "boolean[]") {
                if (currMethod.variables.get(expr).type != "boolean[]") {
                    throw new Exception("Cannot index a non array object");
                }
            } else {
                type = "boolean";
            }
        } else {
            type = "boolean";
        }


        String expr2 = n.f2.accept(this, argu);
        if (expr2 != "int") {
            if (argu.fields.get(expr).type != "int") {
                if (currMethod.variables.get(expr).type != "int") {
                    throw new Exception("Cannot index an array with a non integer");
                }
            }
        }




        return type;
   }

   /**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
   public String visit(ArrayLength n, Object argu) throws Exception {
        String expr = n.f0.accept(this, argu);
        if (expr != "int[]" && expr != "boolean[]") {
            if (argu.fields.get(expr).type != "int[]" && argu.fields.get(expr).type != "boolean[]" ) {
                if (currMethod.variables.get(expr).type != "int[]" && currMethod.variables.get(expr).type != "boolean[]") {
                    throw new Exception("Cannot find the length of a non array object");
                }
            }
        }
        return "int";
   }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( ExpressionList() )?
//     * f5 -> ")"
//     */
//    public String visit(MessageSend n, Object argu) throws Exception {
//    }

//    /**
//     * f0 -> Expression()
//     * f1 -> ExpressionTail()
//     */
//    public String visit(ExpressionList n, Object argu) throws Exception {
//        n.f0.accept(this, argu);
//        n.f1.accept(this, argu);
//    }

   /**
    * f0 -> ( ExpressionTerm() )*
    */
   public String visit(ExpressionTail n, Object argu) throws Exception {
      return n.f0.accept(this, argu);
   }

   /**
    * f0 -> ","
    * f1 -> Expression()
    */
   public String visit(ExpressionTerm n, Object argu) throws Exception {
       return n.f1.accept(this, argu);
   }


   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Object argu) throws Exception {
      return "int";
   }

   /**
    * f0 -> "true"
    */
   public String visit(TrueLiteral n, Object argu) throws Exception {
      return "boolean";
   }

   /**
    * f0 -> "false"
    */
   public String visit(FalseLiteral n, Object argu) throws Exception {
      return "boolean";
   }


   /**
    * f0 -> "new"
    * f1 -> "boolean"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(BooleanArrayAllocationExpression n, Object argu) throws Exception {
       return "boolean[]";
   }

   /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
   public String visit(IntegerArrayAllocationExpression n, Object argu) throws Exception {
       return "int[]";
   }

   /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
   public String visit(AllocationExpression n, Object argu) throws Exception {
        String type = n.f1.accept(this, argu);
		if (this.table.getClass(type) == null)
			throw new Exception("No such class name");
		return type;
   }

   /**
    * f0 -> "!"
    * f1 -> Clause()
    */
   public String visit(NotExpression n, Object argu) throws Exception {
        String clause = n.f1.accept(this, argu);
        if (clause == "boolean") {
            return "boolean";
        } else {
            throw new Exception("Cannot use logical operators on non boolean data");
        }
   
   }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
   public String visit(BracketExpression n, Object argu) throws Exception {
       return n.f1.accept(this, argu);
   }
}
