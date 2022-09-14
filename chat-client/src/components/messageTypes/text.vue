<template>
  <div class="message-type__text" :class="userInfo.userId===message.senderId?'rightContent':''">
    <span :style="userInfo.userId===message.senderId?rightSide:''" ></span>
    <p :style="userInfo.userId===message.senderId?'backgroundColor:#95ec69':''" v-html="message.message"></p>
  </div>
</template>

<script>

  export default {
    props: ['message'],
    data() {
      return {
        rightSide:{
                borderColor: 'transparent',
                borderLeftColor: '#95ec69',
                order:1
            },
      }
    },
    computed: {
      userInfo() {
            return this.$store.state.user.userInfo
        },
    }
    // methods: {
    //   showMenu(e) {
    //     e.preventDefault()
    //     this.$eventBus.$emit('toggleMsgTextMenu', {
    //       show: true,
    //       data: this.message,
    //       left: e.clientX + 5,
    //       top: e.clientY + 5
    //     })
    //   }
    // },
  }
</script>
<style scoped>
.message-type__text span {
        margin-top: 22px ;
        width: 0px;
        height: 0px;
        border:5px solid transparent;
        border-right-color:  #fff;
    }
    .message-type__text p {
        background-color: #fff;
        max-width: 400px;
        min-width: 35px;
        font-size: 14px;
        margin-top: 9px;
        padding: 9px;
        padding-bottom: 9px;
        word-wrap: break-word;
        border-radius: 5px;
    }
    .rightContent {
        justify-content: flex-end;
    }
        .message-type__text {
        display: flex;
        overflow: hidden;
    }
</style>
