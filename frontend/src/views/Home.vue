<template>
  <div class="home">
    <!-- 移动端布局 -->
    <div class="mobile-layout" v-if="isMobileDevice">
      <van-nav-bar title="BSCM - AI智能诊断" fixed />
      <div class="mobile-content">
        <!-- Hero区域 -->
        <div class="mobile-hero">
          <h2 class="mobile-hero-title">智能医疗诊断</h2>
          <p class="mobile-hero-desc">让健康触手可及</p>
          <van-button type="primary" size="large" block class="mobile-hero-button" @click="goToDiagnosis">
            立即开始诊断
          </van-button>
        </div>
        
      </div>
    </div>
    
    <!-- 桌面端布局 -->
    <el-container class="desktop-layout" v-else>
      <el-header class="header-section">
        <div class="header-content">
          <div class="logo-section">
            <div class="logo-icon">BSCM</div>
            <h1>AI智能诊断系统</h1>
          </div>
          <div class="header-subtitle">基于先进AI技术，提供专业医疗诊断服务</div>
        </div>
      </el-header>
      <el-main class="main-section">
        <!-- Hero区域 -->
        <div class="hero-section">
          <div class="hero-content">
            <h2 class="hero-title">智能医疗诊断，让健康触手可及</h2>
            <p class="hero-description">
              采用最新的人工智能技术，结合专业医疗知识库，为您提供准确、快速的诊断建议
            </p>
            <el-button type="primary" size="large" class="hero-button" @click="goToDiagnosis">
              <el-icon><Document /></el-icon>
              立即开始诊断
            </el-button>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'
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
  background: var(--background-color);
}

/* ========== 移动端样式 ========== */
.mobile-layout {
  padding-top: 46px;
  padding-bottom: env(safe-area-inset-bottom);
  background: var(--background-color);
  min-height: 100vh;
}

.mobile-content {
  padding: 0.3rem;
}

/* 移动端Hero区域 */
.mobile-hero {
  background: var(--primary-gradient);
  border-radius: 0.3rem;
  padding: 0.6rem 0.4rem;
  margin-bottom: 0.4rem;
  text-align: center;
  color: white;
}

.mobile-hero-title {
  font-size: 0.56rem;
  font-weight: 600;
  margin-bottom: 0.2rem;
  color: white;
}

.mobile-hero-desc {
  font-size: 0.28rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 0.4rem;
}

.mobile-hero-button {
  margin-top: 0.3rem;
  border-radius: 0.2rem;
  font-weight: 500;
}

/* ========== 桌面端样式 ========== */
.desktop-layout {
  min-height: 100vh;
  background: var(--background-color);
}

/* 头部区域 */
.header-section {
  background: var(--primary-gradient);
  color: white;
  padding: 0;
  box-shadow: 0 2px 12px var(--shadow-color);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  text-align: center;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.logo-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.header-content h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 500;
  letter-spacing: 0;
}

.header-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 6px;
  font-weight: 400;
}

/* 主内容区域 */
.main-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
  width: 100%;
  box-sizing: border-box;
}

/* Hero区域 */
.hero-section {
  background: white;
  border-radius: 8px;
  padding: 48px 32px;
  margin-bottom: 40px;
  border: 1px solid var(--border-color);
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 32px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 16px;
  line-height: 1.4;
}

.hero-description {
  font-size: 16px;
  color: var(--text-secondary);
  line-height: 1.6;
  margin-bottom: 32px;
}

.hero-button {
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.hero-button:hover {
  background-color: var(--primary-dark);
}

/* ========== 响应式 ========== */
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

@media screen and (max-width: 1024px) {
  .main-section {
    padding: 32px 20px;
  }
  
  .hero-title {
    font-size: 28px;
  }
}

@media screen and (max-width: 768px) {
  .main-section {
    padding: 24px 16px;
  }
  
  .hero-section {
    padding: 32px 20px;
  }
  
  .hero-title {
    font-size: 24px;
  }
  
  .hero-description {
    font-size: 14px;
  }
}
</style>
