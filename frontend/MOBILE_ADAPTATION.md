# 移动端适配说明

## 适配方案

本项目采用**双UI库方案**，同时支持移动端和桌面端：

- **移动端**：使用 Vant UI 组件库
- **桌面端**：使用 Element Plus UI 组件库
- **自动检测**：根据设备类型自动切换UI

## 技术实现

### 1. Viewport 适配

在 `index.html` 中配置了移动端viewport：

```html
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no, viewport-fit=cover">
```

### 2. PostCSS px-to-viewport

使用 `postcss-px-to-viewport` 自动将px转换为vw单位：

- 设计稿宽度：375px
- 转换单位：vw
- 排除：node_modules

### 3. Rem 适配（可选）

提供了 `src/utils/rem.js` 用于rem适配方案，可根据需要启用。

### 4. 响应式布局

所有页面组件都实现了：
- 移动端布局（使用Vant组件）
- 桌面端布局（使用Element Plus组件）
- 自动检测设备类型并切换

## 使用方法

### 安装依赖

```bash
cd frontend
npm install
```

### 开发调试

```bash
npm run dev
```

### 移动端调试

1. 使用浏览器开发者工具的移动设备模拟器
2. 或使用手机访问开发服务器（确保在同一网络）

## 移动端优化

### 1. 触摸优化
- 禁用文本选择（输入框除外）
- 禁用双击缩放
- 优化触摸反馈

### 2. 安全区域适配
- 支持iPhone X等设备的刘海屏
- 使用 `env(safe-area-inset-*)` CSS变量

### 3. 性能优化
- 按需加载组件
- 优化图片加载
- 减少重绘重排

## 组件使用

### 移动端组件（Vant）

```vue
<template>
  <van-button type="primary">按钮</van-button>
  <van-cell title="单元格" />
</template>
```

### 桌面端组件（Element Plus）

```vue
<template>
  <el-button type="primary">按钮</el-button>
  <el-table :data="tableData" />
</template>
```

### 条件渲染

```vue
<template>
  <!-- 移动端 -->
  <div v-if="isMobileDevice">
    <van-nav-bar title="标题" />
  </div>
  
  <!-- 桌面端 -->
  <div v-else>
    <el-header>标题</el-header>
  </div>
</template>

<script setup>
import { isMobile } from '../utils/mobile'
const isMobileDevice = isMobile()
</script>
```

## 样式适配

### 移动端样式单位

- 使用 `rem` 或 `vw` 单位
- 避免使用固定px值
- 使用响应式布局

### 响应式断点

- 移动端：`max-width: 768px`
- 桌面端：`min-width: 769px`

## 注意事项

1. **设计稿尺寸**：默认基于375px设计稿，如需调整请修改 `postcss.config.js`
2. **组件选择**：移动端优先使用Vant组件，桌面端使用Element Plus
3. **测试**：务必在真实移动设备上测试
4. **性能**：注意组件按需加载，避免打包体积过大

## 常见问题

### Q: 如何调整设计稿基准宽度？
A: 修改 `postcss.config.js` 中的 `viewportWidth` 值

### Q: 如何禁用某个元素的px转换？
A: 在类名中添加 `.ignore` 或 `.hairlines`

### Q: 移动端和桌面端样式冲突？
A: 使用条件渲染或媒体查询隔离样式


