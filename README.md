<!-- PROJECT LOGO -->
<div align="center">
<h3 align="center">Jar Dependency Analyser</h3>
  <p align="center">
A Java program that checks if the list of jars contains all required dependencies to execute a specified main class    <br />
    <br />
  </p>
</div>

### Installation

To get started with the project, follow these steps:

1. Clone the repository
   ```sh
   git clone https://github.com/Erhan1706/jar-depedency-checker.git
   ```
2. Build the project
   ```sh
   ./gradlew build
   ```
3. Compile all the jar files
   ```sh
   ./gradlew jar
   ```
4. Run the program
   ```sh
    ./gradlew run --args="com.jetbrains.internship2024.ClassB ./build/libs/ModuleB-1.0.jar"
    // Output
    $ False
    $ Dependency: com/jetbrains/internship2024/InternalClassA not found
   ```
The program takes as input the name of the main class to analyze as its first argument, followed by a list of paths to JAR files.
Please ensure that the paths are specified relative to the root directory (by default, the JAR files can be found in `./build/libs`).

5. (Optional) Add and run tests
   ```sh
   ./gradlew test
   ```

<ins>Note:</ins> if you want to add another module, make sure to include the dependency on the `build.gradle.kts` file located at the root directory.
Furthermore, don't forget to run `./gradlew jar` to generate the new jar files. 
## Implementation
The algorithm to check if the jars have all the necessary dependencies to run the main class executes the following steps:

- Step 1: Iterate through the list of provided jars and collect all their .class files (`JarAnalyser`)
- Step 2: Find the class path of the compiled code for the main class (`MainClassAnalyser`). 
- Step 3: Parse the bytecode from the current .class file. More specifically, we are parsing the constant pool section and collecting
all class references - `BytecodeParser` (reference: https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4). 
- Step 4: Using a breadth first search traversal, recursively repeat steps 2-4 for all the newly collected .class dependencies. 
Keep track of what dependencies were already checked and repeat until there are no more new dependencies (`MainClassAnalyser`).
- Step 5: Compare the sets of dependencies from the jars and from the main class, if a certain dependency from the main class is not present on the 
jar dependencies print false to stdout, otherwise print true.