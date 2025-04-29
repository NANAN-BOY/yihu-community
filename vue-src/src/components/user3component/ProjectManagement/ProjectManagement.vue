<script setup>
import {ref, onMounted, computed, watch, nextTick} from "vue";
import {ElButton, ElEmpty, ElInput, ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit, Search, View} from "@element-plus/icons-vue";
import store from "../../../store";
import axios from "axios";
import CreateProject from "./CreateProject.vue";

const showCreateComponent = ref(false); // 控制创建组件的显示
const searchInput = ref(''); // 搜索框的输入
const filteredData = ref([]); // 存储过滤后的项目数据
const allProjects = ref([]); // 存储所有项目数据
const onMobile = ref(typeof window !== 'undefined' && window.matchMedia('(max-width: 768px)').matches)
if (typeof window !== 'undefined') {window.matchMedia('(max-width: 768px)').addListener(e => {onMobile.value = e.matches})}

watch(searchInput, () => {
  projectStatusValue.value = '全部'
  refreshProjectList();
});

// 控制是否显示创建项目页面
const toggleCreateComponent = () => {
  store.dispatch('lockNavbar', '创建项目模板');
  showCreateComponent.value = true;
};

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
//project status classification
const projectStatusValue = ref('全部')
const projectStatusOptions = ['全部','未通过', '已通过']
watch(projectStatusValue, () => {
  refreshProjectList()
})
const projectStatusConvert = async (status) => {
  switch (status) {
    case '全部':
      return null
    case '未通过':
      return 2
    case '已通过':
      return 3
    default:
      return null
  }
}
// 取消创建项目操作
const GiveUpCreateComponent = () => {
  ElMessageBox.confirm(
      '确定放弃当前的创建操作吗？',
      '确定返回吗？',
      {
        confirmButtonText: '放弃',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(() => {
        store.dispatch('unlockNavbar');
        showCreateComponent.value = false;
        ElMessage.warning('已放弃创建');
      })
      .catch(() => {
        ElMessage.info('操作已取消');
      });
};

// 关闭创建项目页面
const CloseCreateComponent = () => {
  store.dispatch('unlockNavbar');
  showCreateComponent.value = false;
  projectListLoad();  // 关闭后刷新项目列表
};
// 在组件挂载时获取我的项目
onMounted(() => {
  projectListLoad();
});
</script>

<template>
  <div v-if="!showCreateComponent">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item><strong>项目管理</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-button type="primary" @click="toggleCreateComponent">创建新项目</el-button>&nbsp;
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
            @click="openActivityDetail(project.id)">
          <div>
            <el-tag v-if="project.status === 0" type="info">未提交</el-tag>
            <el-tag v-if="project.status === 1" type="primary">待审核</el-tag>
            <el-tag v-if="project.status === 2" type="danger">已驳回</el-tag>
            <el-tag v-if="project.status === 3" type="success">已通过</el-tag>
            {{ !project.name ? "未命名项目": project.name}}
          </div>
          <div v-if="!onMobile" class="button-group">
            <el-button size="mini" type="primary" @click.stop="openActivityDetail(project.id)">
              <el-icon>
                <View/>
              </el-icon>
            </el-button>
            <el-button size="mini" type="primary" @click.stop="openActivityDetail(project.id)">
              <el-icon>
                <Edit/>
              </el-icon>
            </el-button>
            <el-button size="mini" type="danger" @click.stop="deleteActivityWarning(project.id,activity.title)">
              <el-icon>
                <Delete/>
              </el-icon>
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
  <div v-if="showCreateComponent">
    <!-- 创建项目模板页面 -->
    <el-breadcrumb separator="/">
      <el-breadcrumb-item @click="GiveUpCreateComponent"><a>项目管理</a></el-breadcrumb-item>
      <el-breadcrumb-item><strong>新建项目</strong></el-breadcrumb-item>
    </el-breadcrumb>
    <br />
    <el-page-header @back="GiveUpCreateComponent" title="放弃">
      <template #content>
        <span class="text-large font-600 mr-3"> 创建项目 </span>
      </template>
    </el-page-header>
    <!-- 创建模板组件 -->
    <CreateProject @closeForm="CloseCreateComponent"/>
  </div>
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
