<template>
    <div class="slaver">
        <div class="little-info-panel-box">
            <div class="first">
                <p>{{user.username}}</p>
                <img :src="user.avatar" alt="">
            </div>
            <div class="second">
                <div><span>地区</span><p>{{user.province.name}}   {{user.city.name}}</p></div>
            </div>
            <div class="third">
                <!-- sendApply -->
                <div><span @click="isShowAddPanel" class="el-icon-s-promotion sendAdd" title="添加好友"></span></div>
            </div>
            <!--  -->
            <div class="addFriendPanel-container" v-if="showAddPanel">
                <add-friend-panel :user="user" @close="closeAddPanel" />
            </div>
        </div>
    </div>
</template>

<script>

import addFriendPanel from '@/components/add/AddFriendPanel.vue'
export default {
    props:{
        user:Object,
    },
    data() {
        return {
            showAddPanel: false,
        }
    },
    
    components: {
        addFriendPanel,
    },
    methods: {
        isShowAddPanel() {
            this.showAddPanel = true
        },
        closeAddPanel() {
            this.showAddPanel = false
            this.$emit('setClose')
        },
        
    }

}
</script>

<style scoped>
.slaver {
    display: flex;
    justify-content: center;
    background-color: #fff;
    width: 296px;
    height: 236px;
    box-shadow: 0px 0px 10px #dcd9d7;
}
.little-info-panel-box {
    display: flex;
    flex-direction: column;
}
.first {
    margin-top: 30px;
    height: 84px;
    width: 232px;
    border-bottom: 1px solid #f4f4f4;
    display: flex;
    justify-content: space-between;
}
    .first img {
        width: 60px;
        height: 60px;
    }
    .first p {
        margin-top: 5px;
    }
.second {
    padding-top: 16px;
    font-size: 12.8px;
    height: 70px;
}
.second div {
    display: flex;
}
.second span {
    color: #999;
    margin-right: 20px;
}
.second p {
    margin: 0;

}

.third {
    display: flex;
    justify-content: flex-end;
}
.third div {
    width: 24px;
    height: 24px;
}
</style>