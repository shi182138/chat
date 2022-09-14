<template>
  <div class="aside">
      <div class="first">
          <!-- @click="showInfo=true" -->
          <img :src="userInfo.avatar" class="avatar" @click.stop="lookMyself">
      </div>
      <div class="infoPanel-container" v-if="showInfo" 
      :style="{'left': menuLeft + 'px', 'top':  menuTop + 'px'}"
      @click.prevent.stop="">
        <!-- <router-view/> -->
        <little-info-panel  :user="userInfo" />
      </div>
      <div class="menu">
          <div class="menu-1" @click="ShowRecentList">
          <img src="../../public/static/image/68.png" v-show="check1" />
          </div>
          <!-- <img src="../../public/static/image/68.png" v-show="check2" /> -->
          <div class="menu-2" @click="FriendGRroupList">
          <img src="../../public/static/image/24.png" alt="" v-show="check2">
          </div>
          <div class="el-icon-s-unfold test" @click="showsetMenu">
              <div class="settingMenu" v-show="isshowsetMenu">
                  <span></span>
                  <span></span>
                  <span @click="showSelfCenter">设置</span>
              </div>
          </div>
          <!-- <img src="../../public/static/image/24.png" v-show="check2" /> -->
      </div>
      <!-- 个人中心 -->
      <div class="selfCenter-container" v-show="isselfCenter">
          <div class="switch-top"><span @click.stop="close">X</span></div>
      </div>
  </div>
</template>

<script>
import LittleInfoPanel from './LittleInfoPanel.vue'
export default {
    name: 'MyAside',
    data() {
        return {
            showInfo: false,
            check1: false,
            check2: false,
            menuTop: 0,
            menuLeft: 0,
            isshowsetMenu: false,
            isselfCenter: false,
        }
    },
    components: {
        LittleInfoPanel
    },
    computed: {
        userInfo() {
            return this.$store.state.user.userInfo
        }
    },
    methods: {
        // 拖拽效果
        // md(e) {
        //     let i = document.querySelector('#Info');
        //     let x = e.pageX - i.offsetLeft;
        //     let y = e.pageY - i.offsetTop;
        //     document.addEventListener('mousemove',move)
        //     function move(e) {
        //         i.style.left = e.pageX - x + 'px';
        //         i.style.top = e.pageY - y + 'px';
        //     }
        //     document.addEventListener('mouseup',function() {
        //         document.removeEventListener('mousemove',move);
        //     })
        // },
        close() {
            this.isselfCenter = false
        },
        showSelfCenter() {
            this.isselfCenter = true
        },
        showsetMenu() {
            this.isshowsetMenu = !this.isshowsetMenu
        },

        lookMyself(e) {
            this.menuTop = e.pageY
            this.menuLeft = e.pageX
            this.showInfo = true
        },
        // aside图标被选中
        ShowRecentList() {
            this.check1 = true,
            this.check2 = false,
            this.$store.commit('user/ShowRecentList')

        },
        FriendGRroupList() {
            this.check1 = false;
            this.check2 = true;
            this.$store.commit('user/FriendGroupList');

        }
    },
    created(){
           
    },
    mounted() {
        // this.userInfo = JSON.parse(window.sessionStorage.getItem('userInfo'));
        // this.$store.commit('user/SetUserInfo',this.userInfo)
        document.addEventListener('click', () => {
            this.showInfo = false
        })
        this.$bus.$on('fuckicon', () => {
            this.ShowRecentList()
        })
    }
}
</script>

<style scoped>
    .aside {
        background-color: #2e2e2e;
        width: 56px;
        height: 100%;
    }
    .first {
        height: 74px;
        /* background-color: aliceblue; */
    }
    .avatar {
        width: 38px;
        height: 38px;
        margin-top: 36px;
        margin-left: 8px;
    }
    .menu img{
        width: 56px;
        height: 19px;
    }
    .menu-1 {
        width: 56px;
        height: 19px;
        margin-top: 27px;
        background-image: url(../../public/static/image/8.png);
        background-size: 56px 19px;
    }
    .menu-2 {
        width: 56px;
        height: 19px;
        margin-top: 16px;
        background-image: url(../../public/static/image/9.png);
        background-size: 56px 19px;
        /* border: 1px solid ; */
    }
 /* 个人信息 */
 .infoPanel-container {
    position: fixed;
    z-index: 1000;
 }
 /*  */
 .test { 
     position: relative;
     color: #6a6a6a;
     margin-top: 400px;
     margin-left: 12px;
     font-size: 26px;
     width: 38px;
     height: 38px;
 }
 /*  */
 .settingMenu {
     background-color: #2e2e2e;
     width: 136px;
     height: 136px;
     position: absolute;
     bottom: 10px;
     left: 44px;
 }
 .settingMenu span {
     display: block;
     height: 45px;
     font-size: 15px;
     line-height: 45px;
     padding-left: 12px;
     width: 124px;
 }
 .settingMenu span:hover {
     background-color: #303134;
 }
 /* 个人设置/个人中心 */
 .selfCenter-container {
     position: fixed;
     top: 78px;
     left: 550px;
     z-index: 1000;
     width: 552px;
     height: 472px;
     background-color: #f5f5f5;
 }
 /*  */
.switch-top {
  display: flex;
  height: 24.8px;
  justify-content: flex-end;
  margin-bottom: 1.6px;
}
.switch-top span {
  width: 32.8px;
  height: 24.8px;
  line-height: 24.8px;
  text-align: center;
}
.switch-top span:hover {
  background-color: #fb7373;
}
/*  */
</style>