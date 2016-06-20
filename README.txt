1. java -jar sablecc.jar sable.scc
2. javac *.java
3a. Compile: java StupsCompiler -compile <Filename.pas>
3b. Liveness: java StupsCompiler -liveness <Filename.pas>
4. Bytecode: java -jar jasmin.jar <Filename.j>
5. Execute compiled file: java Filename