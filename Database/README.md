# First time database setup

1. There is an empty database in the Database/ folder which can be copied and used for development.
When developing changes to the schema and a new database file needs to be created:

   - set `spring.jpa.hibernate.ddl-auto=create` in *`application.properties`*
   - uncomment `@AutoConfigureTestDatabase(replace=Replace.NONE)` in *`ImportServiceTest.java`*
   - uncomment `@Transactional()` in the same test
   - execute a Gradle build

   There are a variety of other ways to do this (like doing bootRun with `Replace.NONE`) but try to stick to the one above.

2. To import data: run the python scripts (in Databse and ScheduleParsing folders) to generate the sql and use your favorite sql visualizer to run the sql on the data. I am using H2's own "H2 console (command line)". It is a pain to work with because you __always have to disconnect from the database manually when you leave the H2 console.__

   The other way to import the data manually through the app.
