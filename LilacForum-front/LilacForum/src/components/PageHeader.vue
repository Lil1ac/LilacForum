<template>
    <el-page-header @back="goBack">
        <template #breadcrumb>
            <el-breadcrumb separator="/">
                <el-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index" :to="item.path">
                    {{ item.label }}
                </el-breadcrumb-item>
            </el-breadcrumb>
        </template>
        <template #content>
            <span class="text-large font-600 mr-3">{{ title }}</span>
        </template>
    </el-page-header>
</template>
  
<script setup lang="ts">
import { useRouter } from 'vue-router';

interface BreadcrumbItem {
    label: string;
    path?: string;
}

const router = useRouter();

const props = defineProps<{
    title: string;
    breadcrumbItems: BreadcrumbItem[];
    goBackPath?: string;
}>();

const goBack = () => {
    if (props.goBackPath) {
        router.push(props.goBackPath);
    } else {
        router.back();
    }
};
</script>


<style scoped >
  .el-page-header {
    background: #f5f5f5;
    padding: 10px;
    border-radius: 5px;
    margin-bottom: 20px;
}
</style>
