<template>
  <div>
      <div class="listBox">
        <!-- || unreadNews[conversationInfo.roomId] !== 0 -->
        <div class="list" @contextmenu.prevent.stop="showMenu"
        :class="{actived:chosen==index}">
          <UnreadNewsCount 
          :count="unreadNews[conversationInfo.roomId]"
          v-show="typeof(unreadNews[conversationInfo.roomId]) != 'undefined' && unreadNews[conversationInfo.roomId] != 0" />
          <img :src="wohsImg" class="avatar"/>
          <div class="content">
            <span style="color:black">
              {{whoIsHe}}
              <label style="font-size:9px;" class="time">{{lastTime}}</label>
            </span>
            <p ref="fuckemoji">{{lastNews}}</p>
          </div>
        </div>
      </div>

      <!-- 菜单 -->
      <div class="recentMenu-container" v-show="isShowMenu" :style="{'left': menuLeft + 'px', 'top':  menuTop + 'px'}">
        <recent-menu @hiddenMenu="hiddenMenu"
                     @remove="remove" />
      </div>
  </div>
</template>

<script>
import UnreadNewsCount from '@/components/unreadNewsCount'
import {MSG_TYPES} from '@/const/index'
import {fromatTime, arrUnique} from "@/utils"
import RecentMenu from '@/components/recentList/RecentMenu.vue'
const conversationObj = {
  createDate: '',
  nickname: '',
  avatar: '',
  signature: '',
  id:'',
  roomId: ''
};

export default {
  props: {
    conversationInfo: {
      type: Object,
      default() {
        return conversationObj
      }
    },
    chosen:Number,
    index:Number
  //   currentConversation: {
  //     type: Object,
  //     default() {
  //       return conversationObj
  //     }
  //   },
  //   type: { // 定义该组件在哪里被使用，在最近会话中或者分组会话等
  //       type: String
  //   },
  //   recentConversation: Array
  },
  components: {
    UnreadNewsCount,
    RecentMenu
  },
  data() {
    return {
      isShowMenu: false,
      menuTop: 0,
      menuLeft: 0,
    }
  },
  computed: {
    whoIsHe() {
      if(this.conversationInfo.conversationType == 'FRIEND') {
        return this.conversationInfo.beiZhu == '' ? this.conversationInfo.nickname : this.conversationInfo.beiZhu
      } else {
        if (this.conversationInfo.groupInfo.title.length > 12) return this.conversationInfo.groupInfo.title.substr(0, 11) + '...'
        return this.conversationInfo.groupInfo.title
      }
    },
    wohsImg() {
      if(this.conversationInfo.conversationType == 'FRIEND') {
        return this.conversationInfo.avatar
      } else {
        return this.conversationInfo.groupInfo.img
      }
    },
    globalCurrentConversation() {
      return this.$store.state.app.currentConversation
    },
    unreadNews() {
      return this.$store.state.news.unreadNews
    },
    lastNews() {
      const lastNewsObj = this.conversationInfo.lastNews || {}
      if(lastNewsObj.messageType == 'text') {
        // if (lastNewsObj.message.length > 10) {
        //   this.lastNewsObj.message.substr(0, 11) + '...'
        // }
        this.$nextTick(() => {
          this.$refs.fuckemoji.innerHTML = lastNewsObj.message
          return ''
        })
      }
      const MSG_TYPE_TEXT = {
        [MSG_TYPES.text]: lastNewsObj.message || '',
        [MSG_TYPES.img]: '[图片]',
        [MSG_TYPES.file]: '[文件]',
        [MSG_TYPES.sys]: '[系统消息]',
        [MSG_TYPES.video]: '[视频通话]',
        [MSG_TYPES.audio]: '[语音通话]',
      }
      return MSG_TYPE_TEXT[lastNewsObj.messageType] || ''
    },
    lastTime() {
      return fromatTime(this.conversationInfo.lastNewsTime,false)
      // return null
    },
    recentConversationList() {
      return this.$store.state.app.recentConversation
    },
  },
  methods: {
    showMenu(e) {
      this.isShowMenu = true
      this.menuTop = e.pageY
      this.menuLeft = e.pageX
    },
    hiddenMenu() {
      this.isShowMenu = false
    },
    remove() {
      this.isShowMenu = false
      if (this.conversationInfo.conversationType == 'FRIEND') {
        const recentFriendIdsStr = window.localStorage.getItem('coMessager-recentConversation-friend') || ''
        const recentFriendIds = arrUnique(recentFriendIdsStr.split(',')).filter(item => item) // 去重
        const index = recentFriendIds.findIndex(item => item === this.conversationInfo.id)
        index !== -1 && recentFriendIds.splice(index, 1)
        window.localStorage.setItem('coMessager-recentConversation-friend', recentFriendIds.join())
      } else {
        const recentGroupIdsStr = window.localStorage.getItem('coMessager-recentConversation-group') || ''
        const recentGroupIds = arrUnique(recentGroupIdsStr.split(',')).filter(item => item) // 去重
        const index = recentGroupIds.findIndex(item => item === this.conversationInfo.roomId)
        index !== -1 && recentGroupIds.splice(index, 1)
        // console.log('测试一下');
        // console.log(recentGroupIds);
        // console.log(recentGroupIds.join());
        window.localStorage.setItem('coMessager-recentConversation-group', recentGroupIds.join())
      }
      
      
      this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'delete', data: this.conversationInfo})
      if (this.conversationInfo.roomId = this.globalCurrentConversation.roomId)
      this.$store.commit('user/initialInterface')
      // if (this.conversationInfo.id === this.globalCurrentConversation.id) {
      //   const conversationList = this.recentConversationList.filter(item => Object.keys(item).length > 0) //相当于取出最近会话列表中第一个，被删除的好友会话已经关闭
      //   this.$emit('setCurrentConversation', conversationList[0] || {})
      // }
    }
  },
  sockets: {
    receiveQuitGroup(oldVal) { //接收拒绝群聊
    console.log('输出old');
    console.log(oldVal);
    const recentGroupIdsStr = window.localStorage.getItem('coMessager-recentConversation-group') || ''
        const recentGroupIds = arrUnique(recentGroupIdsStr.split(',')).filter(item => item) // 去重
        const index = recentGroupIds.findIndex(item => item === oldVal.roomId)
        index !== -1 && recentGroupIds.splice(index, 1)
        window.localStorage.setItem('coMessager-recentConversation-group', recentGroupIds.join())
        this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'delete', data: oldVal})
      if (oldVal.roomId = this.globalCurrentConversation.roomId)
      this.$store.commit('user/initialInterface')
        // console.log("接收到退群时的会话消息为：", oldVal)
        //重新获取我的群聊
        this.$bus.$emit("getMyGroups")
        //若是群主解散群聊，则还需在本地更新一下我的最近群会话列表
        // console.log("是否为群主？", oldVal.holder)
    } 
  },
  created() {
      document.addEventListener('click', () => {
        this.isShowMenu = false
      })
      document.addEventListener('mousedown', (e) => {
        //   console.log(e);
        const {button} = e
        if (button === 2) {
          this.isShowMenu = false
        }
      })
    }

}
</script>

<style scoped>
  .list {
    height: 64px;
    position: relative;
    /* display: block; */
    display: flex;
    /* background-color: red; */
    font-family: STHeiti;
  }
  .list:hover{
    background-color: #dddad9;
    /* color: rgba(114, 89, 89, 0.815); */
  }
  .list:hover label{
    color: #999999;
  }
  .list:hover p{
    color: #999999;
  }
  .avatar {
    width: 40px;
    height: 40px;
    margin-top: 12px;
    margin-right: 11px;
    margin-left: 12px;

  }
  .content {
    display: flex;
    flex-direction: column;
  }
  .content span {
    /* position: relative; */
    margin-top: 13px ;
    padding: 0px;
    font-size: 14px;
  }
  .content label {
    color:#b9c8d7
  }
  .content p{
    margin-top: 6px;
    font-size: 11px;
    color: #99b3cc;
  }
  .time {
    position: absolute;
    right: 12px;
    top:10px
  }
  /* 被选中 */
  .actived:hover {
    background-color: #c4c4c5;
  }
  .actived p {
    color: #999;
  }
  .actived label {
    color: #999;
  }

  /* 菜单 */
  .recentMenu-container {
    position: fixed;
    z-index: 1000;
  }
</style>