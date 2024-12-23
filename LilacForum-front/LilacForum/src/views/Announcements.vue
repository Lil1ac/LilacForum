<template>
  <div>
    <h2>公告</h2>
    <ul>
      <li v-for="announcement in announcements" :key="announcement.id">{{ announcement.content }}</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const announcements = ref<{ id: number, content: string }[]>([]);

const fetchAnnouncements = async () => {
  // 模拟从后端获取数据
  announcements.value = [
    { id: 1, content: '公告 1 的内容' },
    { id: 2, content: '公告 2 的内容' },
    // 继续添加更多公告
  ];
};

onMounted(() => {
  fetchAnnouncements();
});
</script>

<style scoped>
</style>
