<template>
  <div class="picture-preview-com">
    <div class="switch-top"><span @click.stop="close">X</span></div>
    <!-- 上一张，下一张 -->
    <div class="previous"
    @mouseenter="enterLeft"
    @mouseleave="leaveLeft"
    @click.stop="previous"
    :class="{activedTow:isshowForesee}"
    >
      <span v-show="showArrowsLeft">&lt;</span>
    </div>
    <div class="next"
    @mouseenter="enterRight"
    @mouseleave="leaveRight"
    @click.stop="next"
    :class="{activedTow:isshowForesee}">
      <span v-show="showArrowsRight">&gt;</span>
    </div>
    <!-- 图片展示 -->
    <div class="img-wrapper" v-if="!error" :class="{activedTow:isshowForesee}">
      <img
        alt="图片地址"
        class="img-content"
        ref="imgResource"
        :src="img_url"
        :style="`transform:scale(${scale}) rotateZ(${rotate}deg) translate(${translateX}px,${translateY}px)`"
        @wheel="handlerWheelScale"
        @mousedown.prevent="handlerMouseStar"
        @mousemove.prevent="handlerMouseMove"
        @mouseup.prevent="handlerMouseEnd"
        >
    </div>
    <!-- 全部图片预览 -->
    <div class="foresee" v-if="isshowForesee">
      <img v-for="(item, index) in imgList" :key="index" :src="item" :class="{actived:active==index}"
      @click="selected(index)">
    </div>
    <div class="operation-list">
      <span class="el-icon-menu" 
      title="预览"
      @click.stop="showForesee"
      />
      <span class="el-icon-zoom-in " 
      title="放大"
      @click.stop="plus"/>
      <span class="el-icon-zoom-out" 
      title="缩小"
      @click.stop="reduce"/>
      <span class="el-icon-star-off" 
      title="原始大小"
      />
      <span class="el-icon-refresh-right" 
      title="旋转"
      @click.stop="spinRight"/>
      <span class="el-icon-edit" 
      title="编辑"
      />
      <span class="el-icon-download" 
      title="另存为"
      />
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      currentImg: {
        type: String,
        default: 'https://empty'
      },
      imgList: {
        type: Array,
        default() {
          return []
        }
      },
      showOper: {
        type: Boolean,
        default: true
      }
    },
    data() {
      return {
        error: false,
        img_url: '',
        currImgIndex: 0,
        scale: 1, // 图片放大缩小的值
        rotate: 0,
        imgIsLoding: true,
        initWidth:'',
        isMoveing: false,        // 移动图片
        startX: 0,
        startY: 0,
        translateX: 0,
        translateY: 0,
        lastX: 0,
        lastY: 0,
        showArrowsLeft: false,   //箭头按钮
        showArrowsRight: false,
        isshowForesee: false,//图片预览
        active: -1,
      }
    },
    methods: {
      selected(index) {
        this.active = index
        this.img_url = this.imgList[index]
      },
      showForesee() {
        if (!this.isshowForesee) {
          let currIndex = this.imgList.findIndex(item => item == this.img_url)
          this.active = currIndex
        }
        this.isshowForesee = !this.isshowForesee
      },
      close() {
        this.$emit('setShow',false)
      },
      // 放大
      plus() {
        this.scale += 0.1
      },
      reduce() {
        this.scale -= 0.1
      },
      spinRight() {
        if (this.$refs.imgResource.offsetWidth > 400) {
          this.$refs.imgResource.style='width:400px'
        } else {
          this.$refs.imgResource.style=`width:${this.initWidth}px`
        }
        this.rotate +=90
      },
      handlerWheelScale(e) {
        if (e.wheelDelta > 0 ) {
          this.plus()
        } else {
          this.reduce()
        }
      },
      handlerMouseStar(e) {
        this.isMoveing = true
        this.startX = e.pageX,
        this.startY = e.pageY
      },
      handlerMouseMove(e) {
        if (this.isMoveing) {
          this.translateX = this.lastX + e.pageX -this.startX
          this.translateY = this.lastY + e.pageY -this.startY
        }
      },
      handlerMouseEnd() {
        this.isMoveing = false
        this.lastX = this.translateX
        this.lastY = this.translateY
      },
      enterLeft() {
        this.showArrowsLeft = true
      },
      leaveLeft() {
        this.showArrowsLeft = false
      },
      enterRight() {
        this.showArrowsRight = true
      },
      leaveRight() {
        this.showArrowsRight = false
      },
      clickHandler() {
        console.log('click');
      },
      previous() {
        let currIndex = this.imgList.findIndex(item => item == this.img_url)
        if (currIndex == 0 || currIndex == -1) {
          return this.$message({type: 'warning', message: '已经是第一张了~'})
        } else {
          this.img_url = this.imgList[currIndex - 1]
        }
      },
      next() {
        let currIndex = this.imgList.findIndex(item => item === this.img_url)
        console.log(currIndex);
        console.log(this.imgList.length - 1);
        if (currIndex == this.imgList.length - 1) {
          return this.$message({type: 'warning', message: '已经是最后一张了~'})
        } else {
          this.img_url = this.imgList[currIndex + 1]
        }
      },
    },
    mounted() {
      this.initWidth = this.$refs.imgResource.offsetWidth
    },
    created() {
      console.log('图片');
      console.log(this.imgList);
      this.img_url = this.currentImg
    }
}
</script>

<style scoped >
.picture-preview-com {
  width: 752px;
  height: 520px;
  z-index: 1001;
  background-color: #f5f5f5;
  border: 1px solid #d1d1d1;
  position: fixed;
  top: 80px;
  left: 400px;
}
/* <!-- 上一张，下一张 --> */
.previous {
  position: absolute;
  top: 0px;
  left: 0px;
  margin-top: 24.8px;
  width: 252px;
  height: 400px;
  /* height: 336px; */
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: center;
  cursor: pointer;
}
.next {
  position: absolute;
  top: 0px;
  right: 0px;
  margin-top: 24.8px;
  width: 252px;
  height: 400px;
  /* height: 336px; */
  z-index: 1000;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  cursor: pointer;
}

.previous span,.next span {
  width: 37.6px;
  height: 37.6px;
  background-color: #939393;
  border-radius: 18.8px;
  color: #e8e8e8;
  text-align: center;
  line-height: 37.6px;
  font-size: 16px;
  margin-right: 20px;
  margin-left: 20px;
}
/*  */
.switch-top {
  display: flex;
  height: 24.8px;
  justify-content: flex-end;
  margin-bottom: 1.6px;
}
.switch-top span {
  width: 32.8px;
  height: 24.8px;
  line-height: 24.8px;
  text-align: center;
}
.switch-top span:hover {
  background-color: #fb7373;
}
/*  */
.img-wrapper {
  height: 400px;
  /* max-height: 80%; */
  width: 752px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding-bottom: 1px;
  overflow: hidden;
}
.img-wrapper img {
  max-height: 100%;
  max-width: 100%;
  border-bottom: 2.4px solid #e2ecf7;
}
/*  */
.operation-list {
  display: flex;
  /* background-color: gray; */
  margin-top: 18.4px;
  /* width: 752px;
  height: 64px; */
  margin-bottom: 28px;
  position: absolute;
  bottom: 0px;
}
.operation-list span {
  width: 18.4px;
  height: 18.4px;
  margin-left: 16px;
}
.operation-list span:nth-child(2) {
  margin-left: 240px;
}
.operation-list span:nth-child(5) {
  padding-left: 12.8px;
  border-left: 1px solid #e5e5e5;
}
.operation-list span:last-child {
  padding-left: 12.8px;
  border-left: 1px solid #e5e5e5;
}
/* 全部图片预览 */
.foresee{
  height: 60px;
  padding-top: 32px;
  display: flex;
  justify-content: center;
}
.foresee img {
height: 40px;
width: 40px;
margin-right: 1px;
}
.actived {
  border: 2px solid #3dce3d;
}
.activedTow {
  height: 336px;
}
</style>
