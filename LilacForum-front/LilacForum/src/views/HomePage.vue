<template>
  <div class="home-container">
    <!-- 走马灯 -->
    <div class="carousel-container">
      <el-carousel :interval="5000" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <img :src="item.image" alt="Banner" />
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 内容区 -->
    <div class="content-container">
      <div class="recent-container">
        <h2>最新发帖</h2>
        <section class="recent-posts" v-infinite-scroll="loadMorePosts" infinite-scroll-distance="1">
          <el-card v-for="post in recentPosts" :key="post.id" class="post-card">
            <div class="card-content">
              <h3 class="title" @click="gotoPost(post.id)">{{ post.title }}</h3>
              <span class="meta">作者: {{ post.author }} | 发布于: {{ post.createdTime }}</span>
            </div>
          </el-card>
        </section>
      </div>
      <!-- 最近帖子 -->
      <div class="recent-container">
        <h2>最近回复</h2>
        <section class="recent-comments" v-infinite-scroll="loadMoreComments" infinite-scroll-distance="1">
          <el-card v-for="comment in recentComments" :key="comment.id" class="comment-card">
            <div class="card-content">
              <span class="content" @click="gotoPost(comment.postId)">{{ comment.content }}</span>
              <p class="meta">作者: {{ comment.author }} | 回复于: {{ comment.createdTime }}</p>
            </div>
          </el-card>
        </section>
      </div>
    </div>

    <!-- 底部装饰 -->
    <footer class="footer">
      <p>欢迎来到我们的论坛！</p>
      <p>探索更多精彩内容，参与讨论吧！</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { searchPosts } from '@/api/post';
import { searchComments } from '@/api/comment';
import type { Post } from '@/interface/Post';
import type { Comment } from '@/interface/Comment';
import getAssetsFile from '@/utils/file';

const carouselItems = ref<{ image: string }[]>([
  { image: getAssetsFile('carousel-1.png') },
  { image: getAssetsFile('carousel-2.png') },
  { image: getAssetsFile('carousel-3.png') }
]);

const recentPosts = ref<Post[]>([]);
const recentComments = ref<Comment[]>([]);

const router = useRouter();

const postLoading = ref(false);
const commentLoading = ref(false);

const postPage = ref(1);
const commentPage = ref(1);

// 加载更多帖子
async function loadMorePosts() {
  console.log('loadMorePosts');
  if (postLoading.value) return;
  postLoading.value = true;

  try {
    const postPageSize = 10;
    const postResult = await searchPosts('', postPage.value, postPageSize);
    console.log(postResult);
    if (postResult.data.length) {
      console.log('push more posts');
      recentPosts.value.push(...postResult.data);
      postPage.value += 1;
    }
  } catch (error) {
    console.error('Failed to fetch more posts', error);
  } finally {
    postLoading.value = false;
  }
}

// 加载更多回复
async function loadMoreComments() {
  console.log('loadMoreComments');
  if (commentLoading.value) return;
  commentLoading.value = true;


  try {
    const commentPageSize = 10;
    const commentResult = await searchComments('', commentPage.value, commentPageSize);
    if (commentResult.data.length) {
      recentComments.value.push(...commentResult.data);
      commentPage.value += 1;
    }
  } catch (error) {
    console.error('Failed to fetch more comments', error);
  } finally {
    commentLoading.value = false;
  }
}

function gotoPost(postId: number) {
  router.push({ name: 'PostDetail', params: { postId } });
}

onMounted(() => {
  loadMorePosts();
  loadMoreComments();
});
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}



.el-carousel img {
  width: 100%;
  height: auto;
}

.content-container {
  display: flex;
  gap: 20px;
}

.recent-container {
  flex: 1;
  justify-content: space-between;
  flex-direction: column;
}

.recent-posts,
.recent-comments {
  width: 480px;
  height: 400px;
  overflow-y: auto;
}

.post-card,
.comment-card {
  margin-bottom: 5px;
  align-items: center;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;

}

.title,
.content {
  flex-grow: 1;
  margin-right: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: rgb(0, 145, 255);
}

.title:hover,
.content:hover {
  color: rgb(0, 100, 200);
  cursor: pointer;
  text-decoration: underline;
}


.meta {
  color: #888;
  font-size: 14px;
  white-space: nowrap;
  flex-shrink: 0;
}

.footer {
  margin-top: 20px;
  text-align: center;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
}
</style>
