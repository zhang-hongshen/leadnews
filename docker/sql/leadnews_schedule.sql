CREATE DATABASE IF NOT EXISTS leadnews_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leadnews_schedule;
SET NAMES utf8;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
    `id` bigint NOT NULL PRIMARY KEY,
    `execute_time` datetime(3) NOT NULL COMMENT '执行时间',
    `parameters` longblob COMMENT '参数',
    `priority` int(11) NOT NULL COMMENT '优先级',
    `task_type` int(11) NOT NULL COMMENT '任务类型',
    create_time datetime DEFAULT CURRENT_TIMESTAMP ,
    update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY `index_taskinfo_time` (`task_type`,`priority`,`execute_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of task
-- ----------------------------

-- ----------------------------
-- Table structure for task_log
-- ----------------------------
DROP TABLE IF EXISTS `task_log`;
CREATE TABLE `task_log` (
    `id` bigint NOT NULL PRIMARY KEY,
    `task_id` bigint NOT NULL COMMENT '任务id',
    `execute_time` datetime(3) NOT NULL COMMENT '执行时间',
    `parameters` longblob COMMENT '参数',
    `priority` int(11) NOT NULL COMMENT '优先级',
    `task_type` int(11) NOT NULL COMMENT '任务类型',
    `version` int(11) NOT NULL COMMENT '版本号,用乐观锁',
    `status` int(11) DEFAULT 0 COMMENT '状态 0=初始化状态 1=EXECUTED 2=CANCELLED',
    create_time datetime DEFAULT CURRENT_TIMESTAMP ,
    update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of task_log
-- ----------------------------
