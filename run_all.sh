#!/bin/bash

# clean
make clean > /dev/null 2>&1
# compile
make > /dev/null 2>&1

# lists of java programs
declare -a JavaPrograms=("BinaryTree" "BubbleSort" "TreeVisitor" "Factorial" "MoreThan4" "LinkedList" "LinearSearch" "QuickSort" )
declare -a smallJavaPrograms=("And" "Arrays" "If" "Simple" "Classes" )

# parse, generate code, run code
for prog in ${JavaPrograms[@]}; do
    java Main minijava-tests/$prog.java 2>/dev/null
    clang -o out1 results/$prog.ll 2>/dev/null && ./out1 
done
for prog in ${smallJavaPrograms[@]}; do
    java Main llvm-tests/"${prog,,}"/$prog.java 2>/dev/null
    clang -o out1 results/$prog.ll 2>/dev/null && ./out1 
done