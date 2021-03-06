SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `nocturne` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `nocturne` ;

-- -----------------------------------------------------
-- Table `nocturne`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`users` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`users` (
  `_id` INT NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `name_first` VARCHAR(45) NOT NULL,
  `name_last` VARCHAR(45) NOT NULL,
  `email1` VARCHAR(45) NOT NULL,
  `phone_mbl` VARCHAR(45) NOT NULL,
  `phone_home` VARCHAR(45) NULL,
  `addr_line1` VARCHAR(45) NOT NULL,
  `addr_line2` VARCHAR(45) NULL,
  `addr_line3` VARCHAR(45) NULL,
  `postcode` VARCHAR(45) NOT NULL,
  `registration_status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`conditions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`conditions` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`conditions` (
  `_id` INT NOT NULL,
  `condition_name` VARCHAR(45) NOT NULL,
  `condition_desc` VARCHAR(45) NULL,
  PRIMARY KEY (`_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`user_condition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`user_condition` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`user_condition` (
  `user_id` INT NOT NULL,
  `condition_id` INT NOT NULL,
  INDEX `condition_id_idx` (),
  PRIMARY KEY (`user_id`, `condition_id`),
  CONSTRAINT `user_id`
    FOREIGN KEY ()
    REFERENCES `nocturne`.`users` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `condition_id`
    FOREIGN KEY ()
    REFERENCES `nocturne`.`conditions` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`user_connect`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`user_connect` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`user_connect` (
  `patient_user_id` VARCHAR(45) NOT NULL,
  `caregiver_user_id` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`patient_user_id`, `caregiver_user_id`),
  INDEX `user2_id_idx` (),
  CONSTRAINT `user1_id`
    FOREIGN KEY ()
    REFERENCES `nocturne`.`users` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user2_id`
    FOREIGN KEY ()
    REFERENCES `nocturne`.`users` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`alerts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`alerts` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`alerts` (
  `_id` INT NOT NULL,
  `user_Id` INT NOT NULL,
  `alert_name` VARCHAR(45) NOT NULL,
  `alert_desc` VARCHAR(45) NULL,
  `response` VARCHAR(255) NULL,
  `response_sent` TINYINT(1) NULL,
  PRIMARY KEY (`_id`),
  INDEX `user_id_idx` (),
  CONSTRAINT `user_id`
    FOREIGN KEY ()
    REFERENCES `nocturne`.`users` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`sensor`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`sensor` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`sensor` (
  `_id` INT NOT NULL,
  `sensor_name` VARCHAR(45) NULL,
  `sensor_desc` VARCHAR(45) NULL,
  PRIMARY KEY (`_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`sensor_timeperiods`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`sensor_timeperiods` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`sensor_timeperiods` (
  `_id` INT NOT NULL,
  `start_time` VARCHAR(45) NULL,
  `stop_time` VARCHAR(45) NULL,
  `sensor_value_exprected` VARCHAR(45) NULL,
  `sensor_warm_time` VARCHAR(45) NULL,
  `sensor_alert_time` VARCHAR(45) NULL,
  `sensor_id` INT NOT NULL,
  PRIMARY KEY (`_id`, `sensor_id`),
  INDEX `fk_sensor_timeperiods_sensor1_idx` (`sensor_id` ASC),
  CONSTRAINT `fk_sensor_timeperiods_sensor1`
    FOREIGN KEY (`sensor_id`)
    REFERENCES `nocturne`.`sensor` (`_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`sensor_reading`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`sensor_reading` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`sensor_reading` (
  `_id` INT NOT NULL,
  `sensor_id` INT NOT NULL,
  `sensor_value` VARCHAR(45) NOT NULL,
  `sensor_reading_time` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`_id`, `sensor_id`),
  INDEX `fk_sensor_reading_sensors1_idx` (`sensor_id` ASC),
  CONSTRAINT `fk_sensor_reading_sensors1`
    FOREIGN KEY (`sensor_id`)
    REFERENCES `nocturne`.`sensor` (`_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `nocturne`.`user_sensors`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nocturne`.`user_sensors` ;

CREATE TABLE IF NOT EXISTS `nocturne`.`user_sensors` (
  `user_id` INT NOT NULL,
  `sensor_timeperiods_id` INT NOT NULL,
  `sensor_reading__id` INT NOT NULL,
  `sensor_reading_sensor_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `sensor_timeperiods_id`),
  INDEX `fk_user_sensors_sensor_timeperiods1_idx` (`sensor_timeperiods_id` ASC),
  INDEX `fk_user_sensors_sensor_reading1_idx` (`sensor_reading__id` ASC, `sensor_reading_sensor_id` ASC),
  CONSTRAINT `user_id`
    FOREIGN KEY ()
    REFERENCES `nocturne`.`users` ()
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_sensors_sensor_timeperiods1`
    FOREIGN KEY (`sensor_timeperiods_id`)
    REFERENCES `nocturne`.`sensor_timeperiods` (`_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_sensors_sensor_reading1`
    FOREIGN KEY (`sensor_reading__id` , `sensor_reading_sensor_id`)
    REFERENCES `nocturne`.`sensor_reading` (`_id` , `sensor_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
