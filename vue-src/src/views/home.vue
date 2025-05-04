<template>
  <div class="landing-page-wrapper">
    <header ref="headerRef" id="header">
      <div class="logo">易互</div>
      <nav>
        <a href="#project" @click.prevent="scrollToSection('project')">项目管理</a>
        <a href="#activities" @click.prevent="scrollToSection('activities')">活动管理</a>
        <a href="#checkin" @click.prevent="scrollToSection('checkin')">签到统计</a>
        <a href="#news" @click.prevent="scrollToSection('news')">新闻稿</a>
        <a href="#survey" @click.prevent="scrollToSection('survey')">满意度调查</a>
        <a href="#expert" @click.prevent="scrollToSection('expert')">专家定制</a>
        <a href="#contact" @click.prevent="scrollToSection('contact')">联系我们</a>
      </nav>
    </header>

    <main>
      <section class="hero" id="hero">
        <h1 style="font-size:4rem; text-shadow: 0 2px 8px rgba(0,0,0,0.6);">一站式公益项目与活动管理平台</h1>
        <p style="font-size:1.3rem; max-width:900px;">支持多项目与多活动的深度管理，全流程数字化，无缝对接专家，打造高效公益生态。</p>
        <a href="/dashboard">
          <svg t="1746346318090" class="svg" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3816" width="50" height="50"><path d="M856.92 127.83c-5.08 0-10.16 1.17-15.24 3.13L151.8 420.42c-30.86 12.89-32.04 55.86-2.35 71.1L369 601.29c-9.38 15.24-7.43 35.16 5.46 48.05 7.42 7.81 17.58 11.72 27.73 11.72 7.04 0 14.46-1.96 20.71-6.25l109.78 219.54c7.03 14.45 21.1 21.49 34.76 21.49 14.85 0 29.3-7.81 35.95-23.83l289.47-689.88c11.33-26.96-9.77-54.3-35.94-54.3z m-292.6 635.58L481.5 597.39l42.58-42.19c15.23-15.24 15.23-39.85 0-55.08-7.81-7.81-17.58-11.72-27.74-11.72-10.15 0-19.92 3.91-27.73 11.72l-42.19 42.19-165.64-82.82 523.08-219.54-219.54 523.46z" fill="#ffffff" p-id="3817"></path></svg>
          <div class="gotext" >立即前往</div>
        </a>
        <div class="scroll-hint"></div>
      </section>

      <section id="project">
        <div class="content">
          <div >
            <img class="image-container" src="../assets/home/Project1.png" alt="项目管理图" />
            <img class="image-container" src="../assets/home/Project2.png" alt="项目管理图" />
          </div>
          <h2>公益项目管理</h2>
          <p>并行管理多个项目，实时查看状态与进度。清晰分类，专家评审，结果状态一目了然。</p>
          <div class="cards">
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/folder-invoices.png" alt="多项目管理" />
              <h3>多项目管理</h3>
              <p>创建、切换、分档，活动与项目清晰明了。</p>
            </div>
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/share.png" alt="团队协作" />
              <h3>专家审核</h3>
              <p>与专家共享与评论，让项目更优秀。</p>
            </div>
          </div>
        </div>
      </section>

      <section id="activities">
        <div class="content">
          <div>
            <img class="image-container" src="../assets/home/Activity1.png" alt="活动管理图" />
            <img class="image-container" src="../assets/home/Activity2.png" alt="活动管理图" />
          </div>
          <h2>活动管理</h2>
          <p>在项目内创建多个活动，全面管理活动名称、通知、档案与细节图片，提升执行效率。</p>
          <div class="cards">
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/calendar.png" alt="创建活动" />
              <h3>创建与存档</h3>
              <p>活动名称、时间、地点与描述一键填写并实时保存，保障数据安全性。</p>
            </div>
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/gallery.png" alt="档案管理" />
              <h3>档案管理</h3>
              <p>上传活动图片与文档，分类存储，检索快速。</p>
            </div>
          </div>
        </div>
      </section>

      <section id="checkin">
        <div class="content">
          <div>
            <img class="image-container" src="../assets/home/Signin2.png" alt="签到统计图" />
            <img class="image-container" src="../assets/home/Signin3.png" alt="签到统计图" />
          </div>
          <h2>签到统计</h2>
          <p>精准记录签到信息，支持在线与纸质签到表上传，并自动汇总。</p>
          <div class="cards">
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/checklist.png" alt="在线签到" />
              <h3>记录人数</h3>
              <p>详细记录人数，数据实时保存。</p>
            </div>
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/camera.png" alt="签到表" />
              <h3>签到表上传</h3>
              <p>上传纸质签到表照片，留存档案。</p>
            </div>
          </div>
        </div>
      </section>

      <section id="news">
        <div class="content">
          <div>
            <img class="image-container" src="../assets/home/News1.png" alt="新闻稿管理图" />
            <img class="image-container" src="../assets/home/News2.png" alt="新闻稿管理图" />
          </div>
          <h2>新闻稿管理</h2>
          <p>支持图片和链接两种形式上传，一键发布，提高宣传效果。</p>
          <div class="cards">
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/news.png" alt="图片新闻稿" />
              <h3>图片新闻稿</h3>
              <p>上传图文混排新闻稿，如翻书般体验。</p>
            </div>
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/link.png" alt="链接新闻稿" />
              <h3>链接新闻稿</h3>
              <p>嵌入外部媒体链接，提升公信力。</p>
            </div>
          </div>
        </div>
      </section>

      <section id="survey">
        <div class="content">
          <div >
            <img class="image-container" src="../assets/home/Fillin3.png" alt="满意度调查图" />
            <img class="image-container" src="../assets/home/Fillin2.png" alt="满意度调查图" />
          </div>
          <h2>满意度调查</h2>
          <p>内置问卷系统，支持多题型，并提供可视化分析报告。</p>
          <div class="cards">
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/survey.png" alt="问卷系统" />
              <h3>预设问卷</h3>
              <p>系统内置满意度调查问卷，无序用设置，一键收集。</p>
            </div>
            <div class="card">
              <svg class="svg" t="1746346056040" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2740"><path d="M767 879.3H100c-19.3 0-35 15.7-35 35s15.7 35 35 35h667c19.3 0 35-15.7 35-35s-15.7-35-35-35zM524.3 429.2L720 260.1c14.6-12.6 16.2-34.7 3.6-49.4-12.6-14.6-34.7-16.2-49.4-3.6L510.7 348.5c-5.3 4.6-13.2 4.6-18.5 0l-82.2-71c-13.1-11.4-32.6-11.4-45.8 0L211.7 409.3c-14.6 12.6-16.2 34.7-3.6 49.4 12.6 14.6 34.7 16.2 49.4 3.6l120.4-104.1c5.3-4.6 13.2-4.6 18.5 0l82.2 71c13.1 11.4 32.6 11.4 45.7 0z" p-id="2741" fill="#ffffff"></path><path d="M943.2 853.6l-83.5-83.5c29.7-37.4 45.7-83.5 45.7-131.9 0-3.5-0.1-6.9-0.3-10.4h0.7V135.5c0-36.4-29.6-66-66-66H135.5c-36.4 0-66 29.6-66 66V684c0 36.4 29.6 66 66 66h375.6c37.6 60.8 104.8 101.4 181.4 101.4 41.6 0 81.4-11.9 115.5-33.9l85.8 85.8c13.7 13.7 35.8 13.7 49.5 0 13.6-13.8 13.6-36-0.1-49.7zM139.5 679.9V139.5H836v341.4c-37.9-34.6-88.3-55.8-143.6-55.8-117.5 0-213.1 95.6-213.1 213.1 0 14.3 1.4 28.2 4.1 41.7H139.5z m552.9 101.4c-78.9 0-143.1-64.2-143.1-143.1s64.2-143.1 143.1-143.1 143.1 64.2 143.1 143.1-64.2 143.1-143.1 143.1z" p-id="2742" fill="#ffffff"></path></svg>
              <h3>结果分析</h3>
              <p>生成柱状图、饼图、折线图等，实时洞察。</p>
            </div>
          </div>
        </div>
      </section>

      <section id="expert">
        <div class="content">
          <div>
            <img class="image-container" src="../assets/home/Communicate1.png"  alt="专家定制图" />
            <img class="image-container" src="../assets/home/Communicate2.png" alt="专家定制图" />
          </div>
          <h2>专家定制系统</h2>
          <p>内置一对一聊天与指导，项目设计与执行全程获得专业支持。</p>
          <div class="cards">
            <div class="card">
              <img src="https://img.icons8.com/ios-filled/100/ffffff/chat.png" alt="专家沟通" />
              <h3>实时沟通</h3>
              <p>内置聊天系统，支持文件与图片共享，专家实时答疑。</p>
            </div>
            <div class="card">
              <svg class="svg" t="1746345448458" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1660" width="30" height="30"><path d="M889.828994 749.637494c-1.204099-1.548127-119.205779-166.165631-119.205779-287.435579 0-168.401814-76.202251-254.752898-162.725349-286.919536L607.897867 159.973123c0-52.980346-43.175542-95.983874-95.983874-95.983874S415.930119 107.164791 415.930119 159.973123l0 16.685369c-10.836889 7.912649-16.169326 21.845792-11.868974 35.262893 5.332437 16.857383 23.393919 25.974131 40.251302 20.641693 17.717453-5.676466 36.983034-8.428691 56.936671-8.428691l21.32975 0c115.249454 0 184.055098 89.103309 184.055098 238.239543 0 108.024861 72.933983 235.315303 110.261045 293.456073-57.452713 26.146145-183.023014 76.202251-304.809004 76.202251-121.441962 0-247.356291-50.400134-304.809004-76.546279 37.327062-57.968755 110.089031-184.915169 110.089031-293.112044 0-62.441122 11.524945-114.217369 34.574836-153.952629 8.77272-15.309256 3.612296-34.918864-11.69696-43.691584-15.48127-8.944734-34.918864-3.612296-43.691584 11.69696-28.554342 49.540064-43.175542 112.1532-43.175542 185.947253 0 121.269948-118.00168 285.199395-119.205779 286.919536-5.332437 7.568621-7.396607 16.857383-5.332437 25.974131 2.064169 8.944734 7.912649 16.685369 15.997312 20.985721 5.676466 3.096254 107.336805 57.280699 233.423148 84.458928 21.32975 66.397447 73.966068 111.29313 133.654964 111.29313s112.325214-44.895683 133.654964-111.29313c126.086343-27.178229 227.746682-81.018646 233.423148-83.942886 8.084663-4.472367 13.933143-12.040988 16.169326-20.985721C897.225601 766.666891 895.333445 757.206115 889.828994 749.637494zM501.249118 159.973123c-7.224593 0-14.277171 1.032085-21.32975 1.548127L479.919368 159.973123c0-17.545439 14.449185-31.994625 31.994625-31.994625 17.545439 0 31.994625 14.449185 31.994625 31.994625l0 0.860071c-7.224593-0.516042-14.277171-0.860071-21.32975-0.860071L501.249118 159.973123zM511.913993 928.016126c-22.361834 0-43.175542-13.417101-57.968755-35.262893 19.26558 2.064169 38.531161 3.268268 57.968755 3.268268s38.875189-1.376113 57.968755-3.268268C555.089535 914.599026 534.275827 928.016126 511.913993 928.016126z" p-id="1661"></path></svg>
              <h3>实时通知</h3>
              <p>消息实时通知，不错过任何一条消息。</p>
            </div>
          </div>
        </div>
      </section>

      <section id="contact">
        <div class="content">
          <h2>联系我们</h2>
          <p>欢迎留言咨询，我们将尽快与您取得联系！</p>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import router from "../router";

// Template ref for the header element to control its visibility class
const headerRef = ref(null);

// Reactive state for the contact form data
const formData = reactive({
  name: '',
  email: '',
  phone: '',
  message: ''
});
const scrollToSection = (sectionId) => {
  const sectionElement = document.getElementById(sectionId);
  if (sectionElement) {
    sectionElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};

// onMounted lifecycle hook: Runs after the component's DOM has been mounted.
// Used here to replicate the original script's behavior of adding the 'visible' class to the header.
onMounted(() => {
  const mobileMaxWidth = 768;
  // 检测屏幕宽度
  if (window.innerWidth <= mobileMaxWidth) {
    router.push('/mobileHome');
  }
  if (headerRef.value) {
    // Add the 'visible' class to trigger the fade-in animation defined in CSS
    headerRef.value.classList.add('visible');
  }
});
</script>

<style scoped>
/* Global Reset - Applied within the component scope */
* {
  /* Box-sizing is often set globally, but included here for completeness if not set elsewhere */
  box-sizing: border-box;
  /* Margin/Padding reset applied to elements within this component */
  margin: 0;
  padding: 0;
}

/* Apply styles previously on html/body to the component's root wrapper */
.landing-page-wrapper {
  width: 100%;
  height: 100vh; /* Ensure wrapper takes full viewport height */
  overflow: hidden; /* Hide overflow from the wrapper */
  font-family: "Segoe UI", Tahoma, Arial, sans-serif;
  color: #eee;
  background: linear-gradient(270deg, #1A73E8, #4285F4, #6A1B9A, #1A237E);
  background-size: 800% 800%;
  animation: gradientBG 20s ease infinite;
}

a {
  text-decoration: none;
  color: inherit;
}

/* Background gradient animation */
@keyframes gradientBG {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* Top Navigation */
header {
  position: fixed;
  top: 0;
  left: 0; /* Added left: 0 for certainty */
  width: 100%;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(10px);
  z-index: 100;
  transform: translateY(-100%); /* Start fully hidden above */
  opacity: 0;
  transition: transform 1s ease, opacity 1s ease; /* Smooth transition for visibility */
  /* Removed animation property here, handled by class toggle + transition */
}

header.visible {
  transform: translateY(0); /* Slide in */
  opacity: 1; /* Fade in */
}

header .logo {
  font-size: 1.8rem;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2px 6px rgba(0,0,0,0.5);
}

header nav a {
  margin-left: 1.5rem;
  font-size: 1rem;
  color: #fff;
  transition: color .3s;
}

header nav a:hover {
  color: #FFD54F;
}

/* Main Content Container */
main {
  width: 100%;
  height: 100%; /* Takes full height of the wrapper */
  overflow-y: scroll;
  overflow-x: hidden;
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth; /* Native smooth scrolling for anchor links */
}

section {
  width: 100%;
  height: 100vh; /* Each section takes full viewport height */
  scroll-snap-align: start; /* Snap alignment */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 6rem 2rem 2rem; /* Added top padding to avoid overlap with fixed header */
  text-align: center; /* Center text by default */
}

/* Image Container */
.image-container {
  width: 400px;
  max-width: 90%; /* Ensure responsiveness */
  height: 250px;
  margin-bottom: 2rem;
  background: rgba(255,255,255,0.1);
  border-radius: 0.5rem;
  overflow: hidden;
}

.image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block; /* Remove potential bottom space */
}
.image-container svg {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block; /* Remove potential bottom space */
}

/* Fade-in animations (used by content) */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Neon Card Styling */
.card {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.3);
  border-radius: 1rem;
  padding: 2rem;
  width: 300px;
  max-width: 100%; /* Responsive width */
  box-shadow: 0 0 20px rgba(0,0,0,0.2);
  backdrop-filter: blur(8px);
  transition: transform .3s, box-shadow .3s;
  text-align: center; /* Center card content */
}

.card:hover {
  transform: translateY(-10px) scale(1.05);
  box-shadow: 0 0 30px rgba(255,255,255,0.5);
}

.card img {
  width: 60px;
  height: 60px; /* Ensure consistent icon size */
  margin-bottom: 1rem;
  filter: drop-shadow(0 0 5px #fff);
  display: inline-block; /* Center the image properly */
}
.card svg.svg {
  fill: #ffffff;
  width: 60px;
  height: 60px; /* Ensure consistent icon size */
  margin-bottom: 1rem;
  color: white;
  filter: drop-shadow(0 0 5px #fff);
  display: inline-block; /* Center the image properly */
}
.card h3 {
  font-size: 1.6rem;
  margin-bottom: .5rem;
  color: #FFD54F;
  text-shadow: 0 1px 4px rgba(0,0,0,0.5);
}

.card p {
  font-size: 1rem;
  color: #ddd;
}

/* Content Styling (Titles, Paragraphs, Card Layout) */
.content {
  /* Animation applied directly for simplicity if elements are always visible on section load */
  animation: fadeIn 1s ease both;
}
.content h2 {
  font-size: 3rem;
  margin-bottom: 1rem;
  background: linear-gradient(45deg, #FFD54F, #FF8A65);
  -webkit-background-clip: text;
  background-clip: text; /* Standard property */
  -webkit-text-fill-color: transparent;
  /* Removed animation here, applied to .content wrapper */
}

.content p {
  font-size: 1.2rem;
  max-width: 800px;
  line-height: 1.6;
  margin: 0 auto 2rem auto; /* Center paragraph */
  color: #eee;
  /* Removed animation here, applied to .content wrapper */
}

.cards {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
  margin-top: 2rem; /* Added margin top for spacing */
}

/* Scroll Down Hint Arrow */
.scroll-hint {
  position: fixed; /* 改为固定定位 */
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%) rotate(-45deg);
  width: 30px;
  height: 30px;
  border-left: 2px solid #fff;
  border-bottom: 2px solid #fff;
  animation: bounce 2s infinite;
  z-index: 10; /* 确保它在内容之上 */
}

@keyframes bounce {
  0%, 100% {
    transform: translateX(-50%) translateY(0) rotate(-45deg);
  }
  50% {
    transform: translateX(-50%) translateY(-10px) rotate(-45deg);
  }
}

/* Background Texture for Hero Section */
.hero::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('https://www.transparenttextures.com/patterns/cubes.png');
  opacity: 0.05; /* Reduced opacity slightly */
  z-index: -1; /* Ensure it's behind content */
  pointer-events: none; /* Prevent interaction */
}

/* Contact Us Form */
.contact-form {
  display: flex;
  flex-direction: column;
  width: 400px;
  max-width: 90%; /* Responsive */
  gap: 1rem;
  background: rgba(255,255,255,0.1);
  padding: 2rem;
  border-radius: 1rem;
  backdrop-filter: blur(8px);
  margin-top: 2rem; /* Added margin top */
}

.contact-form input,
.contact-form textarea {
  width: 100%;
  padding: 0.8rem;
  border: none;
  border-radius: 0.5rem;
  background: rgba(255, 255, 255, 0.8); /* Slightly transparent white */
  color: #333; /* Darker text for readability */
  font-size: 1rem;
}
.contact-form input::placeholder,
.contact-form textarea::placeholder {
  color: #666; /* Placeholder text color */
}


.contact-form button {
  padding: 1rem;
  border: none;
  border-radius: 0.5rem;
  background: #FFD54F;
  color: #1A237E;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
  font-size: 1rem;
}
.contact-form button:hover {
  background: #fbc02d; /* Slightly darker yellow on hover */
}

/* Remove scroll hint from the very last section */
#contact .scroll-hint {
  display: none;
}
.gotext{
  margin: 5px;
  font-size: 30px;
  filter: drop-shadow(0 0 5px #fff);
}
</style>