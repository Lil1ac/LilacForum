<template>
    <div class="profile-page">
        <!-- 用户信息展示区域 -->
        <el-card class="user-profile-card" shadow="hover">
            <div class="user-profile-header">
                <img :src="userInfo.avatar || defaultAvatar" alt="User Avatar" class="user-avatar" />
                <div class="user-basic-info">
                    <h2 class="username">{{ userInfo.username }}</h2>
                    <p class="bio">{{ userInfo.bio || '暂无个人简介' }}</p>
                </div>
            </div>

            <el-divider></el-divider>

            <!-- 用户详细信息 -->
            <div class="user-details">
                <el-row>
                    <el-col :span="12">
                        <p><strong>邮箱：</strong>{{ userInfo.email }}</p>
                    </el-col>
                    <el-col :span="12">
                        <p><strong>性别：</strong>{{ userInfo.gender || '未填写' }}</p>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <p><strong>年龄：</strong>{{ userInfo.age || '未填写' }}</p>
                    </el-col>
                    <el-col :span="12">
                        <p><strong>职业：</strong>{{ userInfo.profession || '未填写' }}</p>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24">
                        <p><strong>爱好：</strong>{{ userInfo.hobby || '未填写' }}</p>
                    </el-col>
                </el-row>
            </div>
        </el-card>

        <!-- 用户帖子展示区域 -->
        <div class="user-posts">
            <h3>{{ userInfo.username }}的帖子</h3>
            <el-empty v-if="posts.length === 0" description="该用户暂无帖子" />

            <el-row :gutter="20" v-else>
                <el-col :span="24" v-for="post in posts" :key="post.id">
                    <el-card shadow="hover" class="post-card" @click="goToPost(post.id)">
                        <div class="post-header">
                            <h4 class="post-title">{{ post.title }}</h4>
                            <p class="post-info">
                                <span @click.stop="goToSection(post.sectionId)" class="section-link">{{
                                    post.sectionTitle
                                }}</span>
                                <span class="created-time">{{ format.formatDate(post.createdTime) }}</span>
                            </p>
                        </div>
                        <p class="post-content">{{ format.stripHtmlTags(post.content) }}</p>
                    </el-card>
                </el-col>
            </el-row>

            <!-- 触发器元素 -->
            <div ref="postObserver" class="observer-trigger"></div>

            <!-- 加载中指示 -->
            <div v-if="loadingMore" class="loading-more">加载更多帖子中...</div>

            <!-- 没有更多内容提示 -->
            <div v-if="!hasMorePosts && !loadingMore" class="no-more-posts">没有更多帖子了</div>
        </div>
    </div>
</template>


<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getUserInfo } from '@/api/user';
import { getUserPosts } from '@/api/post';
import type { User } from '@/interface/User';
import type { Post } from '@/interface/Post';
import { useIntersectionObserver } from '@vueuse/core';
import format from '@/utils/format';

const route = useRoute();
const router = useRouter();
const userId = Number(route.params.userId);

const userInfo = ref<User>({
    uid: 0,
    username: '',
    password: '',
    email: '',
    gender: '',
    age: 0,
    profession: '',
    hobby: '',
    bio: '',
    avatar: 'https://via.placeholder.com/150',
    role: '',
});



// 获取用户信息
const fetchUserInfo = async () => {
    try {
        const profileData = await getUserInfo(userId);
        userInfo.value = profileData;
        await loadPosts(); // 初始化加载帖子
    } catch (error) {
        throw error;
    }
};

const posts = ref<Post[]>([]);
const page = ref(1);
const pageSize = 10;
const loadingMore = ref(false);
const hasMorePosts = ref(true);
const defaultAvatar = '/path/to/default-avatar.png';
// 加载用户帖子
const loadPosts = async () => {
    if (!hasMorePosts.value || loadingMore.value) return;

    try {
        loadingMore.value = true;
        const postList = await getUserPosts(userId, page.value, pageSize);
        if (postList.length === 0) {
            hasMorePosts.value = false;
        } else {
            posts.value = [...posts.value, ...postList];
            page.value += 1;
        }
    } catch (error) {
        throw error;
    } finally {
        loadingMore.value = false;
    }
};

// 监听 Intersection Observer
const postObserver = ref<HTMLDivElement | null>(null);

const { stop } = useIntersectionObserver(
    postObserver,
    ([{ isIntersecting }]) => {
        if (isIntersecting && hasMorePosts.value) {
            loadPosts();
        }
    },
    {
        root: null,
        threshold: 0.5, // 可见度阈值为 50%
    }
);

// 跳转到帖子详情
const goToPost = (postId: number) => {
    router.push({ name: 'PostDetail', params: { postId } });
};

// 跳转到版块详情
const goToSection = (sectionId: number) => {
    router.push({ name: 'SectionDetail', params: { sectionId } });
};


// 页面加载时获取用户信息
onMounted(() => {
    fetchUserInfo();
});

// 监听路由参数变化
watch(() => route.params.userId, (newUserId, oldUserId) => {
    console.log(`User ID changed from ${oldUserId} to ${newUserId}`);
    fetchUserInfo();
});

// 每次 posts 更新后，确保触发器移动到最新加载内容的末尾
watch(posts, () => {
    if (postObserver.value) {
        (postObserver.value as HTMLDivElement).scrollIntoView();
    }
});
</script>

<style scoped>
.profile-page {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;
    background-color: #f9f9f9;
}

.user-profile-card {
    margin-bottom: 20px;
    border-radius: 8px;
}

.user-profile-header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.user-avatar {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 20px;
    border: 3px solid #eaeaea;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.user-basic-info {
    flex: 1;
}

.username {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 5px;
}

.bio {
    font-size: 14px;
    color: #888;
    line-height: 1.6;
}

.user-details {
    font-size: 16px;
    line-height: 1.8;
}

.user-details p {
    margin: 8px 0;
}

.user-posts {
    margin-top: 30px;
}

.observer-trigger {
    height: 1px;
}


.loading-more,
.no-more-posts {
    text-align: center;
    padding: 10px;
    color: #999;
}

.loading-more {
    color: #007bff;
}

.no-more-posts {
    color: #ff4d4f;
}

.post-card {
    cursor: pointer;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    border-radius: 8px;
    margin-bottom: 20px;
}

.post-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.post-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.post-title {
    font-size: 18px;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.post-info {
    font-size: 14px;
    color: #999;
}

.section-link {
    color: #666;
    cursor: pointer;
    margin-right: 10px;
}

.section-link:hover {
    text-decoration: underline;
}

.created-time {
    color: #666;
}

.post-content {
    color: #555;
    font-size: 16px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
</style>
