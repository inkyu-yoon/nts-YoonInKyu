CREATE SCHEMA `nts-inkyu` ;

USE nts-inkyu;

CREATE TABLE `nts-inkyu`.`user` (
                                    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
                                    `name` VARCHAR(255) NOT NULL,
                                    `password` VARCHAR(255) NOT NULL,
                                    PRIMARY KEY (`user_id`),
                                    UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);


CREATE TABLE `nts-inkyu`.`post` (
                                    `post_id` BIGINT NOT NULL AUTO_INCREMENT,
                                    `title` VARCHAR(255) NOT NULL,
                                    `body` VARCHAR(255) NOT NULL,
                                    `comment_count` BIGINT NULL,
                                    `like_count` BIGINT NULL,
                                    `view_count` BIGINT NULL,
                                    `created_date` DATETIME(6) NOT NULL,
                                    `hashtags` VARCHAR(255) NULL,
                                    `user_id` BIGINT NULL,
                                    PRIMARY KEY (`post_id`),
                                    INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
                                    FOREIGN KEY (`user_id`)
                                        REFERENCES `nts-inkyu`.`user` (`user_id`)
                                        ON DELETE NO ACTION
                                        ON UPDATE NO ACTION);

CREATE TABLE `nts-inkyu`.`post_like` (
                                         `post_like_id` BIGINT NOT NULL AUTO_INCREMENT,
                                         `user_id` BIGINT NULL,
                                         `post_id` BIGINT NULL,
                                         PRIMARY KEY (`post_like_id`),
                                         INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
                                         INDEX `post_id_idx` (`post_id` ASC) VISIBLE,
                                         FOREIGN KEY (`user_id`)
                                             REFERENCES `nts-inkyu`.`user` (`user_id`)
                                             ON DELETE NO ACTION
                                             ON UPDATE NO ACTION,
                                         FOREIGN KEY (`post_id`)
                                             REFERENCES `nts-inkyu`.`post` (`post_id`)
                                             ON DELETE NO ACTION
                                             ON UPDATE NO ACTION);

CREATE TABLE `nts-inkyu`.`comment` (
                                       `comment_id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `body` VARCHAR(255) NOT NULL,
                                       `created_date` DATETIME(6) NOT NULL,
                                       `is_deleted` TINYINT NOT NULL,
                                       `post_id` BIGINT NULL,
                                       `user_id` BIGINT NULL,
                                       PRIMARY KEY (`comment_id`),
                                       INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
                                       INDEX `post_id_idx` (`post_id` ASC) VISIBLE,
                                       FOREIGN KEY (`user_id`)
                                           REFERENCES `nts-inkyu`.`user` (`user_id`)
                                           ON DELETE NO ACTION
                                           ON UPDATE NO ACTION,
                                       FOREIGN KEY (`post_id`)
                                           REFERENCES `nts-inkyu`.`post` (`post_id`)
                                           ON DELETE NO ACTION
                                           ON UPDATE NO ACTION);

CREATE TABLE `nts-inkyu`.`hashtag` (
                                       `hashtag_id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `name` VARCHAR(45) NULL,
                                       PRIMARY KEY (`hashtag_id`),
                                       UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE);

CREATE TABLE `nts-inkyu`.`post_hashtag` (
                                            `post_hashtag_id` BIGINT NOT NULL AUTO_INCREMENT,
                                            `post_id` BIGINT NULL,
                                            `hashtag_id` BIGINT NULL,
                                            PRIMARY KEY (`post_hashtag_id`),
                                            INDEX `post_id_idx` (`post_id` ASC) VISIBLE,
                                            INDEX `hashtag_id_idx` (`hashtag_id` ASC) VISIBLE,
                                            FOREIGN KEY (`post_id`)
                                                REFERENCES `nts-inkyu`.`post` (`post_id`)
                                                ON DELETE NO ACTION
                                                ON UPDATE NO ACTION,
                                            FOREIGN KEY (`hashtag_id`)
                                                REFERENCES `nts-inkyu`.`hashtag` (`hashtag_id`)
                                                ON DELETE NO ACTION
                                                ON UPDATE NO ACTION);