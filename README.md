# AYFM-Scheduling
## Scheduling assistant for "Apply Yourself to the Field Ministry."

See [wiki](https://github.com/fidelcoria/AYFM-Scheduling/wiki) for background details.

### Installation

```git clone https://github.com/fidelcoria/AYFM-Scheduling.git```

Eclipse is the preferred IDE. To import the project to your workspace navigate to `File > Import...` from the menu bar. Under `General` select `Existing Project into Workspace` and click `Next`. Browse for the root directory of the cloned repo. Now click `Finish`.

Java 1.8 is required. 

The build tool is Gradle. For the best Gradle experience you will need the Gradle Buildship plugin for Eclipse. Navigate to `Help > Eclipse Marketplace...` from the menu bar. Search for Buildship and install it. The Gradle Tasks view is the place to start builds, runs, etc. Navigate to `Window > Show View > Other...` from the menu bar. Under `Gradle` select `Gradle Tasks` and click `Open`. For more detailed instructions see the README under the AssignmentPlanner folder.

Python3 is required to execute utility scripts used during development for parsing docx files and generating sql.

H2 is used as the application database. The preferred database tool is `H2 Console`. This is included as part of the H2 installer available [here](http://www.h2database.com/html/main.html). An empty database with the schema already created is available in the Database folder. It is highly recommended to create your own. See the README in the Database folder for more details.

There is a C# CLI tool under the ReminderSlipPopulator folder for preparing and filling out pfd reminder slips. This is being deprecated and will be converted to a Gradle Java project.

### Usage

Try out a build from the Gradle Tasks view by opening `AssignmentPlanner > build` and selecting `build` (with a green gear next to it).

Try out running the application by going to the Gradle Tasks view and opening `AssignmentPlanner > application` and selecting `bootRun` (with a green gear next to it).

### Contributing

Feel free to open a pull request with fixes/features. 

Please reach out to @fidelcoria if you would like to participate in the ongoing development. You can be added to the Trello board used for tracking planned work.

### Licence

Due to a dependency on [iText](https://itextpdf.com/) the licence for this project must be GNU AGPL.

This project strictly adheres to the [terms of use](https://www.jw.org/en/terms-of-use/) for jw.org.
