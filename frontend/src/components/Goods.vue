<template>
    <el-card style="width: 100%">
        <el-input v-model="search" style="width: 240px" placeholder="Please input goods" />
        <el-button :icon="Search" style="margin-left: 10px;" type="primary" circle />
        <el-button type="success" style="float: right;margin-right: 10px;" @click="dialogFormVisible=true;">新建商品</el-button>
        <el-card style="width: 100%;margin-top: 10px;" v-loading="loading_2" >
            <div v-for="item in goods">
                
                <div v-if="item.name==undefined || (item.name.toLowerCase().includes(search.toLowerCase()) ||
                item.description.toLowerCase().includes(search.toLowerCase()))">
                    <el-card style="width: 100%;margin-top: 10px;">
                        <div>
                            <h1>
                                {{ item.name }}
                            </h1>
                                {{ item.description }}

                        </div>
                        <div style="float: right;margin-bottom: 10px">
                            <el-popconfirm title="是否删除？"
                            @confirm="del(item.id)">
                                <template #reference>
                                    <el-button type="danger" :icon="Delete" circle />
                                </template>
                            </el-popconfirm>
                        </div>
                    </el-card>
                </div>
            </div>
        </el-card>
        <!-- <el-tree
            style="max-width: 100%"
            :data="tree"
            @node-click="handleNodeClick"
            :highlight-current="true"
            v-loading="loading"
        >
            <template #default="{ node, data }">
                <span class="custom-tree-node">
                    <span>{{ node.label }}</span>
                    <span>
                        <el-button type="primary" size="small" :icon="Plus" @click="dialogFormVisible_2=true;" circle></el-button>
                        <el-button type="danger" size="small" :icon="Minus" @click="" circle></el-button>
                    </span>
                </span>
            </template>
        </el-tree> -->
    </el-card>
    <el-dialog v-model="dialogFormVisible" title="添加商品" width="500">
        <el-form :model="form" label-width="auto" style="max-width: 600px">
            <el-form-item label="商品类别:">
                <el-tree-select
                    v-model="value"
                    :data="tree"
                    :render-after-expand="false"
                    style="width: 240px"
                />
            </el-form-item>
            <el-form-item label="商品名称:">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="商品描述:">
                <el-input v-model="form.description" />
            </el-form-item>
        </el-form>
        <div style="text-align: center;">
            <el-button type="primary" v-loading="loading_2" @click="commit">创建</el-button>
        </div>
    </el-dialog>
    <el-dialog v-model="dialogFormVisible_2" title="新建子节点" width="500">
        <el-form :model="form" label-width="auto" style="max-width: 600px">
            <el-form-item label="子节点名称:">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="子节点描述:">
                <el-input v-model="form.description" />
            </el-form-item>
        </el-form>
        <div style="text-align: center;">
            <el-button type="primary" v-loading="loading_2" @click="commit_2">创建</el-button>
        </div>
    </el-dialog>
</template>

<script lang="ts" setup>
    import {
        Check,
        Delete,
        Edit,
        Message,
        Search,
        Star,
    } from '@element-plus/icons-vue'
    import { onMounted } from 'vue';
    import { ref } from 'vue'
    import axios from 'axios'
    axios.defaults.baseURL = '/api' 
    import { ElNotification } from 'element-plus'
    import { reactive } from 'vue'
    import {
        Plus,Minus
    } from '@element-plus/icons-vue'

    const loading=ref(true);
    const loading_2=ref(false);
    const value = ref();

    interface Tree {
        label: string
        children?: Tree[]
        id?:string
        description?:string
    }

    interface Good {
        name:string
        id:number
        description?:string
    }

    const handleNodeClick = (data: Tree) => {
        if(data.id==undefined)return;
        cargoID.value=data.id;
    }

    const tree=ref<Tree[]>();
    const goods=ref<Good[]>();

    const input=ref('');
    const search = ref('')

    goods.value=[{title:'百事可乐',description:'洁厕灵味道的饮品',id:1},
    {title:'可口可乐',description:'可乐味饮品',id:2}
    ];

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
                value:input[j].id,
            });
        }
        return res;
    };

    const del=(id:number)=>{
        axios.delete('/cargo-classes/'+id)
        .then(function (res) {
            if(res.data.message=='ok'){
                console.log('1',parse(res.data.data));
                refresh();
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
    };

    const refresh=()=>{
        loading.value=true;
        loading_2.value=true;
        axios.get('/cargo-types/trees')
        .then(function (res) {
            if(res.data.message=='ok'){
                console.log('1',parse(res.data.data));
                tree.value = parse(res.data.data);
                loading.value=false;
                loading_2.value=false;
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

        axios.get('/cargo-classes')
        .then(function (res) {
            if(res.data.message=='ok'){
                goods.value = res.data.data;
                console.log('goods',goods);
                loading.value=false;
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
    };

    

    onMounted(() => {
        refresh();
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
                });
                dialogFormVisible.value=false;
                refresh();
                loading_2.value=false;
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
                });
                loading_2.value=false;
                dialogFormVisible_2.value=false;
                refresh();
            }
            else{
                ElNotification({
                    title: '创建错误',
                    message: res.data.data,
                    type: 'error',
                });
                loading_2.value=false;
            }
        })
        .catch(function (error) {
            ElNotification({
                title: '网络错误',
                message: error,
                type: 'error',
            });
            loading_2.value=false;
        });
    };

    const commit=()=>{
        loading_2.value=true;
        axios.post(`/cargo-classes`, 
        { "name":form.name,"description": form.description,"cargoTypeId":Number(value.value)})
        .then(function (res) {
            if(res.data.message=='ok'){
                ElNotification({
                    title: 'Success',
                    message: '创建成功',
                    type: 'success',
                });
                loading_2.value=false;
                dialogFormVisible.value=false;
                refresh();
            }
            else{
                ElNotification({
                    title: '创建错误',
                    message: res.data.data,
                    type: 'error',
                });
                loading_2.value=false;
            }
        })
        .catch(function (error) {
            ElNotification({
                title: '网络错误',
                message: error,
                type: 'error',
            });
            loading_2.value=false;
        });
        form.name='';
        form.description='';
    };
    const commit_2=()=>{
        loading_2.value=true;
        // if(cargoID.value=='-1'){alert("请选中后添加节点");return};
        // addChild();
        axios.post(`/cargo-classes`, 
        { "name":form.name,"description": form.description,"cargoTypeId":Number(value.value)})
        .then(function (res) {
            if(res.data.message=='ok'){
                ElNotification({
                    title: 'Success',
                    message: '创建成功',
                    type: 'success',
                });
                dialogFormVisible.value=false;
                refresh();
            }
            else{
                ElNotification({
                    title: '创建错误',
                    message: res.data.data,
                    type: 'error',
                });
                loading_2.value=false;
            }
        })
        .catch(function (error) {
            ElNotification({
                title: '网络错误',
                message: error,
                type: 'error',
            });
            loading_2.value=false;
        });
        form.name='';
        form.description='';
    };
</script>

<style>
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>