/*
* Vaisagh's update
*/


-- ------------------------
-- Fixes for `players` table
-- ------------------------
ALTER TABLE `playerLocation` CHANGE `time` `time` BIGINT UNSIGNED;


-- ----------------------------
-- Procedure structure for `addNewLocation`
-- ----------------------------
DROP PROCEDURE IF EXISTS `addNewLocation`;
DELIMITER ;;
CREATE DEFINER=CURRENT_USER PROCEDURE `addNewLocation`(`i_uuid` varchar(255),`i_time` BIGINT, `i_x` int,`i_y` int,`i_z` int)
BEGIN   INSERT IGNORE INTO playerLocation VALUES(i_uuid,i_time,i_x,i_y,i_z); END
;;
DELIMITER ;


-- ---------------------------
-- Update DBVersion To 12
-- --------------------------

UPDATE `config` SET `dbVersion` = 12;