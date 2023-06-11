# minijava compiler
"minijava" compiler written in java, compiles to x86-64. Minijava language grammar can be found online from a google search.

- scanning implemented using JFlex (java implementation of flex)
- parsing implemented using CUP (java implementation of bison)
- semantic analysis, type checking, etc done using AST IR and visitor design paradigm, with symbol tables.
- codegen, uses vtables for dynamic dispatch, offset tables for stack/heap allocation. Generates at&t x86-64.

## building and running
This project uses the ant build system. It can be built using:
$ant

once its build you can run the binary as:
$java -cp build/classes:lib/java-cup-11b.jar MiniJava <option> <filename>

filename is the minijava file you want to compile.

compiler options are as follows:
- no option, will do all compilation steps and output compiled x86-64 to stdout
- -S, will only do scanning phase of compiler and output tokens to stdout
- -P, does scanning and parsing, prints the AST to stdout
- -A, same as -P but prints AST in a different format
- -T, does scanning, parsing, and semantic analysis, prints out the symbol tables to stdout

if you are testing out my compiler, SamplePrograms/SampleMiniJavaPrograms contains lots of good programs to test compilation on.
