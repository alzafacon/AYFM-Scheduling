# Assignment Planner
## Java project using Gradle and Spring.
Using Eclipse Oxygen is recommended since there is documentation below on setup with the Eclipse Oxygen IDE.

### Eclipse Setup
1. Download a plugin for Gradle with the Eclipse Marketplace.  
   
   i. From the menu bar select "Help -> Eclipse Marketplace..."  
   
   ii. Search for "Buildship" and install "Buildship Gradle Integration 2.0" (the one with an elephant logo)

2. Refresh the Gradle script  

   i. Navigate to the "Package Explorer" to find the build.gradle script.

   ii. Right click on the build.gradle script and select "Gradle -> Refresh Gradle Project" (You will need to do this manually when making changes to the gradle script)

3. Configure Gradle JDK

   i. "Gradle Tasks" view should be on the right. (if not, from the menu bar select "Window -> Show View" and find "Gradle Tasks").
   
   ii. Find the task you want to run (probably _AssignmentPlaner/build/build_ or _AssignmentPlanner/application/bootRun_). Right click on the task and select "Open Gradle Run Configuration..."
   
   iii. Select the "Java Home" tab. Browse for the JDK and click "Apply" then "OK"

    note: this process is repeated for every task that needs to be run and also repeated whenever JDK is updated

4. Configure Build Path (needed for JavaFX)

   i. From the "Package Explorer" right click on the project name and select "Build Path -> Configure Build Path..."

   ii. Double click on the JRE

   iii. Under "System library" choose "Alternate JRE:" (use the same JRE as before)

   note: otherwise Eclipse with raise errors about JavaFX not being API

## Working with JavaFX
Download "JavaFX Scene Builder 2" from [here](http://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html). This tool was used to generate the first iteration of the FXML but has not been tested to make modifications after the scene was broken up into four files (this was done in order to use multiple controllers, thus improving encapsulation, modularity, cohesion, and reducing coupling).

To integrate Scene Builder with Eclipse follow [these](https://docs.oracle.com/javase/8/scene-builder-2/work-with-java-ides/sb-with-eclipse.htm#BABBFEBD) instructions. By navigating around that page you can also find information regarding other ides.
