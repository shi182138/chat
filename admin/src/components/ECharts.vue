<template>
  <div :id="eid"></div>
</template>

<script>
import * as echarts from 'echarts'
export default {
    props:{
        isAxisChart: {
            type: Boolean,
            default: true
        },
        chartData: {
            type: Object,
            default() {
                return {
                    xData: [],
                    series
                }
            }
        }
    },
    data() {
        return {
            eid: '',
            axisOption: {
                legend: {},
                tooltip: {
                    // trigger: 'axis'
                },
                xAxis: {
                    type: 'category',
                    axisTick: {
                        alignWithLabel: true
                    },
                    data:[],
                },
                yAxis: {
                    type: 'value'
                },
                series: []
            },
            normalOptin: {
                legend: {},
                tooltip: {
                    // trigger:"item",
                },
                series: []
            },
            echart: null,
        }
    },
    computed: {
        options() {
            return this.isAxisChart? this.axisOption : this.normalOptin
        }
    },
    watch: {
        chartData: {
            handler() {
                this.initChart()
            },deep: true,immediate:true
        }
    },
    methods: {
        // 生成唯一uuid做为唯一标识符
        uuid() {
        return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
            var r = (Math.random() * 16) | 0,
            v = c == "x" ? r : (r & 0x3) | 0x8;
            return v.toString(16);
        });
        },
        initChart() {
            this.initChartData()
            
            this.$nextTick(() => {
                this.echart =  echarts.init(document.getElementById(this.eid))
                this.echart.setOption(this.options)
            })
        },
        initChartData() {
            if (this.isAxisChart) {
                this.axisOption.xAxis.data = this.chartData.xData
                this.axisOption.series = this.chartData.series
            } else {
                this.normalOptin.series = this.chartData.series
            }
        }
    },
    created() {
        this.eid = this.uuid()
    }

}
</script>

<style scoped>

</style>