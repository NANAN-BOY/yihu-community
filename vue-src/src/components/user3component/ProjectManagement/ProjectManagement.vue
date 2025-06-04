<script setup>
import {ref, onMounted, computed, watch, nextTick, h} from "vue";
import {ElButton, ElInput, ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit, More, Search, Upload, View} from "@element-plus/icons-vue";
import store from "../../../store";
import axios from "axios";
import ActivityManagement from "./ActivityManagement/ActivityManagement.vue";

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
const loading = ref(false); // 控制加载状态
const error = ref('')
const hasMore = ref(true)
const noMore = computed(() => !hasMore.value)
const disabled = computed(() => loading.value || noMore.value)
//Project loading method
const projectListLoad = async () => {
  if (disabled.value) return
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
        pageSize: 20,
        status: status,
        name: searchInput.value
      }
    });
    if (response.data.code === 200) {
      // success
      projectList.value = [...projectList.value, ...response.data.data.list]
      hasMore.value = response.data.data.hasNextPage
      currentPage.value++
    } else if (response.data.code === 404) {
      // project not found , list is empty
      hasMore.value = false

      if (currentPage.value === 1) projectList.value = []
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
//Refresh project list
const refreshProjectList = async () => {
  projectList.value = []
  currentPage.value = 1
  hasMore.value = true
  loading.value = false
  error.value = ''
  await nextTick()
  await projectListLoad()
}
// 在组件挂载时获取我的项目
onMounted(() => {
  projectListLoad();
});
//project status classification
const projectStatusValue = ref('全部')
const projectStatusOptions = ['全部','未通过','已通过']
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
      h('p', null, `确认提交审核“${!project.name ? "未命名活动": project.name}”项目吗？`),
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
  <div v-if="pageNum === 'ProjectList'">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>项目管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button type="primary" @click="createProject">创建新项目</el-button>&nbsp;
    <el-input
        v-model="searchInput"
        style="width: auto"
        placeholder="键入以搜索"
        :prefix-icon="Search"
    />
    <br />
    <!--  状态分类-->
    <div class="flex flex-col items-start gap-4" style="margin-bottom: 4px;margin-top: 4px">
      <el-segmented v-model="projectStatusValue" :options="projectStatusOptions" size="large" @click="searchInput=''"/>
    </div>
    <div class="infinite-list-wrapper" style="overflow: auto">
      <ul
          v-infinite-scroll="projectListLoad"
          class="list"
          :infinite-scroll-disabled="disabled"
          infinite-scroll-immediate="false"
          infinite-scroll-distance="100"
      >
        <li v-for="project in projectList" :key="project.id" class="list-item"
            @click="openActivityManagement(project)">
          <div>
            <el-tag v-if="project.status === 0" type="info">未提交</el-tag>
            <el-tag v-if="project.status === 1" type="primary">待审核</el-tag>
            <el-tag v-if="project.status === 2" type="danger">已驳回</el-tag>
            <el-tag v-if="project.status === 3" type="success">已通过</el-tag>
            {{ !project.name ? "未命名项目": project.name}}
          </div>
          <div v-if="!onMobile" class="button-group">
            <el-button  size="default" type="primary" @click.stop="openActivityManagement(project)">
              <el-icon>
                <View/>
              </el-icon>
            </el-button>
            <el-button size="default" type="primary" @click.stop="editProjectName(project)">
              <el-icon>
                <Edit/>
              </el-icon>
            </el-button>
            <el-button size="default" type="primary" @click.stop="submitExampleProject(project)">
              <el-icon>
                <Upload />
              </el-icon>
            </el-button>
            <el-button size="default" type="danger" @click.stop="deleteProject(project.id,project.name)">
              <el-icon>
                <Delete/>
              </el-icon>
            </el-button>
          </div>
          <div v-else>
            <el-button size="default" type="primary" @click.stop="openMoreSelect(project)">
              <el-icon><More /></el-icon>
            </el-button>
          </div>
        </li>
        <li v-if="loading" v-loading="loading" class="list-item"></li>
      </ul>
      <p v-if="loading">加载中...</p>
      <p v-if="noMore">没有更多数据了</p>
      <p v-if="error" style="color: red">{{ error }}</p>
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
</template>

<style scoped>
.infinite-list-wrapper {
  height: calc(100vh - 212px);
  text-align: center;
}

.infinite-list-wrapper .list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.infinite-list-wrapper .list-item:hover {
  background: #b6b6b6;
  transition: background 0.3s ease;
  cursor: pointer;
}

.infinite-list-wrapper .list-item {
  min-height: 50px;
  border-radius: 10px;
  padding: 10px;
  display: flex;
  align-items: center;
  background: #f5f5f5;
  color: #000000;
  margin-bottom: 5px;
  justify-content: space-between;
}
@media (max-width: 768px) {
  .infinite-list-wrapper {
    height: calc(100vh - 212px);
    text-align: center;
  }

  .infinite-list-wrapper .list {
    padding: 0;
    margin: 0;
    list-style: none;
  }

  .infinite-list-wrapper .list-item:hover {
    background: #b6b6b6;
    transition: background 0.3s ease;
    cursor: pointer;
  }

  .infinite-list-wrapper .list-item {
    border-radius: 10px;
    padding: 10px;
    display: flex;
    align-items: center;
    background: #f5f5f5;
    color: #000000;
  }

  .infinite-list-wrapper .list-item + .list-item {
    margin-top: 5px;
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
