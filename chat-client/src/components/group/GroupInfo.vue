<template>
  <div class="groupInfo-box">
      <div class="top-title">
        <p>{{currentConversation.groupInfo.title}}</p>
      </div>
      <div class="center-info-box">
        <div class="center-info">
          <div class="item" v-for="userItem in userList" :key="userItem.userInfo.nickname">
            <img :src="userItem.userInfo.avatar" alt="">
            <p>{{userItem.userInfo.nickname}}</p>
          </div>
        </div>
      </div>
      <button class="send" @click="sendMs">发消息</button>
  </div>
</template>

<script>
import {SET_UNREAD_NEWS_TYPE_MAP} from '@/store/constants'
import {saveRecentConversationTolocal} from '@/utils';
export default {
  data() {
    return {
      // userList:[]
    }
  },
  computed: {
    currentConversation: {
      get() {
          return this.$store.state.app.currentConversation
      },
      set () {

      }
    },
    userList: {
      get() {
        return this.$store.state.user.userBus
      },
      set() {

      }
    }
  },
  methods: {
    
    sendMs() {
      
      this.$store.dispatch('news/SET_UNREAD_NEWS', {
        roomId: this.currentConversation && this.currentConversation.roomId,
        count: 0,
        type: SET_UNREAD_NEWS_TYPE_MAP.clear
      })
      this.$store.dispatch('news/SET_LAST_NEWS', {type: 'default', roomId: this.currentConversation.roomId})
      this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'add', data: this.currentConversation})
      console.log('currentConversation');
      console.log(this.currentConversation);
      saveRecentConversationTolocal(this.currentConversation)
      // this.$store.commit('user/ShowRecentList')
      this.$bus.$emit('fuckicon')
      this.$store.commit('user/ShowChatWindow')
    }
    
  },
  mounted() {
    console.log('userBus');
    console.log(this.userList);
  }
}
</script>

<style scoped>
.top-title {
  height: 42px;
  border-bottom: 1px solid #e7e7e7;
  width: 654px;
}
.top-title p {
  margin-left: 32px;
  /* margin-top: 28px; */
  font-size: 20px;
}
.center-info-box {
  padding-top: 20px;
  width: 652px;
  height: 420px;
  overflow-y: scroll;
}
.center-info {
  width: 552px;
  margin-left: 76px;
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
}
.item {
  width: 68px;
  height: 100px;
  
}
.item p {
  text-align: center;
  margin: 0;
  /* margin-top: 10px; */
  font-size: 12px;
  color: #999;
}
.item:hover {
  background-color: #e6e6e6;
}
.item img {
  width: 52px;
  height: 52px;
  margin-top: 8px;
  margin-left: 8px;
  margin-bottom: 12px;
}

/* 发消息按钮 */
.send {
    outline: none;
    border: 0;
    background-color: #1aad19;
    width: 140px;
    height: 36px;
    color: #fff;
    margin-top: 24px;
    margin-left: 255px;
}
.send:hover {
    background-color: #129611;
}

    /*修改滚动条样式*/
.center-info-box::-webkit-scrollbar{
  width:8px;
  /* height:5px; */
  display: none;
  /**/
}
.center-info-box:hover::-webkit-scrollbar {
      display: block;
      /* position: relative; */
}
.center-info-box::-webkit-scrollbar-track{
  background: rgb(239, 239, 239);
  border-radius:2px;
}
.center-info-box::-webkit-scrollbar-thumb{
  background: #bfbfbf;
  border-radius:10px;
}
.center-info-box::-webkit-scrollbar-thumb:hover{
  background: #333;
}
.center-info-box::-webkit-scrollbar-corner{
  background: #179a16;
}
</style>