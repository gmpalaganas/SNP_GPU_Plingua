cd  org/gcn/plinguacore/parser/input/plingua/
java -cp "/home/genesis/Documents/Code/Utilities/Java/javacc/bin/lib/javacc.jar" javacc PlinguaJavaCcParser.jj
cd ../../../../../../
javac $(find ./org/gcn/plinguacore/* | grep .java)
