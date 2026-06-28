<template>
  <div>
    <h1>隐私政策</h1>
    <p>渐构（JianGou）尊重并保护访客的隐私。</p>
    <h2>收集的信息</h2>
    <ul>
      <li>评论与订阅时您主动提供的邮箱等信息</li>
      <li>为防滥用而记录的必要访问日志（如 IP、User-Agent）</li>
    </ul>
    <h2>信息用途</h2>
    <p>仅用于提供评论、邮件订阅、站点统计与安全防护，不会出售给第三方。</p>
    <h2>Cookie</h2>
    <p>登录用户使用 HttpOnly Cookie 维持会话（含前台评论/点赞与后台 CMS）；主题偏好存储在浏览器 localStorage。</p>
    <h2>联系我们</h2>
    <p>如有疑问，请通过<a href="/guestbook">留言板</a>联系。</p>
  </div>
</template>

<script setup lang="ts">
usePageSeo({
  title: '隐私政策',
  description: '渐构隐私政策说明',
  path: '/privacy'
})
</script>

<style scoped>
ul { padding-left: 1.25rem; }
</style>
