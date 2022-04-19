package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class TypeChecker extends GJDepthFirst<String, Class> {
    SymbolTable table;
    Method currMethod;

    public TypeChecker(SymbolTable table) {
        this.table = table;
        this.currMethod = null;
    }

    public void validType(String type) throws Exception {
        if (type != "int" && type != "boolean" && type != "int[]" && type != "boolean[]") {
            if (this.table.getClass(type) == null) {
                throw new Exception("Not a valid type: " + type);
            }
        }
    }
    
    public String isVar(String possibleVar, Class currClass) throws Exception {
        if (this.currMethod.variables.get(possibleVar) != null) { // first check if local variable, then field
            return this.currMethod.variables.get(possibleVar).type;
        } else if (this.currMethod.parameters.get(possibleVar) != null) {
            return this.currMethod.parameters.get(possibleVar).type;
        } else if (currClass.fields.get(possibleVar) != null) {
            return currClass.fields.get(possibleVar).type;
        } 
        if (currClass.extending != null) {
            if (currClass.extending.fields.get(possibleVar) != null) {
                return currClass.extending.fields.get(possibleVar).type;
            }
        }
        throw new Exception("No such variable " + possibleVar + " in class " + currClass.name + " in method " + this.currMethod.name);
    }

    // public String checkArgs(String args, Method method, Class currClass) throws Exception {
    //     if (!args.equals("null") && !args.isEmpty()) {
    //         for (String arg : args.split(",");Map.Entry<String, Variable> param : method.parameters.entrySet();) {
    //             String paramType = param.getValue().type;
    //             String argType = isVar(arg, currClass);
    //             if (!argType.equals(paramType)) {
    //                 throw new Exception("Argument types do not match parameters types");
    //             }
    //         }
    //     } 
    //     return null;
    // }

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
        String className = n.f1.accept(this, argu);
        Class classInstance = this.table.getClass(className);
        n.f11.accept(this, classInstance);
        for (int i = 0; i < n.f14.size(); i++) {
          String field = n.f14.elementAt(i).accept(this, classInstance);
        }
        for (int i = 0; i < n.f15.size(); i++) {
          String method = n.f15.elementAt(i).accept(this, classInstance);
        }
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
      for (int i = 0; i < n.f3.size(); i++) {
        String field = n.f3.elementAt(i).accept(this, classInstance);
      }
      for (int i = 0; i < n.f4.size(); i++) {
        String method = n.f4.elementAt(i).accept(this, classInstance);
        System.out.println(method);
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
        for (int i = 0; i < n.f5.size(); i++) {
            String field = n.f5.elementAt(i).accept(this, classInstance);
        }
        for (int i = 0; i < n.f6.size(); i++) {
            String method = n.f6.elementAt(i).accept(this, classInstance);
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
        validType(fieldType);
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
   public String visit(MethodDeclaration n, Class argu) throws Exception {
        String methodType = n.f1.accept(this, argu);
        validType(methodType);
        String methodName = n.f2.accept(this, argu);
        this.currMethod = argu.methods.get(methodName);
        String methodParams = n.f4.accept(this, argu);
        String returnType = n.f10.accept(this, argu);
        // if (returnType != argu.methods.get(methodName).returnType && !(isVar(returnType, argu)).equals(argu.methods.get(methodName).returnType)) {
        //     throw new Exception("Return type is different from Method type");
        // }
        return methodType + " " + methodName + ":" + methodParams;
   }
    
   /**
    * f0 -> FormalParameter()
    * f1 -> FormalParameterTail()
    */
   public String visit(FormalParameterList n, Class argu) throws Exception {
        String formalParam = n.f0.accept(this, argu);
        String formalParamTail = n.f1.accept(this, argu);
        return formalParam + " " + formalParamTail;
   }

   /**
    * f0 -> ( FormalParameterTerm() )*
    */
   public String visit(FormalParameterTail n, Class argu) throws Exception {
      return n.f0.accept(this, argu);
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
        System.out.println("FormalParameter " + paramType);
        validType(paramType);
        String paramName = n.f1.accept(this, argu);
        return paramType + " " + paramName;
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

//    /**
//     * f0 -> Identifier()
//     * f1 -> "="
//     * f2 -> Expression()
//     * f3 -> ";"
//     */
//    public String visit(AssignmentStatement n, Class argu) throws Exception {
//         String identifier = n.f0.accept(this, argu);
//         String expr = n.f2.accept(this, argu);
//         if (identifier == expr) {
//             return null;
//         }
//         if (expr != "int") {
//             if (expr != "boolean") {
//                 String exprType = isVar(expr, argu);
//                 if (exprType != "boolean" && exprType != "int") {
//                     throw new Exception("Cannot assign an expression to a variable of different type");
//                 } else {
//                     String identifierType = isVar(identifier, argu);
//                     if (identifierType != exprType) {
//                         throw new Exception("Cannot assign an expression to a variable of different type");
//                     } else {
//                         return null;
//                     }
//                 }

//             } else {
//                 String identifierType = isVar(identifier, argu);
//                 if (identifierType != "boolean") {
//                     throw new Exception("Cannot assign an expression to a variable of different type");
//                 } else {
//                     return null;
//                 }
//             }
//         } else {
//             String identifierType = isVar(identifier, argu);
//             if (identifierType != "int") {
//                 throw new Exception("Cannot assign an expression to a variable of different type");
//             } else {
//                 return null;
//             }
//         }
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
//    public String visit(ArrayAssignmentStatement n, Class argu) throws Exception {
//         String identifier = n.f0.accept(this, argu);
//         String expr = n.f2.accept(this, argu);
//         String expr2 = n.f5.accept(this, argu);

//         if (expr != "int" && isVar(expr, argu) != "int") {
//             throw new Exception("Index should be integer");
//         }
//         String identifierType = isVar(identifier, argu);
//         if (identifierType != "int[]" && identifierType != "boolean[]") {
//             throw new Exception("Cannot index a non array Class");
//         }
//         String exprType = isVar(expr2, argu);
//         if (exprType != identifierType.replaceAll("[^A-Za-z0-9]","")) {
//             throw new Exception("Types don't match");
//         }
//         return null;

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
//    public String visit(IfStatement n, Class argu) throws Exception {
//         String expr = n.f2.accept(this, argu);
//         if (expr != "boolean " && isVar(expr, argu) != "boolean") {
//             throw new Exception("If statement requires a boolean");
//         }
//         String stmt = n.f4.accept(this, argu);
//         String elseStmt = n.f6.accept(this, argu);
//         return null;
//    }

//    /**
//     * f0 -> "while"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> Statement()
//     */
//    public String visit(WhileStatement n, Class argu) throws Exception {
//         String expr = n.f2.accept(this, argu);
//         if (expr != "boolean " && isVar(expr, argu) != "boolean") {
//             throw new Exception("While statement requires a boolean");
//         }
//         String stmt = n.f4.accept(this, argu);
//         return null;
//    }

//    /**
//     * f0 -> "System.out.println"
//     * f1 -> "("
//     * f2 -> Expression()
//     * f3 -> ")"
//     * f4 -> ";"
//     */
//    public String visit(PrintStatement n, Class argu) throws Exception {
//        String expr = n.f2.accept(this, argu);
//         if (expr == "int" || expr == "boolean") {
//             return expr;
//         }
//         String type = isVar(expr, argu);
//         if (type != "int" && type != "boolean") {
//             throw new Exception("Cannot print this type of Class");
//         }
//         return type;
//    }

//    /**
//     * f0 -> Clause()
//     * f1 -> "&&"
//     * f2 -> Clause()
//     */
//    public String visit(AndExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first == "boolean") {
//             if (second == "boolean") {
//                 return "boolean";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2 == "boolean") {
//                     return "boolean";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type == "boolean") {
//                 return "boolean";
//             } else {
//                 throw new Exception("Cannot use logical operators on non boolean data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "<"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(CompareExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first == "int") {
//             if (second == "int") {
//                 return "int";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2 == "int") {
//                     return "int";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type == "int") {
//                 return "int";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "+"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(PlusExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first == "int") {
//             if (second == "int") {
//                 return "int";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2 == "int") {
//                     return "int";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type == "int") {
//                 return "int";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "-"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(MinusExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first == "int") {
//             if (second == "int") {
//                 return "int";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2 == "int") {
//                     return "int";
//                 } else {
//                     throw new Exception("Cannot use logical operators on non boolean data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type == "int") {
//                 return "int";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "*"
//     * f2 -> PrimaryExpression()
//     */
//    public String visit(TimesExpression n, Class argu) throws Exception {
//         String first = n.f0.accept(this, argu);
//         String second = n.f2.accept(this, argu);
//         if (first == "int") {
//             if (second == "int") {
//                 return "int";
//             } else {
//                 String type2 = isVar(second, argu);
//                 if (type2 == "int") {
//                     return "int";
//                 } else {
//                     throw new Exception("Cannot use arithmetic operators on non integer data");
//                 }
//             }
//         } else {
//             String type = isVar(first, argu);
//             if (type == "int") {
//                 return "int";
//             } else {
//                 throw new Exception("Cannot use arithmetic operators on non integer data");
//             }
//         }
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "["
//     * f2 -> PrimaryExpression()
//     * f3 -> "]"
//     */
//    public String visit(ArrayLookup n, Class argu) throws Exception {
//        String expr = n.f0.accept(this, argu);
//        String expr2 = n.f2.accept(this, argu);
//        if (expr == "int[]") {
//            if (expr2 == "int") {
//                return "int";
//            } else {
//                String type2 = isVar(expr2, argu);
//                if (type2 == "int") {
//                    return "int";
//                } else {
//                     throw new Exception("Index should be integer");
//                }
//            }
//        } else if (expr == "boolean[]") {
//            if (expr2 == "int") {
//                return "boolean";
//            } else {
//                String type2 = isVar(expr2, argu);
//                if (type2 == "int") {
//                    return "boolean";
//                } else {
//                     throw new Exception("Index should be integer");
//                }
//            }
//        } else {
//            String type = isVar(expr, argu);
//             if (type == "int[]") {
//                if (expr2 == "int") {
//                     return "int";
//                 } else {
//                     String type2 = isVar(expr2, argu);
//                     if (type2 == "int") {
//                         return "int";
//                     } else {
//                             throw new Exception("Index should be integer");
//                     }
//                 }
//             } else if (type == "boolean[]") {
//                if (expr2 == "int") {
//                     return "boolean";
//                 } else {
//                     String type2 = isVar(expr2, argu);
//                     if (type2 == "int") {
//                         return "int";
//                     } else {
//                             throw new Exception("Index should be integer");
//                     }
//                 }
//             } else {
//                 throw new Exception("Cannot index a non array Class");
//             }
//        }


//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> "length"
//     */
//    public String visit(ArrayLength n, Class argu) throws Exception {
//         String expr = n.f0.accept(this, argu);
//         if (expr != "int[]" && expr != "boolean[]") {
//             String type = isVar(expr, argu);
//             if (type != "int[]" && type != "boolean[]") {
//                 throw new Exception("Cannot find the length of a non array Class");
//             }
//         }
//         return "int";
//    }

//    /**
//     * f0 -> PrimaryExpression()
//     * f1 -> "."
//     * f2 -> Identifier()
//     * f3 -> "("
//     * f4 -> ( ExpressionList() )?
//     * f5 -> ")"
//     */
//    public String visit(MessageSend n, Class argu) throws Exception {
//         String expr = n.f0.accept(this, argu);
//         String method = n.f2.accept(this, argu);
//         String args = n.f4.accept(this, argu);
//         if (argu.methods.get(method) != null) {
//             if (isVar(expr, argu) == argu.name) { 
//                 checkArgs(args, argu.methods.get(method), argu);
//                 return argu.methods.get(method).returnType;
//             }
//         } else {
//              if (this.table.getClass(expr) == null) {
//                 throw new Exception("No such class");
//             } else if (this.table.getClass(expr).methods.get(method) == null) {
//                 throw new Exception("No such class method");
//             } else if (this.table.getClass(expr).methods.get(method) != null) { // found a class with this method 
//                 if (isVar(expr, argu) == this.table.getClass(expr).name) { // is there such a variable in current class or current method? Check types
//                     checkArgs(args, this.table.getClass(expr).methods.get(method), argu);
//                     return this.table.getClass(expr).methods.get(method).returnType;
//                 } else {
//                     throw new Exception("No such method for this class instance");
//                 }
                
//             }
//         } 
//         return null;
//    }

//    /**
//     * f0 -> Expression()
//     * f1 -> ExpressionTail()
//     */
//    public String visit(ExpressionList n, Class argu) throws Exception {
//        return n.f0.accept(this, argu) + n.f1.accept(this, argu);
//    }

//    /**
//     * f0 -> ( ExpressionTerm() )*
//     */
//    public String visit(ExpressionTail n, Class argu) throws Exception {
//       return n.f0.accept(this, argu);
//    }

//    /**
//     * f0 -> ","
//     * f1 -> Expression()
//     */
//    public String visit(ExpressionTerm n, Class argu) throws Exception {
//        return "," + n.f1.accept(this, argu);
//    }

//    /**
//     * f0 -> AndExpression()
//     *       | CompareExpression()
//     *       | PlusExpression()
//     *       | MinusExpression()
//     *       | TimesExpression()
//     *       | ArrayLookup()
//     *       | ArrayLength()
//     *       | MessageSend()
//     *       | Clause()
//     */
//    public String visit(Expression n, Class argu) throws Exception {
//       return n.f0.accept(this, argu);
//    }


//    /**
//     * f0 -> <INTEGER_LITERAL>
//     */
//    public String visit(IntegerLiteral n, Class argu) throws Exception {
//       return "int";
//    }

//    /**
//     * f0 -> "true"
//     */
//    public String visit(TrueLiteral n, Class argu) throws Exception {
//       return "boolean";
//    }

//    /**
//     * f0 -> "false"
//     */
//    public String visit(FalseLiteral n, Class argu) throws Exception {
//       return "boolean";
//    }


//    /**
//     * f0 -> "new"
//     * f1 -> "boolean"
//     * f2 -> "["
//     * f3 -> Expression()
//     * f4 -> "]"
//     */
//    public String visit(BooleanArrayAllocationExpression n, Class argu) throws Exception {
//        return "boolean[]";
//    }

//    /**
//     * f0 -> "new"
//     * f1 -> "int"
//     * f2 -> "["
//     * f3 -> Expression()
//     * f4 -> "]"
//     */
//    public String visit(IntegerArrayAllocationExpression n, Class argu) throws Exception {
//        return "int[]";
//    }

//    /**
//     * f0 -> "new"
//     * f1 -> Identifier()
//     * f2 -> "("
//     * f3 -> ")"
//     */
//    public String visit(AllocationExpression n, Class argu) throws Exception {
//         String type = n.f1.accept(this, argu);
// 		if (this.table.getClass(type) == null)
// 			throw new Exception("No such class name");
// 		return type;
//    }

//    /**
//     * f0 -> "!"
//     * f1 -> Clause()
//     */
//     // type or identifier
//    public String visit(NotExpression n, Class argu) throws Exception {
//         String clause = n.f1.accept(this, argu);
//         if (clause == "boolean") {
//             return "boolean";
//         } else {
//             throw new Exception("Cannot use logical operators on non boolean data");
//         }
   
//    }

//     /**
//     * f0 -> "this"
//     */
//    public String visit(ThisExpression n, Class argu) throws Exception {
//       return argu.name;
//    }

//    /**
//     * f0 -> "("
//     * f1 -> Expression()
//     * f2 -> ")"
//     */
//    public String visit(BracketExpression n, Class argu) throws Exception {
//         String expr = n.f1.accept(this, argu);
//         if (expr == "int" || expr == "boolean" || expr == "int[]" || expr == "boolean[]" ) {
//             return expr;
//         }
//         if (this.table.getClass(expr) != null) {    
//             return this.table.getClass(expr).name;
//         } 
//         return isVar(expr, argu);
//    }
}
