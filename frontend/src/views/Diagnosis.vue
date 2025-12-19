<template>
  <div class="diagnosis">
    <!-- 移动端布局 -->
    <div class="mobile-layout" v-if="isMobileDevice">
      <van-nav-bar
        title="AI智能诊断"
        left-arrow
        @click-left="goBack"
        fixed
      />
      <div class="mobile-content">
        <van-cell-group inset>
          <van-field
            v-model="diagnosisForm.symptoms"
            type="textarea"
            rows="6"
            placeholder="请详细描述您的症状..."
            label="症状描述"
            maxlength="500"
            show-word-limit
          />
        </van-cell-group>
        
        <van-cell-group inset class="upload-section">
          <van-cell title="上传图片" />
          <van-uploader
            v-model="fileList"
            :max-count="5"
            :after-read="handleFileChange"
            accept="image/*"
            multiple
          >
            <template #preview-cover="{ file }">
              <div class="preview-cover">
                <van-icon name="delete" @click.stop="removeFile(file)" />
              </div>
            </template>
          </van-uploader>
          <div class="upload-tip">支持jpg/png格式，最多上传5张</div>
        </van-cell-group>
        
        <div class="button-group">
          <van-button
            type="primary"
            size="large"
            block
            @click="submitDiagnosis"
            :loading="loading"
            loading-text="诊断中..."
          >
            开始诊断
          </van-button>
          <van-button
            size="large"
            block
            @click="resetForm"
            style="margin-top: 0.2rem;"
          >
            重置
          </van-button>
        </div>
        
        <van-cell-group inset v-if="diagnosisResult" class="result-section">
          <van-cell title="诊断结果" />
          <div class="result-content" v-html="diagnosisResult"></div>
        </van-cell-group>
      </div>
    </div>
    
    <!-- 桌面端布局 -->
    <el-container class="desktop-layout" v-else>
      <el-header>
        <el-page-header @back="goBack">
          <template #content>
            <span class="text-large font-600 mr-3">AI智能诊断</span>
          </template>
        </el-page-header>
      </el-header>
      <el-main>
        <el-card>
          <template #header>
            <div class="card-header">
              <span>输入诊断信息</span>
            </div>
          </template>
          
          <el-form :model="diagnosisForm" label-width="120px">
            <el-form-item label="症状描述">
              <el-input
                v-model="diagnosisForm.symptoms"
                type="textarea"
                :rows="6"
                placeholder="请详细描述您的症状..."
              />
            </el-form-item>
            
            <el-form-item label="上传图片">
              <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                :on-change="handleFileChange"
                :file-list="fileList"
              >
                <el-button type="primary">选择文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持jpg/png格式，最多上传5张
                  </div>
                </template>
              </el-upload>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitDiagnosis" :loading="loading">
                开始诊断
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card v-if="diagnosisResult" class="result-card">
          <template #header>
            <div class="card-header">
              <span>诊断结果</span>
            </div>
          </template>
          <div v-html="diagnosisResult"></div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElNotification } from 'element-plus'
import { showToast } from 'vant'
import { diagnosisApi } from '../api/diagnosis'
import { isMobile } from '../utils/mobile'

const router = useRouter()
const isMobileDevice = ref(false)

onMounted(() => {
  isMobileDevice.value = isMobile()
})

const diagnosisForm = ref({
  symptoms: ''
})

const fileList = ref([])
const loading = ref(false)
const diagnosisResult = ref('')

const handleFileChange = (file) => {
  if (Array.isArray(file)) {
    fileList.value = file
  } else {
    fileList.value.push(file)
  }
}

const removeFile = (file) => {
  const index = fileList.value.findIndex(item => item.uid === file.uid)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
}

const submitDiagnosis = async () => {
  if (!diagnosisForm.value.symptoms.trim()) {
    if (isMobileDevice.value) {
      showToast('请输入症状描述')
    } else {
      ElMessage.warning('请输入症状描述')
    }
    return
  }
  
  loading.value = true
  try {
    const result = await diagnosisApi.submitDiagnosis({
      symptoms: diagnosisForm.value.symptoms,
      images: fileList.value.map(f => f.file || f.raw)
    })
    diagnosisResult.value = result.data
    if (isMobileDevice.value) {
      showToast.success('诊断完成')
    } else {
      ElMessage.success('诊断完成')
    }
  } catch (error) {
    if (isMobileDevice.value) {
      showToast.fail('诊断失败，请稍后重试')
    } else {
      ElMessage.error('诊断失败，请稍后重试')
    }
    console.error(error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  diagnosisForm.value.symptoms = ''
  fileList.value = []
  diagnosisResult.value = ''
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.diagnosis {
  min-height: 100vh;
}

/* 移动端样式 */
.mobile-layout {
  padding-top: 46px;
  padding-bottom: env(safe-area-inset-bottom);
  background-color: var(--background-color);
  min-height: 100vh;
}

.mobile-content {
  padding: 0.3rem;
}

.upload-section {
  margin-top: 0.3rem;
}

.upload-tip {
  padding: 0.2rem 0.32rem;
  font-size: 0.24rem;
  color: #969799;
}

.button-group {
  margin-top: 0.4rem;
  padding: 0 0.3rem;
}

.result-section {
  margin-top: 0.4rem;
}

.result-content {
  padding: 0.3rem;
  font-size: 0.28rem;
  line-height: 1.6;
  color: #323233;
}

.preview-cover {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

/* 桌面端样式 */
.desktop-layout {
  min-height: 100vh;
}

.el-header {
  background: var(--primary-gradient);
  color: white;
  border-bottom: none;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px var(--shadow-color);
}

.el-main {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  box-sizing: border-box;
}

.el-card {
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: none;
}

.el-card:hover {
  border-color: var(--border-color);
  box-shadow: 0 2px 8px var(--shadow-color);
}

.result-card {
  margin-top: 20px;
  border-color: var(--primary-color);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .desktop-layout {
    display: none;
  }
  
  .el-main {
    padding: 16px;
  }
}

@media screen and (min-width: 769px) {
  .mobile-layout {
    display: none;
  }
  
  .el-main {
    padding: 24px;
  }
}

@media screen and (min-width: 1200px) {
  .el-main {
    padding: 24px 32px;
  }
}
</style>
