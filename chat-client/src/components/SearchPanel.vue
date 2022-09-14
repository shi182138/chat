<template>
<div>
    <div class="search-panel-box" >
        <search-info-item v-for="item in userList" :key="item.userId" :user='item' @click.native="isShowStrangerInfo($event, item)"  />
    </div>
    <div class="infoPanel-container" v-if="showStrangerInfo" :style="{'left':'596px', 'top':  menuTop + 'px'}">
        <!-- <router-view/> -->
        <little-info-panel  :user="user" @setClose="setclose"/>
    </div>
</div>
</template>

<script>
import SearchInfoItem from '@/components/SearchInfoItem.vue'
import LittleInfoPanel from '@/components/LittleInfoPanel.vue'
import {mapState} from 'vuex'
export default {
    components: {
        SearchInfoItem,
        LittleInfoPanel,
    },
    data() {
        return {
            userList:[],
            user:{},
            showStrangerInfo: false,
            menuTop: 0,
        }
    },
    computed: {
        // ...mapState('user',['showStrangerInfo'])
    },
    methods: {
        setclose() {
            this.showStrangerInfo = false
        },
        test1(t1) {
            this.userList = t1;
        },
        isShowStrangerInfo(e, item){
            // this.$store.commit('user/IsShowStrangerInfo')
            // this.$router.push('/main/strangerInfo')
            this.menuTop = e.pageY
            this.showStrangerInfo = true
            this.user = item;
        }
    },
    mounted() {
        console.log('panel被挂载')
        this.$bus.$on('test',this.test1)
    },
    beforeDestroy(){
        this.$bus.$off('test')
    }
}
</script>

<style scoped>
.search-panel-box {
    width: 248px;
    height: 100%;
    display: flex;
    flex-direction: column;
}
.infoPanel-container {
    /* position: absolute;
    right: 34%;
    top: 33%; */
    position: fixed;
    z-index: 1000;
}
</style>