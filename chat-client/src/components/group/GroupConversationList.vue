<template>
  <div>
      <div class="groupItem-container">
          <span class="groupTag">群聊</span>
          <!--  -->
          <group-item v-for="conversation in groupList" :key="conversation.groupId"
          :conversationInfo="conversation.groupInfo"
          @click.native="showGroupInfo(conversation)"
          @contextmenu.prevent.stop.native="showMenu"
          />
      </div>
        <!-- 菜单 -->
      <div class="menu-container" v-if="isShowMenu" :style="{'left': menuLeft + 'px', 'top':  menuTop + 'px'}">
          <!-- <Menu @hiddenMenu="hiddenMenu" @showPop="isShowDelPop"></Menu> -->
          <group-menu @hiddenMenu="hiddenMenu" />
      </div>

  </div>
</template>

<script>
import groupItem from './groupItem.vue'
import groupMenu from './groupMenu.vue'
export default {
    components: {
        groupItem,
        groupMenu,
    },
    data() {
        return {
            isShowMenu: false,
            menuTop: 0,
            menuLeft: 0,
        }
    },
    computed: {
        groupList() {
            return this.$store.state.app.allGroups.filter(item => item.manager != 0)
        },
    },
    methods: {
        showGroupInfo(conversation) {
            this.$store.dispatch('app/SET_CURRENT_CONVERSATION', conversation)
            this.getGroupInfo(conversation.groupId)
            this.$store.commit('user/IsShowGroupInfo')
        },
        getGroupInfo(id) {
            this.getRequest(`/chat/getGroupInfo?groupId=${id}`).then(resp => {
                console.log('resp');
                console.log(resp);
                this.$store.commit('user/userBus',resp.data.groupInfo)
            })
        },
        showMenu(e) {
            console.log('我被执行了');
            this.isShowMenu = true
            this.menuTop = e.pageY
            this.menuLeft = e.pageX
        },
        hiddenMenu() {
            this.isShowMenu = false
        },
    },
    mounted() {
        console.log('grouplist');
        console.log(this.groupList);
        document.addEventListener('click', () => {
        this.isShowMenu = false
      })
      document.addEventListener('mousedown', (e) => {
        //   console.log(e);
        const {button} = e
        if (button === 2) {
          this.isShowMenu = false
        }
      })
    }
}
</script>

<style scoped>
.groupItem-container span {
    color: #999;
    font-size: 10px;
    margin-left: 12px;
    margin-top: 20px;
    margin-bottom: 5px;
}
.menu-container {
    position: fixed;
    z-index: 1000;
    height: 50px;
}
</style>