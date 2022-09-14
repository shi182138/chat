<template>
<div>
    <div class="search-info-item-box" >
        <img :src="user.avatar" alt="">
        <p>{{user.username}}</p>
    </div>
</div>
</template>

<script>

export default {
    props:{
        user:Object,
    },
    data() {
        return {
            test:[]
        }
    },

    components: {
        // LittleInfoPanel
    }

}
</script>

<style scoped>
.search-info-item-box {
    width: 248px;
    height: 60px;
    display: flex;
}
    .search-info-item-box:hover {
        background-color: #d2d2d2;
    }
    .search-info-item-box img {
        width: 32px;
        height: 32px;
        margin-top: 13px;
        margin-left: 13px;
    }
.test {
    position: absolute;
    right: 24px;
    top: 33%;
}
</style>