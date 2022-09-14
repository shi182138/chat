<template>
  <div>
      <recent-conversation @set-current-converation='setCurrentConversation'
          v-for="(item, index) in outcomeConversationList"
          :key="item.id" 
          :conversationInfo="item" 
          :index="index"
          :chosen="chosen"
          :class="{selected:chosen==index}"
          @click.native="changeCurrentConversation(item, index)"/>
  </div>
</template>

<script>
import RecentConversation from '@/components/recentList/RecentConversation.vue'
import {SET_UNREAD_NEWS_TYPE_MAP} from '@/store/constants'
import { arrUnique } from '@/utils'
export default {
  name: 'RecentList',
  data() {
    return {
      conversationList:[],
      lastNewsMap: {},
      chosen: -1
    }
  },
  components: {
    RecentConversation,
  },
  computed: {
    userInfo: {
      get() {
        return this.$store.state.user.userInfo
      },
      set() {
        
      }
    },
    friendBeiZhu() { // 好友备注Map {id2: '备注1', id1: '备注2'}
        return this.userInfo.friendBeiZhu || {}
    },
    recentConversation() {
      return this.$store.state.app.recentConversation
    }, 
    outcomeConversationList() {
      // console.log(this.conversationList);
      const converationList = JSON.parse(JSON.stringify(this.recentConversation))
      return converationList.length && converationList.map(item => {
        item.beiZhu = this.friendBeiZhu[item.id] ? this.friendBeiZhu[item.id] : ''
        item.lastNews = this.lastNewsMap[item.roomId] ? this.lastNewsMap[item.roomId] : ''
        item.lastNewsTime = this.lastNewsMap[item.roomId] ?
        (this.lastNewsMap[item.roomId].time ? new Date(this.lastNewsMap[item.roomId].time) : new Date(Date.now() - 2000)) :
        new Date(Date.now() - 2000) // -2000ms为了解决没有最近消息的会话的lastNews一直为当前时间（这样会和新发送的消息的会话冲突）
        return item
      }).sort((a,b) => {
        return b.lastNewsTime - a.lastNewsTime //最新到最旧
      })
    }
  },
  watch: {
    '$store.state.news.lastNews': {
        handler(newVal, oldVal) {
          this.lastNewsMap = newVal
        }
    }
  },
  destroyed() {
    console.log('我被销毁了')
  },
  methods:{
    changeCurrentConversation(item, index){
      this.chosen = index
      //展示聊天窗口
      this.setCurrentConversation(item)
      this.$store.commit('user/ShowChatWindow');
      this.$store.dispatch('news/SET_UNREAD_NEWS',{
            roomId: item.roomId,
            count: 0,
            type: SET_UNREAD_NEWS_TYPE_MAP.clear
            })
            this.$store.commit('user/closeDrawer')
    },
    setCurrentConversation(data) {
      this.$store.dispatch('app/SET_CURRENT_CONVERSATION',data)//切换会话时设置一下全局的当前会话
    },
    // --------------------------------------------------------------------------
    async getRecentConversation(){//获取最近单聊
      const recentFriendIdStr = window.localStorage.getItem("coMessager-recentConversation-friend") || ''
    console.log('recentStr')
    console.log(recentFriendIdStr);                               
      if (recentFriendIdStr === '') return
      const recentFriendIds = arrUnique(recentFriendIdStr.split(',')).filter(item => item)
      const resp = await this.postRequest('/chat/recentConversationList',{
        userId: this.userInfo.userId,
        recentFriendIds
      })
      const list = resp.data.singleRecentConversationList
      const myId = this.userInfo.userId
      const conversationList = (list || []).map(item => {
        let res = {}
        res.createDate = item.createDate
        res.roomId = item.userM.userId + '-' + item.userY.userId
        if (item.userM.userId === myId && item.userY.userId !== myId) {
          res = {
            ...res, ...item.userY,
            conversationType: 'FRIEND',
            myNickname:this.userInfo.nickname,
            myId:this.userInfo.userId,
            myAvatar:this.userInfo.avatar
          }
        } else {
          res = {
            ...res, ...item.userM,
            conversationType: 'FRIEND',
            myNickname:this.userInfo.nickname,
            myId:this.userInfo.userId,
            myAvatar:this.userInfo.avatar
          }
        }
        let id = res.userId
        delete res.userId
        res['id'] = id
        return res
      })
      this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'init', data: conversationList})
      // 获取最后一条消息存入Vuex
      const reqArr = []
      conversationList.forEach(element => {
        const resp = this.getRequest(`/chat/getLastMessage?roomId=${element.roomId}`)
        reqArr.push(resp)
      });
//          拿不出来？操
      Promise.all(reqArr).then(res => {
        const lastNewsArr = res.map(item => {
          return item.data.singleLastMessage
        })
        const lastNewsMap = lastNewsArr.reduce((map,item) => {
          if (item && item.roomId) {
            map[item.roomId] = item
          }
          return map
        },{})
        // console.log("lastNewsMap");
        // console.log(lastNewsMap);
        this.$store.dispatch('news/SET_LAST_NEWS',{
          type: 'init',
          res: lastNewsMap
        })

      }).catch(err => {
        console.log(err)
      })
      this.conversationList = conversationList
    },
    // 获取最近群聊
    getRecentGroupConversation() {
      const recentGroupIdStr = window.localStorage.getItem('coMessager-recentConversation-group') || ''
      const recentGroupIds = arrUnique(recentGroupIdStr.split(',')).filter(item => item)
      this.postRequest('/chat/recentGroup', {
        userId: this.userInfo.userId,
        groupIds: recentGroupIds
      }).then(resp => {
        if (resp.code == 200) {
          const groupList = resp.data.recentGroups
          groupList.forEach(item => {
            item.conversationType = 'GROUP'
            item.isGroup = true
            item.roomId = item.groupInfo.gid
          })
          const groupRoomIds = groupList.map(item => item.groupInfo.gid)
          const reqArr = []
          console.log('输出group');
          console.log(groupRoomIds);
          groupRoomIds.forEach(item => {
            console.log(item);
            const req = this.getRequest(`/chat/lastMessage?roomId=${item}`)
            reqArr.push(req)
          })
          Promise.all(reqArr).then(resp => {
            const lastNewsArr = resp.map(item => {
              return item.data.groupLastMessage
            })
            const lastNewsMap = lastNewsArr.reduce((map, item) => {
              if (item && item.roomId) map[item.roomId] = item
              return map
            }, {})
            this.$store.dispatch('news/SET_LAST_NEWS', {
              type: 'concat',
              res: lastNewsMap
            })
          }).catch(err => {
              console.log(err);
            })
          this.conversationList = [...this.conversationList, ...groupList] //合并所有会话
          this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'init', data: this.conversationList})
        }
      })
    },
  },
  async created() {
    
  },
  async mounted() {
    this.userInfo = this.$store.state.user.userInfo
    await this.getRecentConversation()
    this.getRecentGroupConversation()
  }
}
</script>

<style scoped>
/*  */
.selected {
  background-color: #c4c4c5;
}
/* .selected:hover {
  background-color: #c4c4c5;
} */
</style>