/*充电站表*/
CREATE TABLE `c_station` (
`id`  int NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`code`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '代码' ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '站名' ,
`address`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '地址' ,
`longitude`  float NULL COMMENT '经度' ,
`latitude`  float NULL COMMENT '纬度' ,
`pileCount`  int NULL DEFAULT 0 COMMENT '充电桩数量' ,
`status`  tinyint NULL DEFAULT 0 COMMENT '状态（0 停止运营，1 正常运营）' AFTER `pileCount` ,
`createTime`  datetime NULL COMMENT '创建时间' ,
`updateTime`  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
PRIMARY KEY (`id`) ,
UNIQUE INDEX `id` (`id`)
)
COMMENT='充电站表'
;

/*充电桩表*/
CREATE TABLE `c_pile` (
`id`  int NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`code`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '充电桩代码' ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '充电桩名字' ,
`stationId`  int NULL COMMENT '充电站ID' ,
`brandId`  int NULL COMMENT '充电桩品牌ID' ,
`modelId`  int NULL COMMENT '充电桩型号ID' ,
`pileType`  tinyint NULL DEFAULT 1 COMMENT '充电桩类型（1 直流，2 交流）' ,
`gunType`  tinyint NULL DEFAULT 1 COMMENT '充电枪类型（1 国标，2 美标）' ,
`rate`  tinyint NULL DEFAULT 2 COMMENT '充电速率（1 慢速，2 快速，3 超快速）' ,
`status`  tinyint NULL DEFAULT 0 COMMENT '充电桩状态（0 空闲，1 充电）' ,
`createTime`  datetime NULL COMMENT '添加时间' ,
`updateTime`  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `id` (`id`) 
)
COMMENT='充电桩表'
;

/*充电桩品牌表*/
CREATE TABLE `c_brand` (
`id`  int NOT NULL AUTO_INCREMENT ,
`code`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '品牌代码' ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '品牌名称' ,
`logo`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '品牌logo url' ,
`createTime`  datetime NULL COMMENT '添加时间' ,
`updateTime`  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `id` (`id`) 
)
COMMENT='充电桩品牌表'
;

/*充电桩型号表*/
CREATE TABLE `c_model` (
`id`  int NOT NULL AUTO_INCREMENT ,
`code`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '型号代码' ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '型号名称' ,
`brandId`  int NULL COMMENT '品牌ID' ,
`createTime`  datetime NULL COMMENT '添加时间' ,
`updateTime`  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `id` (`id`) 
)
COMMENT='充电桩型号表'
;

/*管理员表*/
CREATE TABLE `c_admin` (
`id`  int NOT NULL AUTO_INCREMENT COMMENT '主键' ,
`userName`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名' ,
`nickName`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '昵称' ,
`pwd`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码' ,
`salt`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '密码加密随机字符串' ,
`createTime`  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '添加时间' ,
`updateTime`  datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' ,
PRIMARY KEY (`id`),
UNIQUE INDEX `id` (`id`) COMMENT '主键索引'
)
COMMENT='管理员表'
;