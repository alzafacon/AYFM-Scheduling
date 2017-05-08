# AYFM-Scheduling
## Tool to aid in scheduling assignments for mid-week meeting "Apply Yourself to the Field Ministry."

### Assignment Planner
An Eclipse project in Java that packages the whole application.

### Schedule Parsing
This "subsystem" handles the input processing and preparation for processing.

### Software Engineering
Documentation and design for the project.

### database
The MySQL workbench model and sql scripts for setting up the database are in this folder.

### Description of Problem Domain for AYFM Scheduling

Apply Yourself to the Field Ministry (AYFM) is the section of the Jehovah's Witness midweek meeting that is meant to prepare those enrolled for the Field Ministry (door to door preaching).

The "Christian Congregation of Jehovah’s Witnesses" publishes a monthly workbook, "Our Christian Life and Ministry-Meeting Workbook," that details the sections of the midweek meetings (e.g. AYFM) for that month and the specific topics and talks under each section.

There are three skits or mock presentations under AYFM:
- Initial Call
- Return visits
- Bible Study

A weekly Bible Reading will also be added to these although the Bible reading is technically part of "Treasures From God's Word" (a different section of the midweek meeting). Bible Reading will be assigned to a brother.

Each mock presentations is completed by two students. Both students must be the same gender unless they are family members. One student will be the publisher, responsible for delivering the message described in the Meeting Workbook. The other role is the householder.

Lessons will be assigned from a book (titled _Benefit_) available to Jehovah's Witnesses. Lessons are given as numbers and the student assigned is expected to read the lesson from _Benefit_ and do the exercise. Students can pass to the next lesson or they can be asked to repeat a lesson depending on their performance.

Some brothers should only be assigned to do bible readings. Some brothers can be assigned to do bible readings, initial calls, and return visits. Some brothers can do any of the assignments.

Sisters are not assigned to Bible Reading.

Some topics from the Meeting Workbook may be more appropriate to assign to one person over another. This may be due to the person’s age, gender, or other factors that the elder making the assignments deems relevant.

The problem is scheduling these in a round-robin fashion. The brothers/sisters should not have to wait excessively long times before being assigned and performing an assignment. Likewise, brothers/sisters should not be assigned so often that others are not given the chance to participate.

This application facilitates the management of student activity and *suggests* monthly assignment schedules.

Further information can be found in [this](/SoftwareEngineering) folder and document S-38-S section 4.
