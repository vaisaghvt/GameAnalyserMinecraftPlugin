/*
* Vaisagh's update
*/

-- ----------------------------
-- Table structure for `playerLocation`
-- ----------------------------
DROP TABLE IF EXISTS `playerLocation`;
CREATE TABLE `playerLocation` (
  `uuid` varchar(255) NOT NULL,
  `time` int(11) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  PRIMARY KEY (`uuid`,`time`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


-- ----------------------------
-- Procedure structure for `addNewLocation`
-- ----------------------------
DROP PROCEDURE IF EXISTS `addNewLocation`;
DELIMITER ;;
CREATE DEFINER=CURRENT_USER PROCEDURE `addNewLocation`(`i_uuid` varchar(255),`i_time` int, `i_x` int,`i_y` int,`i_z` int)
BEGIN   INSERT INTO playerLocation VALUES(i_uuid,i_time,i_x,i_y,i_z); END
;;
DELIMITER ;


-- ---------------------------
-- Update DBVersion To 11
-- --------------------------

UPDATE `config` SET `dbVersion` = 11;