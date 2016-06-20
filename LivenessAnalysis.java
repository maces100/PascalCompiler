import java.util.LinkedList;

public class LivenessAnalysis {
    private LinkedList<Block> blocks;
	private GraphChecker graphChecker;
	
	public LivenessAnalysis(GraphChecker gc){
		graphChecker = gc;
		blocks = graphChecker.getBlocks();
		graphChecker.exit();
		
		//Getting ready for liveness analysis
        int value;
        for (int i : graphChecker.getAddSucs().keySet()) {
            value = graphChecker.getAddSucs().get(i);
            if (value-1 < blocks.size()) blocks.get(i-1).addSuc(blocks.get(value-1));
        }
        for (int i : graphChecker.getRemoveSucs()) blocks.get(i-1).getSuc().removeFirst();
        
        liveness();
	}
	
	//Liveness Analysis
	private void liveness(){
		boolean done = false;
        Block thisBlock = blocks.getFirst();
        LinkedList<String> outWithoutDif;
        LinkedList<String> insOfSuc;
		
		//Add use to ins 
		for (Block block : blocks){
            if (block.getHasUse()){
                for (String s : block.getUse()) block.addIn(s); 
            }
		}

        while (!done) {
            done = true;
            for (Block block : blocks) {
                outWithoutDif = removeDifs(thisBlock);
                for (String s : outWithoutDif)
                    if (!thisBlock.getIn().contains(s)) {
                        thisBlock.addIn(s);
                        done = false;
                    }
                insOfSuc = getInsOfSuc(thisBlock);
                for (String s : insOfSuc)
                    if (!thisBlock.getOut().contains(s)) {
                        thisBlock.addOut(s);
                        done = false;
                    }
                thisBlock = block;
            }
        }
        
        int registers = 0;
        for (Block b : blocks){
            if (registers < b.getOut().size()) registers = b.getOut().size();
        }
        
        System.out.println("Registers: " + registers);
	}

    //Out minus dif
    private LinkedList<String> removeDifs(Block thisBlock) {
        LinkedList<String> outWithoutDif = new LinkedList<String>();
        boolean dupl = false;
        for (String out : thisBlock.getOut()) {
            if (out.equals(thisBlock.getDef()))
                dupl = true;
            if (!dupl) {
                outWithoutDif.add(out);
                dupl = false;
            }
        }
        return outWithoutDif;
    }
    
    //Returns all ins of the suc
    private LinkedList<String> getInsOfSuc(Block thisBlock) {
        LinkedList<String> insOfSuc = new LinkedList<String>();
        if (thisBlock.getHasSuc())
            for (Block suc : thisBlock.getSuc())
                for (String s : suc.getIn())
                    insOfSuc.add(s);
        return insOfSuc;
    }

}
