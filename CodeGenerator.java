import java.util.HashMap;
import StupsCompiler.node.*;
import StupsCompiler.analysis.DepthFirstAdapter;

public class CodeGenerator extends DepthFirstAdapter {
	private String output = "";
    private String type = "";
    private int labelsNumber = 0;
    private int breakNumber = 0;
    private int compareNumber = 0;
    private int stackHeight = 1;
	private HashMap<String, Integer> symbolsMap = new HashMap<String, Integer>();
	private HashMap<String, String> typesMap;
	
	public CodeGenerator(HashMap<String, String> t) {
        typesMap = t;
    }
	
	public String getOutput() {
        return output;
    }
	public HashMap<String, Integer> getSymbolsMap(){
		return symbolsMap;
	}
	
	public int getStackHeight() {
        return stackHeight;
    }
	
	//Assignments
    public void caseAInstrAssignExpr(AInstrAssignExpr node) {
        node.getExpr().apply(this);
        output += "\tistore "+symbolsMap.get(node.getIdent().toString().toLowerCase().replaceAll(" ",""))+"\n";
        stackHeight--;
    }
    
	//Identifiers
	public void outAIdentExpr(AIdentExpr node) {
        boolean test = true;
        String ident = node.getIdent().toString().toLowerCase().replaceAll(" ","");
        Node parent = node;
        String parentName = "";
        while (!parentName.equals("APascalExpr")) {      
            parent = parent.parent();
            parentName = parent.getClass().getSimpleName().replaceAll(" ","");
            if (parentName.equals("ADeclarationExpr")) test = false;
        }
        if (test) {
            output += "\tiload "+symbolsMap.get(ident)+"\n";
            stackHeight++;
            type = typesMap.get(ident);
        }
    }
    
	//Number expression
    public void caseANumberExpr(ANumberExpr node) {
        int number = Integer.parseInt(node.getNumber().toString().replaceAll(" ", ""));
        output += "\tldc "+number+"\n";
        stackHeight++;
        type = "integer";
    }
    
    //Writeln
    public void caseAInstrWritelnExpr(AInstrWritelnExpr node) {
        String boolorInt = "Z";
        output += "\tgetstatic java/lang/System/out Ljava/io/PrintStream;\n";
        node.getExpr().apply(this);
        if (type.equals("integer")) boolorInt = "I";
        output += "\tinvokevirtual java/io/PrintStream/println("+boolorInt+")V\n";
        stackHeight++;
    }
       
	//If then
    public void caseAIfthenExpr(AIfthenExpr node) {
        int temp = labelsNumber++;
        node.getExpr().apply(this);
        output += "\tifeq LabelIfDown"+temp+"\n";
        stackHeight--;
        node.getInstr().apply(this);
        output += "LabelIfDown"+temp+":\n";
    }

    //If then else
    public void caseAIfthenelseExpr(AIfthenelseExpr node) {
        int temp = labelsNumber++;
        node.getExpr().apply(this);
        output += "\tifeq LabelIfElse"+temp+"\n";
        stackHeight--;
        node.getInstr1().apply(this);
        output += "\tgoto LabelIfElseEnd"+temp+"\n";
        output += "LabelIfElse"+temp+":\n";
        node.getInstr2().apply(this);
        output += "LabelIfElseEnd"+temp+":\n";
    }
    
    //While
    public void caseAWhileExpr(AWhileExpr node) {
        int temp = labelsNumber++;
        output += "LabelWhileUp"+temp+":\n";
        node.getExpr().apply(this);
        output += "\tifeq LabelWhileDown"+temp+"\n";
        stackHeight--;
        node.getInstr().apply(this);
        output += "\tgoto LabelWhileUp"+temp+"\n";
        output += "LabelWhileDown"+temp+":\n";
        output += "LabelBreakDown"+(breakNumber++)+":\n";
    }

    
    //Compare
    public void caseACompareExpr(ACompareExpr node) {
        int temp = 0;
        node.getLeft().apply(this);
        node.getRight().apply(this);
        output += "\tisub\n";
        stackHeight--;
        temp = compareNumber;
        node.getCompare().apply(this);
        output += "\tbipush 0\n";
        stackHeight++;
        output += "\tgoto LabelCompEnd"+temp+"\n";
        output += "LabelTrue"+temp+":\n";
        output += "\tbipush 1\n";
        stackHeight++;
        output += "LabelCompEnd"+temp+":\n";
    }
    
    //Compare Operations Block
    public void outAEqualExpr(AEqualExpr node) {
        output += "\tifeq LabelTrue"+(compareNumber++)+"\n";
        stackHeight--;
        type = "boolean";
    }
    public void outALessExpr(ALessExpr node) {
        output += "\tiflt LabelTrue"+(compareNumber++)+"\n";
        stackHeight--;
        type = "boolean";
    }
    public void outAGreaterExpr(AGreaterExpr node) {
        output += "\tifgt LabelTrue"+(compareNumber++)+"\n";
        stackHeight--;
        type = "boolean";
    }
    public void outALEqualExpr(ALEqualExpr node) {
        output += "\tifle LabelTrue"+(compareNumber++)+"\n";
        stackHeight--;
        type = "boolean";
    }
    public void outAGEqualExpr(AGEqualExpr node) {
        output += "\tifge LabelTrue"+(compareNumber++)+"\n";
        stackHeight--;
        type = "boolean";
    }
    public void outAUnequalExpr(AUnequalExpr node) {
        output += "\tifne LabelTrue"+(compareNumber++)+"\n";
        stackHeight--;
        type = "boolean";
    }
    
    // Boolean expressions Block
    public void outAOperatorOrExpr(AOperatorOrExpr node) {
        output += "\tior\n";
        stackHeight--;
        type = "boolean";
    }
    public void outAOperatorXorExpr(AOperatorXorExpr node) {
        output += "\tixor\n";
        stackHeight--;
        type = "boolean";
    }
    public void outAOperatorAndExpr(AOperatorAndExpr node) {
        output += "\tiand\n";
        stackHeight--;
        type = "boolean";
    }
    public void outANotExpr(ANotExpr node) {
        int temp = labelsNumber++;
        output += "\tifeq LabelNotTrue"+temp+"\n";
        output += "\tbipush 0\n";
        output += "\tgoto LabelNotEnd"+temp+"\n";
        output += "LabelNotTrue"+temp+":\n";
        output += "\tbipush 1\n";
        output += "\tLabelNotEnd"+temp+":\n";
        type = "boolean";
    }
    public void caseATrueExpr(ATrueExpr node) {
        output += "\tbipush 1\n";
        stackHeight++;
        type = "boolean";
    }
    public void caseAFalseExpr(AFalseExpr node) {
        output += "\tbipush 0\n";
        stackHeight++;
        type = "boolean";
    }
    
    //Arithmetic operators Block
    public void outAOperatorPlusExpr(AOperatorPlusExpr node) {
        output += "\tiadd\n";
        stackHeight--;
        type = "integer";
    }
    public void outAOperatorMinusExpr(AOperatorMinusExpr node) {
        output += "\tisub\n";
        stackHeight--;
        type = "integer";
    }
    public void outAOperatorMultExpr(AOperatorMultExpr node) {
        output += "\timul\n";
        stackHeight--;
        type = "integer";
    }
    public void outAOperatorDivExpr(AOperatorDivExpr node) {
        output += "\tidiv\n";
        stackHeight--;
        type = "integer";
    }
    public void outAOperatorModExpr(AOperatorModExpr node) {
        output += "\tirem\n";
        type = "integer";
    }
    public void outAMinusUnaryExpr(AMinusUnaryExpr node) {
        output += "\tineg\n";
        type = "integer";
    }
    
    //Break
    public void caseAInstrBreakExpr(AInstrBreakExpr node) {
        output += "\tgoto LabelBreakDown"+breakNumber+"\n";
    }
}
