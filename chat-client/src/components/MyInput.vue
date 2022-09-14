<template>
  <!--搜索框部分-->
    <div class="container">
            <!--输入框所在盒子-->
        <div class="input">
            <input type="text" placeholder="搜索" @blur="blurIcon" @focus="focusIcon" v-model="searchKey"/>
            
        </div>
        <button />
        <img src="../../public/static/image/21.png" class="icon1" v-show="show1"/>
        <img src="../../public/static/image/22.png" class="icon2" v-show="show2"/>
        <img src="../../public/static/image/23.png" class="icon3"/>
    </div>
</template>

<script>
export default {
    name:'MyInput',
    data() {
        return {
            messgaeSearchName:'',
            show1:true,
            show2:false,
            searchKey:'',
        }
    },
    watch:{
        searchKey(val){
            let param = {
                searchContent:val
            }
            this.fetchUser(param)
        }
    },
    methods:{
        blurIcon() {
            this.show1 = true;
            this.show2 = false;
            // this.searchKey = ''
        },
        focusIcon() {
            this.show1 = false;
            this.show2 = true;
            this.$store.commit('user/IsShowSearchPanel')
        },
        async fetchUser(param) {
            if (param.searchContent) {
                const resp = await this.postRequest('/chat/searchUserList',param)
                console.log(resp.data.userList)
                this.$bus.$emit('test',resp.data.userList)
            }else{
                this.$bus.$emit('test',[])
            }
        }
    }
}
</script>

<style scoped>
    .container {
        height: 62px;
        display: flex;
        border-bottom: 1px solid #d6d6d6;
        border-right: 1px solid #d6d6d6;
        background-color: #f7f7f7;
        position: relative;
        /* z-index: 1; */
    }
    input {
        border: 0;
        border-radius: 6px;
        outline: none;
        font-size: 12px;
        padding-left: 24px;
        margin-top:25px ;
        margin-left: 13px;
        margin-right: 12px;
        height: 20px;
        background-color: #e2e2e2;
    }
    input:focus {
        background-color: #fff;
    }
    input:focus::placeholder {
        opacity: 0;
    }
    .icon1,.icon2 {
        position:absolute;
        width: 16px;
        height: 15px;
        top:27px;
        left: 15px;
    }
    .icon3 {
        position: absolute;
        width: 16px;
        height: 16px;
        top: 26px;
        right: 24px;
    }
    button {
        border: 0;
        width: 22px;
        height: 22px;
        background-color: #e2e2e2;
        padding: 0px;
        margin-top: 25px;
        border-radius: 5px;
    }
</style>