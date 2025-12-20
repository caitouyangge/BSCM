<template>
  <div class="history">
    <el-container class="history-layout">
      <el-header class="header-section">
        <el-page-header @back="goBack">
          <template #content>
            <span class="text-large font-600 mr-3">诊断历史</span>
          </template>
        </el-page-header>
      </el-header>
      <el-main>
        <el-card v-if="historyList.length === 0 && !loading" class="empty-card">
          <el-empty description="暂无诊断记录" />
        </el-card>

        <!-- 移动端卡片布局 -->
        <div class="mobile-list" v-if="historyList.length > 0">
          <el-card
            v-for="item in historyList"
            :key="item.id"
            class="history-card"
            shadow="hover"
          >
            <div class="card-content">
              <div class="card-header-mobile">
                <span class="date-text">{{ item.date }}</span>
                <el-button
                  type="danger"
                  size="small"
                  :icon="Delete"
                  circle
                  @click="deleteRecord(item)"
                />
              </div>
              <div class="symptoms-text" @click="viewDetail(item)">
                {{ item.symptoms }}
              </div>
              <div class="card-footer">
                <el-button
                  size="small"
                  type="primary"
                  @click="viewDetail(item)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 桌面端表格布局 -->
        <el-table
          v-if="historyList.length > 0"
          :data="historyList"
          style="width: 100%"
          class="desktop-table"
        >
          <el-table-column prop="date" label="日期" width="180" />
          <el-table-column
            prop="symptoms"
            label="症状描述"
            show-overflow-tooltip
          />
          <el-table-column
            prop="result"
            label="诊断结果"
            show-overflow-tooltip
          />
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)"
                >查看详情</el-button
              >
              <el-button
                size="small"
                type="danger"
                @click="deleteRecord(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
        </el-table>

        <div v-if="loading" class="loading-wrapper">
          <el-skeleton :rows="3" animated />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import { diagnosisApi } from "../api/diagnosis";

const router = useRouter();
const historyList = ref([]);
const loading = ref(false);

onMounted(async () => {
  await loadHistory();
});

const loadHistory = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    const result = await diagnosisApi.getHistory();
    historyList.value = result.data;
  } catch (error) {
    ElMessage.error("加载历史记录失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

const viewDetail = (row) => {
  router.push(`/diagnosis/${row.id}`);
};

const deleteRecord = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除这条记录吗？", "确认删除", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await diagnosisApi.deleteRecord(row.id);
    ElMessage.success("删除成功");
    historyList.value = historyList.value.filter((item) => item.id !== row.id);
  } catch (error) {
    if (error === "cancel") return;
    ElMessage.error("删除失败");
    console.error(error);
  }
};

const goBack = () => {
  router.push("/");
};
</script>

<style scoped>
.history {
  min-height: 100vh;
}

.history-layout {
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

.empty-card {
  text-align: center;
}

.mobile-list {
  display: none;
}

.history-card {
  margin-bottom: 16px;
  cursor: pointer;
}

.card-content {
  padding: 8px 0;
}

.card-header-mobile {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.date-text {
  font-size: 14px;
  color: var(--text-secondary);
}

.symptoms-text {
  font-size: 15px;
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.desktop-table {
  border: 1px solid var(--border-color);
}

.desktop-table th {
  background-color: var(--background-light);
}

.loading-wrapper {
  margin-top: 20px;
}

/* 响应式 */
@media screen and (max-width: 768px) {
  .el-header {
    padding: 0 16px;
  }

  .el-main {
    padding: 16px;
  }

  .mobile-list {
    display: block;
  }

  .desktop-table {
    display: none;
  }

  .history-card {
    border-radius: 12px;
  }
}

@media screen and (min-width: 769px) {
  .mobile-list {
    display: none;
  }

  .desktop-table {
    display: table;
  }
}

@media screen and (min-width: 1200px) {
  .el-main {
    padding: 24px 32px;
  }
}
</style>
