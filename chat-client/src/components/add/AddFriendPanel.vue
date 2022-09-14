<template>
  <div>
      <div class="container">
          <div class="top"><p>申请添加朋友</p></div>
            <div class="base basePlus">
                  <span>发送添加朋友请求</span>
                  <input type="text" v-model="additionMessage">
              </div>
              <div class="base"><span>备注名</span>
                  <input type="text" name="" id="">
              </div>
              <div class="base"><span>标签</span>
                  <input type="text" name="" id="">
              </div>
              <!-- 按钮 -->
              <div class="buttom-box">
                  <button class="comfirm" @click="sendApply">确定</button>
                  <button class="cancel" @click="canelAdd">取消</button>
              </div>
      </div>
  </div>
</template>

<script>
import {mapState} from 'vuex'

export default {
    props:{
        user:Object
    },
    data() {
        return {
            additionMessage: ``,
        }
    },
    computed: {
        ...mapState('app',['sysUsers']),
        ...mapState('user', ['userInfo']),
    },
    methods: {
        // send() {
        //     this.$emit('sendApply')
        // },
        canelAdd() {
            this.$emit('close')
        },

        async sendApply() {
            // console.log(this.sysUsers)
            const validateSysUser = this.sysUsers.filter(item => item.code === '111111')[0]
            const val = {
                roomId:validateSysUser.sid + '-' + this.user.userId,
                senderId: this.userInfo.userId,
                senderName: this.userInfo.username,
                senderNickname: this.userInfo.nickname,
                senderAvatar: this.userInfo.avatar,
                receiverId: this.user.userId,
                time: new Date(),
                additionMessage: this.additionMessage,
                status: 0,
                validateType: 0
            }
            // console.log('--------------->')
            // console.log(val)
            await this.getRequest('/chat/getValidateMessage',{
                roomId: validateSysUser.sid + '-' + this.user.userId,
                status: 0,  //未处理状态
                validateType: 0
            }).then(resp => {
                console.log('validateMessage----------->')
                console.log(resp)
                let validateMessage = resp.data.validateMessage
                // validateMessage == 'null' && validateMessage.roomId == 'null'
                if (null) {
                    this.$message({type: 'warning', message: '您已经向' + this.user.username + '发送好友请求了，请勿重复操作！'})
                } else {
                    this.$message('发送成功');
                    this.$socket.emit('sendValidateMessage',val)
                    // 关闭当前面板
                    this.canelAdd()
                }        
            })
        }
    },
    mounted() {
        this.additionMessage = `我是${this.$store.state.user.userInfo.username}`
    }
}
</script>

<style scoped>
.container {
    width: 360px;
    height: 616px;
    background-color: #ededed;
    position: absolute;
    top: -190px;
    left: 0px;
    z-index: 1000;
}

.top {
    text-align: center;
    border-bottom: 1px solid #e2e2e2;
    font-size: 13px;
}
.second {
    margin: 0 auto;
}
.base {
    height: 72px;
    margin-top: 16px;
    margin-left: 24px;
}
.basePlus {
    margin-top: 12px;
}
.base span {
    font-size: 12px;
    color: #9e9e9e;
}
.base input {
    display: block;
    border: 1px solid #e3e4e5;
    background-color: #fff;
    margin-top: 10px;
    width: 295px;
    height: 36px;
    padding-left: 12px;
    outline: none;
    border-radius: 6px;
}
.buttom-box{
    margin-top: 200px;
}
.comfirm {
    background-color: #07c160;
    color: #fff;
    margin-left: 65px;
    margin-right: 25px;
    border: 0;
    border-radius: 5px;
    width: 100px;
    height: 32px;
}

.cancel {
    background-color: #e2e2e2;
    color: #07c160;
    border: 0;
    border-radius: 5px;
    width: 100px;
    height: 32px;
}
</style>