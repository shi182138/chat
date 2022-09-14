<template>
  <div class="menu-box">
      <span class="menu-item ">设置</span>
      <span class="menu-item" @click.stop="delConversatiion">删除聊天</span>  
  </div>
</template>

<script>
export default {
    methods: {
        delConversatiion() {
            // this.$emit('hiddenMenu')
            this.$emit('remove')
        }
    }
}
</script>

<style scoped>
.menu-box {
    background-color: #fff;
    width: 124px;
}
.menu-item {
  display: block;
  text-align: center;
  font-size: 12px;
  padding-top: 8px;
  padding-bottom: 8px;
}
.menu-item:hover {
  background-color: #e2e2e2;
}
/* .second {
  border-bottom: 1px solid #ededed;
} */
</style>