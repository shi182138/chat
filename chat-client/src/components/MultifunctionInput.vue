<template>
  <div class="multi-inputbox-container">
      <div class="input-box" contenteditable="true"
      spellcheck="false"
      id="input-box">
      </div>
  </div>
</template>

<script>
export default {
    model:{
        prop:'content',
        event:'onContentChange'
    },
    props:{
        content:String
    },
    methods:{
        requestFocus(){
            let inputBox = document.getElementById('input-box');
            inputBox.focus();
        },
        //死循环
        updateContent(value) {
            let inputBox = document.getElementById('input-box');
            if (value !== inputBox.innerHTML) {
                inputBox.innerHTML = value;
            }
        }
    },
    watch:{
        content(newValue){
            this.updateContent(newValue);
        }
    },
    mounted() {

        let inputBox = document.getElementById('input-box');
        if (inputBox) {
            let that = this;
            inputBox.addEventListener('input',function(){
                that.$emit('onContentChange',inputBox.innerHTML);
            })
        }
    }
}
</script>

<style scoped>
    .input-box {
        width: 596px;
        height: 60px;
        margin-left: 25px;
        margin-top: 12px;
        outline: none;
        font-size: 20px;
        overflow: auto;
    }
    .input-box .emoji {
        width: 16px;
    }
    .input-box img {
        max-width: 120px;
        max-height: 56px;
    }
</style>