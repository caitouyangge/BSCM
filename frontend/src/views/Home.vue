<template>
  <div class="home">
    <!-- 移动端布局 -->
    <div class="mobile-layout" v-if="isMobileDevice">
      <van-nav-bar title="BSCM - AI智能诊断" fixed />
      <div class="mobile-content">
        <van-card class="welcome-card-mobile">
          <template #title>
            <h2>欢迎使用AI智能诊断系统</h2>
          </template>
          <template #desc>
            <p>基于先进的人工智能技术，为您提供专业的诊断服务</p>
          </template>
          <template #footer>
            <van-button type="primary" size="large" block @click="goToDiagnosis">
              开始诊断
            </van-button>
          </template>
        </van-card>
        
        <van-grid :column-num="3" :gutter="10" class="feature-grid">
          <van-grid-item>
            <van-icon name="description" size="30" />
            <p>智能分析</p>
          </van-grid-item>
          <van-grid-item>
            <van-icon name="chart-trending-o" size="30" />
            <p>快速诊断</p>
          </van-grid-item>
          <van-grid-item>
            <van-icon name="folder-o" size="30" />
            <p>历史记录</p>
          </van-grid-item>
        </van-grid>
      </div>
    </div>
    
    <!-- 桌面端布局 -->
    <el-container class="desktop-layout" v-else>
      <el-header>
        <h1>BSCM - AI智能诊断</h1>
      </el-header>
      <el-main>
        <el-card class="welcome-card">
          <h2>欢迎使用AI智能诊断系统</h2>
          <p>基于先进的人工智能技术，为您提供专业的诊断服务</p>
          <el-button type="primary" size="large" @click="goToDiagnosis">
            开始诊断
          </el-button>
        </el-card>
        
        <el-row :gutter="20" class="feature-cards">
          <el-col :span="8">
            <el-card shadow="hover">
              <el-icon :size="40"><Document /></el-icon>
              <h3>智能分析</h3>
              <p>基于AI算法进行深度分析</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <el-icon :size="40"><DataAnalysis /></el-icon>
              <h3>快速诊断</h3>
              <p>快速生成诊断报告</p>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover">
              <el-icon :size="40"><FolderOpened /></el-icon>
              <h3>历史记录</h3>
              <p>查看历史诊断记录</p>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document, DataAnalysis, FolderOpened } from '@element-plus/icons-vue'
import { isMobile } from '../utils/mobile'

const router = useRouter()
const isMobileDevice = ref(false)

onMounted(() => {
  isMobileDevice.value = isMobile()
})

const goToDiagnosis = () => {
  router.push('/diagnosis')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

/* 移动端样式 */
.mobile-layout {
  padding-top: 46px; /* 导航栏高度 */
  padding-bottom: env(safe-area-inset-bottom);
}

.mobile-content {
  padding: 0.4rem;
}

.welcome-card-mobile {
  margin-bottom: 0.4rem;
  border-radius: 0.2rem;
}

.welcome-card-mobile h2 {
  font-size: 0.48rem;
  margin-bottom: 0.2rem;
  color: #323233;
}

.welcome-card-mobile p {
  font-size: 0.28rem;
  color: #969799;
  margin-bottom: 0.3rem;
}

.feature-grid {
  margin-top: 0.4rem;
}

.feature-grid p {
  font-size: 0.24rem;
  margin-top: 0.2rem;
  color: #646566;
}

/* 桌面端样式 */
.desktop-layout {
  min-height: 100vh;
}

.el-header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-header h1 {
  margin: 0;
  font-size: 24px;
}

.el-main {
  padding: 40px;
}

.welcome-card {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px;
}

.welcome-card h2 {
  margin-bottom: 20px;
  color: #303133;
}

.welcome-card p {
  margin-bottom: 30px;
  color: #606266;
  font-size: 16px;
}

.feature-cards {
  margin-top: 40px;
}

.feature-cards .el-card {
  text-align: center;
  padding: 30px 20px;
}

.feature-cards h3 {
  margin: 20px 0 10px;
  color: #303133;
}

.feature-cards p {
  color: #909399;
  font-size: 14px;
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
