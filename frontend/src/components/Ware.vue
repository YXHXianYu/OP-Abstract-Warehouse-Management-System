<template>
    <div>
        <el-button type="success" size="small" @click="dialogFormVisible=true;">添加仓库</el-button>
    </div>
    <div>
        <el-table
            ref="multipleTableRef"
            :data="tableData"
            style="width: 100%"
            @selection-change="handleSelectionChange"
        >
            <el-table-column type="selection" width="55" />
            <el-table-column property="name" label="仓库名称" width="120" />
            <el-table-column property="phoneNumber" label="联系方式" width="120" />
            <el-table-column property="description" label="简介" width="120" />
            <el-table-column property="address" label="地址" width="120" />
        </el-table>

        <el-dialog v-model="dialogFormVisible" title="新建仓库" width="500">
            <el-form :model="form" label-width="auto" style="max-width: 600px">
                <el-form-item label="name:">
                    <el-input v-model="form.name" />
                </el-form-item>
                <el-form-item label="phoneNumber:">
                    <el-input v-model="form.phoneNumber" />
                </el-form-item>
                <el-form-item label="address:">
                    <el-input v-model="form.address" />
                </el-form-item>
                <el-form-item label="description:">
                    <el-input v-model="form.description" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" style="float:right" @click="commit">Create</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
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
        phoneNumber: '',
        address:'',
        description:'',
    });
    const tableData = ref([
        {
            date: '',
            name: '',
            address: '',
        }
    ]);

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
        axios.post('/warehouses', 
        { "name":form.name,"description": form.description,
            "address":form.address,"phoneNumber":form.phoneNumber})
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