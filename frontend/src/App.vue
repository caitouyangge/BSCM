<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// BSCM - AI智能诊断App
</script>

<style>
@import "./styles/mobile.css";
@import "./styles/theme.css";

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family:
    -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  background-color: var(--background-color);
  width: 100%;
  max-width: 100vw;
  overflow-x: hidden;
}

/* 桌面端布局优化 */
@media screen and (min-width: 769px) {
  #app {
    background-color: var(--background-color);
  }
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  #app {
    font-size: 14px;
  }
}
</style>
