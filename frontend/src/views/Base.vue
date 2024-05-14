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

    .common-layout{
        height: 100%;
    }
    .header{
        background-color: rgb(60,137,188);
    }
    .header-left{
        float:left;
    }
    .header-right{
        float:right;
        margin-right: 10px;
    }
    .header-com{
        width:25%;
        height:25%;
        margin-right: 65%;
        margin-top: 20%;
    }
    
</style>

<template>
    <div class="common-layout" style="height: 100%;">
        <el-container style="height: 100%;">
            <el-header class="header" style="margin:0px;padding: 0px;">
                <div class="header-left">
                    header-left
                </div>
                <div class="header-right">
                    <div class="header-com">
                        <el-dropdown @command="handleCommand">
                            <span class="el-dropdown-link">
                                <el-avatar size="20%" src='https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png' />
                            </span>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="self">个人中心</el-dropdown-item>
                                    <el-dropdown-item>修改密码</el-dropdown-item>
                                    <el-dropdown-item command="home" @click="exit" >退出登录</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
            </el-header>
            
            <el-container style="padding: 0px;margin: 0px;height: 100%;">
                <el-aside width="200px" style="height:100%;margin:0px;padding: 0px;border:none;" class="full-screen">
                    <el-menu
                        router
                        :default-active="activeIndex"
                        class="el-menu-vertical-demo"
                        active-text-color="#ffd04b"
                        background-color="#545c64"
                        text-color="#fff"
                        @select="changeTab"
                        style="height:100%"
                    >
                        <el-menu-item index='home' ref="item1">
                            <el-icon><HomeFilled /></el-icon>
                            <span>{{ table['home'] }}</span>
                        </el-menu-item>
                        <el-sub-menu index="仓库库区">
                            <template #title>
                                <el-icon><Menu /></el-icon>
                                <span>仓库库区</span>
                            </template>
                            <el-menu-item index="ware">{{ table['ware'] }}</el-menu-item>
                            <el-menu-item index="area">{{ table['area'] }}</el-menu-item>
                            <el-menu-item index="stock">{{ table['stock'] }}</el-menu-item>
                        </el-sub-menu>
                        <el-sub-menu index="物料管理">
                            <template #title>
                                <el-icon><Menu /></el-icon>
                                <span>物料管理</span>
                            </template>
                            <el-menu-item index="cargo">{{ table['cargo'] }}</el-menu-item>
                            <el-menu-item index="goods">{{ table['goods'] }}</el-menu-item>
                        </el-sub-menu>
                        <el-sub-menu index="我的工作">
                            <template #title>
                                <el-icon><Menu /></el-icon>
                                <span>我的工作</span>
                            </template>
                            <el-menu-item index="worklist">{{ table['worklist'] }}</el-menu-item>
                        </el-sub-menu>
                    </el-menu>
                </el-aside>
                <el-container style="height: 100%;">
                    <el-main style="height: 100%;">
                        <el-tabs
                            type="border-card"
                            v-model="editableTabsValue"
                            class="demo-tabs"
                            closable
                            @tab-remove="removeTab"
                            @tab-change="handleChange"
                            style="height: 100%;"
                        >
                            <el-tab-pane
                                v-for="item in editableTabs"
                                :key="item.name"
                                :label="item.title"
                                :name="item.name"
                            >
                                <router-view style="height: 100%;"></router-view>
                            </el-tab-pane>
                        </el-tabs>
                    </el-main>
                    <el-footer>Footer</el-footer>
                </el-container>
                
        </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
    import axios from 'axios'
    import { ElButton } from 'element-plus';
    axios.defaults.baseURL = '/api' 
    import { defineComponent } from 'vue';
    import { ref } from 'vue'
    import type { TabPaneName } from 'element-plus'
    import { useRouter } from 'vue-router';

    const router = useRouter();

    const activeIndex = ref('home')

    const editableTabsValue = ref('home')
    const editableTabs = ref([
        {
            title: '首页',
            name: 'home',
            content: 'home',
        },
    ]);

    const exit=()=>{
        localStorage.removeItem('optoken');
        location.reload();
    };


    const addPage=(
        index: string,
    )=>{
        let flag=false;
        for(let i=0;i<editableTabs.value.length;i++){
            if(editableTabs.value[i].name==index){flag = true;}
        }
        if(!flag){
            const str:string = index
            editableTabs.value.push({
                title: table.value[str],
                name: index,
                content: index,
            });
        }
        router.push("/"+index);
        editableTabsValue.value = index;
        activeIndex.value = index;
    }


    const table = ref(
        {
            home:'首页',

            self:'个人中心',

            ware:'仓库',
            area:'库区',

            cargo:'分类',

            goods:'商品',

            stock:'出/入库',

            worklist:'任务清单(BETA)',
        },
    )

    const handleCommand = (command: string) => {
        addPage(command);
    }

    //页面切换
    const changeTab = (
        index: string,
        item: object
    ) =>{
        addPage(index);
    }

    const handleChange = (
        name:string
    ) => {
        activeIndex.value = name;
        editableTabsValue.value =  name;
        router.push("/"+name);
    };


    const removeTab = (targetName: string) => {
        const tabs = editableTabs.value
        let activeName = editableTabsValue.value
        if (activeName === targetName) {
            tabs.forEach((tab, index) => {
                if (tab.name === targetName) {
                    const nextTab = tabs[index + 1] || tabs[index - 1]
                    if (nextTab) {
                        activeName = nextTab.name
                    }
                }
            })
        }
        editableTabsValue.value = activeName
        editableTabs.value = tabs.filter((tab) => tab.name !== targetName)
        router.push("/"+activeName);
    }
   
</script>
