# EasyChat

![GitLab License](https://img.shields.io/gitlab/license/th1nk-er/EasyChat) ![Gitlab Pipeline Status](https://img.shields.io/gitlab/pipeline-status/th1nk-er/EasyChat?branch=master) ![element-plus](https://img.shields.io/badge/Spring_Boot-3-brightgreen.svg)


## 介绍

[EasyChat](https://gitlab.com/th1nk-er/EasyChat)是一个在线聊天室的后端，基于 [SpringBoot](https://github.com/spring-projects/spring-boot) + [Mybatis-Plus](https://github.com/baomidou/mybatis-plus) 实现。项目前端地址:[EasyChat-Web](https://gitlab.com/th1nk-er/EasyChat-Web)

### 技术栈

[SpringBoot](https://github.com/spring-projects/spring-boot) 、[Spring Security](https://github.com/spring-projects/spring-security)、[Spring Doc](https://github.com/springdoc/springdoc-openapi)、[Spring Websocket](https://docs.spring.io/spring-framework/reference/web/websocket/stomp.html)、[Spring Cache](https://docs.spring.io/spring-boot/reference/io/caching.html)、[Mybatis-Plus](https://github.com/baomidou/mybatis-plus)、[Minio](https://github.com/minio/minio)、[Redis](https://github.com/redis/redis)、[MySQL](https://www.mysql.com/)

## 功能列表

### 基础模块

- 登录/注册
- 邮箱验证码
- 消息缓存
- 权限认证

### 用户模块

- 修改个人信息（密码/邮箱/昵称/头像）
- 登录设备管理

### 好友模块

- 添加/删除好友
- 好友备注
- 消息免打扰

### 群聊模块

- 创建群聊
- 群聊备注
- 退出/解散群聊
- 踢出群成员
- 设置/取消管理员身份
- 禁言/解除禁言
- 屏蔽群成员

### 消息模块

- 表情消息
- 图片消息
- 文件消息

## 部署

### 本地部署

1. 修改`application-loca.yaml`，填写数据库用户名和密码，并配置 Spring mail 和 minio 的账号。

```yaml
spring:
  mail:
    host: 
    password: 
    username: 
    port: 
  datasource:
    username: 
    password: 
    url: 
easy-chat:
  minio:
    access-key: 
    secret-key: 
```

2. 启动 MySQL，将数据表结构 `sql/easychat.sql` 导入数据库

3. 启动 redis 和 minio 服务，默认端口`6379`和`9000`
4. 启动项目

```sh
./gradlew bootRun
```

## LICENSE

[MIT](LICENSE)

Copyright (c) 2024 th1nker
