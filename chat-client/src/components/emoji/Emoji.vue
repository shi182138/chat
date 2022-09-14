<template>
  <div class="container">
    <div class="emoji-panel-container">
      <div class="emoji-title">最近使用</div>
      <div class="emoji-history-container">
        <img class="emoji-item" v-for="i in 5" :src="'https://cdn.sunofbeaches.com/emoji/'+ i +'.png'" :key="i" @click="appendEmoji(i)">
      </div>
      <div class="emoji-title">全部表情</div>
      <div class="emoji-all-container">
        <img class="emoji-item" v-for="i in 130" :src="'https://cdn.sunofbeaches.com/emoji/'+ i +'.png'" :key="i" @click="appendEmoji(i)">
      </div>
    </div>
    <!-- 菜单 -->
    <div class="menu">
      <span></span>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      history:[],
    }
  },
  methods:{
    //输入表情
    appendEmoji(i) {
      //
      let targetUrl = "https://cdn.sunofbeaches.com/emoji/" + i + ".png";
      //告诉外面点击了谁
      this.$emit('emojiClick',targetUrl);
      //保存历史
      this.saveHistory(i);
      //回显数据
      this.restoreHistory();
    },
    saveHistory(emojiIndex) {
      //保存历史
      let record = window.localStorage.getItem('emojiHistory');
      let tarSaveStr = emojiIndex;
      let count = 1;
      if(record) {
        let splitArray = record.split(',');
        for (let i = 0; i < splitArray.length; i++) {
          let item = splitArray[i];
          if(emojiIndex.toString() !== item) {
            tarSaveStr += ',';
            tarSaveStr += item;
            count++;
            if (count > 5) {
              break;
            }
          }
        }
        window.localStorage.setItem('emojiHistory',tarSaveStr);
      }
    },
    removeHistory(){
      window.localStorage.removeItem('emojiHistory');
      this.history = [];
    },
    restoreHistory(){
      let record = window.localStorage.getItem('emojiHistory');
      if(record){
        this.history = record.split(',');
      }else{
        this.history = [];
      }
    },
  },
  mounted(){
    this.restoreHistory();
  }
}
</script>

<style scoped>
  .container {
    box-shadow: 4px #dfdfdf;
    border-radius: 5px;
    background-color: white;
    padding: 5px;
  }
  .emoji-panel-container {
    width: 413px;
    height: 312px;
    margin: 0 auto;
    background-color: #fff;
    overflow-y: scroll;
    overflow-x: hidden;
  }
  /* 滚动条效果 */
  .emoji-panel-container::-webkit-scrollbar{
    width: 10px;
  }
  .emoji-panel-container::-webkit-scrollbar-track {
    background-color: #f9fafb;
    -webkit-border-radius:2em;
    -moz-border-radius:2em;
    border-radius: 2em;
  }
  .emoji-panel-container::-webkit-scrollbar-thumb {
    background-color: #E5E6EB;
    -webkit-border-radius:2em;
    -moz-border-radius:2em;
    border-radius: 2em;
  }
  /* 滚动条效果结束------------------------------------ */
  .emoji-title {
    padding: 10px;
  }
  .emoji-item {
    width: 26px;
    height: 26px;
    padding: 5px;
    margin: 6px;
    cursor: pointer;
    transition: all .3s linear;
    border-radius: 5px;
  }
  .emoji-item:hover {
    transform: scale(1.3);
    transition: transform .3s;
    background-color: #f2f2f2;
  }
  .menu {
    height: 54px;
    background-color: white;
    border-top: 1px solid #f2f2f2;
    position: relative;
  }
  .menu span {
    position: absolute;
    bottom: -35px;
    left: 173px;
    width: 0px;
    height: 0px;
    border: 16px solid transparent;
    border-top-color: #fff;
  }
</style>