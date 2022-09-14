<template>
  <header>
      <div class="l-content">
          <el-button plain class="el-icon-menu menuPlus" 
          @click="clickHandler">
          </el-button>
          <!-- 面包屑 -->
          <!-- <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in tabList" 
            :key="item.path"
             :to="{ path: item.path }">{{item.label}}</el-breadcrumb-item>
          </el-breadcrumb> -->
      </div>
      <div class="r-content">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
                <el-image
                style="width: 40px; height: 40px"
                :src="url"
                fit="fill"></el-image>
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-plus">个人中心</el-dropdown-item>
                <el-dropdown-item icon="el-icon-circle-plus">退出登录</el-dropdown-item>
            </el-dropdown-menu>
            </el-dropdown>
      </div>
  </header>
</template>

<script>
import {mapState} from 'vuex'
export default {
    data() {
        return {
            url: require('../../public/static/image/sasdda.png'),
        }
    },
    computed: {
        ...mapState('app',['tabList'])
    },
    methods: {
        clickHandler() {
            this.$bus.$emit('spread')
        }
    }
}
</script>

<style scoped>
header  {
    display: flex;
    justify-content: space-between;
}
.menuPlus {
    width: 30px;
    height: 25px;
    line-height: 0px;
    padding-left: 7px;
    /* margin-top: 10px; */
}
.l-content {
    display: flex;
    align-items: center;
}
.r-content {
    margin-top: 10px;
}
</style>