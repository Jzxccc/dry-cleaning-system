## Why

将现有 Web 版干洗店管理系统重构为 HarmonyOS 原生应用，以支持华为设备用户，提供更好的移动端体验和离线能力。这是扩展用户覆盖范围和提升用户体验的重要机会。

## What Changes

- 创建全新的 HarmonyOS 应用 `dry_cleaning_system_HmOS`
- 实现与现有 Web 版相同的核心功能：客户管理、订单管理、会员充值、统计报表
- 使用 HarmonyOS ArkTS 语言和 ArkUI 框架
- 采用分布式架构，支持与后端 API 通信
- **BREAKING**: 这是一个全新的应用项目，不影响现有 Web 系统

## Capabilities

### New Capabilities
- `hmos-customer-management`: HarmonyOS 客户管理模块，支持客户列表展示、搜索、添加、编辑和删除功能
- `hmos-order-management`: HarmonyOS 订单管理模块，支持订单创建、搜索、状态更新和衣物管理
- `hmos-prepaid-system`: HarmonyOS 会员充值模块，支持客户搜索、充值金额输入、充值记录查看
- `hmos-statistics`: HarmonyOS 统计报表模块，支持日报、月报查询和图表展示
- `hmos-data-sync`: HarmonyOS 数据同步模块，支持与后端 API 的数据交互和本地数据缓存

### Modified Capabilities
<!-- 无现有能力需要修改，这是全新的 HarmonyOS 应用 -->

## Impact

- **新项目目录**: `dry_cleaning_system_HmOS/`
- **新技术栈**: HarmonyOS SDK、ArkTS、ArkUI
- **后端 API**: 复用现有 Spring Boot 后端接口
- **开发工具**: DevEco Studio
- **目标设备**: 华为手机、平板电脑
- **数据存储**: 使用 HarmonyOS 关系型数据库 (RDB) 进行本地缓存
