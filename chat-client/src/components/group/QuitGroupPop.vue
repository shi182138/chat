<template>
  <div class="quitGroupPop" >
        <div class="quitGroupPop-top"><span @click="cancelQuit">X</span></div>
        <div class="quitGroupPop-center"><span>删除并退出群？</span></div>
        <button class="confirm" @click="confirmQuit">确认</button><button class="cancel" @click="cancelQuit">取消</button>
    </div>
</template>

<script>
export default {
    methods: {
        confirmQuit() {
            this.$emit('quitGroup')
        },
        cancelQuit() {
            this.$emit('closeQuitPop')
        }
    }
}
</script>

<style scoped>
/* 确认删除面板 */
.quitGroupPop {
  width: 360px;
  height: 224px;
  background-color: #f5f5f5;
  position: absolute;
  top: 200px;
  left: -500px;
  z-index: 1000;
  border: 1px solid #e7e7e7;
}
.quitGroupPop-top {
  display: flex;
  justify-content: flex-end;
  color: #99a9c8;
  font-size: 13px;
}
/* x */
.quitGroupPop-top span {
    width: 32px;
    height: 24px;
    text-align: center;
    line-height: 24px;
}
.quitGroupPop-top span:hover {
    background-color: #fb7373;
}
.quitGroupPop-center {
  margin-top: 64px;
  margin-bottom: 70px;
  text-align: center;
}
.confirm {
  width: 72px;
  height: 24px;
  background-color: #1aad29;
  color: #fff;
  font-size: 12px;
  border: 0;
  margin-left: 190px;
  margin-right: 8px;
}
.cancel {
  width: 72px;
  height: 24px;
  background-color: #fff;
  font-size: 12px;
  border: 1px solid #e7e7e7;
}
</style>