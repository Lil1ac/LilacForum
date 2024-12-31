import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import ForumLayout from '@/views/ForumLayout.vue';
import Welcome from '@/views/Welcome.vue';
import Sections from '@/views/Sections.vue';
import Announcements from '@/views/Announcements.vue';
import EditProfile from '@/views/EditProfile.vue'; // 修改个人信息页面
import UserProfile from '@/views/UserProfile.vue'; // 展示其他用户信息页面
import Login from '@/views/Login.vue';
import Chat from '@/views/Chat.vue';
import Register from '@/views/Register.vue';
import SectionDetail from '@/views/SectionDetail.vue'; // 具体板块页面
import PostDetail from '@/views/PostDetail.vue'; // 帖子详情页面
import SearchResults from '@/views/SearchResults.vue'; // 搜索结果页面
import MyFriend from '@/views/MyFriend.vue';
import Notification from '@/views/Notification.vue';

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    component: Login,
    name: 'Login'
  },
  {
    path: '/register',
    component: Register,
    name: 'Register'
  },
  {
    path: '/welcome',
    component: Welcome,
    name: 'Welcome'
  },
  {
    path: '/forumLayout',
    component: ForumLayout,
    children: [
      {
        path: 'homePage',
        component: HomePage,
        name: 'HomePage'
      },
      {
        path: 'sections',
        component: Sections,
        name: 'Sections'
      },
      {
        path: 'sections/:sectionId',
        component: SectionDetail,
        name: 'SectionDetail'
      },
      {
        path: 'posts/:postId',
        component: PostDetail,
        name: 'PostDetail'
      },
      {
        path: 'announcements',
        component: Announcements,
        name: 'Announcements'
      },
      {
        path: 'profile',
        component: EditProfile,
        name: 'Profile'
      },
      {
        path: 'user/:userId',
        component: UserProfile,
        name: 'UserProfile',
      },
      {
        path: 'search',
        component: SearchResults,
        name: 'SearchResults',
        props: (route: { query: { q?: string; mode?: string } }) => ({
          query: route.query.q,
          mode: route.query.mode
        })
      },
      {
        path: 'chat',
        component: Chat,
        name: 'Chat'
      },
      {
        path: '/myFriend',
        component: MyFriend,
        name: 'MyFriend'
      },
      {
        path: '/notification',
        component: Notification,
        name: 'Notification'
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 }; // 始终滚动到页面顶部
    }
  },
});

// 设置路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (!token && to.name !== 'Login' && to.name !== 'Register') {
    next({ name: 'Login' });
  } else {
    next();
  }
});

export default router;
