package src;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Visitor extends GJDepthFirst<String, Void>{
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
    @Override
    public String visit(MainClass n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        super.visit(n, argu);

        System.out.println();

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
    @Override
    public String visit(ClassDeclaration n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        super.visit(n, argu);

        System.out.println();

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
    @Override
    public String visit(ClassExtendsDeclaration n, Void argu) throws Exception {
        String classname = n.f1.accept(this, null);
        System.out.println("Class: " + classname);

        super.visit(n, argu);

        System.out.println();

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
    @Override
    public String visit(MethodDeclaration n, Void argu) throws Exception {
        String argumentList = n.f4.present() ? n.f4.accept(this, null) : "";

        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);

        System.out.println(myType + " " + myName + " -- " + argumentList);
        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, Void argu) throws Exception {
        String ret = n.f0.accept(this, null);

        if (n.f1 != null) {
            ret += n.f1.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterTerm n, Void argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTail n, Void argu) throws Exception {
        String ret = "";
        for ( Node node: n.f0.nodes) {
            ret += ", " + node.accept(this, null);
        }

        return ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, Void argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);
        return type + " " + name;
    }

    @Override
    public String visit(ArrayType n, Void argu) {
        return "int[]";
    }

    public String visit(BooleanType n, Void argu) {
        return "boolean";
    }

    public String visit(IntegerType n, Void argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, Void argu) {
        return n.f0.toString();
    }

   /**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, Class q) throws Exception {
        return n.f1.accept(this, q);
    }

    /**
    * f0 -> "!"
    * f1 -> Clause()
    */
    public String visit(NotExpression n, Class q) throws Exception {
        String type = n.f1.accept(this, q);
        if (!type.equals("boolean")) {
            throw new Exception("Error: notExpression is used only with boolean variables");
        }
        return "boolean";
    }

    /**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, Class q) throws Exception {
        
    }
    
    /**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(ArrayAllocationExpression n, Class q) throws Exception {
        String type = n.f3.accept(this, q);
        if (type != "int") {
            throw new Exception("Error: Array must contain integers");
        }
        return "int[]";
    }

    /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, Class q) throws Exception {
        return q.name.toString();
    }

    /**
    * f0 -> "false"
    */
    public String visit(FalseLiteral n, Class q) throws Exception {
        return "boolean";
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, Class q) throws Exception {
        return "boolean";
    }

    /**
	* f0 -> <INTEGER_LITERAL>
	*/
	public Object visit(IntegerLiteral n, Object argu) throws Exception {
		return "int";
	}

    /**
	* f0 -> Type()
	* f1 -> Identifier()
	* f2 -> ";"
	*/
	public Object visit(VarDeclaration n, Object argu) throws Exception {
		n.f2.accept(this, null);
		return null;
	}

    /**
    * f0 -> Clause()
    * f1 -> "&&"
    * f2 -> Clause()
    */
    public String visit(AndExpression n, Class q) throws Exception {
        String type1 = n.f0.accept(this, q);
        String type2 = n.f2.accept(this, q);
        if (type1 != "boolean" || type2 != "boolean") {
            throw new Exception("Error: Operator & is used only with boolean variables");
        }
        return "boolean";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, Class q) throws Exception {
        String type1 = n.f0.accept(this, q);
        String type2 = n.f2.accept(this, q);
        if (type1 != "int" || type2 != "int") {
            throw new Exception("Error: Operator < is used only with boolean variables");
        }
        return "boolean";
    }
    
    /**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, Class q) throws Exception {
        String type1 = n.f0.accept(this, q);
        String type2 = n.f2.accept(this, q);
        if (type1 != "int" || type2 != "int") {
            throw new Exception("Error: Operator + is used only with boolean variables");
        }
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, Class q) throws Exception {
        String type1 = n.f0.accept(this, q);
        String type2 = n.f2.accept(this, q);
        if (type1 != "int" || type2 != "int") {
            throw new Exception("Error: Operator - is used only with boolean variables");
        }
        return "int";
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, Class q) throws Exception {
        String type1 = n.f0.accept(this, q);
        String type2 = n.f2.accept(this, q);
        if (type1 != "int" || type2 != "int") {
            throw new Exception("Error: Operator * is used only with boolean variables");
        }
        return "int";
    }

    /**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, Class q) throws Exception {
        String type = n.f2.accept(this, q);
        if (!type.equals("int")) {
            throw new Exception("Error: PrintStatement is used only with integer variables");
        }
        return null;
    }
}
