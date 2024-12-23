  <template>
    <div class="section-detail-container">
      <!-- 顶部板块描述 -->
      <el-card class="section-header">
        <!-- 背景图片 -->
        <img :src="section.background" alt="背景图片" class="section-background" />
        <div class="section-content">
          <h1>{{ section.title }}</h1>
          <p class="section-description">{{ section.description }}</p>
        </div>
      </el-card>

      <!-- 功能区域 -->
      <div class="section-actions">
        <el-button type="primary" @click="showPostForm = true">发帖</el-button>
        <div class="date-sort-container">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
            end-placeholder="结束日期" @change="fetchPostList" class="date-picker" />

          <el-select v-model="sortOrder" @change="fetchPostList" placeholder="排序方式" class="sort-select">
            <el-option label="最新发布" value="createdTime"></el-option>
            <el-option label="最后回复" value="lastReplyTime"></el-option>
          </el-select>
        </div>


      </div>

      <!-- 帖子总览 -->
      <div class="posts-container">

        <!-- 没有数据时的提示 -->
        <el-empty v-if="!posts.length" description="该板块暂无帖子" />

        <el-card class="post-card" v-for="post in filteredPosts" :key="post.id">
          <div class="post-header">
            <h3 @click="goToPost(post.id)" class="post-title">{{ post.title }}</h3>
            <div class="post-info">
              <span class="author-label">作者：</span>
              <span @click="goToUserProfile(post.authorId)" class="author-name">
                {{ post.author }}
              </span>
              <span class="separator">|</span>
              <span>发布于: {{ post.createdTime }}</span>
              <span class="separator">|</span>
              <span>最后回复于: {{ post.lastReplyTime }}</span>
            </div>
          </div>
          <div class="post-body">
            <span>{{ format.stripHtmlTags(post.content) }}</span>
          </div>
        </el-card>
      </div>

      <!-- 发帖表单的弹出框 -->
      <el-dialog title="发帖" v-model="showPostForm">
        <PostForm :initialData="blankPost" :submitting="submitting" @submit="handleSubmit"
          @cancel="showPostForm = false" />
      </el-dialog>

      <!-- 底部页码 -->
      <Pagination :currentPage="currentPage" :pageSize="pageSize" :total="totalPosts" @update:currentPage="updatePage"
        @change="fetchPostList" @update:page-size="updatePageSize" class="pagination" />

    </div>
  </template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getPostsBySectionId, createNewPost } from '@/api/post';
import { getSectionDetail } from '@/api/section';
import type { Section } from '@/interface/Section';
import type { Post } from '@/interface/Post';
import PageHeader from '@/components/PageHeader.vue';
import Pagination from '@/components/Pagination.vue';
import PostForm from '@/components/PostForm.vue';
import format from '@/utils/format';

const route = useRoute();
const router = useRouter();

const section = ref<Section>({
  id: 0,
  title: '',
  description: '',
  background: ''
});

const posts = ref<Post[]>([]);
const currentPage = ref<number>(1);
const pageSize = ref<number>(10);
const totalPosts = ref<number>(0);;
const sortOrder = ref<string>('createdTime');
const dateRange = ref<[Date, Date] | null>(null);

const showPostForm = ref<boolean>(false);
const blankPost = ref<{ title: string; content: string }>({
  title: '',
  content: ''
});
const submitting = ref<boolean>(false); // 控制提交按钮状态


const sectionId = parseInt(route.params.sectionId as string, 10);
const fetchSectionDetail = async () => {
  section.value = await getSectionDetail(sectionId);//获取板块信息
  console.log(section.value)
};

const fetchPostList = async () => {
  const [begin, end] = dateRange.value
    ? dateRange.value.map(date => date.toISOString().split('T')[0])
    : [undefined, undefined];
  try {
    //获取帖子列表
    const postList = await getPostsBySectionId(
      sectionId, currentPage.value, pageSize.value, sortOrder.value, begin, end);
    console.log('Fetched post list:', postList.data);
    posts.value = postList.data;
    totalPosts.value = postList.total;
  } catch (error) {
    throw error;
  }
};


const filteredPosts = computed(() => posts.value);

const goToPost = (postId: number) => {
  router.push({ name: 'PostDetail', params: { postId } });
};

const goToUserProfile = (authorId: number) => {
  router.push({ name: 'UserProfile', params: { userId: authorId } });
};

const updatePage = (page: number) => {
  currentPage.value = page;
};

const updatePageSize = (size: number) => {
  pageSize.value = size;
};


const handleSubmit = async (newPost: { title: string; content: string }) => {
  submitting.value = true; // 设置按钮为加载状态，防止重复提交
  try {
    await createNewPost(sectionId, newPost);
    showPostForm.value = false;
    fetchPostList(); // 重新获取帖子列表，更新页面
    newPost = { title: '', content: '' }; // 重置表单内容
  } catch (error) {
    throw error;
  } finally {
    submitting.value = false; // 提交完成后解除按钮加载状态
  }
};

onMounted(() => {
  fetchSectionDetail();
  fetchPostList();
});
</script>

<style scoped>
.section-detail-container {
  margin: 20px auto;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-width: 1000px;
  background-color: white;
}

.section-header {
  position: relative;
  background: #999;
  height: 170px;
  margin-bottom: 20px;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
  filter: brightness(0.6);
}

.section-content {
  position: relative;
  z-index: 1;
  color: white;
}

.section-description {
  font-size: 14px;
  color: #ddd;
  margin-top: 25px;
  white-space: normal;
  word-wrap: break-word;
  overflow-wrap: break-word;
}

.section-actions {
  margin: 30px 0;
  display: flex;
  justify-content: space-between;
}

.date-sort-container {
  display: flex;
  gap: 20px;
}

.sort-select {
  width: 100px;
}

.post-card {
  margin: 15px 0;
  border-radius: 0;
  box-shadow: 0 2px 4px #0000001a;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
}

.post-title {
  font-size: 16px;
  color: #409EFF;
  cursor: pointer;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.post-title:hover {
  text-decoration: underline;
}

.post-info {
  color: #999;
  font-size: 12px;
  display: flex;
  flex-shrink: 0;
  white-space: nowrap;
  align-items: center;
  justify-content: flex-end;
}

.author-name {
  cursor: pointer;
}

.author-name:hover {
  text-decoration: underline;
}

.separator {
  margin: 0 6px;
}

.post-body {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 15px 0;
}
</style>
