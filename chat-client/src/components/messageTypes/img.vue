<template>
  <div class="message-type__img">
    <img  class="messageStyle" :src="message.message" alt="图片加载失败" 
         @click.stop="setShowPicturePreview(true)"  />
    <picture-preview
        v-if="showPicturePreview"
        :current-img="message.message"
        :img-list="imgUrlList"
        @setShow="setShowPicturePreview"
      />
  </div>
</template>

<script>
  import './../../../public/static/iconfont/iconfont.css'
  import './../../../public/static/css/animation.scss'
  import picturePreview from '@/components/picturePreview'

  export default {
    props: ['message', 'imgTypeMsgList'],
    data() {
      return {
        showPicturePreview: false,
        imgLoading: true
      }
    },
    computed: {
      imgUrlList() {
        return (this.imgTypeMsgList || []).map(item => item.message)
      }
    },
    methods: {
      setShowPicturePreview(flag) {
        this.showPicturePreview = flag
      },
      load() {
        this.imgLoading = false
      },
      imgError() {
        this.imgLoading = false
      }
    },
    components: {
      picturePreview
    }
  }
</script>

<style  scoped>
  /* 消息里的图片样式 */
  .messageStyle {
    margin-top: 8px;
    margin-left: 8px;
    margin-right: 8px;
    height: 92px;
    max-width: 176px;
    max-height: 92px;
    border-radius: 2px;
  }
</style>
