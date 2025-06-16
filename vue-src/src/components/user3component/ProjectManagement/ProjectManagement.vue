<script setup>
import {ref, onMounted, computed, watch, nextTick, h} from "vue";
import {ElButton, ElInput, ElMessage, ElMessageBox, ElPagination} from "element-plus";
import {CirclePlus, Delete, Edit, More, Search, Upload, View} from "@element-plus/icons-vue";
import store from "../../../store";
import axios from "axios";
import ActivityManagement from "./ActivityManagement/ActivityManagement.vue";
import zhCn from "element-plus/es/locale/lang/zh-cn";

const pageNum = ref('ProjectList');
const searchInput = ref(''); // 搜索框的输入
const onMobile = ref(typeof window !== 'undefined' && window.matchMedia('(max-width: 768px)').matches)
if (typeof window !== 'undefined') {window.matchMedia('(max-width: 768px)').addListener(e => {onMobile.value = e.matches})}

watch(searchInput, () => {
  projectStatusValue.value = '全部'
  refreshProjectList();
});

//Data required for project list
const projectList = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false); // 控制加载状态
const error = ref('')

//Project loading method
const projectListLoad = async () => {
  if (loading.value) return
  const status = await projectStatusConvert(projectStatusValue.value);
  console.log(status)
  try {
    loading.value = true
    error.value = ''

    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/project/queryByCreateId`, {
      headers: {
        token: store.state.token,
      },
      params:{
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        status: status,
        name: searchInput.value
      }
    });
    if (response.data.code === 200) {
      // success
      projectList.value = response.data.data.list
      total.value = response.data.data.total || 0
    } else if (response.data.code === 404) {
      // project not found , list is empty
      projectList.value = []
      total.value = 0
    } else {
      // Other error
      error.value = response.data.msg || '请求出现异常'
    }

  } catch (err) {
    console.error(err);
    // Network error
    error.value = '数据加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

// 处理分页变化
const handleCurrentPageChange = (page) => {
  currentPage.value = page
  projectListLoad()
}

const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  projectListLoad()
}

//Refresh project list
const refreshProjectList = async () => {
  currentPage.value = 1
  loading.value = false
  error.value = ''
  await nextTick()
  await projectListLoad()
}

// 在组件挂载时获取我的项目
onMounted(() => {
  projectListLoad();
});

// project status classification
const projectStatusValue = ref('全部')
const projectStatusOptions = ['全部','未通过','已通过']
const handleStatusChange = (option) => {// 处理状态改变
  searchInput.value = '' // 清空搜索输入
  projectStatusValue.value = option
}
watch(projectStatusValue, () => {
  refreshProjectList()
})
const projectStatusConvert = async (status) => {
  switch (status) {
    case '全部':
      return null
    case '未通过':
      return 0
    case '已通过':
      return 3
    default:
      return null
  }
}

const createProject = async () => {
  await ElMessageBox.prompt(
      '请输入项目名称',
      '创建新项目',
      {
        confirmButtonText: '创建',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '项目名称不能为空',
        showCancelButton: true,
        // 在这里拦截关闭：只有在 done() 被调用后才会真正关闭弹窗
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            // 开启按钮 loading，并修改文案
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '提交中...'
            // 取到用户输入
            const projectName = (instance.inputValue || '').trim()
            // 调用后端接口
            axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/project/addProject`, {},
                {
                  params: {
                    name: projectName,
                  },
                  headers: {
                    token: store.state.token,
                  },
                })
                .then((response) => {
                  console.log(response)
                  if (response.data.code === 200) {
                    instance.confirmButtonLoading = false
                    instance.confirmButtonText = '创建'
                    ElMessage.success('项目创建成功')
                    refreshProjectList()
                    // openActivityManagement(response.data.data,projectName)
                    done()
                  } else if (response.data.code === 400) {
                    instance.confirmButtonLoading = false
                    instance.confirmButtonText = '创建'
                    ElMessage.error('创建失败，请重试')
                    done()
                  }
                })
                .catch((err) => {
                  console.error(err)
                  // 接口失败，先重置按钮
                  instance.confirmButtonLoading = false
                  instance.confirmButtonText = '创建'
                  ElMessage.error('创建失败，请重试')
                })
          } else {
            done()
          }
        }
      }
  )
}

const editProjectName = async (project) => {
  if(project.status === 1){
    ElMessage.warning("项目审核中，无法编辑!")
    return
  }
  if(project.status === 3){
    ElMessage.warning("项目已经结束，无法编辑!")
    return
  }
  const projectId = project.id
  const projectOldName = project.name
  closeMoreSelect()
  await ElMessageBox.prompt(
      '请输入项目名称',
      '修改项目名称',
      {
        inputValue: projectOldName,
        confirmButtonText: '提交',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '项目名称不能为空',
        showCancelButton: true,
        // 在这里拦截关闭：只有在 done() 被调用后才会真正关闭弹窗
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            // 开启按钮 loading，并修改文案
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '提交中...'
            // 取到用户输入
            const projectName = (instance.inputValue || '').trim()
            // 调用后端接口
            axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/project/update`, {
                  id: projectId,
                  name: projectName,
                },
                {
                  headers: {
                    token: store.state.token,
                  },
                })
                .then((response) => {
                  console.log(response)
                  if (response.data.code === 200) {
                    instance.confirmButtonLoading = false
                    instance.confirmButtonText = '创建'
                    ElMessage.success('修改成功')
                    refreshProjectList()
                    done()
                  } else if (response.data.code === 400) {
                    instance.confirmButtonLoading = false
                    instance.confirmButtonText = '创建'
                    ElMessage.error('修改失败，请重试')
                    done()
                  }
                })
                .catch((err) => {
                  console.error(err)
                  // 接口失败，先重置按钮
                  instance.confirmButtonLoading = false
                  instance.confirmButtonText = '创建'
                  ElMessage.error('修改失败，请重试')
                })
          } else {
            done()
          }
        }
      }
  )
}

const deleteProject = async (projectId, projectName) => {
  closeMoreSelect()
  await ElMessageBox.confirm(
      `你确定要删除项目「${projectName}」吗？所属活动一并删除，该操作不可撤销！`,
      '删除项目',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
        showCancelButton: true,
        confirmButtonClass: 'el-button--danger',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true
            instance.confirmButtonText = '删除中...'
            axios.delete(`${import.meta.env.VITE_BACKEND_IP}/api/project/deleteById`, {
              headers: {
                token: store.state.token,
              },
              params: {
                projectId: projectId,
              },
            })
                .then((response) => {
                  if (response.data.code === 200) {
                    ElMessage.success('删除成功')
                    refreshProjectList()
                    done()
                  } else {
                    ElMessage.error('删除失败，请重试')
                  }
                })
                .catch((err) => {
                  console.error(err)
                  ElMessage.error('删除失败，请重试')
                })
                .finally(() => {
                  instance.confirmButtonLoading = false
                  instance.confirmButtonText = '删除'
                })
          } else {
            done()
          }
        }
      }
  )
}

//活动管理所需数据
const nowProject = ref(null)
const openActivityManagement = async (project) => {
  console.log(project)
  nowProject.value = project
  pageNum.value = 'ActivityManagement'
  closeMoreSelect()
}
const closeActivityManagement = () => {
  pageNum.value = 'ProjectList'
  nowProject.value = null
  refreshProjectList()
}

//手机端更多选项所需数据
const nowSelectProject = ref(null)
const moreSelectVisible = ref(false)
const openMoreSelect = (project) => {
  console.log(project)
  nowSelectProject.value = project
  moreSelectVisible.value = true
}
const closeMoreSelect = () => {
  moreSelectVisible.value = false
}

//提交审核项目
const submitExampleProject = (project) => {
  console.log(project)
  closeMoreSelect()
  if(project.status === 1){
    ElMessage.warning("项目审核中，请耐心等待!")
    return
  }
  if(project.status === 3){
    ElMessage.warning("项目已通过!")
    return
  }
  ElMessageBox({
    title: '提交审核',
    message: h('div', null, [
      h('p', null, `确认提交审核"${!project.name ? "未命名活动": project.name}"项目吗？`),
      h('p', {  }, '请确认项目已经结项，并且活动均填写完整')
    ]),
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    confirmButtonClass: 'confirm-delete-button',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true
        axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/project/submitProject`,
            {
              id: project.id
            }, {
              headers: {
                token: store.state.token
              }
            })
            .then(response => {
              console.log(response)
              const res = response.data
              if (res.code === 200) {
                console.log(res)
                // 成功：关闭弹窗
                ElMessage.success("提交成功,请等待审核")
                refreshProjectList()
                done()
              } else {
                // 失败：取消 loading、提示错误，保持弹窗开启
                instance.confirmButtonLoading = false
                ElMessage.error(res.msg)
                instance.message = `提交失败：${res.message || '未知错误'}`
              }
            })
            .catch(error => {
              // 请求出错
              instance.confirmButtonLoading = false
              instance.message = `网络异常：${error.message}`
              ElMessage.error('提交失败，请重试')
            })
      } else {
        // 点击取消或右上角关闭
        done()
      }
    }
  })
}
</script>

<template>
  <div class="Page">
  <div v-if="pageNum === 'ProjectList'">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">项目管理</h1>
      <p class="page-subtitle">共{{ total }}个项目，？个已通过，？个未通过</p>
    </div>
    <div style="display:flex;justify-content: space-between">
      <el-button type="primary"
                 @click="createProject"
                 style="width: 200px;height: 45px;font-size: 20px"
      >
        <el-icon><CirclePlus /></el-icon>
        新建
      </el-button>
      <el-input
          v-model="searchInput"
          style="width: auto"
          placeholder="键入以搜索"
          :prefix-icon="Search"
      />
    </div>
    <el-divider style="margin-top: 10px;margin-bottom: 10px"/>
    <!--  状态分类-->
    <div style="margin-bottom: 4px;margin-top: 4px">
      <!-- 更新替换为三个按钮 -->
      <div>
        <el-button
            v-for="option in projectStatusOptions"
            :key="option"
            :type="projectStatusValue === option ? 'primary' : 'default'"
            size="large"
            @click="handleStatusChange(option)"
            style="width: 100px;height: 35px"
        >
          {{ option }}
        </el-button>
      </div>
    </div>

    <div class="project-list-wrapper" v-loading="loading">
      <div class="card-grid">
        <div v-for="project in projectList" :key="project.id" class="project-card"
             @click="openActivityManagement(project)">
          <div class="card-header">
            <div class="project-status">
              <el-tag v-if="project.status === 0" type="info">未提交</el-tag>
              <el-tag v-if="project.status === 1" type="primary">待审核</el-tag>
              <el-tag v-if="project.status === 2" type="danger">已驳回</el-tag>
              <el-tag v-if="project.status === 3" type="success">已通过</el-tag>
            </div>
            <div v-if="onMobile" class="mobile-more">
              <el-button size="small" type="primary" @click.stop="openMoreSelect(project)">
                <el-icon><More /></el-icon>
              </el-button>
            </div>
          </div>

          <div class="card-content">
            <div class="project-name">
              {{ !project.name ? "未命名项目": project.name}}
            </div>
            <div class="project-info">
              <span class="project-id">ID: {{ project.id }}</span>
            </div>
          </div>

          <div v-if="!onMobile" class="card-actions">
            <el-button size="small" type="primary" @click.stop="openActivityManagement(project)" title="查看详情">
              <el-icon>
                <View/>
              </el-icon>
            </el-button>
            <el-button size="small" type="primary" @click.stop="editProjectName(project)" title="编辑信息">
              <el-icon>
                <Edit/>
              </el-icon>
            </el-button>
            <el-button size="small" type="success" @click.stop="submitExampleProject(project)" title="提交审核">
              <el-icon>
                <Upload />
              </el-icon>
            </el-button>
            <el-button size="small" type="danger" @click.stop="deleteProject(project.id,project.name)" title="删除项目">
              <el-icon>
                <Delete/>
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态提示 -->
      <div v-if="!loading && projectList.length === 0" class="empty-state">
        <p>暂无项目数据</p>
      </div>

      <!-- 错误提示 -->
      <div v-if="error" class="error-state">
        <p style="color: red">{{ error }}</p>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="pagination-wrapper" v-if="total > 0">
      <el-config-provider :locale="zhCn">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handlePageSizeChange"
            @current-change="handleCurrentPageChange"
        />
      </el-config-provider>
    </div>
  </div>

  <!--  手机端更多选项弹窗-->
  <el-dialog
      v-model="moreSelectVisible"
      title="更多"
      width="500"
      align-center
  >
    <span>
      <el-button  size="small" type="primary" @click.stop="openActivityManagement(nowSelectProject);">查看详情</el-button>
    <el-button size="small" type="primary" @click.stop="editProjectName(nowSelectProject);">编辑信息</el-button>
      <el-button size="small" type="success" @click.stop="submitExampleProject(nowSelectProject);">{{nowSelectProject.status===2?'重新提交':'提交审核'}}</el-button>
    <el-button size="small" type="danger" @click.stop="deleteProject(nowSelectProject.id,nowSelectProject.name)">删除项目</el-button>
    </span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeMoreSelect">关闭</el-button>
      </div>
    </template>
  </el-dialog>

  <ActivityManagement
      v-if="pageNum === 'ActivityManagement'"
      :project="nowProject"
      @closeActivityManagement="closeActivityManagement"
  />
  </div>
</template>

<style scoped>
.Page{
  background-color: white;
}

/* 页面标题 */
.page-header {
  margin-bottom: 10px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0 0;
}

.page-subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}
.project-list-wrapper {
  height: calc(100vh - 300px);
  overflow-y: auto;
  padding: 10px 0;
}

.card-grid {
  display: flex;
  gap: 10px;
}

.project-card {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  min-height: 160px;
  width: 230px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.project-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.project-status {
  flex: 1;
}

.mobile-more {
  margin-left: 10px;
}

.card-content {
  flex: 1;
  text-align: left;
  margin-bottom: 15px;
}

.project-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
  word-break: break-word;
}

.project-info {
  font-size: 12px;
  color: #909399;
}

.project-id {
  font-family: monospace;
}

.card-actions {
  display: flex;
  justify-content: center;
  gap: 2px;
  flex-wrap: wrap;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #999;
  font-size: 16px;
}

.error-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  font-size: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 10px 0;
}

@media (max-width: 768px) {
  .project-list-wrapper {
    height: calc(100vh - 280px);
    overflow-y: auto;
    padding: 10px 0;
  }

  .card-grid {
    grid-template-columns: 1fr;
    gap: 15px;
    padding: 0 15px;
  }

  .project-card {
    min-height: 140px;
    padding: 15px;
  }

  .card-header {
    margin-bottom: 10px;
  }

  .card-content {
    margin-bottom: 10px;
  }

  .project-name {
    font-size: 15px;
  }

  .pagination-wrapper {
    padding: 10px;
  }

  .pagination-wrapper :deep(.el-pagination) {
    justify-content: center;
  }

  .pagination-wrapper :deep(.el-pagination .el-pager) {
    flex-wrap: wrap;
  }
}

.project-status span {
  font-weight: bold;
}

.project-list {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}
</style>