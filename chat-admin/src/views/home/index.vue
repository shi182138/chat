<template>
  <el-row>
    <el-col :span="24">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <template>
            <el-carousel :interval="1400" type="card" height="500px">
              <el-carousel-item v-for="item in imglist" :key="item">
                <!-- <h3 class="medium">{{ item }}</h3> -->
                <img class="test" :src="item" alt="">
              </el-carousel-item>
            </el-carousel>
          </template>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
export default {
  data() {
    return {
      imglist: [
        require('../../../public/static/image/1.jpg'),
        require('../../../public/static/image/2.png'),
        require('../../../public/static/image/3.png'),
        require('../../../public/static/image/4.jpg'),
        require('../../../public/static/image/5.png'),
        require('../../../public/static/image/6.jpg')
      ]
    }
  }
}
</script>

<style scoped>
.test {
  width: 620px;
  height: 500px;
}
.el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
  }
  
  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }
  
  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }
</style>