<template>
  <div class="profile">
    <!-- 移动端布局 -->
    <div class="mobile-layout" v-if="isMobileDevice">
      <van-nav-bar
        title="个人中心"
        left-arrow
        @click-left="goBack"
        fixed
      />
      <div class="mobile-content">
        <van-cell-group inset>
          <van-field
            v-model="userInfo.username"
            label="用户名"
            placeholder="请输入用户名"
          />
          <van-field
            v-model="userInfo.email"
            label="邮箱"
            placeholder="请输入邮箱"
            type="email"
          />
          <van-field
            v-model="userInfo.phone"
            label="手机号"
            placeholder="请输入手机号"
            type="tel"
          />
        </van-cell-group>
        
        <div class="button-group">
          <van-button type="primary" size="large" block @click="saveProfile">
            保存
          </van-button>
        </div>
      </div>
    </div>
    
    <!-- 桌面端布局 -->
    <el-container class="desktop-layout" v-else>
      <el-header>
        <el-page-header @back="goBack">
          <template #content>
            <span class="text-large font-600 mr-3">个人中心</span>
          </template>
        </el-page-header>
      </el-header>
      <el-main>
        <el-card>
          <el-form :model="userInfo" label-width="120px">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userInfo.phone" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { showToast } from 'vant'
import { isMobile } from '../utils/mobile'

const router = useRouter()
const isMobileDevice = ref(false)
const userInfo = ref({
  username: '用户',
  email: '',
  phone: ''
})

onMounted(() => {
  isMobileDevice.value = isMobile()
})

const saveProfile = () => {
  if (isMobileDevice.value) {
    showToast.success('保存成功')
  } else {
    ElMessage.success('保存成功')
  }
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
}

/* 移动端样式 */
.mobile-layout {
  padding-top: 46px;
  padding-bottom: env(safe-area-inset-bottom);
  background-color: #f7f8fa;
  min-height: 100vh;
}

.mobile-content {
  padding: 0.3rem;
}

.button-group {
  margin-top: 0.4rem;
  padding: 0 0.3rem;
}

/* 桌面端样式 */
.desktop-layout {
  min-height: 100vh;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.el-main {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .desktop-layout {
    display: none;
  }
}

@media screen and (min-width: 769px) {
  .mobile-layout {
    display: none;
  }
}
</style>
