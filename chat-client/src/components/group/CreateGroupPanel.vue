<template>
  <div>
      <div class="createPanel-container">
          <div style="margin-left: 8px;margin-top: 10px;margin-bottom:18px;">创建群聊</div>
            <div class="avatar-container">
                <upload-avatar ref="uploadAssembly" />
            </div>
            <div class="base-info">
                <span>群名称</span>
                <input type="text" placeholder="输入群聊名称" v-model="groupName">
            </div>
            <div class="input-box">
                <button class="confirm" @click="confirmCreate">确认</button>
                <button class="cancel" @click="cancelCreate">取消</button>
            </div>
      </div>
  </div>
</template>

<script>
import uploadAvatar from '@/components/UpLoadAvatar.vue'
export default {
    components: {
        uploadAvatar,
    },
    data() {
        return {
            groupName: '',
        }
    },
    methods: {
        async confirmCreate() {
            let url = ''
            url = await this.$refs.uploadAssembly.uploadAvatar()
            this.$emit('createGroupReq', {
                name: this.groupName,
                photo: url
            })
        },
        cancelCreate() {
            this.$emit('cancelCreatGroup')
        }
    }
}
</script>

<style scoped>
.createPanel-container {
    background-color: #ededed;
    padding-bottom: 24px;
}
.avatar-container {
    margin: 0 auto;
    width: 40px;
    margin-bottom: 20px;
}
.base-info{
    width: 280px;
    display: flex;
    /* margin: 0 auto; */
    /* flex-direction: column; */
    margin-bottom: 38px;
}
.base-info span {
    color: #9e9e9e;
    font-size: 12px;
    margin-left:  20px;;
    margin-right: 16px;
}
.base-info input {
    outline: none;
    border: 0;
    border-bottom: 1px solid black;
    background-color: #f2f2f2;
}
.base-info input:focus::placeholder {
    opacity: 0;
}
.confirm {
  width: 72px;
  height: 24px;
  background-color: #1aad29;
  color: #fff;
  font-size: 12px;
  border: 0;
  margin-left: 62px;
  margin-right: 30px;
}
.cancel {
  width: 72px;
  height: 24px;
  background-color: #fff;
  font-size: 12px;
  border: 1px solid #e7e7e7;
}
</style>