import java.util.LinkedList;

public class Block {
	private String def;
    private int blockId;
    private LinkedList<String> use;
    private LinkedList<Block> suc;
    private boolean hasSuc = false;
    private boolean hasUse = false;
    private LinkedList<String> in = new LinkedList<String>();
    private LinkedList<String> out = new LinkedList<String>();
    
    public Block(int b) {
        blockId = b;
    }
    public Block(int b, String s) { 
    	blockId = b;
    	def = s;
    }
    
    public String getDef() {return def;}
    public int getBlockId() {return blockId;}
    public LinkedList<String> getUse() {return use;}
    public LinkedList<Block> getSuc() {return suc;}
    public boolean getHasSuc() {return hasSuc;}
    public boolean getHasUse() {return hasUse;}
    public LinkedList<String> getIn() {return in;}
    public LinkedList<String> getOut() {return out; }

    public void addUse(String input) {
        if (!hasUse) {
            use = new LinkedList<String>();
            hasUse = true;
        }
        use.add(input);
    }
    public void addSuc(Block b) {
        if (!hasSuc) {
            suc = new LinkedList<Block>();
            hasSuc = true;
        }
        suc.add(b);
    }
    public void addIn(String input) {in.add(input);}
    public void addOut(String input) {out.add(input);}
}