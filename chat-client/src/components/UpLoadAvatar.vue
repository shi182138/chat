<template>
  <div class="upload-avatar-container">
    <div class="file-input">
        <input type="file" id="file"
        accept="image/png, image/jpeg, image/gif, image/jpg"
        @change="getPicture">
        <label for="file" title="点击修改图片">
            <img :src="imgSrc" alt="">
        </label>
    </div>
  </div>
</template>

<script>
export default {
    data() {
        return {
            imgSrc: '#',
            file: {},
        }
    },
    methods: {
        getPicture(e) {
            this.imgSrc = window.URL.createObjectURL(e.target.files[0])
            this.file = e.target.files[0]
        },
        async uploadAvatar() {
            const formData = new FormData()
            formData.append('file', this.file)
            let filePath = ''
            await this.postRequest('/chat/uploadFile',formData).then(resp => {
                if (resp.code == 200) {
                    filePath = resp.data.filePath
                }
            })
            return filePath
        }
    }

}
</script>

<style scoped>
.upload-avatar-container {
    width: 40px;
}
.file-input input{
    visibility: hidden;
    width: 0;
}
.file-input label {
    display: inline-block;
    width: 40px;
    height: 40px;
}
.file-input label img {
    width: 40px;
    height: 40px;
}
</style>