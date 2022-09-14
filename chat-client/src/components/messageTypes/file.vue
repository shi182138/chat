<template>
  <div class="container">
    <div class="fileMessageBox">
    <img src="../../assets/img/file.png" alt="" class="fileImage">
    <span class="fileWord">
      <div :title="message.fileRawName">{{message.fileRawName.lastIndexOf('.') > 12 ? message.fileRawName.slice(0, 12) +
        '...' + message.fileRawName.slice(message.fileRawName.lastIndexOf('.')) : message.fileRawName}}</div>
    </span>
    </div>
  </div>
</template>

<script>
  import './../../../public/static/iconfont/iconfont.css'

  export default {
    props: ['message']
  }
</script>
<style scoped>
.container {
  width: 220px;
  height: 96px;
  background-color: #fff;
  border-radius: 4px;
  margin-top: 8px;
  margin-right: 8px;
  margin-left: 8px;
}
.fileMessageBox {
 display: flex;
 height: 72px;
 border-bottom: 1px solid #f7f7f7;
}
.fileMessageBox img {
  widows: 32px;
  height: 40px;
  margin-left: 24px;
  margin-top: 16px;
}
.fileMessageBox span {
  font-size: 16px;
  margin-top: 12px;
  margin-left: 78px;
}
</style>
