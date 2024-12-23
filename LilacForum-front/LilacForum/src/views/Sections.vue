<template>
  <div class="sections-container">
    <!-- 添加板块按钮 -->
    <el-button v-if="canManageSection" type="success" @click="openAddSectionDialog" :icon="Plus" class="add-section-button">
      添加板块
    </el-button>

    <!-- 板块列表 -->
    <el-row :gutter="60">
      <el-col :span="12" v-for="section in sections" :key="section.id">
        <el-card class="section-card" @click="goToSection(section.id)">
          <div class="card-header">
            <img :src="section.background" class="card-background" />
            <div class="card-title">{{ section.title }}</div>
          </div>
          <div class="buttons" v-if="canManageSection">
            <!-- 编辑按钮 -->
            <el-button @click.stop="openEditSectionDialog(section)" type="primary" :icon="Edit" class="edit-button">
              编辑
            </el-button>
            <!-- 删除按钮 -->
            <el-button @click.stop="confirmDeleteSection(section.id)" type="danger" :icon="Delete"
              class="delete-button">
              删除
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加板块对话框 -->
    <el-dialog :title="isEditing ? '更新板块' : '添加板块'" v-model="showSectionDialog" @close="handCancelSection">
      <SectionForm ref="sectionForm" :initialData="currentSection" :submitting="submitting" :key="currentSection.id"
        @submit="handleSubmitSection" @cancel="handCancelSection" class="section-dialog" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { getAllSections, addSection, deleteSection, updateSection, getSectionDetail } from '@/api/section';
import type { Section } from '@/interface/Section';
import { ElMessage, ElMessageBox } from 'element-plus';
import SectionForm from '@/components/SectionForm.vue';
import { Edit, Delete, Plus } from '@element-plus/icons-vue';




const userStore = useUserStore();
const canManageSection = computed(() => userStore.role.includes('ADMIN'));

const router = useRouter();
const sections = ref<Section[]>([]);

const currentSection = ref<Section>({
  id: 0,
  title: '',
  description: '',
  background: '',
});

const fetchSections = async () => {
  try {
    const result = await getAllSections();
    sections.value = result || [];
  } catch (error) {
    throw error;
  }
};
const showSectionDialog = ref(false);
const isEditing = ref(false);
const submitting = ref(false);
const sectionForm = ref<InstanceType<typeof SectionForm> | null>(null);

// 打开添加板块对话框
const openAddSectionDialog = () => {
  showSectionDialog.value = true;
  isEditing.value = false;
};

// 打开编辑板块对话框
const openEditSectionDialog = async (section: Section) => {
  showSectionDialog.value = true;
  isEditing.value = true;
  currentSection.value = { ...section }; // 先设置当前板块
};



// 处理提交
const handleSubmitSection = async (sectionData: Section) => {
  console.log('section',sectionData);
  submitting.value = true;
  try {
    if (isEditing.value) {
      await updateSection(sectionData);
    } else {
      await addSection(sectionData);
    }
    showSectionDialog.value = false;
    fetchSections();
  } catch (error) {
    throw error;
  } finally {
    sectionForm.value?.resetSectionForm();
    submitting.value = false;
  }
};

// 处理取消
const handCancelSection = () => {
  sectionForm.value?.resetSectionForm();
  showSectionDialog.value = false;
};

//确认删除
const confirmDeleteSection = async (sectionId: number) => {
  ElMessageBox.confirm('确定要删除这个板块吗?', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteSection(sectionId);
    fetchSections();
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};


const goToSection = (sectionId: number) => {
  router.push({ name: 'SectionDetail', params: { sectionId } });
};


onMounted(() => {
  fetchSections();
});
</script>

<style scoped>
.sections-container {
  margin: auto;
  padding: 40px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-width: 1000px;
}

.section-card {
  cursor: pointer;
  border-radius: 8px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease;
  position: relative;
  margin-bottom: 30px;
}

.section-card:hover {
  transform: scale(1.05);
}

.card-header {
  position: relative;
  height: 220px;
}

.card-background {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
  filter: brightness(50%);
  border-radius: 8px;
}

.card-title {
  position: absolute;
  bottom: 10px;
  left: 10px;
  color: white;
  font-size: 20px;
  font-weight: bold;
  background: rgba(0, 0, 0, 0.5);
  padding: 5px;
  border-radius: 5px;
}

.add-section-button {
  margin-bottom: 20px;
}

.buttons {
  position: absolute;
  bottom: 10px;
  right: 10px;
}

.section-dialog {
  max-height: 600px;
  overflow: auto;
}
</style>
