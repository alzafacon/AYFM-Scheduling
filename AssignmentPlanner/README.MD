# Assignment Planner
## Java project using Gradle and Spring.
Eclipse Oxygen is the recommended IDE since this setup documentation is for the Eclipse Oxygen IDE.

### Eclipse Setup
1. Download a plugin for Gradle with the Eclipse Marketplace. 
   
    i. From the menu bar select `Help > Eclipse Marketplace...`
   
    ii. Search for "Buildship" and install "Buildship Gradle Integration 2.0" (the one with an elephant logo)

2. Refresh the Gradle script

   i. Navigate to the `Package Explorer` to find the build.gradle script

   ii. Right click on the build.gradle script and select `Gradle > Refresh Gradle Project` (You will need to do this manually when making changes to the gradle script)

3. Configure Gradle JDK

   i. `Gradle Tasks` view should be on the right. (if not, from the menu bar select `Window > Show View` and find `Gradle Tasks`)
   
   ii. Right click on the application name and select `Open Gradle Run Configuration...`
   
   iii. Select the `Java Home` tab. Browse for the JDK and click `Apply` then `OK`

    note: this process is repeated whenever the JDK is updated

4. Configure Build Path with JDK 1.8 (needed for JavaFX) 

   Even if the JDK was installed correctly Eclipse may use an existing JRE or other JDK. Verify that the build path is configured with JDK 1.8 

   i. Right click the project name in the `Package Explorer` and select `Build Path > Configure Build Path...`

   ii. Under the `Libraries` tab, if the `JRE System Library` shows jdk version 1.8 you're done; if not continue to next step

   iii. Double click `JRE System Library` to edit it
   
   iv. The correct jdk can be configured 2 ways (if neither work see step 5)
    
   - choose the last radio button (`Workspace default JRE (<version>)`) if `<version>` is the correct jdk
   
   - Or choose `Alternate JRE` and select correct jdk from dropdown list.

5. Make Eclipse aware of jdk

    There are several ways to do this. Below is the one I think is the most flexible.

    i. `Window > Preferences` from menu bar
    
    ii. Navigate to `Java/Installed JREs`

    iii. Click on `Add...` button

    iv. Select `Standard VM` and click `Next`

    v. Click `Directory`, find jdk install folder, click `Finish`, and click `Apply`

    vi. If you would like the jdk just added to be the workspace default, click the checkbox next to it and click `Apply`

## Working with JavaFX
Download "JavaFX Scene Builder 2" from [here](http://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html). This tool was used to generate the first iteration of the FXML but has not been tested to make modifications after the scene was broken up into four files (this was done in order to use multiple controllers, thus improving encapsulation, modularity, cohesion, and reducing coupling).

To integrate Scene Builder with Eclipse follow [these](https://docs.oracle.com/javase/8/scene-builder-2/work-with-java-ides/sb-with-eclipse.htm#BABBFEBD) instructions. By navigating around that page you can also find information regarding other IDEs.
