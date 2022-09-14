<template>
  <div>
      <div class="msg-item-box">
          <img class="avatar" :src="msgItem.senderAvatar" alt="">
          <div class="right-content">
              <div class="top-info">
                  <span>
                      {{msgItem.senderName}}
                  </span>
                  <span>
                      {{timeTips}}
                  </span>
              </div>
              <div class="msg-content">
                  <p ref="fuckyoum" v-if="msgItem.messageType === 'text'"></p>
                  <img v-else-if="msgItem.messageType === 'img'" :src="msgItem.message" class="imgMessage">
                  <div v-else>文件</div>
              </div>
          </div>
      </div>
  </div>
</template>

<script>
import {formatDateToZH} from '@/utils'

export default {
    props:['msgItem','imgTypeMsgList'],
    computed: {
        timeTips() {
            return formatDateToZH(this.msgItem.time)
        },
    },
    watch: {
        msgItem: {
            handler(val) {
                if (val.messageType == 'text') {
                    console.log('测试');
                        this.$nextTick(() => {
                            this.$refs.fuckyoum.innerHTML = val.message
                        })
                    }
            },deep:true,immediate:true
        }
    },
}
</script>

<style scoped>
.msg-item-box {
    display: flex;
}
    .avatar {
        width: 34px;
        height: 34px;
        margin-top: 24px;
        margin-left: 40px;
        margin-right: 10px;
    }

.right-content{
    /* display: flex;
    flex-direction: column; */
    margin-top: 20px;
    border-bottom: 1px solid #ececec;
}
.top-info {
    display: flex;
    justify-content: space-between;
    width: 428px;
    font-size: 12px;
    color: #999;
    margin-bottom: 10px;
}
.msg-content {
    max-height: 120px;
    padding-bottom: 20px;
}
.msg-content p {
    margin: 0;  
    word-wrap: break-word;
	word-break: break-all;
    max-width: 428px;;
}

.imgMessage {
    max-height: 100px;
    max-width: 124px;
}


</style>