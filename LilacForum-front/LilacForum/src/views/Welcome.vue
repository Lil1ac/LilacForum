<template>
  <div :class="['welcome-container', { 'fade-out': isEntering }]">
    <video autoplay loop muted playsinline class="background-video" @loadeddata="handleVideoLoaded">
      <source src="@/assets/touko.mp4" type="video/mp4" />
    </video>

    <div v-if="isVideoLoaded" class="overlay"></div>
    <transition name="fade-slide">
      <div v-if="isVideoLoaded" class="welcome-content">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="Logo" class="logo" />
        </div>
        <h1 class="welcome-title">Welcome to LilacForum</h1>
        <p class="welcome-description">Discover, Share, and Connect with the Community</p>
        <el-button v-if="isVideoLoaded" type="primary" @click="enterSite" class="enter-button">
          Enter
        </el-button>
      </div>
    </transition>





  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const isEntering = ref(false);
const isVideoLoaded = ref(false);
const hasNavigated = ref(false); // 确保只跳转一次

const enterSite = () => {
  if (hasNavigated.value) return; // 已经跳转则直接返回
  isEntering.value = true;
  hasNavigated.value = true; // 标记为已跳转
  setTimeout(() => {
    router.push({ name: 'HomePage' });
  }, 300);
};

// 自动跳转逻辑
onMounted(() => {
  if (hasNavigated.value) return; // 如果已跳转，跳过自动跳转逻辑
  setTimeout(() => {
    if (!hasNavigated.value) { // 再次检查手动跳转标志
      isEntering.value = true;
      setTimeout(() => {
        router.push({ name: 'HomePage' });
      }, 1000); // 确保动画和路由跳转同步
    }
  }, 3000); // 3秒后触发淡出动画
});

const handleVideoLoaded = () => {
  isVideoLoaded.value = true;
};
</script>

<style scoped>
.welcome-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  position: relative;
  overflow: hidden;
  transition: opacity 1.8s ease;
}

.welcome-container.fade-out {
  opacity: 0;
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.55);
  z-index: 2;
}

.welcome-content {
  text-align: center;
  z-index: 3;
  animation: fadeIn 3s ease-in-out;
  color: white;
}

.logo-container {
  margin-bottom: 20px;
}

.logo {
  width: 150px;
  height: auto;
  animation: bounceInDown 2s;
}

.welcome-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 20px;
  animation: fadeInUp 2s;
  color: white;
}

.welcome-description {
  font-size: 24px;
  margin-bottom: 40px;
  animation: fadeInUp 3s;
  color: white;
}

.enter-button {
  font-size: 20px;
  padding: 10px 30px;
  animation: fadeInUp 1s;
  opacity: 0;
  animation: fadeIn 1s forwards 1s;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bounceInDown {

  from,
  60%,
  75%,
  90%,
  to {
    animation-timing-function: cubic-bezier(0.215, 0.61, 0.355, 1);
  }

  0% {
    opacity: 0;
    transform: translateY(-3000px);
  }

  60% {
    opacity: 1;
    transform: translateY(25px);
  }

  75% {
    transform: translateY(-10px);
  }

  90% {
    transform: translateY(5px);
  }

  to {
    transform: translateY(0);
  }
}

.fade-slide-enter-active {
  transition: opacity 1s ease, transform 1s ease;
}

.fade-slide-leave-active {
  transition: opacity 1.5s ease, transform 1.5s ease;
}

.fade-slide-enter,
.fade-slide-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(20px);
}
</style>
