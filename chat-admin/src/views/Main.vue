<template>
    <el-container style="height: 100%">
        <el-aside width="auto" class="fuck">
            <common-aside />
        </el-aside>
        <el-container >
            <el-header style="background: #333">
                <common-header />
            </el-header>
            <el-main >
                <common-tab />
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script>
import CommonAside from '../components/CommonAside.vue'
import CommonHeader from '../components/CommonHeader.vue'
import CommonTab from '../components/CommonTab.vue'
import {formatDate} from '@/utils'
export default {
    name: 'Main',
    components: {
        CommonAside,
        CommonHeader,
        CommonTab
    },
    data() {
        return {

        }
    },
    methods: {
        async getAllGroup() {
            const resp = await this.$http.getAllGroup()
            console.log('respGroup');
            console.log(this.$http);
            this.$store.commit('app/setAllGroup', resp.data.allGroup)
        },
        async getAllUser() {
            const resp = await this.$http.getAllUser()
            console.log('user');
            console.log(resp);
            const temp = resp.data.userList
            temp.forEach(item => {
                let time = new Date(item.signUpTime)
                item.signUpTime = formatDate(time)
                item.placePlus = item.province.name + item.city.name
            })
            this.$store.commit('app/setAllUser', temp)
        },
    },
    created() {
        this.getAllUser()
        this.getAllGroup()
        this.$router.push('/home')
    }
}
</script>

<style scoped>
.fuck ul {
 border: 0px;
}
</style>