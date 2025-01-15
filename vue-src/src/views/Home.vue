<template>
  <BackgroundCarousel />
  <div class="main-content">
    <h2>页面其他部分内容</h2>
    <p>这里是向下滚动之后的页面内容。</p>
    <h2>页面其他部分内容</h2>
  </div>

  <div class="main-content">
    <div class="section">
      <h2>名句与感悟</h2>
      <p class="quote">“我们没有继承地球，而是从我们的孩子那里借来了它。” — 原住民谚语</p>
      <p class="quote">“每个人都可以成为环保英雄，因为每一小步都能为地球带来改变。” — David Attenborough</p>
    </div>

    <div class="section">
      <h2>优秀环保人物事迹</h2>

      <div class="figure">
        <img src="../assets/user2.jpg" alt="王小">
        <div class="description">
          <strong>王小</strong> — 环保志愿者，10年来一直致力于清理海滩垃圾，组织了超过50次环保行动，带领上千名志愿者共同维护海洋环境。
        </div>
      </div>

      <div class="figure">
        <img src="../assets/user1.jpg" alt="李美">
        <div class="description">
          <strong>李美</strong> — 城市社区环保领袖，推行了废物分类和回收项目，显著减少了社区垃圾处理成本，并为居民提供了清洁的生活环境。
        </div>
      </div>
    </div>

    <div class="section">
      <div class="tip">
        <h3>绿色生活小贴士</h3>
        <p>随手关灯、尽量使用可循环物品、减少一次性塑料使用，这些简单的小习惯不仅可以降低碳足迹，还能让我们共同守护家园。</p>
      </div>
    </div>

    <div class="section">
      <h2>平台功能介绍</h2>
      <div class="grid">
        <div class="grid-item">
          <h3>积分激励</h3>
          <p>通过环保行为获得积分，积分可用于兑换绿色产品。</p>
        </div>
        <div class="grid-item">
          <h3>行为审核</h3>
          <p>提交你的绿色行动记录，我们将审核并给予相应奖励。</p>
        </div>
        <div class="grid-item">
          <h3>团队任务</h3>
          <p>加入或创建一个环保团队，一起完成更多挑战。</p>
        </div>
      </div>
    </div>

    <div class="section">
      <h2>最新动态</h2>
      <div class="grid">
        <div class="grid-item">
          <h3>环保嘉年华</h3>
          <p>加入本周末的社区环保嘉年华，感受绿色生活的乐趣。</p>
        </div>
        <div class="grid-item">
          <h3>线上讲座</h3>
          <p>参与我们的线上环保讲座，获取更多知识和技能。</p>
        </div>
        <div class="grid-item">
          <h3>环保日活动</h3>
          <p>每月的第一个星期六，参与本地的环保活动。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  components: {
    BackgroundCarousel,
  },
};
</script>

<style scoped>
.main-content {
  padding: 40px;
  background-color: #f5f5f5;
}

</style>
