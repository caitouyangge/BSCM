/**
 * Vant工具函数封装
 * 用于统一移动端提示和对话框
 */
import {
  showToast as vantShowToast,
  showConfirmDialog as vantShowConfirmDialog,
} from "vant";

/**
 * 显示成功提示
 */
export function showSuccess(message) {
  return vantShowToast.success(message);
}

/**
 * 显示失败提示
 */
export function showFail(message) {
  return vantShowToast.fail(message);
}

/**
 * 显示普通提示
 */
export function showMessage(message) {
  return vantShowToast(message);
}

/**
 * 显示确认对话框
 */
export function showConfirm(options) {
  return vantShowConfirmDialog(options);
}
