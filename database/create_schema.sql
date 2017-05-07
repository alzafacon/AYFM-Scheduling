-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

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
  `id_person` INT NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(45) NOT NULL,
  `isactive` TINYINT(1) NOT NULL DEFAULT 1,
  `gender` CHAR(1) NOT NULL,
  PRIMARY KEY (`id_person`),
  UNIQUE INDEX `full_name_UNIQUE` (`full_name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ayfm`.`assignment_type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ayfm`.`assignment_type` ;

CREATE TABLE IF NOT EXISTS `ayfm`.`assignment_type` (
  `id_assignment_type` INT NOT NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`id_assignment_type`),
  UNIQUE INDEX `description_UNIQUE` (`description` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ayfm`.`works_on`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ayfm`.`works_on` ;

CREATE TABLE IF NOT EXISTS `ayfm`.`works_on` (
  `person_id` INT NOT NULL,
  `type_id` INT NOT NULL,
  `count` INT NULL,
  PRIMARY KEY (`person_id`, `type_id`),
  INDEX `works_on_fk_assgn_idx` (`type_id` ASC),
  CONSTRAINT `works_on_fk_person`
    FOREIGN KEY (`person_id`)
    REFERENCES `ayfm`.`person` (`id_person`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `works_on_fk_assgn`
    FOREIGN KEY (`type_id`)
    REFERENCES `ayfm`.`assignment_type` (`id_assignment_type`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ayfm`.`assignment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ayfm`.`assignment` ;

CREATE TABLE IF NOT EXISTS `ayfm`.`assignment` (
  `id_assignment` INT NOT NULL AUTO_INCREMENT,
  `date_assgn` DATE NOT NULL,
  `assignee` INT NOT NULL,
  `householder` INT NULL,
  `lesson` INT NULL,
  `section` CHAR NOT NULL,
  `assgn_type` INT NOT NULL,
  `completed` TINYINT(1) NULL DEFAULT 0,
  `passed` TINYINT(1) NULL,
  INDEX `date_assgn_ix` (`date_assgn` ASC),
  INDEX `assgn_fk_person_idx` (`assignee` ASC),
  INDEX `assgn_fk_hholder_person_idx` (`householder` ASC),
  INDEX `assgn_fk_type_assgn_type_idx` (`assgn_type` ASC),
  PRIMARY KEY (`id_assignment`),
  UNIQUE INDEX `id_assignment_UNIQUE` (`id_assignment` ASC),
  CONSTRAINT `assgn_fk_assignee_person`
    FOREIGN KEY (`assignee`)
    REFERENCES `ayfm`.`person` (`id_person`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `assgn_fk_hholder_person`
    FOREIGN KEY (`householder`)
    REFERENCES `ayfm`.`person` (`id_person`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `assgn_fk_type_assgn_type`
    FOREIGN KEY (`assgn_type`)
    REFERENCES `ayfm`.`assignment_type` (`id_assignment_type`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
