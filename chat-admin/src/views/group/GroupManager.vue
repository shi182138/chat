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
          name: '群号'
        },
        {
          prop: 'title',
          name: '群名称'
        },
        {
          prop: 'userNum',
          name: '群人数'
        },
        {
          prop: 'createDate',
          name: '创建时间'
        },
        {
          prop: 'holderName',
          name: '群主账号'
        },
        {
          prop: 'desc',
          name: '群描述'
        },
      ]
    }
  },
  computed: {
    tabelData() {
      return this.$store.state.app.allGroup
    }
  }
}
</script>

<style scoped>

</style>