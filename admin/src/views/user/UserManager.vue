<template>
  <div>
    <common-table :tableLable="tableLable" :tableData="tabelData"/>
  </div>
</template>

<script>
import CommonTable from '@/components/CommonTable.vue'
export default {
  components: {
    CommonTable
  },
  data() {
    return {
      tableLable: [
        {
          prop: 'code',
          name: 'code'
        },
        {
          prop: 'username',
          name: '账号'
        },
        {
          prop: 'nickname',
          name: '昵称'
        },
        {
          prop: 'age',
          name: '年龄'
        },
        {
          prop: 'sex',
          name: '性别'
        },
        {
          prop: 'placePlus',
          name: '所在地'
        },
        {
          prop: 'signUpTime',
          name: '注册时间'
        },
      ]
    }
  },
  computed: {
    tabelData() {
      return this.$store.state.app.allUser
    }
  }
}
</script>

<style>

</style>