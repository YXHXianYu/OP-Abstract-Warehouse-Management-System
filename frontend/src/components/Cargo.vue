<template>
    <el-button type="success" size="small" @click="dialogFormVisible=true;">添加根节点</el-button>
    <el-button type="success" size="small" @click="dialogFormVisible_2=true;">添加子节点</el-button>
    <el-tree
        style="max-width: 600px"
        :data="tree"
        @node-click="handleNodeClick"
    />
    <el-dialog v-model="dialogFormVisible" title="新建根节点  " width="500">
        <el-form :model="form" label-width="auto" style="max-width: 600px">
            <el-form-item label="name:">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="description:">
                <el-input v-model="form.description" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" style="float:right" @click="commit">Create</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
    <el-dialog v-model="dialogFormVisible_2" title="新建子节点  " width="500">
        <el-form :model="form" label-width="auto" style="max-width: 600px">
            <el-form-item label="name:">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="description:">
                <el-input v-model="form.description" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" style="float:right" @click="commit_2">Create</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script lang="ts" setup>
    import { onMounted } from 'vue';
    import { ref } from 'vue'
    import axios from 'axios'
    axios.defaults.baseURL = '/api' 
    import { ElNotification } from 'element-plus'
    import { reactive } from 'vue'

    interface Tree {
        label: string
        children?: Tree[]
        id?:string
        description?:string
    }

    const handleNodeClick = (data: Tree) => {
        if(data.id==undefined)return;
        cargoID.value=data.id;
    }

    const tree=ref<Tree[]>();

    const parse: any=(
        input: any
    )=>{
        let res = [];
        console.log(input);
        for(let j=0;j<input.length;j++){
            let c = [];
            if(input[j].childrenTypes!=null){
                c = parse(input[j].childrenTypes)
            }
            res.push( {
                label:input[j].name,
                children:c,
                id:input[j].id,
                description:input[j].description,
            });
        }
        return res;
    };

    onMounted(() => {
        axios.get('/cargo-types/trees')
        .then(function (res) {
            if(res.data.message=='ok'){
                console.log(parse(res.data.data));
                tree.value = parse(res.data.data);
            }
            else{
                ElNotification({
                    title: '错误',
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
    });

    const cargoID = ref("-1");
    const dialogFormVisible=ref(false);
    const dialogFormVisible_2=ref(false);
    const form = reactive({
        name: '',
        description:'',
    });

    const addRoot=()=>{
        axios.post('/cargo-types/roots', 
        { "name":form.name,"description": form.description})
        .then(function (res) {
            if(res.data.message=='ok'){
                ElNotification({
                    title: 'Success',
                    message: '创建成功',
                    type: 'success',
                })  
            }
            else{
                ElNotification({
                    title: '创建错误',
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
    };

    const addChild=()=>{
        axios.post(`/cargo-types/${cargoID.value}/children`, 
        { "name":form.name,"description": form.description})
        .then(function (res) {
            if(res.data.message=='ok'){
                ElNotification({
                    title: 'Success',
                    message: '创建成功',
                    type: 'success',
                })  
            }
            else{
                ElNotification({
                    title: '创建错误',
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
    };

    const commit=()=>{
        addRoot();
    };
    const commit_2=()=>{
        if(cargoID.value=='-1'){alert("请选中后添加节点");return};
        addChild();
    };
</script>