# BSCM - AI智能诊断App

一个基于Vue3前端和SpringBoot后端的AI智能诊断移动应用。

## 项目结构

```
BSCM/
├── frontend/          # Vue3前端项目
├── backend/           # SpringBoot后端项目
└── README.md         # 项目说明文档
```

## 技术栈

### 前端
- Vue 3
- Vite
- Vue Router
- Pinia (状态管理)
- Vant (移动端UI组件库)
- Element Plus (桌面端UI组件库)
- PostCSS px-to-viewport (移动端适配)

### 后端
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- AI模型集成

## 快速开始

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

### 后端开发

```bash
cd backend
mvn spring-boot:run
# 或使用IDE运行 Application.java
```

## 功能特性

- AI智能诊断
- 用户管理
- 诊断历史记录
- 报告生成

## 移动端适配

项目已实现完整的移动端适配方案：

- ✅ 双UI库支持（Vant移动端 + Element Plus桌面端）
- ✅ 自动设备检测和UI切换
- ✅ PostCSS px-to-viewport自动转换
- ✅ 响应式布局设计
- ✅ 移动端触摸优化
- ✅ 安全区域适配（支持刘海屏）

详细说明请查看：[移动端适配文档](./frontend/MOBILE_ADAPTATION.md)

## 开发计划

- [x] 移动端适配
- [ ] 用户认证系统
- [ ] AI诊断接口
- [ ] 数据管理

