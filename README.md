## Minijava Static Checking (Semantic Analysis)
This program performs type checking on some source files, written in Minijava (a subset of Java). To access the nodes of the parse tree, we used the Visitor Pattern. More precisely, I decided to visit twice the tree and for that I implemented two visitors. This seemed like the cleanest way, in order to store all the useful data in the Symbol Table, regarding classes, methods, fields, variables, parameters and then perform type checking. Moreover, the first visitor stores the offsets of the fields and the methods. The classes I implemented for these tasks are the following:
- Variable(name, type, offset) : used for local variables of methods, method parameters and class fields
- Method(name, returnType, offset, variables, parameters) : variables and parameters are represented by maps(key=variable name, value=variable instance)
- Class(name, fields, methods, whether it inherits from another) : fields and methods are represented by maps(key=name, value=instance)
- Symbol Table(table, fieldOffset, methodOffset, fieldOffsetNext, methodOffsetNext) : the table is a LinkedHashMap(keeps insertion order of objects) having as key the className and as value the class Instance, which is printed at the end of the type checking. The rest of the fields are used to calculate the offset of the fields and methods.
- DeclCollector(SymbolTable) : the first visitor that fills the table with useful data concerning declarations. Some type checking is done in this part too, so as to avoid wasting time with flawed code.
- TypeChecker(SymbolTable) : the second visitor that consults the data accumulated in the Symbol Table, in order to ensure the correctness of the programs. I tried to cover all possible senarios of incompatibilities and print corresponding error messages, observing the grammar rules. Also, in this part it is important to have knowledge of the class and method that are currently examined, to check the scope etc. Thus, I pass the current class instance as an argument to all visit methods and I have a field with the current method instance information.

## Compile & Run
```
$ make && java Main [file1] [file2] ... [fileN]
```
