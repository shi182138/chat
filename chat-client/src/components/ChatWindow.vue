<template>
<!-- 聊天窗口 -->
  <div class="chatWindow">
      <!-- 顶部 -->
      <div class="chatWindow-topInfo"><p>{{talkToWho}}</p>
      <span class="drawer-switch" @click="drawerSwitch"><i class="el-icon-more"></i></span></div>
      <!-- 抽屉 -->
      <chat-drawer v-if="isShowDrawer" />
      <!-- a点击头像查看信息 -->
      <div class="infoPanel-container" v-if="isLookInfo" @click.prevent.stop="" 
      :style="{'left': menuLeft + 'px', 'top':  menuTop + 'px'}">
        <little-info-panel  :user="lookInfo" />
    </div>
    <!-- viedo call      :currentConversation="currentConversation" -->
    <video-call v-if="isVideoCall" @closeVideo="closeVideo"
    :state="webRTCState"
    :web-rtc-type="coVideoWebRtcType"
    />
      <!-- 窗口 -->
      <div class="message-container" ref="Area">
          <div>
            <span class="" @click="loadmessage">{{loading ? '加载更多...' : '没有了'}}</span>
            <i class="el-icon-loading" v-if="isLoading"></i>
          </div>
          <my-message v-for="(message,index) in messagesOutcome" 
            :key="index"
            :message="message"
            :img-type-msg-list="imgTypeMsgList"
            :index="index"
            :isGroup="currentConversation.isGroup"
            @setLittleInfo="lookUser"/>
            <!-- :delFriendStatus="currentConversation.delFriendStatus" -->
          <div class="flag"></div>
      </div>
      <!-- 用户输入 -->
      <div>
          <!-- 表情工具栏 -->
          <!-- <emoji/> -->
        <div class="tools">
            <!-- 表情 -->
            <div class="emoji-container" v-show="showPanel">
                <emoji @emojiClick="onEmojiClick" />
            </div>
            <!-- <img src="../../public/static/image/13.png" alt="发送文件">
            <img src="../../public/static/image/14.png" alt="截图">
            <img src="../../public/static/image/15.png" alt="聊天记录">
            <img src="../../public/static/image/16.png" alt="语音聊天">
            <img src="../../public/static/image/11.png" alt="视频聊天"> -->
            <span class="tool_item emo" ref="emojiTrigger" 
            @click="isShowPanel" title="表情">
            </span>
            <span class="tool_item filee">
                <input
                id="upImg"
                class="fileee"
                type="file"
                title="选择图片"
                accept="image/png, image/jpeg, image/gif, image/jpg"
                @change="uploadImg">
                <!-- @change="uploadImg" -->
            </span>
            <span class="tool_item real-file" >
                <input
                id="upImg"
                class="fileee"
                type="file"
                title="选择文件"
                @change="uploadFile">
            </span>
            <span class="tool_item history" title="聊天记录" @click="setShowHistoryMsg">
            </span>
            <div class="history-msg-container" v-if="showHistoryMsg">
                    <history-msg :current-conversation="currentConversation" 
                    @closeHistoryMsg="setShowHistoryMsg" />
                </div>
            <span class="tool_item voice" title="语音聊天">
                
            </span>
            <span class="tool_item video" title="视频通话" 
            @click="videoCall">
            </span>
        </div>
        <!-- 文本域 -->
        <div class="input-box-container">
            <multifunction-input ref="multiInput" v-model="inputContent" @keydown.native.enter.prevent="doPost"/>
        </div>
        <button class="btn" @click="doPost" >发送(S)</button>
      </div>
  </div>
</template>

<script>
import MyMessage from '@/components/MyMessage.vue'
import Emoji from '@/components/emoji/Emoji.vue'
import MultifunctionInput from '@/components/MultifunctionInput.vue'
import {mapState} from "vuex"
import { SET_UNREAD_NEWS_TYPE_MAP } from '@/store/constants'
import {genGuid, fromatTime} from '@/utils'
import {uploadStatusMap} from '@/const'
import {cloneDeep} from 'lodash'
import HistoryMsg from '@/components/history/HistoryMsg.vue'
import ChatDrawer from './drawer/MyDrawer.vue'
// import user from '@/store/user'
import littleInfoPanel from './LittleInfoPanel.vue'
import videoCall from './VideoCall.vue'

export default {
    name:"ChatWindow",
    data() {
        return {
            showPanel:false,//是否显示表情面板
            inputContent:'',
            messages:[],
            isLoading: false,
            pageIndex: 0,
            pageSize: 15,
            lastEnterTime: Date.now(), // 对方进入该会话的时间
            showHistoryMsg: false,
            datetamp: Date.now(), // 切换群聊重新强制加载群聊详情
            scrollBottom: true,
            loading: true,
            // --------------------
            showHistoryMsg: false,
            timeFlag: true,
            menuTop: 0,//
            menuLeft: 0,
            isLookInfo: false,
            lookInfo: {},
            isVideoCall: false,//视频聊天
            webRTCState: 'apply', // 用于定义进入webRTC通信组件时的状态，如果是apply就发起请求，如果是reply就回复
            coVideoWebRtcType: '', //video/audio
        }
    },
    computed: {
        ...mapState('user',['userInfo','isShowDrawer']),
        ...mapState('app',['isToCoArtBoard','isVideoing','isAudioing']),
        currentConversation: {
            get() {
                return this.$store.state.app.currentConversation
            },
            set() {
                
            }
        },
        talkToWho() {
            if(this.currentConversation.conversationType == 'FRIEND') {
                return this.currentConversation.beiZhu == '' ? this.currentConversation.nickname : this.currentConversation.beiZhu
            } else {
                return this.currentConversation.groupInfo.title + '(' + this.currentConversation.groupInfo.userNum + ')'
            }
        },
        messagesOutcome() {
        // console.log("生成消息列表时的会话信息为:", this.currentConversation)
        // console.log("生成消息列表时的信息为:", this.messages)
            return this.messages.filter(item => {
                return item.roomId === this.currentConversation.roomId
            })
        },
        imgTypeMsgList() {
            return (this.messagesOutcome || []).filter(item => item.messageType === 'img')
        } 
    },
    watch: {
      currentConversation: {
        handler(newVal, oldVal) {
        if (newVal && newVal.id) {
          this.scrollBottom = true
          this.pageIndex = 0
          this.showHistoryMsg = false
          this.setLoading(true)
          this.messageText = ""
          this.messages = []
          this.joinChatRoom()
          this.getRecentMessages()
          this.datetamp = Date.now()
        }
           },deep: true
      },
        messages: {
            handler(newVal, oldVal) {
          if (this.scrollBottom) {
        this.test()
        }
        },deep: true, immediate: true
        }
    },
    components: {
        Emoji,
        MultifunctionInput,
        MyMessage,
        HistoryMsg,
        ChatDrawer,
        littleInfoPanel,
        videoCall
    },
    methods: {
        videoCall() {
            if (this.isToCoArtBoard || this.isVideoing || this.isAudioing) return
            this.$store.dispatch('app/SET_IS_VIDEOING', true)
            this.watchWebRtc({type: 'video'})
            this.coVideoWebRtcType = 'video'
            this.isVideoCall = true
        },
        watchWebRtc(e) {
            const {type} = e
            console.log(type);
            const common = this.generatorMessageCommon()
            const newMessage = {
                ...common,
                message: '',
                messageType: type
            }
            console.log('newMessage');
            console.log(newMessage);
            this.messages = [...this.messages, newMessage]
            this.$socket.emit('sendNewMessage', newMessage)
            this.$store.dispatch('news/SET_LAST_NEWS', {
                type: 'edit',
                res: {
                    roomId: this.currentConversation.roomId,
                    news: newMessage
                }
            })
        },
        closeVideo() {
            this.isVideoCall = false
        },
        lookUser(data) {
            this.lookInfo = data.lookUserInfo
            this.menuLeft = data.pageX
            this.menuTop = data.pageY
            this.isLookInfo = true
        },
        drawerSwitch() {
            if (this.currentConversation.conversationType == 'GROUP') {
                this.getGroupInfo(this.currentConversation.groupId)
            } else {
                console.log('current');
                console.log(this.currentConversation);
                this.$store.commit('user/userBus',[this.currentConversation])
            }
            // this.isShowDrawer = !this.isShowDrawer
            this.$store.commit('user/setDrawer')
        },
        async getGroupInfo(id) {
            const resp =  await this.getRequest(`/chat/getGroupInfo?groupId=${id}`)
            let users = []
            resp.data.groupInfo.forEach(item => {
                users.push(item.userInfo)
            })
            this.$store.commit('user/userBus', users)
        },
        setShowHistoryMsg() {
            this.showHistoryMsg = !this.showHistoryMsg
        },
        isShowPanel() {
            this.showPanel = !this.showPanel;
        },
        onEmojiClick(emojiUrl) {
            let inputBox = this.$refs.multiInput;
            if (document.activeElement !== inputBox) {
                inputBox.requestFocus();
            }
            let imgtag = `<img src="${emojiUrl}" style="width: 20px">`
            console.log(imgtag);
            document.execCommand('insertHTML',false,imgtag);
        },
        generatorMessageCommon() {
            return {
                roomId: this.currentConversation.roomId,
                senderId: this.userInfo.userId,
                senderName: this.userInfo.username,
                senderNickname: this.userInfo.nickname,
                senderAvatar: this.userInfo.avatar,
                time: Date.now(),
                isReadUser: [this.userInfo.userId],
                conversationType: this.currentConversation.conversationType,
            }
        },
        doPost() {
            console.log('输出一下当前会话');
            console.log(this.currentConversation);
            const common = this.generatorMessageCommon()
            let newMessage = {}
            if (this.currentConversation.delFriendStatus != 1) {
                newMessage = {
                ...common,
                message: this.inputContent,
                messageType: 'text',
                }
            } else {
                newMessage = {
                ...common,
                message: this.inputContent,
                messageType: 'text',
                delFriendStatus: 1
                }
            }
            this.messages = [...this.messages,newMessage]
            console.log('message');
            if (this.currentConversation.delFriendStatus != 1) {
                this.$socket.emit('sendNewMessage',newMessage)
                this.$store.dispatch('news/SET_LAST_NEWS',{
                    type: 'edit',
                    res: {
                        roomId: this.currentConversation.roomId,
                        news: newMessage
                    }
                })
            }
            this.test()

            this.inputContent = '';
        },
        joinChatRoom() {
            this.$socket.emit("join", this.currentConversation)
        },
        async getRecentMessages(init = true) {
            const {roomId, conversationType} = this.currentConversation
            if (conversationType === 'FRIEND') {
                const resp = await this.getRequest(`/chat/getRecentSingleMessages?roomId=${roomId}&pageIndex=${this.pageIndex}&pageSize=${this.pageSize}`)
                if (resp.data.recentMessage.length != 0) {
                // reverse() 会改变原数组，并且当前作用域的对象都会改变 
                    resp.data.recentMessage.reverse()
                    this.messages = [...resp.data.recentMessage, ...this.messages]
                    this.pageIndex++
                } else {
                    this.loading = false
                }
            } else if (conversationType == 'GROUP') {
                const resp = await this.getRequest(`/chat/getRecentGroupMessages?roomId=${roomId}&pageIndex=${this.pageIndex}&pageSize=${this.pageSize}`)
                if (resp.data.recentGroupMessages.length != 0) {
                    resp.data.recentGroupMessages.reverse()
                    this.messages = [...resp.data.recentGroupMessages, ...this.messages]
                    this.pageIndex++
                } else {
                    this.loading = false
                }
            }
        },
        setLoading(flag) {
            this.loading = flag
        },
        /**最后进入该会话的时间 */
        setLastEnterTime(time) {
            this.lastEnterTime = time
        },
        loadmessage() {
            this.scrollBottom = false
            this.getRecentMessages(false)
        },
        test() {
            this.$nextTick(() => {
                this.$refs.Area.scrollTop = this.$refs.Area.scrollHeight
            })
        },
        //文件上传
        uploadFileSuccess(resp, file) {
           let filePath = resp.data.filePath 
           let fileType
           console.log("文件类型");
           console.log(file.type);
        //    console.log(file.raw.type);
           if (file.type.indexOf('image') > -1) {
              fileType = 'img'  //图片类型 
           } else {
            fileType = 'file' //文件类型
           }
           const common = this.generatorMessageCommon()
            const newMessage = {
            ...common,
            fileRawName: file.name,
            message: filePath,
            messageType: fileType, // emoji/text/img/file/sys/artboard/audio/video
            }
           this.messages = [...this.messages, newMessage]
           this.$socket.emit("sendNewMessage", newMessage)
           this.$store.dispatch('news/SET_LAST_NEWS', {
               type: 'edit',
               res: {
                   roomId: this.currentConversation.roomId,
                   news: newMessage
               }
           })
        //    this.inputContent = ""
        },
        async uploadFile(e) {
            const file = e.target.files[0]
            const formdata = new FormData() //构造表单
            formdata.append('file',file)
            const resp = await this.postRequest('/chat/uploadFile',formdata)
            console.log("上传文件结果");
            console.log(resp);
            this.uploadFileSuccess(resp,file)
        },

        //图片上传
        getUploadResult(res) {
            const {guid} = res //图片唯一标识
            const msgListClone = cloneDeep(this.messages)
            if (res.status === uploadStatusMap.error) {
                this.$message.error('文件上传失败！')
                return
            }
            if (res.status === uploadStatusMap.complete) {
                console.log('图片上传肯成功');
                const common = this.generatorMessageCommon()
                const newMessage = {
                    ...common,
                    fileRawName: res.data.name,
                    message: res.data.key,
                    messageType: res.data.type, // emoji/text/img/file/sys/artboard/audio/video
                }
                msgListClone.forEach(element => {
                    if (element.guid === guid) {
                        element.uploading = false
                        delete element.uploadPercent
                    }
                })
                this.messages = msgListClone
                this.$socket.emit('sendNewMessage',newMessage)
                this.$store.dispatch('news/SET_LAST_NEWS', {
                    roomId: this.currentConversation.roomId,
                    news: newMessage
                }) 
                // this.inputContent = ''
            }
        },
        getLocalUrl(url, guid) {
            const common = this.generatorMessageCommon()
            const newMessage = {
                ...common,
                uploading: true,
                guid,
                message: url,
                messageType: "img",
            }
            this.messages = [...this.messages, newMessage]
            this.$store.dispatch('news/SET_LAST_NEWS', {
            type: 'edit',
            res: {
                roomId: this.currentConversation.roomId,
                news: newMessage
            }
            })
        },
        createObjURL(file, guid) {
            //先读取本地文件进行展示，然后等待上传完成
            const url = window.URL.createObjectURL(file)
            // console.log("获取文件本地url为：", url)
            this.getLocalUrl(url, guid)
        },
        uploadImg(e) {
            const guid = genGuid()
            console.log(e);
            const file = e.target.files[0]
            const isLt1M = file.size / 1024 / 1024 < 1;
            if (!isLt1M) {
                this.$message.error('图片大小不能超过 1MB！请重新选择图片！')
                return
            }
            // console.log("即将要上传到服务器的文件为：", file)
            typeof this.getLocalUrl === 'function' && this.createObjURL(file, guid)
            const formdata = new FormData() //构造表单
            formdata.append('file',file)
            this.postRequest('/chat/uploadFile',formdata).then(resp => {
                console.log(resp);
                if (resp.code === 200) {
                    this.getUploadResult({
                        status: uploadStatusMap.complete,
                        data: {key: resp.data.filePath, type: 'img', name: file.name},
                        guid
                    })
                } else {
                   this.$message({type: 'error', message: '上传图片失败！'})
                }
            })
        },
        getSysMessage(news) {
            console.log('我被执行了3333');
            this.joinChatRoom()
            // this.$nextTick(() => {
            //     this.messages = [...this.messages,news]
            // })
        }
    },
    sockets: {
        receiveMessage(news) {
            this.messages = [...this.messages,news]
            console.log('收到消息');
            console.log(this.currentConversation);
            console.log(news.roomId);
            if (news.roomId === this.currentConversation.roomId) {
                setTimeout(() => {
                    this.$store.dispatch('news/SET_UNREAD_NEWS', {
                        roomId: news.roomId,
                        count: 0,
                        type: SET_UNREAD_NEWS_TYPE_MAP.clear
                    })
                },0)
            }
        }
    },
    created() {
        this.once = this.$store.state.user.once
        this.test()
    },

    mounted() {
        
        let emojiTrigger = this.$refs.emojiTrigger;
        if (emojiTrigger) {
            emojiTrigger.addEventListener('mousedown',function(e){
                return e.preventDefault();
            })
        }
        document.addEventListener('click', () => {
            this.isLookInfo = false
        })
        this.getRecentMessages()
        this.$bus.$on('sysMessage',(news) => {
            this.getSysMessage(news)
        })
        this.test()
    },
    beforeDestroy() {
        this.$bus.$off('sysMessage')
    },
}
</script>

<style scoped>
/* 顶部 */
    .chatWindow {
        width: 68%;
        position: relative;
    }
    .chatWindow-topInfo {
        height: 45px;
        font-size: 17px;
        padding-left: 22px;
        border-bottom: 1px solid #d6d6d6;
        position: relative;
    }
    /* 抽屉 */
    .drawer-switch {
        /* transform: scale(100px,100px); */
        font-size: 16px;
        position: absolute;
        height: 20px;
        /* line-height: 30px; */
        right: 8px;
        bottom: 20px;
    }
    /* 个人信息面板 */
    .infoPanel-container {
        position: fixed;
        z-index: 1000;
    }
    /* 窗口 */
      .message-container {
        overflow-y: scroll;
        height: 408px;
        border-bottom: 1px solid #d6d6d6;
    }
    /*修改滚动条样式*/
.message-container::-webkit-scrollbar{
  width:5px;
  /* height:5px; */
  display: none;
  /**/
}
.message-container:hover::-webkit-scrollbar {
      display: block;
}
.message-container::-webkit-scrollbar-track{
  background: rgb(239, 239, 239);
  border-radius:2px;
}
.message-container::-webkit-scrollbar-thumb{
  background: #bfbfbf;
  border-radius:10px;
}
.message-container::-webkit-scrollbar-thumb:hover{
  background: #333;
}
.message-container::-webkit-scrollbar-corner{
  background: #179a16;
}
    /* --------------------------------------------- */
    /* 用户输入 */
    /* 工具栏 */
    .tools {
        position: relative;
        display: flex;
        margin-left: 25px;
    }
    .tools .tool_item {
        height: 21px;
        width: 21px;
        margin-top: 12px;
        margin-right: 13px;
        background-size: 21px 21px;
    }
    .emoji-container {
        position: absolute;
        left: -183px;
        bottom: 54px;
    }
    .emo {
        background-image: url(../../public/static/image/12.png);
        /* background-size: 21px 21px; */
    }
    .filee {
        background-image: url(../../public/static/image/13.png);
        /* opacity: 0; */
    }
    .fileee {
        opacity: 0;
        width: 21px;
    }
    .real-file {
        background-image: url(../../public/static/image/13.png);
    }
    .history {
        background-image: url(../../public/static/image/15.png);
    }
    .voice {
        background-image: url(../../public/static/image/16.png);
    }
    .video {
        background-image: url(../../public/static/image/11.png);
    }

    .btn {
        position: absolute;
        border: 0;
        width: 100px;
        height: 22px;
        font-size: 16px;
        color: #07c160;
        background-color: #e9e9e9;
        border-radius: 5px;
        right: 2px;
        bottom: 12px;
    }
    
</style>