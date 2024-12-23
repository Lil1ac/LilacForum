<template>
  <div class="post-detail-container">
    <!-- 帖子内容和发帖人信息 -->
    <div class="post-header-container">
      <!-- 左侧发帖人信息 -->
      <el-card class="user-info-card">
        <img :src="post.avatar" alt="用户头像" class="post-avatar" @click="goToUserProfile(post.authorId)" />
        <p class="username" @click="goToUserProfile(post.authorId)">{{ post.author }}</p>
      </el-card>

      <!-- 右侧帖子内容 -->
      <el-card class="post-content-card">
        <div class="post-header">
          <h1 class="post-title">{{ post.title }}</h1>

          <!-- 贴子管理菜单，只有作者或管理员可见 -->
          <el-dropdown v-if="canManagePost" trigger="click" @command="handlePostCommand" class="post-options">
            <el-icon>
              <MoreFilled class="el-icon-more" />
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="editPost" :icon="Edit">编辑贴子</el-dropdown-item>
                <el-dropdown-item command="deletePost" :icon="Delete">删除贴子</el-dropdown-item>
              </el-dropdown-menu>
            </template>

          </el-dropdown>
        </div>
        <p class="post-content" v-html="post.content"></p>
        <div class="post-footer">
          <p class="post-section">板块：{{ post.sectionTitle }}</p>
          <p class="post-date">发布于: {{ post.createdTime }}</p>
        </div>
      </el-card>
    </div>

    <el-divider></el-divider>
    <!-- 发帖表单的弹出框 -->
    <el-dialog title="更新帖子" v-model="showPostForm">
      <PostForm :initialData="post" :submitting="submitting" @submit="handleSubmitPost" :key="post.id"
        @cancel="showPostForm = false" />
    </el-dialog>



    <!-- 评论部分 -->
    <div>
      <div class="comment-header">
        <h2>评论</h2>
        <div v-if="comments.length" class="comment-header-actions">
          <!-- 批量删除，只有作者或管理员可见 并且需要有至少一条评论-->
          <div v-if="canManagePost" class="batch-delete-container">
            <el-button type="danger" :icon="Delete" @click="isBatchDeleting ? confirmBatchDelete() : startBatchDelete()"
              class="batch-delete-button">
              {{ isBatchDeleting ? '确认删除' : '批量删除' }}
            </el-button>
            <el-button v-if="isBatchDeleting" @click="cancelBatchDelete()">取消</el-button>
          </div>
          <!-- 排序方式 -->
          <el-radio-group v-model="sortOrder" @change="toggleSortOrder" class="sort-order-radio-group">
            <el-radio-button value="asc">正序</el-radio-button>
            <el-radio-button value="desc">倒序</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 没有数据时的提示 -->
      <el-empty v-if="!comments.length" description="该帖子暂无评论" />

      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <el-checkbox-group v-if="isBatchDeleting" v-model="selectedComments" class="checkbox-group">
          <el-checkbox :key="comment.id" :value="comment.id" class="checkbox">
          </el-checkbox>
        </el-checkbox-group>
        <div class="comment-header-container">
          <!-- 左侧评论者信息 -->
          <el-card class="comment-user-info-card">
            <img :src="comment.avatar" alt="评论者头像" class="comment-avatar" @click="goToUserProfile(comment.authorId)" />
            <p class="username" @click="goToUserProfile(comment.authorId)">{{ comment.author }}</p>
          </el-card>

          <!-- 右侧评论内容 -->
          <el-card class="comment-content-card">
            <div class="comment-content-card-header">
              <p class="comment-content">{{ comment.content }}</p>

              <!-- 管理菜单，只有管理员、评论作者或帖子作者可见 -->
              <el-dropdown v-if="canManageComment(comment)" trigger="click"
                @command="(command: string) => handleCommentCommand(command, comment.id)">
                <el-icon class="el-icon-more">
                  <MoreFilled />
                </el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="deleteComment" :icon="Delete">删除评论</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <p class="comment-date">{{ comment.createdTime }}</p>
          </el-card>

        </div>
      </div>

      <!-- 分页控件 -->
      <Pagination :currentPage="currentPage" :pageSize="pageSize" :total="totalComments" @change="fetchCommentList"
        @update:currentPage="updatePage" @update:page-size="updatePageSize" />

      <!-- 添加评论 -->
      <el-form @submit.prevent="submitComment" class="comment-form">
        <el-form-item>
          <h3 class="add-comment-title">添加评论</h3>
          <el-input type="textarea" v-model="newComment" placeholder="添加评论..." :rows="5" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitComment" :loading="submitting">提交</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>

</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getPostDetail, deletePostById } from '@/api/post';
import { getCommentsByPostId, createNewComment } from '@/api/comment';
import { useUserStore } from '@/stores/userStore';
import type { Post } from '@/interface/Post';
import type { Comment } from '@/interface/Comment';
import PageHeader from '@/components/PageHeader.vue';
import Pagination from '@/components/Pagination.vue';
import { ElMessage } from 'element-plus';
import { Edit, Delete, MoreFilled } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const post = ref<Post>({
  id: 0,
  title: '',
  content: '',
  createdTime: '',
  lastReplyTime: '',
  sectionId: 0,
  sectionTitle: '',
  authorId: 0,
  author: '',
  avatar: ''
});

const comments = ref<Comment[]>([]);
const currentPage = ref<number>(1);
const pageSize = ref<number>(10);
const totalComments = ref<number>(0);

const postId = parseInt(route.params.postId as string, 10);
const fetchPostDetail = async () => {
  try {
    post.value = await getPostDetail(postId); // 获取帖子详细信息
  } catch (error) {
    throw error;
  }
};


const goToUserProfile = (authorId: number) => {
  router.push({ name: 'UserProfile', params: { userId: authorId } });
};


// 贴子菜单
import { ElMessageBox } from 'element-plus';
import { updatePost } from '@/api/post';
import PostForm from '@/components/PostForm.vue';
const showPostForm = ref<boolean>(false);

// 判断当前用户是否是帖子作者或管理员
const canManagePost = computed(() => {
  const currentUser = userStore;
  return currentUser.userId === post.value.authorId || currentUser.role.includes('ADMIN');
});
//管理菜单弹出事件
const handlePostCommand = (command: string) => {
  if (command === 'editPost') {
    editPost(); //确认编辑
  } else if (command === 'deletePost') {
    confirmDeletePost(); // 确认删除
  }
};


const editPost = async () => {
  try {
    await fetchPostDetail(); // 先确保获取到帖子的详细信息

    showPostForm.value = true; // 然后再打开弹窗
  } catch (error) {
    throw error;
  }
};

const confirmDeletePost = () => {
  ElMessageBox.confirm('确定要删除这个帖子吗?', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deletePostById(postId); // 删除贴子
    router.back(); // 回退到上一页
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

//更新
const handleSubmitPost = async (updatedPost: Post) => {
  submitting.value = true; // 设置按钮为加载状态，防止重复提交
  console.log('updatedPost', updatedPost);
  try {
    console.log('postId', postId);
    await updatePost(updatedPost);
    showPostForm.value = false;
    fetchPostDetail(); // 重新获取帖子列表，更新页面
  } catch (error) {
    throw error;
  } finally {
    submitting.value = false; // 提交完成后解除按钮加载状态
  }
};



const fetchCommentList = async () => {
  try {
    const commentList = await getCommentsByPostId(postId, currentPage.value, pageSize.value, sortOrder.value); // 获取评论列表
    comments.value = commentList.data;
    totalComments.value = commentList.total;
  } catch (error) {
    throw error;
  }
};

const updatePage = (page: number) => {
  currentPage.value = page;
};

const updatePageSize = (size: number) => {
  pageSize.value = size;
};

const newComment = ref<string>('');
const submitting = ref<boolean>(false);
const submitComment = async () => {
  if (!newComment.value.trim()) return;

  submitting.value = true;
  try {
    await createNewComment(postId, newComment.value);
    newComment.value = ''; // 清空输入框
    await fetchCommentList(); // 重新获取评论列表，刷新页面
  } catch (error) {
    throw error;
  } finally {
    submitting.value = false; // 提交完成后解除按钮加载状态
  }
};



//评论菜单
import { deleteCommentById } from '@/api/comment';
// 判断当前用户是否能管理评论
const canManageComment = (comment: Comment) => {
  const currentUser = userStore;
  return (
    currentUser.userId === comment.authorId || // 评论的作者
    currentUser.userId === post.value.authorId || // 帖子的作者
    currentUser.role.includes('ADMIN') // 管理员
  );
};

// 处理管理菜单命令
const handleCommentCommand = (command: string, commentId: number) => {
  if (command === 'deleteComment') {
    confirmDeleteComment(commentId);
  }
};


// 确认删除评论
const confirmDeleteComment = (commentId: number) => {
  ElMessageBox.confirm('确定要删除这条评论吗?', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteCommentById(commentId); // 调用 API 删除评论
    await fetchCommentList(); // 删除后刷新评论列表
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};





//批量删除
const isBatchDeleting = ref<boolean>(false);
const selectedComments = ref<number[]>([]);

const startBatchDelete = () => {
  isBatchDeleting.value = true;
  if (!isBatchDeleting.value) {
    selectedComments.value = [];
  }
}

const confirmBatchDelete = () => {
  if (selectedComments.value.length === 0) return;

  ElMessageBox.confirm('确定要删除选中的评论吗?', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      for (const commentId of selectedComments.value) {
        await deleteCommentById(commentId);
      }
      selectedComments.value = [];
      await fetchCommentList();
    } catch (error) {
      throw error;
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  }).finally(() => {
    isBatchDeleting.value = false;
  });
};

const cancelBatchDelete = () => {
  isBatchDeleting.value = false;
};



//切换时间顺序
const sortOrder = ref<'asc' | 'desc'>('asc');

const toggleSortOrder = async () => {
  await fetchCommentList();
};



onMounted(() => {
  fetchPostDetail();
  fetchCommentList();
});
</script>




<style scoped>
.post-detail-container {
  margin: 20px auto;
  padding: 20px;
  background: #ffffff;
  max-width: 1000px;
}

.post-header-container {
  display: flex;
  margin-bottom: 20px;
  padding: 20px;
}

.user-info-card {
  width: 160px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 15px;
  margin-right: 20px;
}

.post-content-card {
  flex: 1;
  background: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  position: relative;
}

.post-avatar {
  width: 150px;
  height: 150px;
  border: 2px solid #ddd;
  margin-bottom: 10px;
  cursor: pointer;
}

.comment-avatar {
  width: 100px;
  height: 100px;
  border: 2px solid #ddd;
  cursor: pointer;
}

.username {
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  color: #333;
  cursor: pointer;
}

.username:hover {
  color: #007bff;
  text-decoration: underline;
}


.post-content-card h1 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #222;
}

.post-content-card p {
  line-height: 1.6;
  color: #555;
  margin-bottom: 10px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  overflow: hidden;
}

.post-title {
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
  line-height: 1.5;
  margin: 0;
  padding: 0;
  width: 95%;
}




.post-content {
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
}
.post-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  position: absolute;
  bottom: 10px;
  right: 20px;
}

.post-section {
  font-size: 14px;
  color: #007bff;
  margin-right: 20px;
}

.post-date {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

/* 贴子管理菜单 */
.post-options {
  cursor: pointer;
}

.post-options i {
  font-size: 24px;
  color: #666;
}

.comment-item {
  margin-bottom: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
}

.comment-header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sort-order-radio-group {
  margin-left: 30px;
}


.comment-header-container {
  display: flex;
  margin-bottom: 10px;
}

.comment-user-info-card {
  margin-right: 15px;
}

.comment-user-info-card {
  width: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 10px;
}

.comment-content-card {
  flex: 1;
  background: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 5px;
  position: relative;
  overflow: hidden;
}

.comment-content-card-header {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}


.comment-content {
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: pre-wrap;
  line-height: 1.5;
  margin: 0;
  padding: 0;
  width: 95%;
}


.el-icon-more {
  font-size: 18px;
  color: #666;
  cursor: pointer;
}


.comment-date {
  font-size: 12px;
  color: #999;
  position: absolute;
  bottom: 10px;
  right: 10px;
}


.comment-form {
  margin-top: 20px;
  padding: 15px;
  background: #f9f9f9;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.add-comment-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}
</style>
