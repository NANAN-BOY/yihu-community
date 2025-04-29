<script setup>
import {useStore} from 'vuex'
import {computed, h, onMounted, ref} from 'vue'
import {ElCascader, ElMessage, ElMessageBox} from 'element-plus'
import {ElIcon} from 'element-plus';
import {Avatar, Edit} from "@element-plus/icons-vue";
import axios from "axios";
import {regionData} from "element-china-area-data";
import router from "../../../router";

function getRegionName(regionCode) {
  const codeToNameMap = {};

  // 遍历省级数据
  regionData.forEach((province) => {
    codeToNameMap[province.value] = province.label;

    // 遍历市级数据
    province.children.forEach((city) => {
      codeToNameMap[city.value] = `${province.label} ${city.label}`;

      // 遍历县级数据
      city.children.forEach((county) => {
        codeToNameMap[county.value] = `${province.label} ${city.label} ${county.label}`;
      });
    });
  });

  return codeToNameMap[regionCode] || 'Error';
}

onMounted(() => {
  getUserInfo()
})
const store = useStore()
const IsLoading = ref(false)
const getUserInfo = async () => {
  IsLoading.value = true
  try {
    if (!store.state.token) {
      return;
    }
    const response = await axios.get(`${import.meta.env.VITE_BACKEND_IP}/api/user/reLogin`, {
      headers: {
        'token': `${store.state.token}`
      }
    });
    IsLoading.value = false
    if (response.data.code !== 200) {
      throw new Error('身份已过期，请重新登录。');
    }
    if (response.status === 'error') {
      throw new Error(response.message);
    }
    if (response.data.code === 200 && response.data.data)
      user.value = response.data.data
    else
      throw new Error('身份已过期，请重新登录。');
  } catch (error) {
    ElMessage.error('身份已过期，请重新登录。');
    localStorage.removeItem('token');
    await store.dispatch('setToken', null);
    await store.dispatch('setUser', null);
    await router.push('/login');
  }
}
const user = ref(store.state.user)
const role = computed(() => {
  if (store.state.user.role === 1) {
    return '系统管理员'
  }
  if (store.state.user.role === 2) {
    return '政府'
  }
  if (store.state.user.role === 3) {
    return '社会组织'
  }
  if (store.state.user.role === 4) {
    return '专家'
  }
})
const formatDate = (timestamp) => {
  if (timestamp) {
    try {
      const date = new Date(Number(timestamp))
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }).replace(/\//g, '-')
    } catch {
      return '无效时间'
    }
  } else {
    return '暂无记录'
  }
}

const editMyName = () => {
  ElMessageBox.prompt('请输入要修改的新名称', '编辑名称', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /^[\w\u4e00-\u9fa5]{1,30}$/,
    inputErrorMessage: '请输入1 - 30个字符',
    inputValue: store.state.user.name,
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        const value = instance.inputValue;
        instance.confirmButtonLoading = true;
        instance.confirmButtonText = '加载中...';
        axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/user/update-info`, {
              id: store.state.user.id,
              name: value,
              description: store.state.user.description,
              location: store.state.user.location,
            }, {
              headers: {
                'token': `${store.state.token}`,
              },
            },
        )
            .then((response) => {
              if (response.data.code === 200) {
                store.state.user.name = value;
                ElMessage.success(response.data.data);
                // 成功后关闭窗口
                done();
                setTimeout(() => {
                  instance.confirmButtonLoading = false;
                  instance.confirmButtonText = '提交';
                }, 300);
              } else {
                ElMessage.error(response.data.data);
                // 失败则取消加载状态，不关闭窗口
                instance.confirmButtonLoading = false;
                instance.confirmButtonText = '提交';
              }
            })
            .catch((error) => {
              ElMessage.error('请求出错：' + error.message);
              // 出错则取消加载状态，不关闭窗口
              instance.confirmButtonLoading = false;
              instance.confirmButtonText = '提交';
            });
      } else {
        done();
      }
    },
  })
      .then((action) => {
        if (action === 'confirm') {
          // 这里可以根据需要添加额外逻辑
        }
      })
      .catch(() => {
        ElMessage.info('您已取消修改');
      });
};
const editMyDescription = () => {
  ElMessageBox.prompt('请输入要修改的新简介', '编辑简介', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
    inputPattern: /^[\w\u4e00-\u9fa5]{1,50}$/,
    inputErrorMessage: '请输入1 - 50个字符',
    inputValue: store.state.user.description,
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        const value = instance.inputValue;
        instance.confirmButtonLoading = true;
        instance.confirmButtonText = '加载中...';
        axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/user/update-info`, {
              id: store.state.user.id,
              name: store.state.user.name,
              description: value,
              location: store.state.user.location,
            }, {
              headers: {
                'token': `${store.state.token}`,
              },
            },
        )
            .then((response) => {
              if (response.data.code === 200) {
                store.state.user.description = value;
                ElMessage.success(response.data.data);
                // 成功后关闭窗口
                done();
                setTimeout(() => {
                  instance.confirmButtonLoading = false;
                  instance.confirmButtonText = '提交';
                }, 300);
              } else {
                ElMessage.error(response.data.data);
                // 失败则取消加载状态，不关闭窗口
                instance.confirmButtonLoading = false;
                instance.confirmButtonText = '提交';
              }
            })
            .catch((error) => {
              ElMessage.error('请求出错：' + error.message);
              // 出错则取消加载状态，不关闭窗口
              instance.confirmButtonLoading = false;
              instance.confirmButtonText = '提交';
            });
      } else {
        done();
      }
    },
  })
      .then((action) => {
        if (action === 'confirm') {
          // 这里可以根据需要添加额外逻辑
        }
      })
      .catch(() => {
        ElMessage.info('您已取消修改');
      });
};

// 定义地区选择相关数据
const selectedArea = ref([]);
const areaOptions = ref(regionData);
const areaProps = {
  value: 'value',
  label: 'label',
  children: 'children',
  emitPath: false,
};
const editMyLocation = () => {
  ElMessageBox({
    title: '选择地区',
    message: () => {
      return h(ElCascader, {
        modelValue: selectedArea.value,
        'onUpdate:modelValue': (newValue) => (selectedArea.value = newValue),
        options: areaOptions.value,
        props: areaProps,
        showAllLevels: true,
        placeholder: '请选择所在地区',
        clearable: true,
        style: 'width: 100%',
      });
    },
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        const value = selectedArea.value;
        instance.confirmButtonLoading = true;
        instance.confirmButtonText = '加载中...';
        axios.post(`${import.meta.env.VITE_BACKEND_IP}/api/user/update-info`, {
              id: store.state.user.id,
              name: store.state.user.name,
              description: store.state.user.description,
              location: value,
            }, {
              headers: {
                'token': `${store.state.token}`,
              },
            },
        )
            .then((response) => {
              if (response.data.code === 200) {
                store.state.user.location = value;
                ElMessage.success(response.data.data);
                // 成功后关闭窗口
                done();
                setTimeout(() => {
                  instance.confirmButtonLoading = false;
                  instance.confirmButtonText = '提交';
                }, 300);
              } else {
                ElMessage.error(response.data.data);
                // 失败则取消加载状态，不关闭窗口
                instance.confirmButtonLoading = false;
                instance.confirmButtonText = '提交';
              }
            })
            .catch((error) => {
              ElMessage.error('请求出错：' + error.message);
              // 出错则取消加载状态，不关闭窗口
              instance.confirmButtonLoading = false;
              instance.confirmButtonText = '提交';
            });
      } else {
        done();
      }
    },
  }).then((action) => {
    if (action === 'confirm') {
      // 这里可以根据需要添加额外逻辑
    }
  }).catch(() => {
    ElMessage.info('您已取消修改');
  });
}

// 对话框显示控制
const modifyPhoneDialogVisible = ref(false)
// 表单数据
const user_NewPhoneNumber = ref('')
const sms_code = ref('')
// 验证码倒计时
const countdown = ref(0)
let timer = null

// 发送验证码
const sendSMSCode = async () => {
  try {
    // 格式校验
    if (!/^1[3-9]\d{9}$/.test(user_NewPhoneNumber.value)) {
      ElMessage.warning('请输入有效的手机号码')
      return
    }

    // 新旧号码校验
    if (user_NewPhoneNumber.value === store.state.user.phone) {
      ElMessage.warning('新手机号不能与当前手机号相同')
      return
    }

    // 倒计时逻辑
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)

    // 调用发送验证码接口
    await axios.post(
        `${import.meta.env.VITE_BACKEND_IP}/api/user/captcha/generate`,
        null,
        {params: {phone: user_NewPhoneNumber.value}}
    )

    ElMessage.success('验证码已发送')
  } catch (error) {
    ElMessage.error('验证码发送失败')
    console.error('验证码发送错误:', error)
    clearInterval(timer)
    countdown.value = 0
  }
}

// 提交修改
const modifyMyPhone = async () => {
  try {
    // 表单校验
    if (!user_NewPhoneNumber.value || !sms_code.value) {
      ElMessage.warning('请填写完整信息')
      return
    }

    // 调用后端接口
    const response = await axios.post(
        `${import.meta.env.VITE_BACKEND_IP}/api/user/update-phone`,
        null,
        {
          params: {
            oldPhone: store.state.user.phone,  // 从用户信息获取旧手机号
            newPhone: user_NewPhoneNumber.value,
            captcha: sms_code.value
          }
          ,
          headers: {
            'token': `${store.state.token}`,
          },
        }
    )
    // 处理响应
    if (response.data.code === 200) {
      ElMessage.success('手机号修改成功')
      ElMessage.error('身份验证已过期，请重新登陆！')
      await store.dispatch('systemLogout')
    } else {
      ElMessage.error(response.data.msg || '手机号修改失败')
    }
  } catch (error) {
    ElMessage.error('请求失败，请检查网络')
    console.error('手机号修改失败:', error)
  }
}
</script>
<template>
  <div v-loading="IsLoading" class="user-info-container">
    <div v-if="!IsLoading" class="header-section">
      <el-avatar :size="100" class="user-avatar" @click="ElMessage.warning('功能暂未开放')">
        <el-icon size="50">
          <Avatar/>
        </el-icon>
      </el-avatar>
      <div class="base-info">
        <h2>
          {{ user.name || '未设置名称' }}
          <el-icon
              :size="21"
              color="blue"
              @click="editMyName"
              class="interactive-icon">
            <Edit/>
          </el-icon>
        </h2>
        <p class="role-tag">{{ role }}</p>
      </div>
    </div>
    <el-form v-if="!IsLoading" class="detail-section" label-width="100px">
      <el-form-item label="关联手机号">
        {{ user.phone ? user.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : '未绑定手机' }}
        <el-icon
            :size="21"
            color="blue"
            @click="modifyPhoneDialogVisible = true"
            class="interactive-icon">
          <Edit/>
        </el-icon>
      </el-form-item>

      <el-form-item label="简介" class="form-item-description">
        <div class="description-text">
          {{ user.description || '暂无简介' }}
        </div>
        <el-icon
            :size="21"
            color="blue"
            @click="editMyDescription"
            class="interactive-icon">
          <Edit/>
        </el-icon>
      </el-form-item>

      <el-form-item label="地区">
        {{ getRegionName(user.location) || '未设置地区' }}
        <el-icon
            :size="21"
            color="blue"
            @click="editMyLocation"
            class="interactive-icon">
          <Edit/>
        </el-icon>
      </el-form-item>

      <!-- 账户信息 -->
      <el-divider content-position="left"><h3>账户信息</h3></el-divider>
      <el-form-item label="账户状态">
        <el-tag :type="user.status === 'active' ? 'danger' : 'success'">
          {{ user.status === 'active' ? '已封禁' : '正常' }}
        </el-tag>
      </el-form-item>

      <el-form-item label="账户余额">
        <div style="font-weight: bold;">{{ `¥ ${user.balance}` }}</div>
        <el-button
            :disabled="user.status === 'active'"
            plain
            style="margin-left: 10px;"
            type="primary"
            @click="ElMessage.warning('功能暂未开放')"
        >提现
        </el-button>
      </el-form-item>

      <el-divider content-position="left"><h3>其他</h3></el-divider>
      <el-form-item label="注册时间">
        <div style="font-weight: bold;">{{ formatDate(user.createAt) }}</div>
      </el-form-item>

      <el-form-item label="最后登录">
        <div style="font-weight: bold;">{{ formatDate(new Date()) }}</div>
      </el-form-item>
    </el-form>
  </div>
  <!-- 修改手机号对话框 -->
  <el-dialog
      v-model="modifyPhoneDialogVisible"
      align-center
      title="修改手机号"
      width="500"
  >
    <el-form label-width="100px">
      <el-form-item label="新手机号" required>
        <div style="display: flex; gap: 10px">
          <el-input
              v-model="user_NewPhoneNumber"
              :maxlength="11"
              placeholder="请输入新手机号"
          />
          <el-button
              :disabled="countdown > 0"
              plain
              type="primary"
              @click="sendSMSCode"
          >
            {{ countdown > 0 ? `${countdown}秒后重试` : '获取验证码' }}
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="验证码" required>
        <el-input
            v-model="sms_code"
            :maxlength="6"
            placeholder="请输入6位验证码"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="modifyPhoneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="modifyMyPhone">确认修改</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.user-info-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.header-section {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  margin-right: 30px;
  background: #409eff;
}

.base-info h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.role-tag {
  color: #666;
  font-size: 14px;
}

.detail-section {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.form-item-description .description-text {
  /* 保留连续空格，并对换行符生效 */
  white-space: pre-wrap;
  /* 遇到长字符串时也能折行 */
  overflow-wrap: break-word;
  word-break: break-all;
}


.el-divider h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
}

.interactive-icon {
  background-color: transparent;
  transition: background-color 0.3s ease;
  border-radius: 50%;
}

.interactive-icon:hover, .interactive-icon:active {
  background-color: rgba(0, 0, 0, 0.3);
}
</style>
