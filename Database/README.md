# First time database setup

1. SQLite database is created by running ```sqlite3 ayfm.db < ayfm_schema.sql```

2. (Optional) Student profiles can be created in the database using an enrollment.csv file.
Table `person` is populated in this process. This is meant as a setup convenience for the user, not for frequent use.
  - columns for .csv file:
    - Gender,Last Name,First Name,Active,Reading,Initial Call,Return Visit,Bible Study,Talk
  - prepare the SQL by running: ```enrollment.py```
  - populate the tables: ```populatePerson.sql```

3. The assignment table is populated from .docx schedules.
Generating SQL should be limited to Java code as much as possible.
The .docx file is converted into a .csv file for the Java code by a python script:
```convert.py```
