# First time database setup

1. Schema `ayfm` is setup by running: ```ayfm_schema.sql```

2. (Optional) Student profiles can be created in the database using an enrollment.csv file.
Tables `person` and `works_on` are populated in this process. This is meant as a setup convenience for the user, not for frequent use.
  - columns for .csv file:
    - Gender,Last Name,First Name,Active,Reading,Initial Call,Return Visit,Bible Study
  - prepare the SQL by running: ```enrollment.py```
  - populate the tables: ```populatePersonAndWorksOn.sql```

3. The assignment table is populated from .docx schedules.
Generating SQL should be limited to Java code as much as possible.
The .docx file is converted into a .csv file for the Java code by a python script:
```main.py```
