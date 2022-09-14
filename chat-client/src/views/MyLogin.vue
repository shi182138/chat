<template>
  <div>
    <!-- npm run serve -->
        <el-form ref="loginForm" :rules='rules' :model="loginForm" class="login-box"  v-loading="loading">
          <el-avatar class="avatar" shape="square" :size="80" :src="urlava"></el-avatar>
          <el-form-item label="" prop="username">
            <el-input type="text" auto-complete="false" placeholder="请输入用户名" v-model="loginForm.username"></el-input>
          </el-form-item>
          <el-form-item label="" prop="password">
            <el-input type="password" auto-complete="false" placeholder="请输入密码" v-model="loginForm.password"></el-input>
          </el-form-item>
          <el-form-item prop="vcCode" class="test">
            <el-input type="text" auto-complete="false" v-model="loginForm.vcCode" placeholder="点击更换验证码" style="width:200px; margin-right:5px;height: 56px;margin-top: 0px;"></el-input>
            <img :src="captchaUrl" @click="updateCaptcha" style="display:block;float:right">
          </el-form-item>
          <span class="loginRemember">点我注册</span>
          <el-form-item>
            <!-- <el-checkbox v-model="checked" class="loginRemember">注册</el-checkbox> -->
            
            <el-button type="primary" style="width: 100%;background-color: #07c160;" @click="submitLogin">登录</el-button>
          </el-form-item>
      </el-form>
  </div>
</template>

<script>
export default {
  data() {
      return {
        captchaUrl:'/chat/captcha',
        loading: false,
        loginForm: {
          username: '',
          password: '',
          vcCode:'',//验证码
        },
        checked:true,
        urlava:'http://192.168.85.132:8888/group1/M00/00/00/wKhVhGJ7bMqAZo18AAAd-iVWD5c416.png',
        rules:{
          username:[{required:true,message:'请输入用户名',tirgger:'blur'}],
          password:[{required:true,message:'请输入密码',tirgger:'blur'}],
          vcCode:[{required:true,message:'请输入验证吗',tirgger:'blur'}],
        }
      };
    },
  methods: {
    updateCaptcha() {
      this.captchaUrl = "/chat/captcha?time=" + new Date();
    },
    submitLogin() {
      this.$refs.loginForm.validate((valid) => {
          if (valid) {
            this.loading = true;
            this.postRequest('/chat/user/login',this.loginForm).then(async (resp) => {
              if(resp.code === 1000 ) {
                this.loading = false;
                //存储用户token
                const tokenStr = resp.data.token;
                // console.log(tokenStr);
                window.sessionStorage.setItem('tokenStr',tokenStr);
                const res =  await this.getRequest('/chat/getUserInfo')
                if(res) {
                  this.$store.dispatch('user/SET_USERINFO', res.userInfo)
                }
                this.$router.push("/main");
              }else{
                this.loading = false;
              }  
            })     
          } else {
            this.$message('请输入所有字段');
            return false;
          }
        });
    }
  }
}
</script>

<style  scoped>
  .login-box{
    /* border-radius: 15px; */
    background-clip: padding-box;
    margin: 90px auto;
    width: 350px;
    padding: 15px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6; 
  }
  /* .login-box el-form-item {
    width: ;
  } */
  /*  */
  .avatar {
    margin: 28px 0px 15px 135px;
  }

  .login-title{
    margin: 0px auto 40px auto;
    text-align: center;
  }
  .loginRemember {
    display: block;
    text-align: left;
    margin: 0px 0px 30px;
    margin-bottom: 50px;
  }
  .test {
    display: flex;
    /* padding-top: 20px; */
  }
  .test img {
    height: 38px;
  }
</style>

