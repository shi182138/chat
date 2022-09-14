<template>
  <div class="menu-box">
      <span class="menu-item ">设置备注</span>
      <span class="menu-item second">切换分组</span>
      <span class="menu-item" @click.stop="isShowDelPop">删除好友</span>  
  </div>
</template>

<script>
export default {
  data() {
    return {
    }
  },
  methods:{
    isShowDelPop() {
      this.$emit('showPop')
      this.$emit('hiddenMenu')
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
.second {
  border-bottom: 1px solid #ededed;
}
</style>