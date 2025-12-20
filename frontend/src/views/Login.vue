<template>
  <div class="login">
    <div class="login-container">
      <div class="login-form">
        <h2 class="form-title">登录</h2>
        <el-form
          :model="loginForm"
          :rules="loginRules"
          ref="loginFormRef"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入手机号"
              size="large"
              maxlength="11"
              clearable
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              clearable
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span
              >还没有账号？<a href="#" @click.prevent="goToRegister"
                >立即注册</a
              ></span
            >
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "../stores/user";
import { authApi } from "../api/auth";
import { Phone, Lock, Message } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const rememberPassword = ref(false);
const showQuickLogin = ref(false);
const codeCountdown = ref(0);

// 响应式对话框宽度
const dialogWidth = computed(() => {
  return window.innerWidth <= 768 ? "90%" : "400px";
});

const loginForm = reactive({
  phone: "",
  password: "",
});

const quickLoginForm = reactive({
  phone: "",
  verificationCode: "",
});

const loginFormRef = ref(null);

const loginRules = {
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
  ],
};

const quickLoginRules = {
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  verificationCode: [
    { required: true, message: "请输入验证码", trigger: "blur" },
    { pattern: /^\d{6}$/, message: "请输入6位验证码", trigger: "blur" },
  ],
};

onMounted(() => {
  // 加载记住的密码
  const rememberedPhone = localStorage.getItem("rememberedPhone");
  const rememberedPassword = localStorage.getItem("rememberedPassword");
  if (rememberedPhone && rememberedPassword) {
    loginForm.phone = rememberedPhone;
    loginForm.password = rememberedPassword;
    rememberPassword.value = true;
  }
});

// 发送验证码
const sendCode = async () => {
  const phone = quickLoginForm.phone || loginForm.phone;
  if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
    ElMessage.error("请输入正确的手机号");
    return;
  }

  try {
    await authApi.sendCode(phone);
    ElMessage.success("验证码已发送");

    // 开始倒计时
    codeCountdown.value = 60;
    const timer = setInterval(() => {
      codeCountdown.value--;
      if (codeCountdown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (error) {
    ElMessage.error(error.response?.data?.message || "发送验证码失败");
  }
};

// 登录
const handleLogin = async () => {
  await loginFormRef.value?.validate();

  loading.value = true;
  try {
    const response = await authApi.login(loginForm.phone, loginForm.password);
    const { token, user } = response.data.data;

    userStore.setToken(token);
    userStore.setUserInfo(user);

    // 记住密码
    if (rememberPassword.value) {
      localStorage.setItem("rememberedPhone", loginForm.phone);
      localStorage.setItem("rememberedPassword", loginForm.password);
    } else {
      localStorage.removeItem("rememberedPhone");
      localStorage.removeItem("rememberedPassword");
    }

    ElMessage.success("登录成功");
    router.push("/");
  } catch (error) {
    ElMessage.error(error.response?.data?.message || "登录失败");
  } finally {
    loading.value = false;
  }
};

const goToRegister = () => {
  router.push("/register");
};
</script>

<style scoped>
.login {
  min-height: 100vh;
  background: var(--background-color);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-form {
  background: white;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-title {
  font-size: 24px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 30px;
  text-align: center;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.quick-login-link {
  color: var(--primary-color);
  font-size: 14px;
  cursor: pointer;
}

.quick-login-link:hover {
  text-decoration: underline;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.form-footer a {
  color: var(--primary-color);
  text-decoration: none;
}

.form-footer a:hover {
  text-decoration: underline;
}

/* ========== 响应式 ========== */
@media screen and (max-width: 768px) {
  .login {
    padding: 16px;
    align-items: flex-start;
    padding-top: 20px;
  }

  .login-form {
    padding: 24px 20px;
    border-radius: 12px;
  }

  .form-title {
    font-size: 20px;
    margin-bottom: 24px;
  }

  .form-options {
    font-size: 14px;
  }

  .quick-login-link {
    font-size: 13px;
  }
}
</style>
