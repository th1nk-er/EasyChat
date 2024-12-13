-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- 主机： 10.61.197.125
-- 生成日期： 2024-12-13 04:35:59
-- 服务器版本： 8.4.2
-- PHP 版本： 8.2.24

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `easychat`
--

-- --------------------------------------------------------

--
-- 表的结构 `ec_chat_message`
--

CREATE TABLE `ec_chat_message` (
  `id` int NOT NULL COMMENT '主键ID',
  `sender_id` int DEFAULT NULL COMMENT '发送者ID',
  `chat_type` tinyint DEFAULT NULL COMMENT '聊天类型',
  `receiver_id` int DEFAULT NULL COMMENT '接收者ID',
  `message_type` int DEFAULT NULL COMMENT '消息类型',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '消息内容',
  `params` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '命令参数',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_group`
--

CREATE TABLE `ec_group` (
  `group_id` int NOT NULL COMMENT '群组ID',
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '群组名称',
  `group_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '群组描述信息',
  `status` tinyint DEFAULT NULL COMMENT '群组状态',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '群组头像',
  `deleted` tinyint DEFAULT NULL COMMENT '是否已删除',
  `create_time` datetime DEFAULT NULL COMMENT '群组创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_group_member`
--

CREATE TABLE `ec_group_member` (
  `id` int NOT NULL COMMENT '主键ID',
  `group_id` int DEFAULT NULL COMMENT '群组ID',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `user_group_nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户群昵称',
  `group_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户给群组的备注',
  `role` tinyint DEFAULT NULL COMMENT '用户角色',
  `muted` tinyint DEFAULT NULL COMMENT '是否静音',
  `deleted` tinyint DEFAULT NULL COMMENT '是否删除',
  `create_time` datetime DEFAULT NULL COMMENT '加入时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_group_member_ignored`
--

CREATE TABLE `ec_group_member_ignored` (
  `id` int NOT NULL COMMENT '主键ID',
  `group_id` int DEFAULT NULL COMMENT '群组ID',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `ignored_id` int DEFAULT NULL COMMENT '屏蔽的用户ID',
  `ignored` tinyint DEFAULT NULL COMMENT '是否屏蔽',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 表的结构 `ec_group_member_muted`
--

CREATE TABLE `ec_group_member_muted` (
  `id` int NOT NULL COMMENT '主键ID',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `group_id` int DEFAULT NULL COMMENT '群聊ID',
  `muted` tinyint DEFAULT NULL COMMENT '是否禁言',
  `mute_time` datetime DEFAULT NULL COMMENT '禁言起始时间',
  `unmute_time` datetime DEFAULT NULL COMMENT '禁言结束时间',
  `admin_id` int DEFAULT NULL COMMENT '执行禁言的管理员ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 表的结构 `ec_group_notification`
--

CREATE TABLE `ec_group_notification` (
  `id` int NOT NULL COMMENT '主键ID',
  `group_id` int DEFAULT NULL COMMENT '群组ID',
  `target_id` int DEFAULT NULL COMMENT '目标用户ID',
  `operator_id` int DEFAULT NULL COMMENT '操作用户ID',
  `type` tinyint DEFAULT NULL COMMENT '通知类型',
  `create_time` datetime DEFAULT NULL COMMENT '通知创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_user`
--

CREATE TABLE `ec_user` (
  `id` int NOT NULL COMMENT '主键ID',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户密码',
  `email` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '头像地址',
  `role` tinyint DEFAULT '0' COMMENT '用户角色 0-普通用户 1-管理员',
  `sex` tinyint DEFAULT '2' COMMENT '性别 0-男 1-女 2-保密',
  `register_ip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '注册时IP地址',
  `login_ip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '登录时IP',
  `locked` tinyint DEFAULT '0' COMMENT '是否锁定 0-正常 1-锁定',
  `deleted` tinyint DEFAULT '0' COMMENT '是否删除 0-正常 1-已删除',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_user_add_friend`
--

CREATE TABLE `ec_user_add_friend` (
  `id` int NOT NULL COMMENT '主键ID',
  `uid` int DEFAULT NULL COMMENT '用户ID',
  `stranger_id` int DEFAULT NULL COMMENT '陌生人用户ID',
  `add_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '添加时的附加消息',
  `create_time` datetime DEFAULT NULL COMMENT '添加时间',
  `status` tinyint DEFAULT NULL COMMENT '处理状态 0-未处理 1-已同意 2-已拒绝 3-已忽略',
  `add_type` tinyint DEFAULT NULL COMMENT '添加状态 0-添加对方 1-被对方添加'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_user_conversation`
--

CREATE TABLE `ec_user_conversation` (
  `id` int NOT NULL COMMENT '主键ID',
  `uid` int DEFAULT NULL COMMENT '用户ID',
  `chat_id` int DEFAULT NULL COMMENT '对方ID',
  `chat_type` tinyint DEFAULT NULL COMMENT '聊天类型',
  `unread_count` int DEFAULT NULL COMMENT '未读消息数量',
  `last_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '最后一条消息内容',
  `message_type` tinyint DEFAULT NULL COMMENT '最后一条消息的类型',
  `update_time` datetime DEFAULT NULL COMMENT '最后消息时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_user_friend`
--

CREATE TABLE `ec_user_friend` (
  `id` int NOT NULL COMMENT '主键ID',
  `uid` int DEFAULT NULL COMMENT '用户ID',
  `friend_id` int DEFAULT NULL COMMENT '好友的用户ID',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '好友备注',
  `muted` tinyint DEFAULT NULL COMMENT '是否免打扰 0-正常 1-免打扰',
  `create_time` datetime DEFAULT NULL COMMENT '添加好友的时间',
  `deleted` tinyint DEFAULT NULL COMMENT '是否已删除 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

-- --------------------------------------------------------

--
-- 表的结构 `ec_user_token`
--

CREATE TABLE `ec_user_token` (
  `id` int NOT NULL COMMENT '主键ID',
  `user_id` int DEFAULT NULL COMMENT '用户ID',
  `login_ip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '登录IP',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '设备UA',
  `token` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '用户Token',
  `issue_time` datetime DEFAULT NULL COMMENT '签发时间',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

--
-- 转储表的索引
--

--
-- 表的索引 `ec_chat_message`
--
ALTER TABLE `ec_chat_message`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `ec_group`
--
ALTER TABLE `ec_group`
  ADD PRIMARY KEY (`group_id`) USING BTREE;

--
-- 表的索引 `ec_group_member`
--
ALTER TABLE `ec_group_member`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `ec_group_member_ignored`
--
ALTER TABLE `ec_group_member_ignored`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `ec_group_member_muted`
--
ALTER TABLE `ec_group_member_muted`
  ADD PRIMARY KEY (`id`);

--
-- 表的索引 `ec_group_notification`
--
ALTER TABLE `ec_group_notification`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `ec_user`
--
ALTER TABLE `ec_user`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `ec_user_add_friend`
--
ALTER TABLE `ec_user_add_friend`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `ec_user_conversation`
--
ALTER TABLE `ec_user_conversation`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `ec_user_friend`
--
ALTER TABLE `ec_user_friend`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 表的索引 `ec_user_token`
--
ALTER TABLE `ec_user_token`
  ADD PRIMARY KEY (`id`) USING BTREE;

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `ec_chat_message`
--
ALTER TABLE `ec_chat_message`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_group`
--
ALTER TABLE `ec_group`
  MODIFY `group_id` int NOT NULL AUTO_INCREMENT COMMENT '群组ID';

--
-- 使用表AUTO_INCREMENT `ec_group_member`
--
ALTER TABLE `ec_group_member`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_group_member_ignored`
--
ALTER TABLE `ec_group_member_ignored`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_group_member_muted`
--
ALTER TABLE `ec_group_member_muted`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_group_notification`
--
ALTER TABLE `ec_group_notification`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_user`
--
ALTER TABLE `ec_user`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_user_add_friend`
--
ALTER TABLE `ec_user_add_friend`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_user_conversation`
--
ALTER TABLE `ec_user_conversation`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_user_friend`
--
ALTER TABLE `ec_user_friend`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';

--
-- 使用表AUTO_INCREMENT `ec_user_token`
--
ALTER TABLE `ec_user_token`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID';
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
