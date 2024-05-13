<style>
    /* 把我们所有标签的内外边距清零 */
    * {
        margin: 0;
        padding: 0
    }
    /* em 和 i 斜体的文字不倾斜 */
    em,
    i {
        font-style: normal
    }
    /* 去掉li 的小圆点 */
    li {
        list-style: none
    }
    
    img {
        /* border 0 照顾低版本浏览器 如果 图片外面包含了链接会有边框的问题 */
        border: 0;
        /* 取消图片底侧有空白缝隙的问题 */
        vertical-align: middle
    }
    
    button {
        /* 当我们鼠标经过button 按钮的时候，鼠标变成小手 */
        cursor: pointer
    }
    
    a {
        color: #666;
        text-decoration: none
    }
    
    a:hover {
        color: #c81623
    }
    
    button,
    input {
        /* "\5B8B\4F53" 就是宋体的意思 这样浏览器兼容性比较好 */
        font-family: Microsoft YaHei, Heiti SC, tahoma, arial, Hiragino Sans GB, "\5B8B\4F53", sans-serif
    }
    
    body {
        /* CSS3 抗锯齿形 让文字显示的更加清晰 */
        -webkit-font-smoothing: antialiased;
        background-color: #fff;
        font: 12px/1.5 Microsoft YaHei, Heiti SC, tahoma, arial, Hiragino Sans GB, "\5B8B\4F53", sans-serif;
        color: #666
    }
    
    .hide,
    .none {
        display: none
    }
    /* 清除浮动 */
    .clearfix:after {
        visibility: hidden;
        clear: both;
        display: block;
        content: ".";
        height: 0
    }

    
    .body{
        width:100%;
        height:100%;
    }
    .img1{
        position:absolute;
        left:5%;
        top:0%;
        width:35%;
        height:60%;
    }
    .img1 img {
        width: 100%;
        height: 100%;
        object-fit: fill;
    }
    .img2{
        position:absolute;
        left:27%;
        top:54%;
        width:50%;
        height:45%;
    }
    .img2 img {
        width: 100%;
        height: 100%;
        object-fit: fill;
    }
    .img3{
        position:absolute;
        left:60%;
        top:10%;
        width:40%;
        height:60%;
    }
    .img3 img {
        width: 100%;
        height: 100%;
        object-fit: fill;
    }
    .main-box{
        position:absolute;
        left:50%;
        top:50%;
        transform:translate(-50%,-50%);
        width:60%;
        height:70%;
        min-width: 400px;
        min-height: 200px;
        max-height: 500px;
        box-shadow: 10px 10px 10px #d1d9e6, -10px -10px 10px #f9f9f9;
        border-radius: 12px;
        padding: 30px;
        opacity: 0.95;
        background-color:white
    }
    .container {
        justify-content: center;
        position: absolute;
        top: 0;
        width: 60%;
        height: 100%;
        padding: 25px;
        border-radius: 12px;
        background-color: rgb(255,255,255);
        transition: all 1.25s;
        display: flex;
        flex-direction: column;
        align-items: center;
   }
    .container-register {
        z-index: 100;
        left: calc(40%);
    }
    .container-login {
        left: calc(40%);
        z-index: 0;
    }
    .is-login {
        left: 0;
        transition: 1.25s;
        transform-origin: right;
    }
    .is-register {
        z-index: 200;
        transition: 1.25s;
    }
    .switch {
        display: flex;
        justify-content: center;
        align-items: center;
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        width: 40%;
        /* padding: 50px; */
        z-index: 200;
        transition: 1.25s;
        background-color: #ecf0f3;
        overflow: hidden;
        box-shadow: 4px 4px 10px #d1d9e6, -4px -4px 10px #f9f9f9;
        border-radius: 12px;
        .switch__circle__bottom {
            position: absolute;
            width: 60%;
            height: 50%;
            border-radius: 50%;
            background-color: #ecf0f3;
            box-shadow: inset 8px 8px 12px #d1d9e6, inset -8px -8px 12px #f9f9f9;
            bottom: -30%;
            left: -20%;
            transition: 1.25s;
        }

        .switch__circle_top {
            position: absolute;
            width: 60%;
            height: 50%;
            border-radius: 50%;
            background-color: #ecf0f3;
            box-shadow: inset 8px 8px 12px #d1d9e6, inset -8px -8px 12px #f9f9f9;
            top: -30%;
            left: 70%;
            transition: 1.25s;
        }
        .switch__container {
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
            position: absolute;
            padding: 50px 55px;
            transition: 1.25s;
            width:110%;
            h2 {
                font-size: 34px;
                font-weight: 700;
                line-height: 3;
                color: #181818;
            }
            p {
                font-size: 14px;
                letter-spacing: 0.25px;
                text-align: center;
                line-height: 1.6;
            }
        }
    }
    .login {
        left: calc(60%);
        .switch__circle__bottom {
            left: 70%;
        }
        .switch__circle_top {
            left: -20%
        }
    }
</style>

<template>
    <div class="body">
        <div class="img1">
            <img src="https://dbd-video-0.oss-cn-beijing.aliyuncs.com/img/%7BE7565D31-2FA8-4d8f-A5F4-0A22E195A04B%7D.png" alt="">
        </div>
        <div class="img2">
            <img src="https://dbd-video-0.oss-cn-beijing.aliyuncs.com/img/%7BDE8D5BCB-4E8B-498d-A0AF-E60433D38445%7D.png" alt="">
        </div>
        <div class="img3">
            <img src="https://dbd-video-0.oss-cn-beijing.aliyuncs.com/img/%7BBF6E6DB5-ED60-4269-8CB5-838EA31D55D8%7D.png" alt="">
        </div>
        <div class="main-box">
            <div :class="['container', 'container-register', { 'is-login': isLogin }]">
                <el-form
                    ref="formRef"
                    label-width="auto"
                    :model="registerForm"
                    style="max-width: 600px"
                    :rules="rules"
                >
                    <el-form-item label="Email" prop="email">
                        <el-input v-model="registerForm.email" />
                    </el-form-item>
                    <el-form-item label="Phone Number" prop="number">
                        <el-input v-model="registerForm.number" />
                    </el-form-item>
                    <el-form-item label="Password" prop="password">
                        <el-input v-model="registerForm.password" type="password" />
                    </el-form-item>
                    <el-form-item label="Name" prop="name">
                        <el-input v-model="registerForm.name" />
                    </el-form-item>
                </el-form>
                <div class="bottom">
                    <el-button type="primary" @click="Register(formRef)">Submit</el-button>
                </div>
            </div>
            <div :class="['container', 'container-login', { 'is-login is-register': isLogin }]">
                <el-form
                    label-width="auto"
                    :model="loginForm"
                    style="max-width: 600px"
                    :rules="rules"
                >
                    <el-form-item label="Name" prop="name">
                        <el-input v-model="loginForm.name" />
                    </el-form-item>
                    <el-form-item label="Password" prop="password">
                        <el-input v-model="loginForm.password" type="password"/>
                    </el-form-item>
                </el-form>
                <div class="bottom">
                    <el-button type="primary" @click="Login(formRef)">Submit</el-button>
                </div>
            </div>
            <div :class="['switch', { login: isLogin }]">
                <div class="switch__circle__bottom"></div>
                <div class="switch__circle_top"></div>
                <div class="switch__container">
                    <h2>{{ isLogin ? 'Hello Friend !' : 'Welcome Back !' }}</h2>
                    <p style="overflow: hidden;height:50px">
                    {{
                        isLogin
                        ? 'Enter your personal details and start journey with us'
                        : 'To keep connected with us please login with your personal info'
                    }}
                    </p>
                    <div class="primary-btn" @click="isLogin = !isLogin">
                        <el-button style="margin-top: 40%;" v-if="isLogin==true" @click="isLogin=true">立即注册</el-button>
                        <el-button style="margin-top: 40%;" v-if="isLogin==false" @click="isLogin=false">立即登录</el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElNotification } from 'element-plus'
import axios from 'axios'
import { useRouter } from 'vue-router';

axios.defaults.baseURL = '/api'

const router = useRouter();
let temp = true
let isLogin = ref(temp);
const formRef = ref<FormInstance>()
const Login = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  axios.post('/users/login', { "username":loginForm.name,"password": loginForm.password,"role":"WAREHOUSE_MANAGER"})
  .then(function (res) {
    if(res.data.message=='ok'){
        ElNotification({
            title: 'Success',
            message: '登录成功',
            type: 'success',
        })
        //token存入
        window.localStorage.setItem("optoken",res.data.data.token)
        window.localStorage.setItem("opID",res.data.data.user.id);
        // window.localStorage.setItem("opID","15");
        //跳转
        router.push("/home");
    }
    else{
        ElNotification({
            title: '登录失败',
            message: res.data.msg,
            type: 'error',
        })
        loginForm.password='';
    }
  })
  .catch(function (error) {
    ElNotification({
        title: '网络错误',
        message: error,
        type: 'error',
    })
  });
}
const Register = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  axios.post('/warehouse-manager', 
  { "username":registerForm.name,"password": registerForm.password,
    "email":registerForm.email,"phoneNumber":registerForm.number})
  .then(function (res) {
    if(res.data.message=='ok'){
        ElNotification({
            title: 'Success',
            message: '注册成功',
            type: 'success',
        })  
        isLogin.value=true;
        loginForm.name=registerForm.name;
        loginForm.password=registerForm.password;
    }
    else{
        ElNotification({
            title: '注册错误',
            message: res.data.data,
            type: 'error',
        })
    }
  })
  .catch(function (error) {
    ElNotification({
        title: '网络错误',
        message: error,
        type: 'error',
    })
  });
}

interface RuleForm {
  name: string
  number?: number
  password: string
  email?:string
}
    

const registerForm = reactive<RuleForm>({
    name: '',
    password: '',
    email:'',
})

const loginForm = reactive<RuleForm>({
    name: '',
    password: '',
})

const rules = reactive<FormRules<RuleForm>>({
    number: [
        { required: true, message: 'Please input your number', trigger: 'blur' },
        { min: 11, max: 11, message: 'Please input correct number', trigger: 'blur' },
    ],
    name: [
        { required: true, message: 'Please input your name', trigger: 'blur' },
    ],
    password: [
        { required: true, message: 'Please input password', trigger: 'blur' },
        { min: 5, max: 12, message: 'Length should be 5 to 12', trigger: 'blur' },
    ],
    email: [
        { required: true, message: 'Please input your email', trigger: 'blur' },
    ],
})


</script>
