-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema ayfm
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `ayfm` ;

-- -----------------------------------------------------
-- Schema ayfm
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ayfm` DEFAULT CHARACTER SET utf8 ;
USE `ayfm` ;

-- -----------------------------------------------------
-- Table `ayfm`.`person`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ayfm`.`person` ;

CREATE TABLE IF NOT EXISTS `ayfm`.`person` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(45) NOT NULL,
  `isactive` TINYINT(1) NOT NULL DEFAULT '1',
  `gender` CHAR(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `full_name_UNIQUE` (`full_name` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ayfm`.`assignment_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ayfm`.`assignment_type` ;

CREATE TABLE IF NOT EXISTS `ayfm`.`assignment_type` (
  `id` INT(11) NOT NULL,
  `description` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `description_UNIQUE` (`description` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ayfm`.`assignment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ayfm`.`assignment` ;

CREATE TABLE IF NOT EXISTS `ayfm`.`assignment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `week` DATE NOT NULL,
  `assignee` INT(11) NOT NULL,
  `householder` INT(11) NULL DEFAULT NULL,
  `lesson` INT(11) NULL DEFAULT NULL,
  `classroom` CHAR(1) NOT NULL,
  `type` INT(11) NOT NULL,
  `completed` TINYINT(1) NULL DEFAULT '0',
  `passed` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `week_ix` (`week` ASC),
  INDEX `assgn_fk_person_id_idx` (`assignee` ASC),
  INDEX `assgn_fk_person_householder_idx` (`householder` ASC),
  INDEX `assgn_fk_assgnment_type_type_idx` (`type` ASC),
  CONSTRAINT `assignment_fk_person_assignee`
    FOREIGN KEY (`assignee`)
    REFERENCES `ayfm`.`person` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `assignment_fk_person_householder`
    FOREIGN KEY (`householder`)
    REFERENCES `ayfm`.`person` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `assignment_fk_assignment_type_type`
    FOREIGN KEY (`type`)
    REFERENCES `ayfm`.`assignment_type` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `ayfm`.`works_on`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ayfm`.`works_on` ;

CREATE TABLE IF NOT EXISTS `ayfm`.`works_on` (
  `person_id` INT(11) NOT NULL,
  `type_id` INT(11) NOT NULL,
  INDEX `works_on_fk_assgn_idx` (`type_id` ASC),
  INDEX `works_on_fk_person_idx` (`person_id` ASC),
  CONSTRAINT `works_on_fk_type`
    FOREIGN KEY (`type_id`)
    REFERENCES `ayfm`.`assignment_type` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `works_on_fk_person`
    FOREIGN KEY (`person_id`)
    REFERENCES `ayfm`.`person` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `ayfm`.`assignment_type`
-- -----------------------------------------------------
START TRANSACTION;
USE `ayfm`;
INSERT INTO `ayfm`.`assignment_type` (`id`, `description`) VALUES (1, 'Bible Reading');
INSERT INTO `ayfm`.`assignment_type` (`id`, `description`) VALUES (2, 'Initial Call');
INSERT INTO `ayfm`.`assignment_type` (`id`, `description`) VALUES (3, 'Return Visit');
INSERT INTO `ayfm`.`assignment_type` (`id`, `description`) VALUES (4, 'Bible Study');

COMMIT;

