/**
 * 移动端rem适配
 * 基于设计稿宽度375px，1rem = 37.5px
 */
(function () {
  function setRem() {
    const designWidth = 375; // 设计稿宽度
    const baseSize = 37.5; // 基准值，1rem = 37.5px

    // 获取当前屏幕宽度
    const scale = document.documentElement.clientWidth / designWidth;

    // 设置根元素字体大小
    document.documentElement.style.fontSize =
      baseSize * Math.min(scale, 2) + "px";
  }

  // 初始化
  setRem();

  // 监听窗口大小变化
  window.addEventListener("resize", setRem);

  // 监听屏幕旋转
  window.addEventListener("orientationchange", () => {
    setTimeout(setRem, 100);
  });
})();
