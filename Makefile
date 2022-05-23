all: compile

compile:
	rm -rf results/
	java -jar ./jtb132di.jar -te minijava/minijava.jj
	java -jar ./javacc5.jar minijava/minijava-jtb.jj
	javac ./src/*.java
	javac Main.java

clean:
	rm -f *.class *~
	rm -f src/*.class
	rm -f MiniJavaParser.java MiniJavaParserConstants.java MiniJavaParserTokenManager.java 
	rm -f ParseException.java Token.java TokenMgrError.java JavaCharStream.java
	rm -rf visitor syntaxtree
	rm -rf results/
	rm -rf out1