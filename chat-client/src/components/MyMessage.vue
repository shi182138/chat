<template>
  <div class="chatWindow-message" >
        <!-- <ul>
        </ul> -->
        <div class="time" ><p>{{timeTips}}</p></div>
        <div v-if="message.delFriendStatus == 1"><span>你还不是他(她)的好友！</span></div>
        <div v-if="message.messageType == 'sys'">{{message.message}}</div>
        <!-- 左边 -->
        <!-- :class="side" -->
        <div class="chatWindow-message-msg" :class="userInfo.userId===message.senderId?'rightSideBox':''" v-if="message.messageType != 'sys'">
        <!-- 头像 -->
            <img class="avatar" :src="message.senderAvatar" @click="lookUserInfo" />
            <div class="messageAndName">
                <!-- :class="side1" -->
                <span v-if="isGroup" style="fontSize:11px;color:#b2b2b2;" :class="userInfo.userId===message.senderId?'rightSideName':''">{{message.senderNickname}}</span>
                <!-- 消息内容 -->
                <div class="messageContent" :class="userInfo.userId===message.senderId?'rightContent':''">
                    <!-- style="borderColor: transparent;border-left-color: #000;order:1" -->
                    <span
                        :is="messageTypesCmp[message.messageType+'cmp']"
                        :message="message"
                        :img-type-msg-list="imgTypeMsgList"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
  import {mapState} from 'vuex'
  import {formatDateToZH} from '@/utils'
  import {messageTypesCmp} from '@/components/messageTypes'

export default {
    props:{
        message:Object,
        index:Number,
        imgTypeMsgList:Array,
        isGroup:Boolean
    },
    data(){
        return {
            
            // --------------------
            messageTypesCmp,
        }
    },
    computed: {
        ...mapState('user', ['userInfo']),
        timeTips() {
            if(this.message.time == 1) {
                return 1
            }
            return formatDateToZH(this.message.time)
        }
    },
    methods: {
        async lookUserInfo(e) {
            const resp = await this.getRequest(`/chat/getUserInfoById?uid=${this.message.senderId}`)
            if (resp.code == 200) {
                this.$emit('setLittleInfo',{
                    lookUserInfo: resp.data.userInfo,
                    pageX: e.pageX,
                    pageY: e.pageY
                })
            }
        }
    },
    mounted() {
    }
}
</script>

<style scoped>
    /* 窗口中间的时间 */
    .time {
        display: flex;
        font-size: 10px;
        color: #fff;
        text-align: center;
        justify-content: center;
        width: 600px;
        /* height: 15px; */
    }
    .time p {
        background-color: #dadada;
        padding: 5px;
    }
    /* 左边消息 */
    .chatWindow-message ul{
        /* padding-left: 39px; */
        list-style: none;
        margin-top: 0px;
        padding-left: 0px ;
        /* padding-left: 28px; */
    }
    .chatWindow-message-msg {
        display: flex;
        margin-left: 28px;
        width: 600px;
    }
    .avatar {
        margin: 0px;
        width: 35px;
        height: 35px;
        margin-top: 10px;
    }
    .messageAndName {
        display: flex;
        flex-direction: column;
    }
    .messageAndName>span:first-child{
        /* height: 15px; */
        margin-left: 10px;
        margin-right: 10px;
        padding-top: 2px;
    }
    /* .messageContent:hover {
        opacity: 0.9;
    } */
    .rightSideBox {
        flex-direction: row-reverse;
        }
    .rightSideName {
        text-align: right;
    }
    .rightContent {
        justify-content: flex-end;
    }
    /* .emoji {
        width: 20px;
    } */
</style>