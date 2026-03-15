<template>
  <section class="page-shell">
    <div class="page-card">
      <div class="el-card__body">
        <div class="empty-block">
          <h1 class="page-title" style="font-size: 28px;">无权限访问当前页面</h1>
          <p class="page-desc">当前账号没有对应菜单权限，请联系管理员分配角色后再试。</p>
          <RouterLink class="app-link" to="/dashboard">返回工作台</RouterLink>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
</script>
