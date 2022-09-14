<template>
  <div>
    <!-- npm run serve -->
        <el-form ref="loginForm" :rules='rules' :model="loginForm" class="login-box"  v-loading="loading">
          <el-form-item label="" prop="account">
            <el-input type="text" auto-complete="false" placeholder="请输入用户名" v-model="loginForm.account"></el-input>
          </el-form-item>
          <el-form-item label="" prop="password">
            <el-input type="password" auto-complete="false" placeholder="请输入密码" v-model="loginForm.password"></el-input>
          </el-form-item>
          <el-form-item>
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
          account: '',
          password: '',
        },
        checked:true,
        rules:{
          account:[{required:true,message:'请输入用户名',tirgger:'blur'}],
          password:[{required:true,message:'请输入密码',tirgger:'blur'}],
        }
      };
    },
  methods: {
    submitLogin() {
      this.$refs.loginForm.validate((valid) => {
          if (valid) {
            this.loading = true;
            this.$http.login(this.loginForm).then((resp) => {
              if(resp.code === 1000 ) {
                this.loading = false;
                //存储用户token
                const tokenStr = resp.data.token;
                console.log('resp');
                console.log(resp);
                // console.log(tokenStr);
                window.sessionStorage.setItem('tokenStr',tokenStr);
                this.$store.commit('user/setAdminInfo', resp.data.userInfo)
                this.$router.push('/')
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
    text-align: left;
    margin: 0px 0px 15px;
  }
</style>