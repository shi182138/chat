<template>
  <div>
      <div class="top"><p>新的朋友</p></div>
      <!-- v-for="item in outComeList" :key="item.id" -->
      <div class="addItem-container" >
        <add-item v-for="item in outComeList" :key="item.id" :addItem="item"  @agreeValidate="agreeValidate" />
      </div>
    </div>
</template>

<script>
import addItem from '@/components/add/AddItem.vue'
export default {
  components: {
    addItem,
  },
  data() {
    return {
      validateNewsList:[],
      filterStatus: 0, // -1全部，0未处理，1已同意，2已拒绝
      filterType: 0, // 0人，1群
    }
  },

  computed: {
    userInfo(){
      return this.$store.state.user.userInfo
    },
    outComeList() {
        console.log("验证消息列表为：", this.validateNewsList)
        return this.validateNewsList.filter(item => {
          if (this.filterStatus === -1) {
            return item.validateType === this.filterType
          } else {
            console.log('输出一下');
            console.log((item.status === this.filterStatus && item.validateType === this.filterType));
            // item.status === this.filterStatus && item.validateType === this.filterType
            return this.validateNewsList
          }
        })
      },
  },
  methods: {
    async fetchMyValidateNews() {
      // 拿的到userInfo
      const userId = this.userInfo.userId
      const resp = await this.getRequest(`/chat/getMyValidateMessageList?userId=${userId}`)
      let validateMessageList = resp.data.myValidateMessageList
      console.log(resp)
      if (resp.code === 200 ) {
       this.validateNewsList = validateMessageList
       console.log("----------------------->")
       console.log(this.validateNewsList)
      }
    },
    //同意好友或群聊请求
    async agreeValidate(item) {
      if (item.validateType === 0) {
        this.$socket.emit('sendAgreeFriendValidate',item)
        this.$bus.$emit('getMyFriends')
        console.log(111111111)
      } else {
        this.$socket.emit('sendAgreeGroupValidate',item)
        this.$bus.$emit('getMyGroup')
      }
      this.$store.dispatch('app/SET_AGREE_FRIEND_VALIDATE',true)
      // this.isAdding = true
      setTimeout(() => {
        // this.$emit('changeValidateNewsStatus', item, 1)   待解放
        this.$message({
          message: '添加成功',
          type: 'success'
        });
      })
    }
  },
  sockets: {
    receiveValidateMessage(data) {
      //这里是停留在系统消息页面时，组装刚添加的验证消息
        // console.log('在system页面接收新的验证消息：', data)
      thsi.validateNewsList = [...this.validateNewsList,data]
    }
  },
  mounted() {
    this.fetchMyValidateNews()
  }
}
</script>

<style scoped>
  .top {
    padding-left: 30px;
    padding-top: 25px;
    border-bottom: 1px solid #e7e7e7;
    width: 624px;
  }
  .top p {
    margin: 0px;
    margin-bottom: 16px;
  }
</style>