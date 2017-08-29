-- sqlite3 ayfm.db

-- https://sqlite.org/pragma.html#pragma_encoding
PRAGMA encoding = "UTF-8";
PRAGMA FOREIGN_KEYS = off;

BEGIN TRANSACTION;

-- http://sqlite.org/lang_keywords.html
-- backticks (`) used to enclose identifiers

-- -----------------------------------------------------
-- Table `person`
-- -----------------------------------------------------
DROP TABLE IF EXISTS ayfm.person;

CREATE TABLE IF NOT EXISTS `person` (
  `id` INTEGER NOT NULL PRIMARY KEY,
  `is_active`  INTEGER(1) NOT NULL DEFAULT 1,
  `gender`     TEXT(1) NOT NULL,
  `first_name` TEXT NOT NULL,
  `last_name`  TEXT NOT NULL,
  `is_eligible_reading`   INTEGER(1) NOT NULL DEFAULT 0,
  `is_eligible_init_call` INTEGER(1) NOT NULL DEFAULT 0,
  `is_eligible_ret_visit` INTEGER(1) NOT NULL DEFAULT 0,
  `is_eligible_bib_study` INTEGER(1) NOT NULL DEFAULT 0,
  `is_eligible_talk`      INTEGER(1) NOT NULL DEFAULT 0,
  
  CONSTRAINT `full_name_UNIQUE` 
    UNIQUE (`last_name`, `first_name`)
);


-- -----------------------------------------------------
-- Table `assignment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `assignment` ;

CREATE TABLE IF NOT EXISTS `assignment` (
  `id` INTEGER NOT NULL PRIMARY KEY,
  `week` TEXTDATE NOT NULL,
  `assignee`    INTEGER NOT NULL,
  `householder` INTEGER NULL DEFAULT NULL,
  `lesson`    INTEGER NOT NULL DEFAULT 0,
  `classroom` INTEGER NOT NULL,
  `type`      INTEGER NOT NULL,
  `is_completed` INTEGER NOT NULL DEFAULT 0,
  `is_passed`    INTEGER NOT NULL DEFAULT 0,

  CONSTRAINT `assignment_fk_person_assignee`
    FOREIGN KEY (`assignee`)
    REFERENCES `person` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `assignment_fk_person_householder`
    FOREIGN KEY (`householder`)
    REFERENCES `person` (`id`)
    ON DELETE CASCADE
);

DROP INDEX IF EXISTS `week_ix`;
CREATE INDEX `week_ix` ON `assignment` (`week` ASC);

DROP INDEX IF EXISTS `assgn_fk_person_id_ix`;
CREATE INDEX `assgn_fk_person_id_ix` ON `assignment` (`assignee` ASC);

DROP INDEX IF EXISTS `assgn_fk_person_householder_ix`;
CREATE INDEX `assgn_fk_person_householder_ix` ON `assignment` (`householder` ASC);

COMMIT TRANSACTION;
PRAGMA FOREIGN_KEYS = on;
