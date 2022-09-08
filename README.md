## Minijava Static Checking (Semantic Analysis)


[![Java CI Actions Status](https://github.com/nefelitav/llvm-translator/workflows/Java%20CI/badge.svg)](https://github.com/nefelitav/llvm-translator/actions)

This program performs type checking on some source files, written in Minijava (a subset of Java). To access the nodes of the parse tree, we used the Visitor Pattern. More precisely, I decided to visit twice the tree and for that I implemented two visitors. This seemed like the cleanest way, in order to store all the useful data in the Symbol Table, regarding classes, methods, fields, variables, parameters and then perform type checking. Moreover, the first visitor stores the offsets of the fields and the methods. The classes I implemented for these tasks are the following:

- Variable(name, type, offset) : used for local variables of methods, method parameters and class fields
- Method(name, returnType, offset, variables, parameters) : variables and parameters are represented by maps(key=variable name, value=variable instance)
- Class(name, fields, methods, whether it inherits from another) : fields and methods are represented by maps(key=name, value=instance)
- Symbol Table(table, fieldOffset, methodOffset, fieldOffsetNext, methodOffsetNext) : the table is a LinkedHashMap(keeps insertion order of objects) having as key the className and as value the class Instance, which is printed at the end of the type checking. The rest of the fields are used to calculate the offset of the fields and methods.
- DeclCollector(SymbolTable) : the first visitor that fills the table with useful data concerning declarations. Some type checking is done in this part too, so as to avoid wasting time with flawed code.
- TypeChecker(SymbolTable) : the second visitor that consults the data accumulated in the Symbol Table, in order to ensure the correctness of the programs. I tried to cover all possible senarios of incompatibilities and print corresponding error messages, observing the grammar rules. Also, in this part it is important to have knowledge of the class and method that are currently examined, to check the scope etc. Thus, I pass the current class instance as an argument to all visit methods and I have a field with the current method instance information.

## LLVM Translator (Intermediate Code Generation)

This program generates code to LLVM IR using one more tree visitor (LLVMGenerator). This visitor translates Minijava code to LLVM, accumulating the generated code into a string that is then printed in a file. Also, a vtable is created for each class, based on the methods' offsets, which were created by the first visitor and stored in the Symbol Table. In addition to the checks made by the first and the second visitor, we implement runtime checks to ensure that we do not access unallocated memory. In almost every non-terminal visit function, the "Accumulator" function is triggered, which performs the right actions based on the type of the given input/expression. This was the most challenging part of the project, since we had to ensure that we deal accordingly, in an abstract way, with the input, no matter the type of it. Another challenging task was ensuring that the same register/label is not used, which I coped with using global counters. Finally, I created a simple bash script to run all given tests/examples to make sure that everything works well. The generated results are stored in the /results folder.

## Dependencies

```
$ sudo apt update && sudo apt install clang
```

## Compile & Parse

```
$ make && java Main [file1] [file2] ... [fileN]
```

## Generate Code & Run LLVM program

```
$ clang -o out1 file1.ll && ./out1
```

## Run all programs

```
$ ./run_all.sh
```
