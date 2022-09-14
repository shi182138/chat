<template>
<el-menu default-active="1-4-1" class="el-menu-vertical-demo" @open="handleOpen" 
@close="handleClose" 
:collapse="isCollapse" 
background-color="#545c64"
text-color="#fff"
active-text-color="#ffd04b"
style="height: 100%;margin-right:0px">
    <div class="title">chat-admin</div>
    <!-- :index="item.path"  -->
    <el-menu-item v-for="(item) in noChildren" 
    :key="item.path"
    :index="item.path"
    @click="clickMenu(item)">
        <i :class="'el-icon-'+item.icon"></i>
        <span slot="title">{{item.label}}</span>
    </el-menu-item >
    <el-submenu v-for="(item, index) in hasChildren" :index="index+''" :key="index" >
        <template slot="title">
        <i :class="'el-icon-'+item.icon"></i>
        <span slot="title">{{item.label}}</span>
        </template>
        <el-menu-item-group v-for="(subItem) in item.children" 
        :key="subItem.path"
        :index="subItem.path"
        @click.native="clickHandler(subItem)">
        <el-menu-item :index="subItem.path">{{subItem.label}}</el-menu-item>
        </el-menu-item-group>
    </el-submenu>
</el-menu>
</template>
<style scoped>
  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 200px;
    min-height: 400px;
  }
  .title {
    color: #fff;
    overflow: hidden;
    height: 60px;
    line-height: 60px;
    text-align: center;
  }
</style>

<script>
  export default {
    data() {
      return {
        isCollapse: false,
        menu: [
            {
                path: '/home',
                name: 'home',
                label: '首页',
                icon: 's-home',
                url: 'Home/Home'
            },
            {
                label: '普通用户',
                icon: 'user',
                children: [
                    {
                        path: '/statistics',
                        name: 'statistics',
                        label: '用户统计',
                        icon: '',
                        url: ''
                    },
                    {
                        path: '/userManager',
                        name: 'userManager',
                        label: '用户管理',
                        icon: '',
                        url: ''
                    }
                ]
            },
            {
                label: '群组',
                icon: 'copy-document',
                children: [
                    {
                        path: '/groupManager',
                        name: 'groupManager',
                        label: '组管理',
                        icon: '',
                        url: ''
                    },
                ]
            }
        ]
      };
    },
    computed: {
        noChildren() {
            return this.menu.filter(item => !item.children)
        },
        hasChildren() {
            return this.menu.filter(item => item.children)
        }
    },
    methods: {
      handleOpen(key, keyPath) {
        console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        console.log(key, keyPath);
      },
      clickMenu(item) {
          this.$router.push({
              name: item.name
          })
          // this.$store.commit('app/selectmenu', )
      },
      clickHandler(subItem) {
        this.$router.push({
          name: subItem.name
        })
        this.$store.commit('app/selectmenu', subItem)
      }
    },
    mounted() {
      this.$bus.$on('spread', () => {
        this.isCollapse = !this.isCollapse
      })
    },
    beforeDestroy() {
      this.$bus.$off('spread')
    }
  }
</script>