<template>
  <div>
      <div class="addItem">
        <img :src="addItem.senderAvatar" />
          <div class="center">
            <span>{{addItem.senderName}}</span>
            <p >{{addItem.additionMessage}}</p>
          </div>
          <button @click="agree" v-if="addItem.status === 0 && !flag">同意</button>
          <button  v-if="addItem.status === 1 || flag" style="background-color:#f5f5f5;color:#999">已同意</button>
      </div>
  </div>
</template>

<script>
export default {
  props:["addItem"],
  data() {
    return {
      flag: false,
    }
  },
  methods: {
    agree() {
      this.flag = true
      this.$emit('agreeValidate',this.addItem)
    }
  }
}
</script>

<style scoped>
.addItem {
  width: 520px;
  height: 80px;
  display: flex;
  border-bottom: 1px solid #e7e7e7;
  margin: 0 auto;
}
  .addItem img {
    margin-top: 16px;
    width: 52px;
    height: 52px;
  }
.center {
  margin-right: 208px;
  margin-top: 22px;
  margin-left: 12px;
  font-size: 12px;
}
.center span {
  width: 200px;
  display: block;
}
.center p {
    color: #9999a9;
}
button {
  /* width: 44px; */
  height: 24px;
  background-color: #1aad19;
  border: 0;
  font-size: 10px;
  color: #fff;
  margin-top: 28px;
}


</style>