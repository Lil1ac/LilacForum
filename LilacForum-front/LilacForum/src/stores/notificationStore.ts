// stores/notificationStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useNotificationStore = defineStore('notification', () => {
  const notificationCount = ref<number>(0);

  // 设置通知数量
  const setNotificationCount = (count: number) => {
    notificationCount.value = count;
  };

  return {
    notificationCount,
    setNotificationCount
  };
});
