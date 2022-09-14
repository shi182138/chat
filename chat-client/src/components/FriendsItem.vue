<template>
  <div>
      <div class="friend-item" 
      @contextmenu.prevent.stop="showMenu"
      :class="{actived:active==index}">
          <img :src="friend.avatar" >
          <p>{{friend.beiZhu == '' ? friend.nickname : friend.beiZhu}}</p>
      </div>

      <!-- 菜单 -->
      <div class="menu-container" v-if="isShowMenu" 
      :style="{'left': menuLeft + 'px', 'top':  menuTop + 'px'}">
          <Menu @hiddenMenu="hiddenMenu" @showPop="isShowDelPop"></Menu>
      </div>
      <!-- 确认删除弹框 -->
      <div class="delPop" v-if="showDelPop">
        <div class="delPop-top"><p>删除盆友</p><span @click="fuckClose">X</span></div>
        <div class="delPop-center"><span>删除{{friend.nickname}}</span></div>
        <button class="confirm" @click="deleteFriend">确认</button>
        <button class="cancel" @click="fuckClose">取消</button>
      </div>
  </div>
</template>

<script>
import Menu from '@/components/friend/Menu.vue'
import { arrUnique } from '@/utils'
export default {
    props:{
      friend:Object,
      index:Number,
      active:Number
    },
    components: {
        Menu,
    },
    computed: {
      userInfo() {
        return this.$store.state.user.userInfo
      }
    },
    data() {
        return {
            isShowMenu: false,
            menuTop: 0,
            menuLeft: 0,
            showDelPop: false,

        }
    },
    methods: {
      fuckClose() {
        this.showDelPop = false
      },
        showMenu(e) {
            this.isShowMenu = true
            this.menuTop = e.pageY
            this.menuLeft = e.pageX
        },
        hiddenMenu() {
            this.isShowMenu = false
        },
        isShowDelPop() {
            this.showDelPop = true
             // this.$emit('hiddenMenu')
        },
        remove() {
            const recentFriendIdsStr = window.localStorage.getItem('coMessager-recentConversation-friend') || ''
            const recentFriendIds = arrUnique(recentFriendIdsStr.split(',')).filter(item => item) // 去重
            const index = recentFriendIds.findIndex(item => item === this.friend.id)
            index !== -1 && recentFriendIds.splice(index, 1)
            window.localStorage.setItem('coMessager-recentConversation-friend', recentFriendIds.join())
            this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'delete', data: this.friend})
            console.log(this.$store.state.app.recentConversation);
        },
        async deleteFriend() {
            const resp = await this.deleteRequest('/chat/deleteGoodFriend',{
                userM: this.userInfo.userId,
                userY: this.friend.id,
                roomId: this.friend.roomId
            })
            if (resp.code === 200) {
              this.$socket.emit("sendDelGoodFriend", this.friend)
              this.remove() //更新最近会话列表
                this.$bus.$emit('getMyFriends')
                
                this.$store.dispatch('app/SET_ALL_FRIENDS', {
                    resource: this.conversation.id,
                    type: 'delete'
                })
                this.$message({
                    message: '删除成功！',
                    type: 'success'
                })
                this.$store.commit('user/initialInterface')
            }
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
.friend-item {
    display: flex;
    width: 248px;
    height: 60px;
}
.friend-item:hover {
  background-color: #dddad9;
}
.actived {
  background-color: #c4c4c5;
}
.actived:hover {
  background-color: #c4c4c5;
}
    .friend-item img {
        margin-top: 12px;
        margin-left: 12px;
        margin-right: 12px;
        width: 36px;
        height: 36px;
    }
    .friend-item p {
        font-size: 14px;
        margin-top: 22px;
    }
.menu-container {
    position: fixed;
    z-index: 1000;
    height: 50px;
}

/* 确认删除面板 */
.delPop {
  width: 360px;
  height: 224px;
  background-color: #f5f5f5;
  position: absolute;
  top: 200px;
  left: 250px;
  z-index: 1000;
  border: 1px solid #e7e7e7;
}
.delPop-top {
  display: flex;
  justify-content: space-between;
  color: #99a9c8;
  font-size: 13px;
}
.delPop-top p {
  margin: 0;
  margin-top: 8px;
  margin-left: 12px;
}
.delPop-center {
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