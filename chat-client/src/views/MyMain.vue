<template>
<div class="messagesBox">
    <my-aside/>
    <!--左侧-->
    <div class="messagesBox-left">
        <my-input />
        <keep-alive include="RecentList">
            <component :is="itemName"></component>
        </keep-alive>
    </div>
    <!-- 聊天窗口 -->
    <component :is="itemName1"></component>
    <!-- <friend-info v-show="showFriendInfo"/>
    <chat-window v-show="showChatWindow"/> -->
    <!-- 语音提醒 -->
    <audio src="../../public/static/audio/apple.mp3" ref="audio" muted></audio>
    <!-- 消息弹框 -->
    <div class="msgPop" v-show="isPopShow">
        <div class="top">
            <img src="" alt="">
            <div>
                <i></i>
                <span></span>
            </div>
        </div>
        <div class="footer">
            <span class="el-icon-check" @click="agree"></span>
            <span class="el-icon-close" @click="disagree"></span>
        </div>
    </div>
    <!--  -->
    <video-call v-if="isVideoCall" @closeVideo="closeVideo"
    :currentConversation="currentConversation"
    :state="webRTCState"
    :web-rtc-type="coVideoWebRtcType"
    />
</div>
</template>

<script>
import MyAside from '../components/MyAside.vue'
import MyInput from '../components/MyInput.vue'
import RecentList from '../components/RecentList.vue'
import ChatWindow from '@/components/ChatWindow.vue'
import FriendGroupList from '../components/FriendGroupList.vue'
import FriendInfo from '@/components/FriendInfo.vue'
import SearchPanel from '@/components/SearchPanel.vue'
import ValidateFriend from '@/components/add/ValidateFriend.vue'
import GroupInfo from '@/components/group/GroupInfo.vue'
import {SET_UNREAD_NEWS_TYPE_MAP} from '@/store/constants'
import {saveMyFriendsToLocalStorage} from '@/utils'
import {saveRecentConversationTolocal,saveMyGroupToLocalStorage} from '@/utils'
import VideoCall from '@/components/VideoCall.vue'
// import {saveMyGroupToLocalStorage} from '@/utils'

import {mapState} from 'vuex'

export default {
    data() {
        return {
            friendConversationList:[],
            gorupConversationList: [],
            webRTCState: '', // 用于定义进入webRTC通信组件时的状态，如果是apply就发起请求，如果是reply就回复
            isPopShow: false,
            popData: {},
            currentConversation: {},
            isVideoCall: false,
            coVideoWebRtcType : '',
        }
    },
    computed:{
        // ...mapState([showList])
        // showList(){
        //     return this.$store.state.showList
        // }
        ...mapState('user',['itemName','itemName1','userInfo']),
        ...mapState('app', ['isToCoArtBoard','isVideoing','isAudioing']),
        allConversation() {
            return this.$store.state.app.allConversation
        },
        // currentConversation() {
        //     return this.$store.state.app.currentConversation
        // }
    },
    components:{
        MyAside,
        MyInput,
        RecentList,
        ChatWindow,
        FriendGroupList,
        FriendInfo,
        SearchPanel,
        ValidateFriend,
        GroupInfo,
        VideoCall
    },
    methods:{
        async sysUserJoinSocket() {
            const resp = await this.getRequest('/chat/getSysUsers')
            const sysUserList = resp.data.systemUsers
            if (resp.code === 200) {
                sysUserList.forEach(elemen => {
                    this.$store.commit('app/SetSysUsers',sysUserList)
                    const val = {
                        roomId: elemen.sid + '-' + this.userInfo.userId
                    }
                    this.$socket.emit("join",val)
                });
            }
        },
        // 获取好友列表
        async getMyFriends() {
            const userId = this.userInfo.userId;
            const resp = await this.getRequest(`/chat/getMyFriendsList?userId=${userId}`);
            if (resp.code === 200) {
                let myFriendsList = resp.data.myFriendsList;
                myFriendsList.forEach(item => {
                item.conversationType = "FRIEND";
                // item.userName = this.userInfo.username;
                item.myNickname = this.userInfo.nickname
                item.myId = this.userInfo.userId
                item.myAvatar = this.userInfo.avatar
                });
                this.friendConversationList = myFriendsList;
                this.$store.dispatch('app/SET_ALL_CONVERSATION',myFriendsList)
                this.$store.dispatch('app/SET_ALL_FRIENDS',{
                resource: myFriendsList,
                type: 'init'
                })
                // 把好友的id保存到本地，可能会用到
                const saveLocalData = myFriendsList.map(item => {
                    return item.id
                })
                saveMyFriendsToLocalStorage(saveLocalData)
            }
        },
        async getMyGroup() {
            const userId = this.userInfo.userId
            const resp = await this.getRequest(`/chat/getMyGroupList?userId=${userId}`)
            if (resp.code == 200) {
                let myGroupList = resp.data.myGroupList
                myGroupList.forEach(item => {
                    item.conversationType = 'GROUP'
                    item.isGroup = true
                    item.groupInfo = item.groupInfo[0]
                    item.roomId = item.groupId
                })
                this.gorupConversationList = myGroupList
                this.$store.dispatch('app/SET_ALL_CONVERSATION',this.gorupConversationList)
                this.$store.dispatch('app/SET_ALL_GROUPS', {
                    resource: myGroupList,
                    type: 'init'
                })
                const saveLocalData = myGroupList.map(item => item.groupInfo.gid)
                saveMyGroupToLocalStorage(saveLocalData)
            }   
        },
        joinChatRoom() {
            this.friendConversationList.forEach(item => {
                this.$socket.emit("join",item)
            });
        },
        joinGroupChatRoom() {
            this.gorupConversationList.forEach(item => {
                this.$socket.emit("join",item)
            })
        },
        agree() {
        },
        disagree() {
        },
        webRtcMsgWatch(newVal) {

        },
        closeVideo() {
            this.isVideoCall = false
        },
        
    },
    watch: {
        friendConversationList: {
            handler() {
                this.joinChatRoom()
            },deep:true,immediate:true
        },
        gorupConversationList: {
            handler() {
                this.joinGroupChatRoom()
            }
        },
        isVideoing: {
            handler(newVal) {
                this.webRtcMsgWatch(newVal)
            }, deep: true, immediate: true
        },
        isAudioing: {
            handler(newVal) {
                this.webRtcMsgWatch(newVal)
            }, deep: true, immediate: true
        }

    },
    created() {
    },
    sockets: {
        receiveMessage(news) {
            // this.$refs.audio.play()
            let wenben = ''
            if (news.messageType !== 'text') {
                // wenben = new.fileRawName ? news.fileRawName.slic
            } else {
                wenben = news.message ? news.message.slice(0,10) : ''
            }
            this.$notify({
                title: '收到新消息',
                message: wenben,
                type: 'success'
            })
            this.$store.dispatch('news/SET_UNREAD_NEWS', {
                roomId: news.roomId,
                count: 1,
                type: 'ADD'
            })
            const senderConversation = this.allConversation.find(item => item.roomId === news.roomId)
            this.$store.dispatch('app/SET_RECENT_CONVERSATION', {
                type: 'add',
                data: senderConversation
            })
            this.$store.dispatch('news/SET_LAST_NEWS', {
                type: 'edit',
                res: {
                    roomId: news.roomId,
                    news: news
                }
            })
            console.log('输出一下news');
            console.log(news);
            saveRecentConversationTolocal(news)
        },
        receiveValidateMessage(news) {
            this.$refs.audio.play()
            this.$store.dispatch('news/SET_UNREAD_NEWS', {
            roomId: news.roomId,
            count: 1,
            type: SET_UNREAD_NEWS_TYPE_MAP.add
            })
        },
        receiveAgreeFriendValidate(data) {
            this.$bus.$emit('getMyFriends')
        },
        forceCramGroup() {
            this.getMyGroup()
        },
        receiveDelGoodFriend(oldVal) {
            this.$store.dispatch('app/SET_RECENT_CONVERSATION', {type: 'edit', data: oldVal})
            this.$store.dispatch('app/SET_CURRENT_CONVERSATION',{type: 'edit', data: oldVal})
        },
        apply(data) {
            const webRtcType = data.webRtcType
            if (this.isVideoing || this.isAudioing) {
                this.$socket.emit('reply', {...data, type: 'busy'})
                return
            } else {
                let text = ''
                // this.isPopShow = true
                if (webRtcType == 'video') {
                    text = '视频通话'
                } else if (webRtcType == 'audio') {
                    text = '语音通话'
                }
                this.$confirm(`您的好友${data.myNickname}请求与你进行${text}, 是否同意?`, "提示", {
                confirmButtonText: "同意",
                cancelButtonText: "拒绝",
                type: "warning"
                }).then(async () => {
                    this.webRTCState = 'reply'
                    this.currentConversation = data
                    this.coVideoWebRtcType = webRtcType
                    this.isVideoCall = true
                    this.$store.dispatch('app/SET_CURRENT_CONVERSATION', data)
                    if (webRtcType === 'audio') {
                        this.$store.dispatch('app/SET_IS_AUDIOING', true)
                    } else if (webRtcType === 'video') {
                        this.$store.dispatch('app/SET_IS_VIDEOING', true)
                    }
                })

            }
        }
    },
    mounted() {
        console.log('我被挂载了');
        // 加载系统用户
        this.getMyFriends()
        this.getMyGroup()
        this.sysUserJoinSocket()
        this.$bus.$on("getMyFriends",() => {
            this.getMyFriends();
        })
        this.$bus.$on('getMyGroup', () => {
            this.getMyGroup()
        })
    }
}
</script>

<style scoped>
    .messagesBox {
        margin: 50px auto 50px  ;
        height: 612px;
        width: 958px;
        background-color: #f5f5f5;
        display: flex;
        box-shadow: 0 0 2px #cdcdcd;
        /* z-index: 1; */
        /* position: absolute; */
    }
    .messagesBox-left {
        width: 248px;
        height: 100%;
        background-color: #e6e6e6;
    }
    /* 消息弹框 */
    .msgPop {
        width: 280px;
        height: 108px;
        border-radius: 2px;
        background-color: #f7f7f7;
        position: fixed;
        right: 50%;
        top: 0px;
    }
    .top {
        display: flex;
        margin-top: 12px;
        margin-left: 12px;
        height: 48px;
    }
    .top img {
        width: 36px;
        height: 36px;
        margin-right: 8px;
    }
    .top div i {
        display: block;
        font-size: 12px;
        margin-bottom: 12px;
    }
    .top div span {
        color: #999;
        font-size: 12px;
    }
    .footer {
        display: flex;
        font-size: 32px;
    }
</style>