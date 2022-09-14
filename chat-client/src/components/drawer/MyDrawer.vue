<template>
  <div class="drawer-box">
      <!-- 搜索框 -->
      <input class="top-input" type="text" name="" id="">
      <!-- 群成员 -->
      <div class="tool-box">
          <div class="user-box">
          <div class="item" v-for="item in memberList" :key="item.nickname">
              <img :src="item.avatar" alt="">
              <span>{{item.nickname}}</span>
          </div>
          <div class="add-item"><span @click="showAddMember">+</span><p>添加</p></div>
        </div>
        <div class="base-info" v-if="currentConversation.conversationType == 'GROUP'">
            <div><span>群公告</span><p></p></div>
            <div><span>备注</span><p></p></div>
            <div><span>我在本群昵称</span><p></p></div>
        </div>
        <button class="bottom-btn">删除聊天记录</button>
        <button class="bottom-btn" @click="quitGroupPop">删除并退出</button>
      </div>
      <!-- 添加弹框 -->
      <div class="addPanel-box" v-if="isShowAddMember">
          <add-member-panel @closeAddMemberPanel="closeAddMemberPanel" 
          :whereOpen="1"
          />
          <!--           :groupId="this.memberList[0].groupId" -->
      </div>
      <!-- 确认退出弹框 -->
      <quit-group-pop v-if="showQuitPop" @closeQuitPop="closeQuitPop"
      @quitGroup="quitGroup"/>
  </div>
</template>

<script>
import addMemberPanel from '@/components/fenZuManagement/AddMemberPanel.vue'
import quitGroupPop from '../group/QuitGroupPop.vue'
export default {
    components: {
        addMemberPanel,
        quitGroupPop
    },
    data() {
        return {
            isShowAddMember: false,
            showQuitPop: false
        }
    },
    computed: {
        userInfo() {
            return this.$store.state.user.userInfo
        },
        currentConversation() {
            return this.$store.state.app.currentConversation
        },
        beiZhu() { // 备注map
            return this.userInfo.friendBeiZhu || {}
        },
        friendList() {
            return this.$store.state.app.allFriends
        },
        hasBeiZuList() {
            // const conversationList = this.friendList
            console.log('输出一下friendList');
            // console.log(this.friendList);
            const conversationList = JSON.parse(JSON.stringify(this.friendList))
            conversationList.forEach(item => {
                item.beiZhu = this.beiZhu[item.id] ? this.beiZhu[item.id] : ''
            })
            return conversationList
        },
        memberList() {
            return this.$store.state.user.userBus
        },

    },
    methods: {
        showAddMember() {
            // 设置一下addmemberList
            this.setAddMemberList()
            this.$store.dispatch('app/ClearRight')
            this.isShowAddMember = !this.isShowAddMember
        },
        setAddMemberList() {
            let addList = JSON.parse(JSON.stringify(this.hasBeiZuList))
            if (this.memberList[0].conversationType == 'FRIEND') {
                addList.forEach(item => {
                item.isAdd = false
                for (let i = 0; i < this.memberList.length; i++) {
                    if (item.id == this.memberList[i].id) {
                        item.isIn = 1
                        break
                    }
                }
            })
            } else {
                addList.forEach(item => {
                item.isAdd = false
                for (let i = 0; i < this.memberList.length; i++) {
                    if (item.id == this.memberList[i].userId) {
                        item.isIn = 1
                        break
                    }
                }
            })
            }
            this.$store.dispatch('app/SET_ADD_MEMBER_LIST', addList)
        },
        closeAddMemberPanel() {
            this.isShowAddMember = false

        },
        quitGroupPop() {
            this.showQuitPop = true
        },
        closeQuitPop() {
            this.showQuitPop = false
        },
        quitGroup() {
            console.log('当前会话');
            console.log(this.currentConversation);
            this.postRequest('/chat/quitGroup', {
                holder: this.currentConversation.holder,
                groupId: this.currentConversation.roomId,
                userId: this.userInfo.userId
            }).then(resp => {
                if (resp.code == 200) {
                    this.$message({
                        type: 'success',
                        message: '退出成功',
                    })
                    //关闭当前窗口
                    this.$store.commit('user/initialInterface')
                    //删除最近聊天
                    this.$store.dispatch('app/SET_RECENT_CONVERSATION', {
                        type: 'delete',
                        data: this.currentConversation
                    })
                    //删除groupList
                    this.$store.dispatch('app/SET_ALL_GROUPS', {
                        resource: this.currentConversation.roomId,
                        type: 'delete'
                    })
                    //发送 socket 通知该群的所有成员
                    this.$socket.emit('sendQuitGroup', this.currentConversation)
                }
            })
        }
    },
    mounted() {
        // console.log('组成员');
        // console.log(this.memberList);
        // console.log(this.currentConversation);
    }

}
</script>

<style scoped>
.drawer-box {
    width: 252px;
    height: 612px;
    background-color: #f5f5f5;
    position: absolute;
    right: -255px;
    top: 0px;
    box-shadow: 0 0 1px #cdcdcd;
}
.top-input {
    height: 24px;
    width: 200px;
    margin-top: 24px;
    margin-left: 24px;
    outline: none;
    border: 0;
    background-color: #e1e1e1;
    margin-bottom: 12px;
    border-radius: 5px;
}
.tool-box {
    height: 551px;
    overflow-y: scroll;
}
.user-box {
    min-height: 144px;
    /* height: 160px; */
    padding-bottom: 16px;
    border-bottom: 1px solid #ececec;
    display: flex;
    flex-wrap: wrap;
    width: 220px;
    margin-left: 16px;
    align-content: flex-start;
}
    /*修改滚动条样式*/
.tool-box::-webkit-scrollbar{
  width:5px;
  /* height:5px; */
  display: none;
  /**/
}
.tool-box:hover::-webkit-scrollbar {
      display: block;
}
.tool-box::-webkit-scrollbar-track{
  background: rgb(239, 239, 239);
  border-radius:2px;
}
.tool-box::-webkit-scrollbar-thumb{
  background: #bfbfbf;
  border-radius:10px;
}
.tool-box::-webkit-scrollbar-thumb:hover{
  background: #333;
}
.tool-box::-webkit-scrollbar-corner{
  background: #179a16;
}
.item,.add-item {
    height: 56px;
    width: 42px;
    margin-top: 15px;
    margin-right: 12px;
}
.item img {
    width: 36px;
    height: 36px;
    display: block;
    margin: 0 auto;
}
.item span {
    font-size: 12px;
    max-width: 42px;
}
 .add-item span{
    font-size: 36px;
    display: block;
    text-align: center;
    width: 35px;
    height: 35px;
    line-height: 35px;
    border: 1px solid #dadada;
    color: #666;
}
.add-item span:hover {
    color: #343434;
    border-color: #cdcdcd;
}
.add-item p {
    font-size: 12px;
    margin: 0;
    margin-top: 4px;
    max-width: 42px;
    text-align: center;
}
/*  */
.base-info {
    width: 220px;
    border-bottom: 1px solid #ececec;
    margin-left: 24px;
    height: 232px;
}
.base-info div {
    margin-top: 24px;

}
/*  */
.addPanel-box {
    position: absolute;
    top: 42px;
    right: 280px;
    z-index: 1000;
}
/*  */
.bottom-btn {
    width: 220px;
    height: 56px;
    margin-left: 16px;
    border: 0;
    background-color: #f5f5f5;
    color: #f45454;
    border-bottom: 1px solid #ececec;
}
</style>