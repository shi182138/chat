<template>
  <div class="friend-info-container" >
      <div class="worker">
        <div class="first">
            <div>
                <!-- currentConversation.nickname -->
                <p>{{currentConversation.beiZhu == ''? currentConversation.nickname : beiZhu}}</p>
                <span></span>
            </div>
            <img :src="currentConversation.avatar" alt="">
        </div>
        <div class="second">
            <div>
                <span>备注</span>
                <p class="beiZhuTag" v-show="!isShowEditInput" @click="showEditInput">{{beiZhu}}</p>
                <input class="editInput" type="text" v-show="isShowEditInput"
                ref="editInput"
                v-model="newBeiZhu"
                @keyup.enter="editBeiZhu"
                @blur="hiddentInput">
            </div>
            <div><span>地区</span><p>中国-{{friendInfo.city.name}}</p></div>
            <div><span>账号</span><p>{{friendInfo.code}}</p></div>
            <!-- <div><span>来源</span><p></p></div> -->
        </div>
        <button class="send" @click="cut">发消息</button>
      </div>
  </div>
</template>

<script>
import {SET_UNREAD_NEWS_TYPE_MAP} from '@/store/constants'
import {saveRecentConversationTolocal} from '@/utils';
export default {
    data() {
        return {
            isShowEditInput: false,
            newBeiZhu: ''
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
        friendInfo() {
            return this.$store.state.user.friendInfo
        },
        beiZhu() {
            if (this.currentConversation.beiZhu == '') {
                return '点击添加备注'
            } else {
                this.newBeiZhu = this.currentConversation.beiZhu
                return this.currentConversation.beiZhu
            }
        },
        userInfo() {
            return this.$store.state.user.userInfo
        },
    },
    watch: {
    },
    methods: {
        cut() {
            //-------------------------------------------------------
            this.$store.dispatch('news/SET_UNREAD_NEWS',{
            roomId: this.currentConversation && this.currentConversation.roomId,
            count: 0,
            type: SET_UNREAD_NEWS_TYPE_MAP.clear
            })
            this.$store.dispatch('news/SET_LAST_NEWS', {type: 'default', roomId: this.currentConversation.roomId})
            // this.$store.dispatch('app/SET_CURRENT_CONVERSATION',this.currentConversation)
            this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'add', data: this.currentConversation})
            // 将该会话下的消息设置为已读begin
            this.currentConversation.conversationType === 'FRIEND' && this.postRequest('/chat/isRead',{
            roomId: this.currentConversation.roomId,
            userId: this.userInfo.userId
            }).then(resp => {
            if (resp.state >= 400 || resp.data.code !== 200) return
            this.$socket.emit('isReadMsg', {
                roomId: this.currentConversation.roomId || '',
                status: false
            })
            // if (oldVal.conversationType === "FRIEND") {
            //     this.$socket.emit('isReadMsg', {
            //     roomId: oldVal.roomId || '',
            //     status: false
            //     })
            // }
            })
            saveRecentConversationTolocal(this.currentConversation)

            // this.$store.commit('user/ShowRecentList')
            this.$bus.$emit('fuckicon')
            this.$store.commit('user/ShowChatWindow');
        },
        showEditInput() {
            this.isShowEditInput = true
            this.$nextTick(() => {
                this.$refs.editInput.focus()
            })
        },
        hiddentInput() {
            this.isShowEditInput = false
        },
        async editBeiZhu() {
            const params = {
                userId: this.currentConversation.myId,
                friendId: this.currentConversation.id,
                friendBeiZhuName: this.newBeiZhu
            }
            await this.postRequest('/chat/modifyFriendBeiZhu',params)
            const resp = await this.getRequest(`/chat/getUserInfo?userId=${this.userInfo.userId}`)
            this.$store.dispatch('user/SET_USERINFO',resp.userInfo)
            this.$store.dispatch('app/SET_CURRENT_CONVERSATION',{
                type: 'modifyBeiZhu',
                beiZhu: this.newBeiZhu
            })
            this.isShowEditInput = false
        },
    },

    mounted() {
        this.currentConversation = this.$store.state.app.currentConversation
    }
}
</script>

<style scoped>
.friend-info-container {
    width: 68%;
    /* background-color: #f5f5f5; */
    /* background-color: black; */
    display: flex;
    justify-content: center;
}
.worker {
    display: flex;
    flex-direction: column;
}
.first {
    height: 105px;
    width: 372px;
    border-bottom: 1px solid #e7e7e7;
    padding-top: 95px;
    display: flex;
    justify-content: space-between;
}
    .first p {
        margin-top: 0px;
    }
    .first img {
        width: 60px;
        height: 60px;
    }
.second {
    height: 148px;
    border-bottom: 1px solid #e7e7e7;
    /* display: flex;
    flex-direction: column; */
    padding-top: 32px;
    font-size: 16px;
    margin-bottom: 36px;
}
.second div {
    display: flex;
}
.second span,.second p {
    margin: 0;
    margin-top: 8px;
    margin-top: 8px;
}
.second span {
    margin-right: 40px;
    color: #999;
}
.beiZhuTag:hover {
    background-color: #eceef0;
}
.editInput {
    height: 20px;
    width: 152px;
    background-color: #f2f2f2;
    border: 0;
    outline: none;
    border-bottom: 1px solid black;
    margin-top: 6px;
}
/* 发消息按钮 */
.send {
    outline: none;
    border: 0;
    background-color: #1aad19;
    width: 140px;
    height: 36px;
    margin: 0 auto;
    color: #fff;
}
.send:hover {
    background-color: #129611;
}
</style>