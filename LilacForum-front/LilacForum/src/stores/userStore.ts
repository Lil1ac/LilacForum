
import { defineStore } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null as number | null,
    username: '' as string,
    avatar: '' as string,
    role: '' as string
  }),
  actions: {
    setUserInfo(userId: number, username: string, avatar: string, role: string) {
      this.userId = userId;
      this.username = username;
      this.avatar = avatar;
      this.role = role;
    },
    clearUserInfo() {
      this.userId = null;
      this.username = '';
      this.avatar = ''
      this.role = ''
    },
  },
  persist: {
    paths: ['userId', 'username', 'avatar', 'role']
  },
});
