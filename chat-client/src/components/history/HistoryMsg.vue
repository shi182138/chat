<template>
  <div>
      <div class="historyMsgBox">
          <div class="top">
              <!-- <div class="historyMsgBox-info"></div> -->
              <!-- <div class="historyMsgBox-search"><input type="text"></div> -->
              <p>{{groupOrFriend}}</p>
              <div class="close"><span @click="close" >X</span></div>
              <!-- 搜索框 -->
              <input class="search-input" type="text" placeholder="搜索" v-model="searchWord">
              <!-- 导航标签 -->
              <div class="navigation">
                  <div v-for="(item, index) in list" :class="{actived:active==index}" :key="index" @click="change(index)">{{item}}</div>
              </div>
          </div>
          <div class="msg-list-container" v-show="typeIndex === 0">
              <msg-item v-for="item in filterMsgList" :key="item.time"
              :msg-item="item"
              :img-type-msg-list="imgTypeMsgList" />
          </div>
          <div class="msg-list-container" v-show="typeIndex === 1">
              <msg-item v-for="item in flieTypeMsgList" :key="item.time"
              :msg-item="item"
              :img-type-msg-list="imgTypeMsgList" />
          </div>
          <div class="msg-list-container" v-show="typeIndex === 2">
              <msg-item v-for="item in imgTypeMsgList" :key="item.time"
              :msg-item="item"
              :img-type-msg-list="imgTypeMsgList" />
          </div>
          <div></div>
      </div>
  </div>
</template>

<script>
import msgItem from '@/components/history/HistoryMsgItem.vue'

const typeTextToValue = {
    '全部': 'all',
    '图片': 'img',
    '文件': 'file'
}
export default {
    props: ['currentConversation'],
    data() {
        return {
            active: 0,
            list: ['全部','文件', '图片与视频', '连接'],
            //---------------------------
            msgList: [],
            searchType: '全部',
            searchWord: '',
            searchDate: '',
            pageIndex: 1,
            pageSize: 10,
            total: 0,
            isLoading: false,
            typeIndex: 0
        }
    },
    computed: {
        groupOrFriend() {
            if (this.currentConversation.conversationType == 'FRIEND') {
               return this.currentConversation.username
            } else {
                return this.currentConversation.groupInfo.title
            }
        },
        imgTypeMsgList() {
            return (this.msgList || []).filter(item => item.messageType === 'img')
        },
        flieTypeMsgList() {
            return (this.msgList || []).filter(item => item.messageType === 'file')
        },
        filterMsgList() {
            return this.msgList.filter( item => {
                return item.message.indexOf(this.searchWord) !== -1
            })
        }
    },
    methods: {
        change(index) {
            this.active = index
            this.typeIndex = index
        },
        close() {
            this.$emit('closeHistoryMsg')
        },
        async getHistoryMsg() {
            const params = {
                roomId: this.currentConversation.roomId,
                type: typeTextToValue[this.searchType],
                query: this.searchWord,
                date: this.searchDate,
                pageIndex: this.pageIndex - 1,
                pageSize: this.pageSize
            }
            // const fetch = await this.currentConversation.isGroup ? this.postRequest('/chat/historyMessage',params) : this.postRequest('/chat/historyMessage',params)
            // console.log(fetch);
            // fetch(params).then(resp => {
            //     console.log("输出一下resp");
            //     console.log(resp);
            //     this.msgList = resp.data.data.msgList
            //     this.total = resp.data.data.total
            // })
            let resp = {}
            if (this.currentConversation.isGroup) {
                resp = await this.getRequest(`/chat/groupHistoryMsg?roomId=${this.currentConversation.roomId}`)
            } else {
                resp = await this.getRequest(`/chat/getHistoryMsg?roomId=${this.currentConversation.roomId}`)
            }
            
            this.msgList = resp.data.msgList
            // this.total = fetch.data.total
        }
    },
    components: {
        msgItem,
    },
    mounted() {
        console.log('当前会话');
        console.log(this.currentConversation);
        this.getHistoryMsg()
    }
}
</script>

<style scoped>
.historyMsgBox {
    background-color: #f5f5f5;
    height: 680px;
    width: 548px;
    position: absolute;
    bottom: -136px;
    left: 0px;
    box-shadow: 0px 0px 19px -15px black;
    z-index: 1000;
}
.top {
    /* display: flex;
    flex-direction: column; */
    width: 548px;
    height: 140px;
    border-bottom: 1px solid #ececec;
    position: relative;
}
    .top:first-child P{
        margin: 0px;
        font-size: 16px;
        margin-left: 12px;
        padding-top: 8px; 
        color: #999;
    }
    /* .close {
        
    } */
    .close span {
        position: absolute;
        top: 0px;
        right: 0px;
        height: 24px;
        line-height: 24px;
        width: 32px;
        text-align: center;
        font-size: 10px;
    }
    .close span:hover {
        background-color: red;
        color: #f5f5f5;
    }
.search-input {
    border: 0;
    outline: none;
    border-radius: 6px;
    height: 20px;
    background-color: #e2e2e2;
    font-size: 12px;
    margin-left: 40px;
    margin-top: 24px;
    width: 468px;
    margin-bottom: 24px;
}
.search-input:focus {
    background-color: #fff;
}
.search-input:focus::placeholder {
    opacity: 0;
}

.navigation {
    width: 308px;
    /* background-color: black; */
    margin: 0 auto;
    display: flex;
    font-size: 16px;
}
    .navigation div {
        margin-right: 33px;
        padding-bottom: 5px;
    }
    
    .actived {
        color: #1aad19;
        border-bottom: 2px solid #1aad19;
    }

.msg-list-container {
    overflow-y: scroll;
    max-height: 544px;
}

.msg-list-container::-webkit-scrollbar{
  width:5px;
  /* height:5px; */
  display: none;
  /**/
}
.msg-list-container:hover::-webkit-scrollbar {
      display: block;
}
.msg-list-container::-webkit-scrollbar-track{
  background: rgb(239, 239, 239);
  border-radius:2px;
}
.msg-list-container::-webkit-scrollbar-thumb{
  background: #bfbfbf;
  border-radius:10px;
}
.msg-list-container::-webkit-scrollbar-thumb:hover{
  background: #333;
}
.msg-list-container::-webkit-scrollbar-corner{
  background: #179a16;
}
</style>