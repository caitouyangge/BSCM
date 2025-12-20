<template>
  <div class="diagnosis">
    <el-container class="diagnosis-layout">
      <el-header class="header-section">
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

          <el-form :model="diagnosisForm" :label-width="labelWidth">
            <el-form-item label="症状描述">
              <el-input
                v-model="diagnosisForm.symptoms"
                type="textarea"
                :rows="6"
                placeholder="请详细描述您的症状..."
                maxlength="500"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="上传图片">
              <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                :on-change="handleFileChange"
                :on-remove="removeFile"
                :file-list="fileList"
                :limit="5"
                multiple
                accept="image/*"
              >
                <el-button type="primary">选择文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">支持jpg/png格式，最多上传5张</div>
                </template>
              </el-upload>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                @click="submitDiagnosis"
                :loading="loading"
              >
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
          <div class="result-content" v-html="diagnosisResult"></div>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { diagnosisApi } from "../api/diagnosis";

const router = useRouter();

// 响应式标签宽度
const labelWidth = computed(() => {
  return window.innerWidth <= 768 ? "80px" : "120px";
});

const diagnosisForm = ref({
  symptoms: "",
});

const fileList = ref([]);
const loading = ref(false);
const diagnosisResult = ref("");

const handleFileChange = (file, fileListRef) => {
  fileList.value = fileListRef;
};

const removeFile = (file, fileListRef) => {
  fileList.value = fileListRef;
};

const submitDiagnosis = async () => {
  if (!diagnosisForm.value.symptoms.trim()) {
    ElMessage.warning("请输入症状描述");
    return;
  }

  loading.value = true;
  try {
    const result = await diagnosisApi.submitDiagnosis({
      symptoms: diagnosisForm.value.symptoms,
      images: fileList.value.map((f) => f.raw || f),
    });
    diagnosisResult.value = result.data;
    ElMessage.success("诊断完成");
  } catch (error) {
    ElMessage.error("诊断失败，请稍后重试");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  diagnosisForm.value.symptoms = "";
  fileList.value = [];
  diagnosisResult.value = "";
};

const goBack = () => {
  router.push("/");
};
</script>

<style scoped>
.diagnosis {
  min-height: 100vh;
}

.diagnosis-layout {
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

.result-content {
  line-height: 1.6;
  color: var(--text-primary);
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .el-header {
    padding: 0 16px;
  }

  .el-main {
    padding: 16px;
  }

  .el-card {
    border-radius: 12px;
  }
}

@media screen and (min-width: 1200px) {
  .el-main {
    padding: 24px 32px;
  }
}
</style>
