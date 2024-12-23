<template>
  <div id="app">
    <!-- 背景视频 -->
    <div v-if="showBackgroundVideo" class="background-video">
      <video autoplay muted loop playsinline>
        <source src="@/assets/ginka.mp4" type="video/mp4" />
      </video>
    </div>

    <!-- 路由视图 -->
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
import { computed } from 'vue';

const route = useRoute();

// 只在登录和注册页面显示背景视频
const showBackgroundVideo = computed(() => {
  return route.path === '/login' || route.path === '/register';
});
</script>

<style>
html,
body,
#app {
  margin: 0;
  padding: 0;
  height: 100%;
  background: transparent;
}

.background-video video {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: -1;
}
</style>