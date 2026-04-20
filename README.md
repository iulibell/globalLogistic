# globalMallToLogistics

面向「商城 / 运营 → 物流」的一体化微服务示例工程：订单（OMS）、运输（TMS）、仓储（WMS）、认证与系统用户、网关聚合、消息等模块拆分部署，通过 **Nacos** 做服务发现，通过 **OpenFeign** 做服务间调用；部分跨服务写库链路使用 **Seata AT** 做分布式事务协调。

## 技术栈

| 类别 | 说明 |
|------|------|
| 运行时 | Java 17 |
| 框架 | Spring Boot **4.0.5**、Spring Cloud **2025.1.0** |
| 阿里云生态 | Spring Cloud Alibaba **2025.1.0.0**（Nacos 注册发现） |
| 数据访问 | MyBatis-Plus **3.5.15**（`mybatis-plus-spring-boot4-starter`） |
| 认证 | Sa-Token **1.45.0**（JWT 等配置见各模块 `application.yml`） |
| 消息 | Spring AMQP（RabbitMQ） |
| 缓存 | Spring Data Redis |
| 分布式事务 | Apache Seata **2.4.0**（`seata-spring-boot-starter`，见 OMS / WMS / TMS） |

## 模块说明

| 模块 | 说明 |
|------|------|
| `gl-common` | 公共组件、通用配置与工具 |
| `gl-gateway` | Spring Cloud Gateway，统一入口与路由 |
| `gl-auth` | 认证相关接口 |
| `gl-system` | 系统用户、登录注册等 |
| `logi-admin` | 管理端聚合 / BFF 类能力 |
| `logi-oms` | 订单服务 |
| `logi-tms` | 运输 / 司机 / 线路等 |
| `logi-wms` | 仓储、库存锁、出入库等 |
| `gl-message` | 消息相关服务 |

## 环境依赖

本地开发至少需要：

1. **JDK 17**
2. **Maven 3.9+**（建议）
3. **MySQL 8.x**，库名示例：`global_logistic`（与 `application.yml` 中 JDBC 一致）
4. **Redis**（默认 `127.0.0.1:6379`）
5. **RabbitMQ**（默认 `127.0.0.1:5672`，guest/guest）
6. **Nacos**（默认 `127.0.0.1:8848`，`DEFAULT_GROUP`）
7. **Seata Server**（TC，与客户端 **2.4.x** 对齐；默认客户端指向 `127.0.0.1:8091`）

各业务模块中的中间件地址可通过环境变量覆盖（如 `NACOS_SERVER_ADDR`、`RABBITMQ_HOST` 等，见各 `application.yml`）。

## 网关路由（默认）

网关端口：`8502`（可用环境变量 `GATEWAY_PORT` 覆盖）。

| 路径前缀 | 目标服务 |
|----------|----------|
| `/system/**` | `gl-system` |
| `/auth/**` | `gl-auth` |
| `/admin/**` | `logi-admin` |
| `/tms/**` | `logi-tms` |
| `/oms/**` | `logi-oms` |
| `/wms/**` | `logi-wms` |

无 Nacos 时，可通过网关 `application.yml` 中的 `SERVICE_URI_*` 指向直连 `http://host:port`。

## 默认服务端口（开发）

| 服务 | 默认端口 |
|------|----------|
| `gl-system` | 8501 |
| `gl-gateway` | 8502 |
| `gl-auth` | 8602 |
| `logi-admin` | 8601 |
| `logi-tms` | 8701 |
| `logi-oms` | 8711 |
| `logi-wms` | 8721 |
| `gl-message` | 8731（`SERVER_PORT`） |

## 构建

在项目根目录执行：

```bash
mvn clean install -DskipTests
```

仅编译部分模块示例：

```bash
mvn -pl logi-oms,logi-wms,logi-tms -am -DskipTests compile
```

## 运行

在对应模块目录下，或使用 `-pl` 指定模块启动 Spring Boot 主类（各模块 `*Application.java`）。启动顺序建议：

1. 基础设施：MySQL、Redis、RabbitMQ、Nacos、Seata TC  
2. 各业务微服务（`gl-system`、`gl-auth`、`logi-*` 等）  
3. `gl-gateway`（若需统一入口）

## 数据库与 Seata

### 业务库

创建数据库（名称需与配置一致），并导入项目所需业务表结构（若仓库内未附带 SQL，请按团队现有脚本初始化）。

### `undo_log`（Seata AT 模式）

在参与 AT 事务的 **MySQL 库**中创建 `undo_log` 表。当前 OMS / WMS / TMS 默认指向同一库 `global_logistic`，**只需在该库创建一次**。

```sql
CREATE TABLE IF NOT EXISTS `undo_log`
(
  `branch_id`     BIGINT       NOT NULL COMMENT 'branch transaction id',
  `xid`           VARCHAR(128) NOT NULL COMMENT 'global transaction id',
  `context`       VARCHAR(128) NOT NULL COMMENT 'undo_log context, such as serializer',
  `rollback_info` LONGBLOB     NOT NULL COMMENT 'rollback info',
  `log_status`    INT          NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created`   DATETIME(6)  NOT NULL COMMENT 'create datetime',
  `log_modified`  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
  `ext`           VARCHAR(100)          DEFAULT NULL COMMENT 'reserved field',
  PRIMARY KEY (`branch_id`, `xid`),
  UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='AT transaction mode undo table';
```

### Seata 客户端配置（OMS / WMS / TMS）

三者在 `application.yml` 中已配置 **file 注册中心 + 直连 TC** 的开发默认值，并支持环境变量覆盖：

| 环境变量 | 含义 | 默认值 |
|----------|------|--------|
| `SEATA_TX_SERVICE_GROUP` | 事务分组名 | `global_logistic_tx_group` |
| `SEATA_VGROUP_MAPPING` | vgroup 映射的集群名 | `default` |
| `SEATA_SERVER_ADDR` | TC 地址（`host:port`） | `127.0.0.1:8091` |

生产环境建议将 Seata 的 **registry / config** 改为 **Nacos** 等与运维一致的方案，并保证 TC 集群与 `vgroup-mapping` 一致。

### 说明：Seata 与 Redis / MQ

Seata AT 只协调 **JDBC 数据源**上的分支。订单超时、派单等场景若同时使用 **Redis、RabbitMQ**，其状态不会随全局事务自动回滚，需在业务或补偿流程中单独设计。

## 安全与内部调用

部分 `/oms`、`/wms`、`/tms` 下的系统接口支持内部令牌校验（见各模块 `security.internal`）。开发环境默认多为关闭，上线前请按环境调整 `SECURITY_INTERNAL_TOKEN` 与 `enabled` 等配置。

## 许可证

见项目根目录许可证文件（若未添加，请由仓库维护者补充）。
