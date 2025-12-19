<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// BSCM - AI智能诊断App
</script>

<style>
@import './styles/mobile.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
  background-color: #f5f5f5;
  width: 100%;
  max-width: 100vw;
  overflow-x: hidden;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  #app {
    font-size: 14px;
  }
}
</style>

