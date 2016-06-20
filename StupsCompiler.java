import java.io.*;
import StupsCompiler.lexer.*;
import StupsCompiler.parser.*;
import StupsCompiler.node.*;

public class StupsCompiler {
	public static void main(String[] args) throws 
	LexerException, IOException, ParserException{
		
		if(args.length != 2){
			System.out.println("An error has occured: Type 'java StupsCompiler -compile <Filename.pas>' or 'java StupsCompiler -liveness <Filename.pas>'");
			System.exit(1);
		}
		
		if(args[1].endsWith(".pas")){
			String file = "";
			try {
				BufferedReader br = new BufferedReader(new FileReader(args[1]));
				String line = br.readLine();
				while (line != null) {
					file = file + line + "\n";
					line = br.readLine();
				}
				br.close();
				if(args[0].equals("-compile")) compile(file, args[1]);
				else if(args[0].equals("-liveness")) liveness(file);
				else{
					System.out.println("An error has occured: Type 'java StupsCompiler -compile <Filename.pas>' or 'java StupsCompiler -liveness <Filename.pas>'");
					System.exit(1);
				}
			} catch (FileNotFoundException e) {
	            System.out.println("An error has occured: File '" + args[1] + "' was not found.");
	        } catch (ParserException e) {
	        	System.out.println("An error has occured: " + e.getMessage());
	        } catch (LexerException e) {
	        	System.out.println("An error has occured: " + e.getMessage());
	        }
		} else {
			System.out.println("An error has occured: Valid file extension is only '.pas'.");
            System.exit(1);
		}
	}
	
	private static void compile(String file, String fileName) throws ParserException,
	LexerException, IOException {
		StringReader stringReader = new StringReader(file);
		PushbackReader pushbackReader = new PushbackReader(stringReader, 100);
		Lexer lexer = new Lexer(pushbackReader);
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		
		/*
		ASTPrinter printer = new ASTPrinter();
		start.apply(printer);
		*/
		
		TypeChecker typeChecker = new TypeChecker();           
        start.apply(typeChecker);
        
        CodeGenerator codeGenerator = new CodeGenerator(typeChecker.getTypesMap());
        int i = typeChecker.getTypesMap().size();
        for (String id : typeChecker.getTypesMap().keySet())
            codeGenerator.getSymbolsMap().put(id, i--);
        start.apply(codeGenerator);
        
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName.replaceAll(".pas", "")+".j"),"UTF8"));
        writer.append(makeOutput(fileName, codeGenerator));
        writer.close();

        //System.out.println(makeOutput(fileName, codeGenerator));
        //System.out.println("Passed Compile");
	}
	
	private static void liveness(String file) throws ParserException, 
	LexerException, IOException {
		StringReader stringReader = new StringReader(file);
		PushbackReader pushbackReader = new PushbackReader(stringReader, 100);
		Lexer lexer = new Lexer(pushbackReader);
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
        
        /*
		ASTPrinter printer = new ASTPrinter();
		start.apply(printer);
		*/

        TypeChecker typeChecker = new TypeChecker();            
        start.apply(typeChecker);
        
        GraphChecker graphChecker = new GraphChecker();
        start.apply(graphChecker);
        
        new LivenessAnalysis(graphChecker);
        
        //System.out.println("Passed Liveness");
	}
	
	private static String makeOutput(String fileName, CodeGenerator codeGenerator){
		int size = codeGenerator.getSymbolsMap().size()+1;
        return  ".bytecode 50.0\n"+
                ".class public "+fileName+"\n"+
                ".super java/lang/Object\n"+
                ".method public <init>()V\n"+
                    "\t.limit stack 1\n"+
                    "\t.limit locals 1\n"+
                    "\taload_0\n"+
                    "\tinvokespecial java/lang/Object/<init>()V\n"+
                    "\treturn\n"+
                    ".end method\n"+
                    "\t.method public static main([Ljava/lang/String;)V\n"+
                    "\t.limit stack "+codeGenerator.getStackHeight()+"\n"+    
                    "\t.limit locals "+size+"\n"+
                        codeGenerator.getOutput() +
                    "\treturn\n"+
                ".end method\n";
	}
}
