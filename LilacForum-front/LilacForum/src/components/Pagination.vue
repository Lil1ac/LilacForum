<template>
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      layout="prev, pager, jumper, total, next, sizes"
      :page-sizes="[5, 10, 20, 30]"
    />
  </template>
  
  <script setup lang="ts">
  import { defineProps, defineEmits } from 'vue';
  
  const props = defineProps({
    currentPage: Number,
    pageSize: Number,
    total: Number,
  });
  
  const emit = defineEmits(['update:currentPage', 'update:pageSize']);
  
  const handlePageChange = (page: number) => {
    emit('update:currentPage', page);
  };
  
  const handleSizeChange = (size: number) => {
    emit('update:pageSize', size);
  };
  </script>
  