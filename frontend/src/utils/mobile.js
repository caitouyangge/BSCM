/**
 * 移动端工具函数
 */

/**
 * 检测是否为移动设备
 */
export function isMobile() {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(
    navigator.userAgent
  )
}

/**
 * 检测是否为iOS设备
 */
export function isIOS() {
  return /iPhone|iPad|iPod/i.test(navigator.userAgent)
}

/**
 * 检测是否为Android设备
 */
export function isAndroid() {
  return /Android/i.test(navigator.userAgent)
}

/**
 * 获取设备像素比
 */
export function getDevicePixelRatio() {
  return window.devicePixelRatio || 1
}

/**
 * 禁止页面缩放（移动端）
 */
export function disableZoom() {
  document.addEventListener('touchstart', function (event) {
    if (event.touches.length > 1) {
      event.preventDefault()
    }
  }, { passive: false })

  let lastTouchEnd = 0
  document.addEventListener('touchend', function (event) {
    const now = Date.now()
    if (now - lastTouchEnd <= 300) {
      event.preventDefault()
    }
    lastTouchEnd = now
  }, false)
}


