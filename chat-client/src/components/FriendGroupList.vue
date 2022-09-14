<template>
  <div>
    <div class="contacts-container">
      <div class="test">
        <button @click="showCreateGroup">创建群聊</button>
      </div>
      <!-- 创建群聊面板 -->
      <div class="creatGroupPanel-container" v-if="isShowCreateGroup"> 
        <create-group-panel @createGroupReq="createGroup" 
        @cancelCreatGroup="showCreateGroup" />
      </div>
      <div class="new-friend-box" @click="showPanel">
        <span>新的朋友</span>
        <div class="new-friend-item">
          <UnreadNewsCount 
          :count="unreadNews[sysUsersRoomId]"
          v-show="typeof(unreadNews[sysUsersRoomId]) != 'undefined' && unreadNews[sysUsersRoomId] != 0" />
          <img src="../../public/static/image/74.png" alt="">
          <p>新的朋友</p>
        </div>
      </div>
      <!-- 好友分组 -->
      <div class="fenZu-container" @click="isShowFenZuItem">
        <span>分组</span>
        <!-- 菜单 -->
          <div class="fenZuMenu-container" v-if="showFenZuMenu" :style="{'left': menuLeft + 'px', 'top':  menuTop + 'px'}">
            <!--  @hiddenMenu="hiddenMenu" @showPop="isShowDelPop" -->
            <fen-zu-menu @hiddenFenZuMenu="hiddenFenZuMenu"
                         @isShowAddMember="showAddMemberPanelFun"
                         @showDelFenZuPop="showDelFenZuPop"/>
          </div>
          <!-- 分组标签 -->
        <div v-for="(item,index) in friendFenZu" :key="index" @contextmenu.prevent.stop="isShowFenZuMenu($event, item)">
          <p class="fenzu-tag" @dblclick="showEditFenZuName(index,item)" v-show="!(editIndex == index)" >{{item}} ({{outcomeConversation[item].length}})</p>
          <input type="text" v-if="editIndex == index" 
                  class="editNameInput"
                  ref="editNameInput"
                  v-model="editNewFenZuName"
                  @blur="myEditBlur"
                  @keyup.enter="editFenZuName(item)">
          <div class="item-container" >
            <friend-item v-for="fenZuitem in outcomeConversation[item]" :key="fenZuitem.id" :friend='fenZuitem'
            @click.native="isShowFriendInfo(fenZuitem)"/>
          </div>
        </div>
        <!-- 添加分组 -->
        <div>
          <span @click.stop="isShowAddFenzu" v-show="!showAddFenzu">添加分组</span>
          <input type="text" v-show="showAddFenzu" ref="addFenZuInput" v-model="newFenZuName" @keyup.enter="addNewFenZuItem" @blur="myBlur">
        </div>
      </div>
      <!-- 添加分组成员面板 -->
      <div class="addMemberPanel-container" v-if="showAddMemberPanel">
        <!-- :addFenZuMemberList="addFenZuMemberList" -->
        <add-member-panel @closeAddMemberPanel="closeAddMemberPanel" 
        :currentFenZu="currClickFenZu"
        />
      </div>
      <!-- 确认删除分组弹框 -->
      <!-- 删除确定弹框 -->
      <div class="delFenZuPop" v-show="isShowDelFenZuPop">
          <div class="delFenZuPop-top"><p>提示</p><span>X</span></div>
          <div class="delFenZuPop-center"><span>是否删除该分组？</span></div>
          <button class="confirm" @click="deleteFenZuItem">确认</button><button class="cancel">取消</button>
      </div>
      <!-- 群列表 -->
      <div class="groupList-container">
        <gorup-conversation-list />
      </div>
      <!-- 好友列表 -->
      <div class="item-rank" >
        <span>A</span>
        <friend-item   v-for="(friend, index) in hasBeiZuList"
        @click.native="isShowFriendInfo(friend, index)" 
        :key="friend.nickname"
        :index="index" 
        :friend='friend' 
        :active="active"/>
      </div>
    </div>
  </div>
</template>

<script>
import FriendItem from '@/components/FriendsItem.vue'
import UnreadNewsCount from '@/components/unreadNewsCount'
import {SET_UNREAD_NEWS_TYPE_MAP} from '@/store/constants'
import FenZuMenu from '@/components/friend/FenZuMenu.vue'
import AddMemberPanel from '@/components/fenZuManagement/AddMemberPanel.vue'
import createGroupPanel from './group/CreateGroupPanel.vue'
import GorupConversationList from './group/GroupConversationList.vue'

export default {
  data(){
    return {
      showFenZuItem: false,
      showAddFenzu: false,
      showFenZuMenu: false,
      showAddMemberPanel: false,
      isShowDelFenZuPop: false,
      isShowCreateGroup: false,
      menuTop: 0,
      menuLeft: 0,
      // -----------------------------
      newFenZuName:'未命名',
      editNewFenZuName: '',
      count: 0,
      currClickFenZu: '', // 当前点击的分组
      editIndex: -1,
      active: -1
    }
  },
  components:{
    FriendItem,
    UnreadNewsCount,
    FenZuMenu,
    AddMemberPanel,
    createGroupPanel,
    GorupConversationList
  },
  computed: {
    userInfo() {
      return this.$store.state.user.userInfo
    },
    friendFenZu() { // 获取所有分组 [分组1， 分组2]
      return Object.keys(this.userInfo.friendFenZu)
    },
    sysUsersRoomId() {
      const sysUsers =  this.$store.state.app.sysUsers
      const validateSysUser = sysUsers.filter(item => item.code === '111111')[0]
      return validateSysUser.sid + '-' + this.userInfo.userId
    },
    unreadNews() {
      return this.$store.state.news.unreadNews
    },
    beiZhu() { // 备注map
      return this.userInfo.friendBeiZhu || {}
    },
    friendList: {
      get() {
        return this.$store.state.app.allFriends
      },
      set() {

      }
    },
    
    //给会话列表加上备注属性
    hasBeiZuList() {
      // const conversationList = this.friendList
      const conversationList = JSON.parse(JSON.stringify(this.friendList))
      conversationList.forEach(item => {
        item.beiZhu = this.beiZhu[item.id] ? this.beiZhu[item.id] : ''
      })
      return conversationList
    },
    //分组会话列表
    outcomeConversation() {
      // const conversationList = this.hasBeiZuList
      const conversationList = JSON.parse(JSON.stringify(this.hasBeiZuList))
      const fenZuMap = this.userInfo.friendFenZu // {分组1: [id1, id2, ...], 分组2: [id3, id4, ...]}
      const fenZuKeys = Object.keys(fenZuMap) // [分组1, 分组2, ...]
      const res = {} 
      fenZuKeys.forEach(item => {
        res[item] = []
      })
      for (let i = 0; i < conversationList.length; i++) {
        const item = conversationList[i]
        fenZuKeys.forEach(fenZuItem => {
          if (fenZuMap[fenZuItem].includes(item.id)) {
            res[fenZuItem].push(item)
          }
        })
      }
      return res
    },
    
  },
  watch: {
    currClickFenZu: {
      deep:true,
      handler() {
        // console.log('wo被执行了？');
      let memberList = JSON.parse(JSON.stringify(this.hasBeiZuList))
      // const memberList = this.friendList
      memberList.forEach(item => {
        item.isAdd = false
        if (this.currClickFenZu == '') {return}
        for (let i = 0; i < this.outcomeConversation[this.currClickFenZu].length; i++) {
          if (this.outcomeConversation[this.currClickFenZu][i].id === item.id) {
              item.isIn = 1
              break
          }
        }
      })
      console.log(memberList);
      this.$store.dispatch('app/SET_ADD_MEMBER_LIST', memberList)
      }
    }
  },
  methods:{
    showCreateGroup() {
      this.isShowCreateGroup = !this.isShowCreateGroup
    },
    async createGroup({name,photo}) {
      const params = {
        title: name,
        desc: '',
        img: photo,
        holderName: this.userInfo.username,
        holderUserId: this.userInfo.userId,
        createType: 0
      }
      const resp = await this.postRequest('/chat/createGroup', params)
      let res = resp.data.groupCreator
      res.conversationType = 'GROUP'
      res.isGroup = true
      res.roomId = res.groupId
      this.$store.dispatch('app/SET_ALL_GROUPS', {
        type: 'add',
        resource: resp.data.groupCreator
      })
      this.isShowCreateGroup = false
    },
    async editFenZuName(item) {
      const resp = await this.postRequest('/chat/editFenZu', {
        oldFenZu: item,
        newFenZu: this.editNewFenZuName,
        userId: this.userInfo.userId
      })
      if (resp.code == 200) {
        const res = await this.getRequest(`/chat/getUserInfo?userId=${this.userInfo.userId}`)
        this.editIndex = -1
        this.$store.dispatch('user/SET_USERINFO',res.userInfo)
        this.$message({type: 'success', message: '修改成功！'})
      } else {
        this.$message({type: 'error', message: '修改失败！'})
      }
    },
    myEditBlur() {
      this.editIndex = -1
    },
    showEditFenZuName(index,item) {
      console.log('测试一下');
      this.editIndex = index
      this.editNewFenZuName = item
      this.$nextTick(() => {
        this.$refs.editNameInput[index].focus()
        this.$refs.editNameInput[index].setSelectionRange(0, item.length)
      })
    },
    myBlur() {
      this.showAddFenzu = false
    },
    showDelFenZuPop() {
      this.isShowDelFenZuPop = true
    },
    showAddMemberPanelFun() {
      this.$store.dispatch('app/ClearRight')
      this.showAddMemberPanel = true
    },
    hiddenFenZuMenu() {
      this.showFenZuMenu = false
    },
    closeAddMemberPanel() {
      this.showAddMemberPanel = false
    },
    isShowFenZuMenu(e,item) {
      this.showFenZuMenu = true
      this.menuTop = e.pageY
      this.menuLeft = e.pageX
      this.currClickFenZu = item
      console.log('分组会话列表');
      console.log(this.outcomeConversation);
    },
    isShowAddFenzu() {
      this.showAddFenzu = true
      this.$nextTick(() => {
        this.$refs.addFenZuInput.focus()
      })
    },
    isShowFenZuItem(e) {
      if (e.target.className == 'fenzu-tag') {
        //迫不得已亲自操作dom
        let itemContainer = e.target.parentNode.querySelector('.item-container')
        itemContainer.classList.toggle('item-container-on')
      }
    },
    showPanel() {
      this.$store.dispatch('news/SET_UNREAD_NEWS',{
            roomId: this.sysUsersRoomId,
            count: 0,
            type: SET_UNREAD_NEWS_TYPE_MAP.clear
            })
      this.$store.commit('user/showNewPanel')
    },
    isShowFriendInfo(friend, index) {
      this.active = index
      this.getFriendInfo(friend.id)
      this.setCurrentConversation(friend)
      this.$store.commit('user/IsShowFriendInfo');
    },
    getFriendInfo(id) {
      this.getRequest(`/chat/getUserInfoById?uid=${id}`).then(resp => {
        this.$store.commit('user/SetFriendInfo',resp.data.userInfo)
      })
    },
    // -------------------------------------------------
    setCurrentConversation(data) {
      this.$store.dispatch('app/SET_CURRENT_CONVERSATION',data)
    },
    async addNewFenZuItem() {
      if (this.newFenZuName == '') {
        this.showAddFenzu = false
        this.$message({type: 'warning', message: '分组名不能为空'})
      }
      if (this.friendFenZu.includes(this.newFenZuName.trim())) {
        this.$message({type: 'warning', message: '已有该分组'})
          this.newFenZuName = '' //重置其值为空
          this.showAddFenzu = false
          return
      }
      const params = {
        fenZuName: this.newFenZuName.trim(),
        userId: this.userInfo.userId
      }
      const resp = await this.postRequest('/chat/addFenZu', params)
      if (resp.code != 200) {
        this.count++
        this.$message({message: data.message, type: warning})
      }
      this.showAddFenzu = false
      const resp1 = await this.getRequest(`/chat/getUserInfo?userId=${this.userInfo.userId}`)
      this.$store.dispatch('user/SET_USERINFO',resp1.userInfo)
    },
    async deleteFenZuItem() {
      const params = {
        fenZuName: this.currClickFenZu,
        userId: this.userInfo.userId
      }
      await this.deleteRequest('/chat/delFenZu', params)
      // this.$message({message: '删除成功', type: 'success'})
      this.isShowDelFenZuPop = false
      const res = await this.getRequest(`/chat/getUserInfo?userId=${this.userInfo.userId}`)
      this.$store.dispatch('user/SET_USERINFO',res.userInfo)
    }
  },
  mounted() {
    document.addEventListener('click', () => {
      this.showFenZuMenu = false
    })
    document.addEventListener('mousedown', (e) => {
      //   console.log(e);
      const {button} = e
      if (button === 2) {
        this.showFenZuMenu = false
      }
    })
  },
}
</script>

<style scoped>
/*  */
.contacts-container {
  overflow-y: scroll;
  height: 547px;
  /* width: 200px; */
  overflow-x: hidden;
}
    /*修改滚动条样式*/
.contacts-container::-webkit-scrollbar{
  width:5px;
  /* height:5px; */
  display: none;
  /**/
}
.contacts-container:hover::-webkit-scrollbar {
      display: block;
}
.contacts-container::-webkit-scrollbar-track{
  background: rgb(239, 239, 239);
  border-radius:2px;
}
.contacts-container::-webkit-scrollbar-thumb{
  background: #bfbfbf;
  border-radius:10px;
}
.contacts-container::-webkit-scrollbar-thumb:hover{
  background: #333;
}
.contacts-container::-webkit-scrollbar-corner{
  background: #179a16;
}
/* 创建群聊面板 */
.creatGroupPanel-container {
  /* position: relative; */
  position: absolute;
  transform: translate(70%,50%);
  z-index: 1000;
}
.item-rank,
.new-friend-box {
  border-top: 1px solid #dad9d9;
  display: flex;
  flex-direction: column;
}
  .item-rank span,.new-friend-box span,.fenZu-container span:first-child {
    color: #999;
    font-size: 10px;
    margin-left: 12px;
    margin-top: 20px;
    margin-bottom: 5px;
  }
.new-friend-item {
    position: relative;
    display: flex;
    width: 248px;
    height: 60px;
}
.new-friend-item:hover {
    background-color: #c5c4c4;
}
    .new-friend-item img {
        margin-top: 12px;
        margin-left: 12px;
        margin-right: 12px;
        width: 36px;
        height: 36px;
    }
    .new-friend-item p {
        font-size: 14px;
        margin-top: 22px;
        /* margin: 0; */
    }
  
  /* 分组 */
  /* .fenZu-container {
    background-color: aliceblue;
  } */
  .fenzu-tag {
    font-size: 16px;
    margin-left: 15px;
    margin: 0;
    display: block;
    height: 28px;
    line-height: 28px;
    padding-left: 20px;
  }
 .item-container {
    display: none;
  }
  .item-container-on {
    display: block;
  }
  /* 更改分组名输入框 */
  .editNameInput {
  outline: none;
  border: 0;
  padding: 0;
  height: 28px;
  width: 92%;
  padding-left: 20px;
  background-color: #d2d2d2;
  font-size: 16px;
}
  /* 添加分组成员面板 */
  .addMemberPanel-container {
    position: absolute;
    top: 94px;
    left: 400px;
  }
  /* 分组菜单 */
  .fenZuMenu-container {
    position: fixed;
    z-index: 1000;
    height: 50px;
  }

  /* 顶部按钮 */
  .test button{
    display: block;
    background-color: #fff;
    border: 1px solid #dad9d9;
    width: 224px;
    height: 40px;
    text-align: center;
    margin-left: 12px;
  }
  .test button:first-child {
    margin-top: 12px;
  }
  /* 删除分组确定弹框 */
/* 确认删除面板 */
.delFenZuPop {
  width: 360px;
  height: 224px;
  background-color: #f5f5f5;
  position: absolute;
  top: 200px;
  left: 250px;
  z-index: 1000;
  border: 1px solid #e7e7e7;
}
.delFenZuPop-top {
  display: flex;
  justify-content: space-between;
  color: #99a9c8;
  font-size: 13px;
}
.delFenZuPop-top p {
  margin: 0;
  margin-top: 8px;
  margin-left: 12px;
}
.delFenZuPop-center {
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

/* -------- */

</style>