<template>
  <div class="history">
    <!-- 移动端布局 -->
    <div class="mobile-layout" v-if="isMobileDevice">
      <van-nav-bar
        title="诊断历史"
        left-arrow
        @click-left="goBack"
        fixed
      />
      <div class="mobile-content">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="loadHistory"
        >
          <van-cell-group inset>
            <van-cell
              v-for="item in historyList"
              :key="item.id"
              :title="item.symptoms"
              :label="item.date"
              is-link
              @click="viewDetail(item)"
            >
              <template #right-icon>
                <van-icon name="delete" @click.stop="deleteRecord(item)" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-list>
      </div>
    </div>
    
    <!-- 桌面端布局 -->
    <el-container class="desktop-layout" v-else>
      <el-header>
        <el-page-header @back="goBack">
          <template #content>
            <span class="text-large font-600 mr-3">诊断历史</span>
          </template>
        </el-page-header>
      </el-header>
      <el-main>
        <el-table :data="historyList" style="width: 100%">
          <el-table-column prop="date" label="日期" width="180" />
          <el-table-column prop="symptoms" label="症状描述" />
          <el-table-column prop="result" label="诊断结果" />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              <el-button size="small" type="danger" @click="deleteRecord(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { showToast, showConfirmDialog } from 'vant'
import { diagnosisApi } from '../api/diagnosis'
import { isMobile } from '../utils/mobile'

const router = useRouter()
const isMobileDevice = ref(false)
const historyList = ref([])
const loading = ref(false)
const finished = ref(false)

onMounted(async () => {
  isMobileDevice.value = isMobile()
  await loadHistory()
})

const loadHistory = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const result = await diagnosisApi.getHistory()
    historyList.value = result.data
    finished.value = true
  } catch (error) {
    if (isMobileDevice.value) {
      showToast.fail('加载历史记录失败')
    } else {
      ElMessage.error('加载历史记录失败')
    }
    console.error(error)
  } finally {
    loading.value = false
  }
}

const viewDetail = (row) => {
  router.push(`/diagnosis/${row.id}`)
}

const deleteRecord = async (row) => {
  try {
    if (isMobileDevice.value) {
      await showConfirmDialog({
        title: '确认删除',
        message: '确定要删除这条记录吗？'
      })
    } else {
      await ElMessageBox.confirm('确定要删除这条记录吗？', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    }
    
    await diagnosisApi.deleteRecord(row.id)
    if (isMobileDevice.value) {
      showToast.success('删除成功')
    } else {
      ElMessage.success('删除成功')
    }
    historyList.value = historyList.value.filter(item => item.id !== row.id)
  } catch (error) {
    if (error === 'cancel') return
    if (isMobileDevice.value) {
      showToast.fail('删除失败')
    } else {
      ElMessage.error('删除失败')
    }
    console.error(error)
  }
}

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.history {
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

.el-table {
  border: 1px solid var(--border-color);
}

.el-table th {
  background-color: var(--background-light);
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
