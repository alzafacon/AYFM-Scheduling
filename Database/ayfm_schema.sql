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
  `is_active` TINYINT(1) NOT NULL DEFAULT '1',
  `gender` CHAR(1) NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `is_eligible_reading` TINYINT(1) NOT NULL DEFAULT 0,
  `is_eligible_init_call` TINYINT(1) NOT NULL DEFAULT 0,
  `is_eligible_ret_visit` TINYINT(1) NOT NULL DEFAULT 0,
  `is_eligible_bib_study` TINYINT(1) NOT NULL DEFAULT 0,
  `is_eligible_talk` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `full_name_UNIQUE` (`last_name` ASC, `first_name` ASC))
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
  `classroom` INT(11) NOT NULL,
  `type` INT(11) NOT NULL,
  `is_completed` TINYINT(1) NULL DEFAULT '0',
  `is_passed` TINYINT(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `week_ix` (`week` ASC),
  INDEX `assgn_fk_person_id_idx` (`assignee` ASC),
  INDEX `assgn_fk_person_householder_idx` (`householder` ASC),
  CONSTRAINT `assignment_fk_person_assignee`
    FOREIGN KEY (`assignee`)
    REFERENCES `ayfm`.`person` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `assignment_fk_person_householder`
    FOREIGN KEY (`householder`)
    REFERENCES `ayfm`.`person` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
