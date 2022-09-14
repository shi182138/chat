<template>
  <div>
      <el-table
        :data="dataList"
        style="width: 100%"
        @cell-click="test"
        :formatter="tableFormat">
        <el-table-column v-for="item in tableLable"
        :key="item.prop"
            :prop="item.prop"
            :label="item.name"
            width="180">
        </el-table-column>
        <el-table-column 
            prop="status"
            label="状态"
            width="180">
            <template slot-scope="scope" >
               <el-button
                v-if="true"
                    size="mini"
                    @click="handleEdit(scope.$index, scope.row)">正常</el-button>
                <el-button
                    v-else
                    size="mini"
                    type="danger"
                    @click="handleDelete(scope.$index, scope.row)">已冻结</el-button>
            </template>
        </el-table-column>
    </el-table>
    <div class="block">
        <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="5"
        layout="total, prev, pager, next, jumper"
        :total="total">
        </el-pagination>
  </div>
  </div>
</template>

<script>
export default {
    props: {
        tableData:Array,
        tableLable:Array,
    },
    data() {
        return {
            value: true,
            currentPage: 1,
            obj: {},
            objKey: 0,
            dataList:[],
            total: 0,
            yes:true,
            no:false,
            test: [
                {
                    status: 0
                }
            ]
        }
    },
    watch: {
        tableData: {
            handler(val) {
                this.total = val.length
                if (val.length > 5) {
                    for (let i = 0; i<Math.ceil(val.length / 5); i++) {
                        this.obj[i] = val.slice(5*i, 5*(i+1))
                    }
                    this.dataList = this.obj[this.objKey]
                } else {
                    this.dataList = this.tableData
                }
            },immediate:true
        }
    },
    methods: {
        test(row, column, cell, event) {
            console.log('测试一下');
            console.log(row);
            console.log(column);
            console.log(cell);
            console.log(event);
        },
        freeze(index) {
            console.log('冻结');
            console.log(index);
        },
      handleSizeChange(val) {
        console.log(`每页 ${val} 条`);
      },
      handleCurrentChange(val) {
        this.dataList = this.obj[(val - 1)]
      }
    },
}
</script>

<style scoped>
.block {
    position: fixed;
    margin-top: 30px;
    right: 50px;
    bottom: 80px;
}
</style>