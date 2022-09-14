<template>
  <div>
      <div class="addMenberPanpel-container">
          <div class="left">
            <input type="text">
            <div class="rankItem">
                <span>A</span>
                <!-- @click.native="isCheck(item)" -->
                <!-- :member="item" -->
                <div class="leftItem-container">
                    <!-- <left-item v-for="item in addFenZuMemberList" :key="item.id"
                    member="item"
                    @click="isChoose(item.id)"
                    /> -->
                    <div class="left-item-container"  v-for="item in addFenZuMemberList" :key="item.nickname" @click="isChoose(item)">
                        <img :src="item.avatar" alt="">
                        <span>{{item.beiZhu == '' ? item.nickname : item.beiZhu}}</span>
                        <!-- <input type="checkbox"> -->
                        <input type="checkbox" class="left-checkInput" :checked="item.isAdd" />
                    </div>
                </div>
            </div>
          </div>
          <div class="right">
            <div class="close">
                <label @click="close">X</label>
            </div>
            <div class="tips">
                <span v-show="rightList.length == 0">未选择聊天</span>
                <span v-show="rightList.length != 0">已选择{{rightList.length}}个聊天</span>
            </div>
            <div class="rightItem-container">
                <!-- <right-item v-for="item in rightList" :key="item.id"
                :checkedItem="item"/> -->
                <div class="right-item-container" v-for="item in rightList" :key="item.id">
                    <img :src="item.avatar" alt="">
                    <span>{{item.beiZhu == '' ? item.nickname : item.beiZhu}}</span>
                    <i class="el-icon-error"></i>
                </div>
            </div>
            <div class="button-box"><button class="confirm" @click="confirmAddMember">确认</button><button class="cancel" @click.stop="close">取消</button></div>
          </div>
      </div>
  </div>
</template>

<script>
import {saveRecentConversationTolocal} from '@/utils';
export default {
    // ,'groupId'
    props: ['currentFenZu','whereOpen'],
    components: {
    },
    data() {
        return {
            isSend: false,
        }
    },
    computed: {
        userInfo() {
            return this.$store.state.user.userInfo
        },
        addFenZuMemberList() {
            return this.$store.state.app.addMemberLis
        },
        rightList() {
            return this.$store.state.app.rightList
        },
        currentConversation: {
            get() {
                return this.$store.state.app.currentConversation
            },
            set() {

            }
        }
    },
    methods: {
        close() {
            this.$emit('closeAddMemberPanel')
        },
        isChoose(item) {
            if (item.isIn == 1) return
            this.$store.dispatch('app/SET_ADD_MEMBER_LIST', {
                type: 'edit',
                addItem: item.id 
            })
        },
        async confirmAddMember() {
            if (this.rightList.length == 0) return
            if (this.whereOpen == 1) {
                const sysUser = this.$store.state.app.sysUsers.filter(item => item.code === '111111')[0]
                const roomId = []
                let nameStr = ''
                let names = []
                let ids = []
                let userList = this.$store.state.user.userBus
                this.rightList.forEach(item => {
                    roomId.push(sysUser.sid + '-' + item.id)
                    ids.push(item.id)
                    names.push(item.username)
                    userList.push(item)//-------------------------------------
                    nameStr += ',' + item.nickname
                })
                this.$store.commit('user/userBus', userList)
                if (this.currentConversation.conversationType == 'FRIEND') {
                    roomId.push(sysUser.sid + '-' + this.currentConversation.id)
                    ids.push(this.currentConversation.id)
                    names.push(this.currentConversation.username)
                    nameStr = this.currentConversation.nickname + nameStr
                    let nicknameStr = this.currentConversation.myNickname + ',' + this.currentConversation.nickname
                    this.rightList.forEach(item => {
                        nicknameStr += ',' + item.nickname
                    })
                    const resp = await this.postRequest('/chat/createGroup', {
                        title: nicknameStr,
                        desc: '',
                        img: 'http://192.168.85.132:8888/group1/M00/00/00/wKhVhGKa_vCAHUWzAATeS1j5sOc687.jpg',
                        holderName: this.userInfo.username,
                        holderUserId: this.userInfo.userId,
                        createType: 1
                    })
                    let conversation = resp.data.groupCreator
                    conversation.groupInfo.userNum += ids.length
                    conversation.conversationType = 'GROUP'
                    conversation.roomId = conversation.groupId
                    console.log('conversation');
                    console.log(conversation);
                    this.$store.dispatch('app/SET_RECENT_CONVERSATION', {
                        type: 'add',
                        data: conversation
                    })
                    this.$store.dispatch('app/SET_CURRENT_CONVERSATION', conversation)
                    this.$socket.emit('join', conversation)
                    this.isSend = true
                } else {
                    this.currentConversation.groupInfo.userNum += ids.length
                }
                const message = {
                    roomId: this.currentConversation.groupId,
                    senderId: this.userInfo.userId,
                    senderName: this.userInfo.username,
                    time: Date.now(),
                    conversationType: 'GROUP',
                    message: this.userInfo.username + '邀请了' + nameStr + '加入群聊',
                    messageType: 'sys',
                    }
                    this.$socket.emit('sendNewMessage', message)
                    //
                    this.$store.dispatch('news/SET_LAST_NEWS', {
                        type: 'edit',
                        res: {
                            roomId: message.roomId,
                            news: message
                        }
                    })
                const params = {
                    memberId: ids,
                    memberName: names,
                    groupId: this.currentConversation.groupId
                }
                await this.postRequest('/chat/addGroupMember', params)
                const params1 = {
                    roomIds: roomId,
                }
                await this.$socket.emit('addMember', params1)
                if (this.isSend) {
                    this.$bus.$emit('sysMessage',message)
                    this.$store.commit('user/setDrawer')
                }
                this.rightList = []
                // this.$store.dispatch('app/ClearRight')
                saveRecentConversationTolocal(this.currentConversation)
                this.isSend = false
                this.$bus.$emit('getMyGroup')
                this.$emit('closeAddMemberPanel')
            } else {
                let i = 0
                const ids = [] 
                this.rightList.forEach(item => {
                    ids[i] = item.id
                    i++
                })
                const params = {
                    userId: this.userInfo.userId,
                    friendIds: ids,
                    fenZuName: this.currentFenZu
                }
                const resp = await this.postRequest('/chat/addFenZuMembers', params)
                if (resp.code == 200) {
                    const res = await this.getRequest(`/chat/getUserInfo?userId=${this.userInfo.userId}`)
                    this.$store.dispatch('user/SET_USERINFO',res.userInfo)
                    this.$emit('closeAddMemberPanel')
                    this.$message({type: 'success', message: '添加成功'})
                } 
            }
        }
    },
    mounted() {
        
        console.log('addmember');
        console.log(this.addFenZuMemberList);
    }

}
</script>

<style scoped>
.addMenberPanpel-container {
    width: 548px;
    height: 484px;
    background-color: #fff;
    display: flex;
    border: 1px solid #dadada;
}
/* 左边部分 */
.left {
    width: 50%;
    border-right: 1px solid #dadada;
}
.left input:first-child {
    border: 0;
    background-color: #e8e8e8;
    height: 24px;
    width: 228px;
    margin-top: 24px;
    margin-left: 24px;
    border-radius: 5px;
    outline: none;
    margin-bottom: 16px;
}
.rankItem span:first-child {
    color: #99abce;
    margin-left: 24px;
    font-size: 9px;
}
/* 右边部分 */
.right {
    width: 50%;
    position: relative;
}
.close {
    display: flex;
    justify-content: flex-end;
    font-size: 10px;
    color: #454545;
    /* text-align: center; */
}
.close label {
    width: 32px;
    height: 24px;
    text-align: center;
    padding-top: 8px;
    margin-bottom: 8px;
}
.close label:hover {
    background-color: #fb7373;
}
.tips {
    display: flex;
    justify-content: flex-end;
    font-size: 13px;
    color: #7f7f7f;
    margin-right: 16px;
}

/* --- */
.left-item-container {
    display: flex;
    width: 252px;
    height: 60px;
    padding-left: 24px;
    padding-top: 12px;
}
.left-item-container img {
    width: 36px;
    height: 36px;
    margin-right: 12px;
}
.left-item-container:hover {
    background-color: #e8e7e7;
}

.right-item-container {
    display: flex;
    padding-top: 12px;
    width: 252px;
    height: 60px;
}
.right-item-container img {
    width: 36px;
    height: 36px;
    margin-left: 25px;
    margin-right: 12px;
}
/* 右边底部按钮 */
.button-box {
    position: absolute;
    right: 16px;
    bottom: 16px;
}
.confirm {
    width: 60px;
    height: 24px;
    font-size: 13px;
    color: #fff;
    background-color: #1aad19;
    border: 0;
    margin-right: 10px;
}
.cancel {
    width: 60px;
    height: 24px;
    font-size: 13px;
    border: 1px solid #e7e7e7;

}
/* 多选按钮样式 */
.left-checkInput {
    outline: none;
    height: 26px;
    width: 100px;
    border: solid 0px #cccccc;
}
.left-checkInput:checked {
    color: #1aad19;
}
</style>