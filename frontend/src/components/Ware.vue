<style>
    .d{
        margin-top: 30px;
    }
</style>

<template>
    <el-card style="width: 100%">
        <div>
            <el-button type="danger" style="float:right;margin-right: 1%;" size="small" @click="">删除仓库</el-button>
            <el-button type="success" style="float:right;margin-right: 1%;" size="small" @click="dialogFormVisible=true;">添加仓库</el-button>
        </div>
        <div class="d">
            <el-table
                :data="tableData"
                style="width: 100%;"
                border
                v-loading="loading"
            >
                <el-table-column type="selection" width="38px"/>
                <el-table-column property="name" label="仓库名称" />
                <el-table-column property="phoneNumber" label="联系方式"/>
                <el-table-column property="description" label="简介" />
                <el-table-column property="address" label="地址"  />
                <el-table-column fixed="right" width="80">
                    <template #default="scope">
                        <el-button size="small" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <el-dialog v-model="dialogFormVisible" title="新建仓库" width="500">
                <el-form :model="form" ref="ruleFormRef" label-width="auto" :rules="rules" style="max-width: 600px">
                    <el-form-item label="仓库名称:" prop="name">
                        <el-input v-model="form.name" />
                    </el-form-item>
                    <el-form-item label="联系电话:" prop="phoneNumber">
                        <el-input v-model="form.phoneNumber" />
                    </el-form-item>
                    <el-form-item label="仓库地址:" prop="address">
                        <el-input v-model="form.address" />
                    </el-form-item>
                    <el-form-item label="仓库介绍:" prop="description">
                        <el-input v-model="form.description" />
                    </el-form-item>
                </el-form>
                <div style="display: flex;justify-content: center;align-items: center;">
                    <el-button type="primary" v-loading="loading_2" style="" @click="commit(ruleFormRef)">创建仓库</el-button>
                </div>
            </el-dialog>
        </div>
    </el-card>
</template>

<script lang="ts" setup>
    import { ref } from 'vue';
    import { reactive } from 'vue'
    import axios from 'axios'
    axios.defaults.baseURL = '/api' 
    import { ElNotification } from 'element-plus'
    import { onMounted } from 'vue';
    import type { FormInstance, FormRules } from 'element-plus'

    const dialogFormVisible=ref(false);

    const ruleFormRef = ref<FormInstance>()

    interface RuleForm {
        name:string
        phoneNumber:string
        address:string
        description:string
    };
    const form = reactive<RuleForm>({
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
    const rules = reactive<FormRules<RuleForm>>({
        name:[
            { required: true, message: 'Please input name', trigger: 'blur'},
            { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
        ],
        phoneNumber:[
            { required: true, message: 'Please input phoneNumber', trigger: 'blur'},
        ],
        address:[
            { required: true, message: 'Please input address', trigger: 'blur'},
        ],
        description:[
            { required: true, message: 'Please input description', trigger: 'blur'},
        ],
    });

    const loading=ref(true);
    const loading_2=ref(false);

    const refresh=()=>{
        loading.value=true;
        axios.get('/warehouses')
        .then(function (res) {
            if(res.data.message=='ok'){
                tableData.value = res.data.data;
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

    const commit=(formEl: FormInstance | undefined)=>{
        if (!formEl) {alert(2);return;}
        formEl.validate((valid, fields) => {
            if (valid) {
                loading_2.value=true;
                axios.post('/warehouses', 
                { "name":form.name,"description": form.description,
                    "address":form.address,"phoneNumber":form.phoneNumber})
                .then(function (res) {
                    if(res.data.message=='ok'){
                        ElNotification({
                            title: 'Success',
                            message: '创建成功',
                            type: 'success',
                        });
                        dialogFormVisible.value=false;
                        loading_2.value=false;
                        refresh();
                    }
                    else{
                        ElNotification({
                            title: '创建错误',
                            message: res.data.data,
                            type: 'error',
                        })
                        loading_2.value=false;
                    }
                })
                .catch(function (error) {
                    ElNotification({
                        title: '网络错误',
                        message: error,
                        type: 'error',
                    })
                    loading_2.value=false;
                });
            } else {
                return;
            }
        })
    }
</script>