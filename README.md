# Minijava Static Checking (Semantic Analysis)
This program performs type checking on some source files, written in Minijava (a subset of Java). To access the nodes of the parse tree, we used the Visitor Pattern. More precisely, I decided to visit twice the tree and for that I implemented two visitors. This seemed like the cleanest way, in order to store all the useful data in the Symbol Table, regarding classes, methods, fields, variables, parameters and then perform type checking. Moreover, the first visitor stores the offsets of the fields and the methods. The classes I implemented for these tasks are the following:
- Variable(name, type, offset) : used for method local variables, method parameters and class fields
- Method(name, returnType, offset, variables, parameters)
- Class(name, fields, methods, whether it inherits from another)
- Symbol Table()
