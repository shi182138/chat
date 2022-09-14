<template>
  <div class="groupItem-box">
      <img :src="conversationInfo.img" alt="">
      <p>{{conversationInfo.title}}</p>
  </div>
</template>

<script>
export default {
    props: ['conversationInfo'],
    mounted() {
    }

}
</script>

<style scoped>
.groupItem-box {
    display: flex;
    width: 248px;
    height: 60px;
}
.groupItem-box:hover {
    background-color: #c5c4c4;
}
    .groupItem-box img {
        margin-top: 12px;
        margin-left: 12px;
        margin-right: 12px;
        width: 36px;
        height: 36px;
    }
    .groupItem-box p {
        font-size: 14px;
        margin-top: 22px;
    }
</style>