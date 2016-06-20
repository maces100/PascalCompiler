import java.util.HashMap;
import StupsCompiler.node.*;
import StupsCompiler.analysis.DepthFirstAdapter;

public class TypeChecker extends DepthFirstAdapter {
	private String type;
    private HashMap<String, Boolean> initMap = new HashMap<String, Boolean>();
	private HashMap<String, String> typesMap = new HashMap<String, String>();

	public HashMap<String, String> getTypesMap(){
		return typesMap;
	}
	
	//Quick check if variable is not declared yet.
	private void decl(String s) {
        if (!typesMap.containsKey(s)) {
            System.out.println("An error has occured: Variable '"+s+"' is not declared.");
            System.exit(1);
        }
    }

	//Set type to "integer".
    public void caseANumberExpr(ANumberExpr node) {
        type = "integer";
    }
    
    //Set type to "boolean".
    public void caseATrueExpr(ATrueExpr node) {
        type = "boolean";
    }

    //Set type to "boolean".
    public void caseAFalseExpr(AFalseExpr node) {
        type = "boolean";
    }
    
	//Check if variable is already declared. 
    public void caseADeclarationExpr(ADeclarationExpr node){
        String[] variables = node.getLeft().toString().toLowerCase().split(" ");
		String type = node.getRight().toString().toLowerCase().replaceAll(" ","");
		
		for (String var : variables) {
            if (typesMap.containsKey(var)){
            	System.out.println("An error has occured: Variable '" + var + "' has already been declared (as "  + typesMap.get(var) + ").");
            	System.exit(1);
            }
            else { 
                typesMap.put(var, type);
                initMap.put(var, false);
            }
        }
    }
    
    //Check if variable is initialized.
    public void caseAIdentExpr(AIdentExpr node) {
        String var = node.getIdent().toString().toLowerCase().replaceAll(" ","");
        decl(var);
        
        if (!initMap.get(var)) {
            System.out.println("An error has occured: Variable '"+var+"' is declared, but not initialized.");
            System.exit(1);
        }
        type = typesMap.get(var);
    }
    
    //Check if variable is correctly assigned.
    public void caseAInstrAssignExpr(AInstrAssignExpr node) {
        String var = node.getIdent().toString().toLowerCase().replaceAll(" ","");
        decl(var);
        initMap.put(var, true);
        node.getExpr().apply(this);
        String assignCheck = node.getExpr().getClass().getSimpleName();

        if (assignCheck.equals("AIdentExpr")) {
            String ident = node.getExpr().toString().toLowerCase().replaceAll(" ",""); 
            decl(ident);
            if (!typesMap.get(var).equals(typesMap.get(ident))) {
            	System.out.println("An error has occured: Variable '" + var + "' is '" + typesMap.get(var) + "' and variable '" + ident + "' is '" + typesMap.get(ident) + "'.");
            	System.exit(1);
            }
        }
        
        if (assignCheck.equals("ANumberExpr")) {
            if (!typesMap.get(var).equals("integer")) {
                System.out.println("An error has occured: Variable '" +var + "' is not an Integer.");
                System.exit(1);
            }
        }
        
        if (assignCheck.equals("ATrueExpr") 
        		|| assignCheck.equals("AFalseExpr")) {
            if (!typesMap.get(var).equals("boolean")) {
                System.out.println("An error has occured: Cannot assign boolean. Variable '"+var+"' is '"+typesMap.get(var)+"'.");
                System.exit(1);
            }
        }
        
        if (assignCheck.equals("AOperatorPlusExpr")
        		|| assignCheck.equals("AOperatorMinusExpr") 
        		|| assignCheck.equals("AOperatorMultExpr") 
        		|| assignCheck.equals("AOperatorDivExpr") 
        		|| assignCheck.equals("AOperatorModExpr") 
        		|| assignCheck.equals("APlusUnaryExpr") 
        		|| assignCheck.equals("AMinusUnaryExpr")) {
            if (!typesMap.get(var).equals("integer")) {
                System.out.println("An error has occured: Variable '" +var + "' is not a Integer.");
                System.exit(1);
            }
        }
        
        if (assignCheck.equals("ACompareExpr") 
        		|| assignCheck.equals("AOperatorOrExpr")
        		|| assignCheck.equals("AOperatorXorExpr")
        		|| assignCheck.equals("AOperatorAndExpr")
        		|| assignCheck.equals("ANotExpr")) {
            if (!typesMap.get(var).equals("boolean")) {
                System.out.println("An error has occured: Wrong type of '"+var+"'. Expected 'boolean'.");
                System.exit(1);
            }
        }
    }
    
    //Check if the '+' operator is used with integers.
    public void caseAOperatorPlusExpr(AOperatorPlusExpr node) {
    	node.getLeft().apply(this);
        String left = type;
        node.getRight().apply(this);
        String right = type;
        if(!left.equals("integer") || !right.equals("integer")){
        	System.out.println("An error has occured: You can only use the '+' operator on integers.");
            System.exit(1);
        }
    }
    
    //Check if the '-' operator is used with integers.
    public void caseAOperatorMinusExpr(AOperatorMinusExpr node) {
    	node.getLeft().apply(this);
        String left = type;
        node.getRight().apply(this);
        String right = type;
        if(!left.equals("integer") || !right.equals("integer")){
        	System.out.println("An error has occured: You can only use the '-' operator on integers.");
            System.exit(1);
        }
    }
    
    //Check if the '*' operator is used with integers.
    public void caseAOperatorMultExpr(AOperatorMultExpr node) {
    	node.getLeft().apply(this);
        String left = type;
        node.getRight().apply(this);
        String right = type;
        if(!left.equals("integer") || !right.equals("integer")){
        	System.out.println("An error has occured: You can only use the '*' operator on integers.");
            System.exit(1);
        }
    }

    //Check if the 'div' operator is used with integers.
    public void caseAOperatorDivExpr(AOperatorDivExpr node) {
    	node.getLeft().apply(this);
        String left = type;
        node.getRight().apply(this);
        String right = type;
        if(!left.equals("integer") || !right.equals("integer")){
        	System.out.println("An error has occured: You can only use the 'div' operator on integers.");
            System.exit(1);
        }
    }
    
    //Check if the 'mod' operator is used with integers.
    public void caseAOperatorModExpr(AOperatorModExpr node) {
    	node.getLeft().apply(this);
        String left = type;
        node.getRight().apply(this);
        String right = type;
        if(!left.equals("integer") || !right.equals("integer")){
        	System.out.println("An error has occured: You can only use the 'mod' operator on integers.");
            System.exit(1);
        }
    }

    //Check if the unary '+' is used with an integer.
    public void caseAPlusUnaryExpr(APlusUnaryExpr node) {
        node.getExpr().apply(this);
        if (!type.equals("integer")) {
            System.out.println("An error has occured: You can only use the unary '+' operator on an integer.");
            System.exit(1);
        }
    }

    //Check if the unary '-' is used with an integer.
    public void caseAMinusUnaryExpr(AMinusUnaryExpr node) {
        node.getExpr().apply(this);
        if (!type.equals("integer")) {
            System.out.println("An error has occured: You can only use the unary '-' operator on an integer.");
            System.exit(1);
        }
    }
    
    //Check if the two boolean expressions are compared.
    public void caseACompareExpr(ACompareExpr node) {
        node.getLeft().apply(this);
        String left = this.type;
        node.getRight().apply(this);
        String right = this.type;
        if (!left.equals(right)){
        	System.out.println("An error has occured: You can only compare a boolean expression to another boolean expression.");
        	System.exit(1);
        }
        type = "boolean"; //Type needs to be set to "boolean" at the end to be able to compare e.g. "(3>5)" with "false".
    }
    
    //Check if the "or" operator is used with two boolean expressions.
    public void caseAOperatorOrExpr(AOperatorOrExpr node) {
    	node.getLeft().apply(this);
        String left = type;      
    	node.getRight().apply(this);
        String right = type;
        if (!left.equals(right)){
        	System.out.println("An error has occured: You can only use the 'or' operator with two boolean expressions.");
    		System.exit(1);
    	}
    }

    //Check if the "xor" operator is used with two boolean expressions.
    public void caseAOperatorXorExpr(AOperatorXorExpr node) {
    	node.getLeft().apply(this);
        String left = type;      
    	node.getRight().apply(this);
        String right = type;
        if (!left.equals(right)){
        	System.out.println("An error has occured: You can only use the 'xor' operator with two boolean expressions.");
    		System.exit(1);
    	}
    }

    //Check if the "and" operator is used with two boolean expressions.
    public void caseAOperatorAndExpr(AOperatorAndExpr node) {
    	node.getLeft().apply(this);
        String left = type;      
    	node.getRight().apply(this);
        String right = type;
        if (!left.equals(right)){
        	System.out.println("An error has occured: You can only use the 'and' operator with two boolean expressions.");
    		System.exit(1);
    	}
    }

    //Check if the "not" operator is used with a boolean expression.
    public void caseANotExpr(ANotExpr node) {
        node.getExpr().apply(this);
        if (!type.equals("boolean")) {
            System.out.println("An error has occured: You can only use the 'not' operator with a boolean expression.");
            System.exit(1);
        }
    } 
    
    //Check if "break" is used inside of "while".
    public void caseAInstrBreakExpr(AInstrBreakExpr node) {
    	Node par = node.parent();
    	while (!par.getClass().getSimpleName().replaceAll(" ","").equals("APascalExpr")){
    		if(par.getClass().getSimpleName().replaceAll(" ","").equals("AWhileExpr")) return;
    		par = par.parent();
    	}
    	System.out.println("An error has occured: You can only use 'break' inside 'while'.");
        System.exit(1);
    }
}
