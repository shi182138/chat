<template>
  <div>
    <el-card class="box-card">
      男女比例
      <e-chart :chartData="sexData" :isAxisChart="false" 
            style="height: 280px"/>
    </el-card>
    <el-card class="box-card">
      注册趋势
      <e-chart :chartData="signUpTimeData"  
            style="height: 260px"/>
    </el-card>
  </div>
</template>

<script>
import eChart from '@/components/ECharts.vue'
export default {
  components: {
    eChart
  },
  data() {
    return {
    }
  },
  computed: {
    userList() {
      return this.$store.state.app.allUser
    },
    sexData() {
      let female = 0
      let male = 0
      let unkonw = 0
      let temp = this.userList
      temp.forEach(item => {
        if (item.sex == 0) {
          male++
        } else if (item.sex == 1) {
          female++
        } else {
          unkonw++
        }
      })
      return {
        title: {

        },
        series: [
          {
            type: 'pie',
            data: [
              {
                value: female,
                name: '女'
              },
              {
                value: male,
                name: '男'
              },
              {
                value: unkonw,
                name: '未知'
              },
            ],
          }
        ]
      }
    },
    signUpTimeData() {
      let temp = [0,0,0,0,0,0,0,0,0,0,0,0]
      this.userList.forEach(item => {
        let date = new Date(item.signUpTime).getMonth()
        temp[date] += 1
      })
      return {
        xData: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
        series: [
          {
            data: temp,
            type: 'line'
          }
        ]
      }

    }
  },
mounted() {
  
}
}
</script>

<style>

</style>