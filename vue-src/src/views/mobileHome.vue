<template>
  <div class="mobile-landing">
    <header class="mobile-header" :class="{ visible: headerVisible }">
      <div class="logo">易互</div>
      <button class="hamburger" @click="toggleMenu">
        <span :class="{ open: menuOpen }"></span>
        <span :class="{ open: menuOpen }"></span>
        <span :class="{ open: menuOpen }"></span>
      </button>
      <nav v-if="menuOpen" class="mobile-nav">
        <a v-for="item in navItems" :key="item.id" @click.prevent="navigate(item.id)">{{ item.label }}</a>
      </nav>
    </header>

    <main>
      <section class="hero">
        <h1>一站式公益项目与活动管理平台</h1>
        <p>多项目 & 多活动深度管理，数字化、专家对接，高效公益生态。</p>
        <el-button type="success" @click="goDashboard">&nbsp;&nbsp;立即体验&nbsp;&nbsp;</el-button>
      </section>

      <template v-for="section in sections" :key="section.id">
        <section :id="section.id" class="section-block">
          <div class="content">
            <h2>{{ section.title }}</h2>
            <p>{{ section.description }}</p>
          </div>
          <div class="cards">
            <div
                v-for="card in section.cards"
                :key="card.title"
                class="card"
                :class="{ 'image-only': cardStates[card.title] }"
                @click.stop="toggleCard(card.title)"
            >
              <transition name="fade" mode="out-in">
                <div class="card-inner" :key="cardStates[card.title] ? card.title + '-image' : card.title + '-icon'">
                  <!-- icon view -->
                  <div v-if="!cardStates[card.title]" class="icon-view">
                    <img :src="card.icon" :alt="card.title" />
                    <h3>{{ card.title }}</h3>
                    <p>{{ card.text }}</p>
                  </div>
                  <!-- full-image view -->
                  <div v-else class="image-view">
                    <img :src="card.image || 'IMAGE_PLACEHOLDER_URL'" :alt="card.title + ' image'" />
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </section>
      </template>

      <section id="contact" class="section-block contact">
        <div class="content">
          <h2>联系我们</h2>
          <p>欢迎留言咨询，我们将尽快与您取得联系！</p>
        </div>
      </section>

      <div class="scroll-hint"></div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import router from '../router';

const menuOpen = ref(false);
const headerVisible = ref(false);
const formData = ref({ name: '', email: '', message: '' });
import { reactive } from 'vue';

const cardStates = reactive({});

function toggleCard(title) {
  cardStates[title] = !cardStates[title];
}
const navItems = [
  { id: 'project', label: '项目管理' },
  { id: 'activities', label: '活动管理' },
  { id: 'checkin', label: '签到统计' },
  { id: 'news', label: '新闻稿' },
  { id: 'survey', label: '满意度调查' },
  { id: 'expert', label: '专家定制' },
  { id: 'contact', label: '联系我们' }
];
import Project1Image from '../assets/home/Project1.png';
import Project2Image from '../assets/home/Project2.png';
import Activity1Image from '../assets/home/Activity1.png';
import Activity2Image from '../assets/home/Activity2.png';
import Signin2Image from '../assets/home/Signin2.png';
import Signin3Image from '../assets/home/Signin3.png';
import News1Image from '../assets/home/News1.png';
import News2Image from '../assets/home/News2.png';
import Fillin2Image from '../assets/home/Fillin2.png';
import Fillin3Image from '../assets/home/Fillin3.png';
import Communicate1Image from '../assets/home/Communicate1.png';
import Communicate2Image from '../assets/home/Communicate2.png';
const sections = [
  {
    id: 'project',
    title: '公益项目管理',
    description: '并行管理多项目，实时查看状态与进度。',
    cards: [
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/folder-invoices.png', title: '多项目管理', text: '创建、分档，状态清晰' ,image:Project1Image},
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/share.png', title: '专家审核', text: '共享评论，优化更快' ,image:Project2Image}
    ]
  },
  {
    id: 'activities',
    title: '活动管理',
    description: '一键创建、通知、档案管理，提高效率。',
    cards: [
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/calendar.png', title: '创建与存档', text: '实时保存，保障数据安全' ,image:Activity1Image},
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/gallery.png', title: '档案管理', text: '图片文档分类存储' ,image:Activity2Image}
    ]
  },
  {
    id: 'checkin',
    title: '签到统计',
    description: '在线签到与表单上传，自动汇总。',
    cards: [
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/checklist.png', title: '记录人数', text: '实时数据保存' ,image: Signin2Image},
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/camera.png', title: '表单上传', text: '照片上传留存档案',image: Signin3Image }
    ]
  },
  {
    id: 'news',
    title: '新闻稿管理',
    description: '图片/链接双模式，一键发布。',
    cards: [
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/news.png', title: '图片新闻稿', text: '图文混排，易读体验',image: News1Image },
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/link.png', title: '链接新闻稿', text: '嵌入外部媒体',image: News2Image }
    ]
  },
  {
    id: 'survey',
    title: '满意度调查',
    description: '多题型问卷，数据可视化。',
    cards: [
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/survey.png', title: '预设问卷', text: '无需配置，快速收集',image: Fillin2Image },
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/combo-chart.png', title: '结果分析', text: '柱状图/饼图实时洞察',image: Fillin3Image }
    ]
  },
  {
    id: 'expert',
    title: '专家定制系统',
    description: '一对一沟通，专业支持。',
    cards: [
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/chat.png', title: '实时沟通', text: '文件&图片共享',image: Communicate1Image },
      { icon: 'https://img.icons8.com/ios-filled/80/ffffff/bell.png', title: '实时通知', text: '不遗漏任何消息',image: Communicate2Image }
    ]
  }
];
sections.forEach(section => {
  section.cards.forEach(card => {
    cardStates[card.title] = false; // 初始为内容显示
  });
});
function toggleMenu() {
  menuOpen.value = !menuOpen.value;
}

function navigate(id) {
  menuOpen.value = false;
  const el = document.getElementById(id);
  if (el) el.scrollIntoView({ behavior: 'smooth' });
}

function goDashboard() {
  router.push('/dashboard');
}

function submitForm() {
  alert(`感谢您的留言，${formData.value.name}！`);
  formData.value = { name: '', email: '', message: '' };
}

onMounted(() => {
  setTimeout(() => (headerVisible.value = true), 10);
});
</script>

<style scoped>
/* Global Reset */
* { box-sizing: border-box; margin: 0; padding: 0; }

.mobile-landing {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  font-family: "Segoe UI", Tahoma, Arial, sans-serif;
  color: #eee;
  background: linear-gradient(270deg, #1A73E8, #4285F4, #6A1B9A, #1A237E);
  background-size: 800% 800%;
  animation: gradientBG 20s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

header.mobile-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(10px);
  z-index: 100;
  transform: translateY(-100%);
  opacity: 0;
  transition: transform 1s ease, opacity 1s ease;
}
header.mobile-header.visible {
  transform: translateY(0);
  opacity: 1;
}

.logo {
  font-size: 1.8rem;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 6px rgba(0,0,0,0.5);
}

.hamburger {
  background: none;
  border: none;
  display: flex;
  flex-direction: column;
  gap: 4px;
  z-index: 101;
}
.hamburger span {
  width: 24px;
  height: 2px;
  background: #fff;
  transition: transform .3s;
}
.hamburger span.open:nth-child(1) {
  transform: rotate(45deg) translateY(6px);
}
.hamburger span.open:nth-child(2) {
  opacity: 0;
}
.hamburger span.open:nth-child(3) {
  transform: rotate(-45deg) translateY(-6px);
}

.mobile-nav {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  display: flex;
  flex-direction: column;
  background: rgba(0,0,0,0.8);
  padding: 1rem;
}
.mobile-nav a {
  padding: .8rem 0;
  border-bottom: 1px solid #fff;
  font-size: 1rem;
  color: #fff;
  text-decoration: none;
}

main {
  width: 100%;
  height: 100%;
  overflow-y: scroll;
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
}

section {
  width: 100%;
  height: 100vh;
  scroll-snap-align: start;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 6rem 2rem 2rem;
  text-align: center;
}

.hero::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('https://www.transparenttextures.com/patterns/cubes.png');
  opacity: 0.05;
  z-index: -1;
}

.content {
  animation: fadeIn 1s ease both;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.cards {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
  margin-top: 2rem;
}
.card {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 1rem;
  padding: 2rem;
  width: 300px;
  max-width: 100%;
  backdrop-filter: blur(8px);
  box-shadow: 0 0 20px rgba(0,0,0,0.2);
  transition: transform .3s, box-shadow .3s;
  text-align: center;
}
.card.image-only {
  padding: 0;
  border-radius: 1rem;
  background: none;
  border: none;
  box-shadow: none;
}

.card.image-only .image-view img {
  width: 100%;
  height: 100%;
  border-radius: 1rem;
  object-fit: cover;
  display: block;
}
.card:hover {
  transform: translateY(-10px) scale(1.05);
  box-shadow: 0 0 30px rgba(255,255,255,0.5);
}
.card .icon-view img {
  width: 60px;
  height: 60px;
  margin-bottom: 1rem;
  filter: drop-shadow(0 0 5px #fff);
}
.card .icon-view img {
  width: 60px;
  height: 60px;
  margin-bottom: 1rem;
  filter: drop-shadow(0 0 5px #fff);
}
.card .image-view {
  width: 100%;
  text-align: center;
}
.card .image-view img {
  width: 100%;
  height: auto;
  border-radius: 1rem;
}

/* adjust inner title/text spacing in icon view */
.card .icon-view h3,
.card .icon-view p {
  margin: 0.5rem 0;
}


.scroll-hint {
  position: fixed;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%) rotate(-45deg);
  width: 30px;
  height:30px;
  border-left:2px solid #fff;
  border-bottom:2px solid #fff;
  animation: bounce 2s infinite;
  z-index:10;
}
@keyframes bounce {
  0%,100% { transform: translateX(-50%) translateY(0) rotate(-45deg); }
  50% { transform: translateX(-50%) translateY(-10px) rotate(-45deg); }
}

.contact-form {
  display: flex;
  flex-direction: column;
  width: 400px;
  max-width:90%;
  gap:1rem;
  background: rgba(255,255,255,0.1);
  padding:2rem;
  border-radius:1rem;
  backdrop-filter: blur(8px);
  margin-top:2rem;
}
.contact-form input,
.contact-form textarea {
  width:100%;
  padding:0.8rem;
  border:none;
  border-radius:0.5rem;
  background:rgba(255,255,255,0.8);
  color:#333;
  font-size:1rem;
}
.contact-form input::placeholder,
.contact-form textarea::placeholder { color:#666; }
.contact-form button {
  padding:1rem;
  border:none;
  border-radius:0.5rem;
  background:#FFD54F;
  color:#1A237E;
  font-weight:bold;
  cursor:pointer;
  transition: background-color .3s ease;
  font-size:1rem;
}
.contact-form button:hover { background:#fbc02d; }

#contact .scroll-hint { display:none; }

.card-image-view img {
  width: 100%;
  border-radius: 1rem;
  object-fit: cover;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.4s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.card-inner > img {
  width: 60px;
  height: 60px;
}
</style>
