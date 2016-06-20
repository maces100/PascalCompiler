import java.util.HashMap;
import java.util.LinkedList;
import StupsCompiler.node.*;
import StupsCompiler.analysis.DepthFirstAdapter;

public class GraphChecker extends DepthFirstAdapter{
	private Block thisBlock;
    private Block lastBlock;
    private int blockId = 1;
    private LinkedList<Block> blocks = new LinkedList<Block>();
    private HashMap<Integer, Integer> addSucs = new HashMap<Integer, Integer>();
    private LinkedList<Integer> removeSucs = new LinkedList<Integer>();
    
    public LinkedList<Block> getBlocks() {return blocks;}
    public HashMap<Integer, Integer> getAddSucs() {return addSucs;}
    public LinkedList<Integer> getRemoveSucs() {return removeSucs;}
    
    //Adding thisBlock and setting lastBlock to thisBlock
    private void nextBlock(){
    	if (lastBlock != null) lastBlock.addSuc(thisBlock);
        blocks.add(thisBlock);       
        lastBlock = thisBlock;
    }
    
    public void exit() {
        thisBlock = new Block(blockId++);
        nextBlock();
    }
    
    //Def and Use
    public void caseAInstrAssignExpr(AInstrAssignExpr node) {
        String ident = node.getIdent().toString().toLowerCase().replaceAll(" ","");
        thisBlock = new Block(blockId++, ident);
        node.getExpr().apply(this);         
        nextBlock();
    }

    //Add to thisBlock addUse with the ident
    public void caseAIdentExpr(AIdentExpr node) {
        String ident = node.getIdent().toString().toLowerCase().replaceAll(" ","");
        if (thisBlock != null) {
            if (!thisBlock.getHasUse()) {
                thisBlock.addUse(ident);
            } else {
                if (!thisBlock.getUse().contains(ident)) {
                    thisBlock.addUse(ident);
                }
            }
        }
    }
    
    //Writeln
    public void caseAInstrWritelnExpr(AInstrWritelnExpr node) {
        thisBlock = new Block(blockId++);
        node.getExpr().apply(this);        
        nextBlock();
    }
    
    //If then
    public void caseAIfthenExpr(AIfthenExpr node) {
        int temp = blockId;
        thisBlock = new Block(blockId++);
        node.getExpr().apply(this);         
        nextBlock();
        node.getInstr().apply(this);
        addSucs.put(temp, blockId);
    }

    //If then else
    public void caseAIfthenelseExpr(AIfthenelseExpr node) {
    	int if_instr = blockId;
        int then_instr;
       
        thisBlock = new Block(blockId++);
        node.getExpr().apply(this);        
        nextBlock();
        node.getInstr1().apply(this);
        then_instr = blockId-1;                
        addSucs.put(if_instr, blockId);
        node.getInstr2().apply(this);

        removeSucs.add(then_instr-1);		
        addSucs.put(then_instr, blockId); 
    }
    
    //While
    public void caseAWhileExpr(AWhileExpr node) {
        int temp = blockId;
        thisBlock = new Block(blockId++);
        node.getExpr().apply(this);         
        nextBlock();
        node.getInstr().apply(this);
        addSucs.put(temp, blockId);   		
        addSucs.put(blockId - 1, temp);
        removeSucs.add(blockId - 1);    	
    }
}
