<template>
    <div style="display: flex;width:100%">
        <div style="margin-left: 3%;width:15%;float: left">
            <el-card style="width: 100%">
                <el-table :data="tableData" style="width: 100%" @row-click="click">
                    <el-table-column prop="name" label="仓库名称" style="width:100%" />
                </el-table>
            </el-card>
        </div>
        <div style="width:75%;margin-left: 2%;margin-right: 5%;">
            <el-card style="width: 100%">
                <div>
                    <el-button type="success" size="small" @click="dialogFormVisible=true;">添加库区</el-button>
                </div>
                <el-table
                    ref="multipleTableRef"
                    :data="tableData_2"
                    style="width: 100%"
                >
                    <el-table-column type="selection" width="55" />
                    <el-table-column property="name" label="名称" width="320" />
                    <el-table-column property="description" label="简介" width="320" />
                    <el-table-column property="capacity" label="容量" width="320" />
                </el-table>

                <el-dialog v-model="dialogFormVisible" title="新建库区" width="500">
                    <el-form :model="form" label-width="auto" style="max-width: 600px">
                        <el-form-item label="name:">
                            <el-input v-model="form.name"/>
                        </el-form-item>
                        <el-form-item label="description:">
                            <el-input v-model="form.description" />
                        </el-form-item>
                        <el-form-item label="capacity:">
                            <el-input type="number" v-model="form.capacity" />
                        </el-form-item>
                        <el-form-item label="cargoType:">
                            <el-input type="number" v-model="form.cargoType" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" style="float:right" @click="commit">Create</el-button>
                        </el-form-item>
                    </el-form>
                </el-dialog>
            </el-card>
        </div>
    </div>
</template>

<script lang="ts" setup>
    import { ref } from 'vue';
    import { reactive } from 'vue'
    import axios from 'axios'
    axios.defaults.baseURL = '/api' 
    import { ElNotification } from 'element-plus'
    import { onMounted } from 'vue';

    const dialogFormVisible=ref(false);
    const form = reactive({
        name: '',
        cargoType: 0,
        capacity:0,
        description:'',
    });
    const tableData = ref([
        {
            id: '',
            name: '',
        }
    ]);
    const tableData_2 = ref([
        {
            id: '',
            name: '',
        }
    ]);

    const nowID = ref(0)

    const click =(
        row: any,
    )=>{
        nowID.value=row.id;
        load();
    };

    const load=()=>{
        axios.get(`/warehouses/${nowID.value}/areas`)
        .then(function (res) {
            if(res.data.message=='ok'){
                tableData_2.value = res.data.data;
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
        axios.get('/warehouses')
        .then(function (res) {
            if(res.data.message=='ok'){
                tableData.value = res.data.data;
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
    const commit=()=>{
        axios.post(`/warehouses/${nowID.value}/areas`, 
        { "name":form.name,"description": form.description,
            "capacity":Number(form.capacity),"cargoType":Number(form.cargoType)})
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
    }
</script>