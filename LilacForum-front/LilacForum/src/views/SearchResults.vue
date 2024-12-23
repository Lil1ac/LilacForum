<template>
    <div class="search-results">
        <div class="search-header">
            <h2 class="search-title">搜索结果: "{{ keyword }}"</h2>
            <!-- 排序方式选择 -->
            <el-select v-model="sortOrder" @change="fetchSearchResults" placeholder="选择排序方式" class="search-mode-select">
                <el-option label="最新发布" value="createdTime" />
                <el-option label="最后回复" value="lastReplyTime" />
            </el-select>
        </div>

        <!-- 没有找到内容时的提示 -->
        <el-empty v-if="!searchPostList.length && !loading" description="没有找到匹配的内容" />

        <!-- 搜索结果列表 -->
        <el-row :gutter="20" v-if="searchPostList.length">
            <el-col :span="24" v-for="post in searchPostList" :key="post.id">
                <el-card class="search-result-card">
                    <h3 class="post-title" @click="goToPost(post.id)">{{ post.title }}</h3>
                    <p class="post-content">{{ format.stripHtmlTags(post.content) }}</p>
                    <div class="post-info">
                        <span class="label">作者:
                            <span class="author" @click="goToUserProfile(post.authorId)">{{ post.author }}</span>
                        </span>
                        <span class="label">板块:
                            <span class="section" @click="goToSection(post.sectionId)">{{ post.sectionTitle }}</span>
                        </span>
                        <span class="label">发布日期:
                            <span class="createdTime">{{ format.formatDate(post.createdTime) }}</span>
                        </span>
                        <span class="label">最后回复:
                            <span class="lastReplyTime">{{ format.formatDate(post.lastReplyTime) }}</span>
                        </span>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 分页控件 -->
        <Pagination :currentPage="currentPage" :pageSize="pageSize" :total="totalResults" @change="fetchSearchResults"
            @update:currentPage="updatePage" @update:page-size="updatePageSize" />
    </div>
</template>


<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { searchPosts } from '@/api/post';
import type { Post } from '@/interface/Post';
import Pagination from '@/components/Pagination.vue';
import format from '@/utils/format';

const route = useRoute();
const router = useRouter();
const searchPostList = ref<Post[]>([]);
const totalResults = ref<number>(0);
const currentPage = ref<number>(1);
const pageSize = ref<number>(10);
const loading = ref<Boolean>(false);
const sortOrder = ref<string>('createdTime');

const keyword = ref<string>('');
const searchMode = ref<string>('');

// 获取搜索结果
const fetchSearchResults = async () => {
    keyword.value = route.query.keyword as string;
    searchMode.value = route.query.mode as string;
    if (keyword && searchMode) {
        loading.value = true;
        try {
            const results = await searchPosts(keyword.value, currentPage.value, pageSize.value, searchMode.value, sortOrder.value);
            searchPostList.value = results.data;
            console.log('searchPostList', searchPostList.value);
            totalResults.value = results.total;
        } catch (error) {
            console.error(error);
        } finally {
            loading.value = false;
        }
    }
};

const updatePage = (page: number) => {
    currentPage.value = page;
};

const updatePageSize = (size: number) => {
    pageSize.value = size;
};

// 跳转到帖子详情
const goToPost = (postId: number) => {
    router.push({ name: 'PostDetail', params: { postId } });
};

// 跳转到版块详情
const goToSection = (sectionId: number) => {
    router.push({ name: 'SectionDetail', params: { sectionId } });
};


//跳转作者
const goToUserProfile = (authorId: number) => {
    router.push({ name: 'UserProfile', params: { userId: authorId } });
};

// 监听路由参数的变化，如果 keyword 和 mode 变化则触发搜索逻辑
watch(
    () => [route.query.keyword, route.query.mode],
    ([newKeyword, newMode], [oldKeyword, oldMode]) => {
        if (newKeyword !== oldKeyword || newMode !== oldMode) {
            fetchSearchResults();
        }
    }
);

onMounted(() => {
    fetchSearchResults();
});
</script>

<style scoped>
.search-results {
    padding: 20px;
    width: 1000px;
    margin: 0 auto;
    background-color: white;
}

.search-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.search-title {
    font-size: 24px;
    font-weight: bold;
}

.search-mode-select {
    width: 200px;
}

.search-result-card {
    margin-bottom: 20px;
}


.post-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.post-title {
    font-size: 18px;
    color: #409EFF;
    cursor: pointer;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}


.post-info {
    font-size: 14px;
    color: #666;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-end;
}


.post-content {
    color: #555;
    font-size: 16px;
    margin-top: 10px;
    line-height: 1.6;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.label {
    margin-right: 15px;
}

.author,
.section {
    color: #666;
    cursor: pointer;
}

.post-title:hover,
.author:hover,
.section:hover {
    text-decoration: underline;
    color: #409EFF
}
</style>