<template>
    <div style="display: flex;width:100%">
        <div style="width:100%;margin-left: 0%;margin-right: 0%;">
            <el-card style="width: 100%" v-loading="loading || loading_2 ||loading_3">
                <div>
                    <el-input v-model="search_2" style="width: 240px" placeholder="Please input goods" />
                    <el-button :icon="Search" style="margin-left: 10px;" type="primary" circle />
                    <el-select
                        v-model="valueSelect"
                        clearable
                        placeholder="Select"
                        style="float:right;width: 240px;"
                    >
                        <el-option
                            v-for="item in _options"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                     </el-select>
                </div>
                
                <div v-for="item in stock" v-on:click="clickOn(item)">
                    <!-- {{ Number(nowID_) }} -->
                    <div v-if="Number(nowID_)==item.createdUser?.id || Number(nowID_)==item.pickerUser?.id">
                    <!-- <div v-if="true"> -->
                    <div v-if="item.state!=3 && item.state!=0 && (valueSelect==item.state || valueSelect==100 )&&(item.id==search_2 || !search_2)">
                    
                        <el-card style="width: 100%;margin-top: 10px;">
                            <!-- 第一层 -->
                            <div style="display: flex;">
                                <div v-if="item.isOutOrder">
                                    <h2 style="color:rgb(245,108,108)">出库单</h2>
                                </div>
                                <div v-if="!item.isOutOrder">
                                    <h2 style="color:rgb(43,194,210)">入库单</h2>
                                </div>
                                <div style="margin-left: auto;">
                                    <div v-if="item.state==0">
                                        <h1>
                                            <el-icon><Close /></el-icon>
                                            未分配</h1>
                                    </div>
                                    <div v-if="item.state==1">
                                        <h1>
                                            <el-icon><Clock /></el-icon>
                                            未处理</h1>
                                    </div>
                                    <div v-if="item.state==2">
                                        <h1 style="color:rgb(255,186,93)">
                                            <el-icon><Loading /></el-icon>
                                            处理中</h1>
                                    </div>
                                    <div v-if="item.state==3" style="display: flex;">
                                        <h1 style="color:rgb(103,194,58)">
                                            <el-icon><Finished /></el-icon>
                                            已完成</h1>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <h3>单号：{{ item.id }}</h3>
                            </div>
                            <div>
                                <h3>介绍：{{ item.description }}</h3>
                            </div>
                            <div>
                                
                            </div>
                            <!-- 最底层 -->
                            <div style="display: flex">
                                <div><h5>(类别模块正在开发中...)</h5></div>
                                <div style="margin-left: auto;">
                                    <h4>创建时间：{{moment.unix(item.createdTime/1000).format('YYYY-MM-DD HH:mm:ss')}}</h4>
                                </div>
                            </div>
                        </el-card>
                    </div>
                    </div>
                </div>
                <el-dialog v-model="dialogFormVisible" title="入库" width="500">
                    <el-form  ref="ruleFormRef" :model="form" label-width="auto" :rules="rules" style="max-width: 600px">
                        <el-form-item label="货物种类:" prop="name">
                            <!-- <el-input v-model="form.name"/> -->
                            <el-tree-select
                                v-model="value_cargo"
                                :data="tree"
                                :render-after-expand="false"
                                style="width: 240px"
                            />
                        </el-form-item>
                        <el-form-item label="货物名称:" prop="name">
                            <!-- <el-input v-model="form.name"/> -->
                            <el-select
                                v-model="value_class"
                                placeholder="Select"
                                size="large"
                                style="width: 240px"
                                >
                                <div v-for="item in option_class">
                                    <div v-if="item.cargoTypeId==value_cargo">
                                        <el-option
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id"
                                        ></el-option>
                                    </div>
                                </div>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="入库货区:" prop="name_">
                            <!-- <el-input v-model="form.name"/> -->
                            <el-select
                                v-model="value_warehouse"
                                placeholder="Select"
                                size="large"
                                style="width: 240px"
                                >
                                <div v-for="item in option_warehouse">  
                                    <el-option
                                    :key="item.id"
                                    :label="'库区 '+item.name+'  (容量:'+item.capacity+')'"
                                    :value="item.id"
                                    ></el-option>
                                </div>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="简介:" prop="description">
                            <el-input v-model="form.description" />
                        </el-form-item>
                        <el-form-item label="数量:" prop="capacity">
                            <el-input type="number" v-model="form.capacity" />
                        </el-form-item>
                    </el-form>
                    <div style="float:flex;text-align: center;">
                        <el-button v-loading="loading_3" type="primary"  @click="commit(ruleFormRef,false)">创建入库</el-button>
                    </div>
                </el-dialog>
                <el-dialog v-model="dialogFormVisible_2" title="出库" width="500">
                    <el-form  ref="ruleFormRef" :model="form" label-width="auto" :rules="rules" style="max-width: 600px">
                        <el-form-item label="货物种类:" prop="name">
                            <!-- <el-input v-model="form.name"/> -->
                            <el-tree-select
                                v-model="value_cargo"
                                :data="tree"
                                :render-after-expand="false"
                                style="width: 240px"
                            />
                        </el-form-item>
                        <el-form-item label="货物名称:" prop="name">
                            <!-- <el-input v-model="form.name"/> -->
                            <el-select
                                v-model="value_class"
                                placeholder="Select"
                                size="large"
                                style="width: 240px"
                                >
                                <div v-for="item in option_class">
                                    <div v-if="item.cargoTypeId==value_cargo">
                                        <el-option
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id"
                                        ></el-option>
                                    </div>
                                </div>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="简介:" prop="description">
                            <el-input v-model="form.description" />
                        </el-form-item>
                        <el-form-item label="数量:" prop="capacity">
                            <el-input type="number" v-model="form.capacity" />
                        </el-form-item>
                    </el-form>
                    <div style="float:flex;text-align: center;">
                        <el-button v-loading="loading_3" type="primary"  @click="commit(ruleFormRef,true)">创建出库</el-button>
                    </div>
                </el-dialog>
                <el-dialog v-model="dialogFormVisible_3" title="表单详细" width="500">
                    <el-form  ref="ruleFormRef" :model="form_2" label-width="auto" :rules="rules" style="max-width: 600px">
                        <!-- 调状态 -->
                        <el-form-item label="状态:" prop="description">
                            <el-select
                                v-model="nowItem.state"
                                placeholder="Select"
                                size="large"
                                style="width: 240px"
                                >
                                <el-option
                                    v-for="item in options"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                />
                            </el-select>
                            <el-button v-loading="loading_3" style="margin-left: auto;" type="primary"  @click="commitState(ruleFormRef)">保存</el-button>
                        </el-form-item>
                        <el-card style="width: 100%;margin-top: 10px;">
                            <!-- 第一层 -->
                            <div style="display: flex;">
                                <div v-if="nowItem?.isOutOrder">
                                    <h2 style="color:rgb(245,108,108)">出库单</h2>
                                </div>
                                <div v-if="!nowItem?.isOutOrder">
                                    <h2 style="color:rgb(43,194,210)">入库单</h2>
                                </div>
                                <div style="margin-left: auto;">
                                    <div v-if="nowItem?.state==0">
                                        <h1>
                                            <el-icon><Close /></el-icon>
                                            未分配</h1>
                                    </div>
                                    <div v-if="nowItem?.state==1">
                                        <h1>
                                            <el-icon><Clock /></el-icon>
                                            未处理</h1>
                                    </div>
                                    <div v-if="nowItem?.state==2">
                                        <h1 style="color:rgb(255,186,93)">
                                            <el-icon><Loading /></el-icon>
                                            处理中</h1>
                                    </div>
                                    <div v-if="nowItem?.state==3" style="display: flex;">
                                        <h1 style="color:rgb(103,194,58)">
                                            <el-icon><Finished /></el-icon>
                                            已完成</h1>
                                    </div>
                                </div>
                            </div>
                            <div>
                                <h4>单号：{{ nowItem?.id }}</h4>
                            </div>
                            <div>
                                <el-collapse v-model="activeName" accordion>
                                    <el-collapse-item title="创建人信息" name="1">
                                        <div>
                                            <el-descriptions title="User Info" column="1" border>
                                                <el-descriptions-item label="Username">{{ nowItem?.createdUser.username }}</el-descriptions-item>
                                                <el-descriptions-item label="Telephone">{{ nowItem?.createdUser.phoneNumber }}</el-descriptions-item>
                                                <el-descriptions-item label="email">{{ nowItem?.createdUser.email }}</el-descriptions-item>
                                                <el-descriptions-item label="registrationDate">{{ moment.unix(nowItem.createdUser.registrationDate/1000).format('YYYY-MM-DD HH:mm:ss') }}</el-descriptions-item>
                                            </el-descriptions>
                                        </div>
                                    </el-collapse-item>
                                    <el-collapse-item title="分拣员信息" name="2">
                                        <div v-if="nowItem.pickerUser!=null">
                                            <el-descriptions title="User Info" column="1" border>
                                                <el-descriptions-item label="Username">{{ nowItem.pickerUser.username }}</el-descriptions-item>
                                                <el-descriptions-item label="Telephone">{{ nowItem.pickerUser.phoneNumber }}</el-descriptions-item>
                                                <el-descriptions-item label="email">{{ nowItem.pickerUser.email }}</el-descriptions-item>
                                                <el-descriptions-item label="registrationDate">{{ moment.unix(nowItem.pickerUser.registrationDate/1000).format('YYYY-MM-DD HH:mm:ss') }}</el-descriptions-item>
                                            </el-descriptions>
                                        </div>
                                        <div v-else>
                                            无
                                        </div>
                                    </el-collapse-item>
                                </el-collapse>
                            </div>
                            <div>
                                <h4>介绍：{{ nowItem?.description }}</h4>
                            </div>
                            <div>
                                <h4>货物：<h6>(开发中...)</h6></h4>
                            </div>
                            <!-- 最底层 -->
                            <div style="display: flex">
                                <div style="margin-left: auto;">
                                    <h5>创建时间：{{moment.unix(nowItem.createdTime/1000).format('YYYY-MM-DD HH:mm:ss')}}</h5>
                                </div>
                            </div>
                        </el-card>
                        
                    </el-form>
                    <div style="float:flex;text-align: center;">
                    </div>
                </el-dialog>
            </el-card>
        </div>
    </div>
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
    import { ref } from 'vue';
    import moment from 'moment'
    import { reactive } from 'vue'
    import axios from 'axios'
    axios.defaults.baseURL = '/api' 
    import { ElNotification } from 'element-plus'
    import { onMounted, computed } from 'vue';
    import type { FormInstance, FormRules } from 'element-plus'

    const loading = ref(true);
    const loading_2 = ref(false);
    const loading_3 = ref(false);

    const valueSelect=ref<number>();


    const nowID_=window.localStorage.getItem("opID");

    const value_cargo=ref<number>();
    const value_class=ref<number>();
    const search_2=ref<number>();
    const activeName=ref("");
    const value_warehouse=ref<number>();

    let search = ref(' ')
    interface User {
        id:number
        name: string
    }
    
    const tableRowClassName = ({
        row,
    }: {
        row: User
        }) => {
            if (row.id == nowID.value) {
                return 'warning-row'
            }
            return ''
    }

    const dialogFormVisible=ref(false);
    const dialogFormVisible_2=ref(false);
    const dialogFormVisible_3=ref(false);

    interface RuleForm {
        name: string
        cargoType: number
        capacity:number
        description:string
    };
    const form = reactive<RuleForm>({
        name: '',
        cargoType: 0,
        capacity:0,
        description:'',
    });

    interface RuleForm_2 {
        name: string
        cargoType: number
        capacity:number
        description:string
    };
    const form_2 = ref<RuleForm_2>();

    const ruleFormRef = ref<FormInstance>()
    const rules = reactive<FormRules<RuleForm>>({
        name:[
            { required: true, message: 'Please input name', trigger: 'blur'},
        ],
        cargoType:[
            { required: true, message: 'Please input cargoType', trigger: 'blur'},
        ],
        capacity:[
            { required: true, message: 'Please input capacity', trigger: 'blur'},
        ],
        description:[
            { required: true, message: 'Please input description', trigger: 'blur'},
        ],
    });

    let tableData : User[]=[
        {
            id: 1,
            name: '',
        }
    ];
    // const tableData_2 = ref([

    // ]);
    const filterTableData = computed(() =>
        tableData.filter(
            (data) =>
            !search.value ||
            data.name.toLowerCase().includes(search.value.toLowerCase())
        )
    )

    interface _User{
        id:number
        username:string
        email:string
        phoneNumber:string
        registrationDate:number
    }

    interface Stock {
        id:number
        isOutOrder:boolean
        createdTime:number
        description:string
        state:number
        warehouseAreaId:number
        createdUser?:_User
        pickerUser?:_User
    }
    interface Option_class {
        id:number
        cargoTypeId:number
        description:string
        name:string
    }
    const option_class=ref<Option_class[]>();

    const u1=ref<_User>();
    const u2=ref<_User>();
    u1.value={id:1,username:'分拣员1',email:'1985136419@qq.com',phoneNumber:'15047802650',registrationDate:1};
    u2.value={id:1,username:'创建员1',email:'-',phoneNumber:'-',registrationDate:1};

    const stock=ref<Stock[]>();
    // stock.value=[{id:1,isOutOrder:true,createdTime:1000,description:'test1',state:1,warehouseAreaId:1,createdUser:u2,pickerUser:u1},
    //     {id:2,isOutOrder:false,createdTime:1001,description:'test2',state:2,warehouseAreaId:1,createdUser:u2,pickerUser:u1},
    // ];

    interface Option {
        value:number
        label:string
    }

    const options=ref<Option[]>();
    options.value=[{label:'未分配',value:0},
                   {label:'未处理',value:1},
                   {label:'处理中',value:2},
                   {label:'已完成',value:3},

    ];

    const _options=ref<Option[]>();
    _options.value=[{label:'未处理',value:1},
                    {label:'处理中',value:2},
                    {label:'全部',value:100}
    ];

    valueSelect.value=100;

    const nowID = ref(0)
    const nowName = ref('无')

    const nowItem = ref<Stock>();
    nowItem.value={
        id:-1,
        isOutOrder:false,
        createdTime:0,
        description:'',
        state:0,
        warehouseAreaId:-1,};

    const click =(
        row: any,
    )=>{
        nowID.value=row.id;
        nowName.value = row.name;
        load();
    };

    const clickOn=(item:Stock)=>{
        // alert(item.id);
        nowItem.value=item;
        dialogFormVisible_3.value=true;
    }

    const load=()=>{    
        loading_2.value=true;
        axios.get(`/warehouses/${nowID.value}/areas`)
        .then(function (res) {
            if(res.data.message=='ok'){
                // tableData_2.value = res.data.data;
                loading_2.value=false;
            }
            else{
                ElNotification({
                    title: '错误',
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
    };

    const refresh=()=>{
        loading.value=true;
        axios.get('/warehouses')
        .then(function (res) {
            if(res.data.message=='ok'){
                tableData = res.data.data;  
                console.log(tableData);
                search.value='';
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

        axios.get('/in-out-orders')
        .then(function (res) {
            if(res.data.message=='ok'){
                stock.value = res.data.data;
                loading.value=false;
                console.log(tableData);
                search.value='';
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

    let option_all=ref();
    option_all.value={
        '未分配':0,
        '未处理':1,
        '处理中':2,
        '已完成':3,
    }

    onMounted(() => {
        refresh();
    });
    const commit=(formEl: FormInstance | undefined,isOutOrder:boolean)=>{
       
                loading_3.value=true;
                axios.post(`in-out-orders`, 
                { "isOutOrder":isOutOrder,"description": form.description,"warehouseAreaId":value_warehouse.value,
                    "cargoClassList":[{"cargoClassId": value_class.value,"amount": Number(form.capacity)}]})
                .then(function (res) {
                    if(res.data.code=='200'){
                        ElNotification({
                            title: 'Success',
                            message: res.data.message,
                            type: 'success',
                        });
                        loading_3.value=false;
                        dialogFormVisible.value=false;
                        dialogFormVisible_2.value=false;
                        refresh();
                        // load();
                    }
                    else{
                        ElNotification({
                            title: '创建错误',
                            message: res.data.message,
                            type: 'error',
                        });
                        loading_3.value=false;
                    }
                })
                .catch(function (error) {
                    ElNotification({
                        title: '网络错误',
                        message: error,
                        type: 'error',
                    });
                    loading_3.value=false;
                });


}

interface Tree {
    label: string
    children?: Tree[]
    id?:string
    description?:string
}

const commitState=(formEl: FormInstance | undefined)=>{
    loading_3.value=true;
    axios.patch(`/in-out-orders/${Number(nowItem.value.id)}`, 
    { "state":Number(nowItem.value.state),"pickerUserId":15})
    .then(function (res) {
        if(res.data.message=='ok'){
            ElNotification({
                title: 'Success',
                message: '创建成功',
                type: 'success',
            });
            loading_3.value=false;
            dialogFormVisible.value=false;
            dialogFormVisible_2.value=false;
            dialogFormVisible_3.value=false;
            refresh();
            // load();
        }
        else{
            ElNotification({
                title: '创建错误',
                message: res.data.message,
                type: 'error',
            });
            loading_3.value=false;
        }
    })
    .catch(function (error) {
        ElNotification({
            title: '网络错误',
            message: error,
            type: 'error',
        });
        loading_3.value=false;
    });

}


const tree=ref<Tree[]>();
const option_warehouse=ref();

const InAndOut=(isOutOrder:boolean)=>{
    axios.get('/cargo-types/trees')
    .then(function (res) {
        if(res.data.message=='ok'){
            console.log('1',parse(res.data.data));
            tree.value = parse(res.data.data);
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

    axios.get(`/warehouses/${nowID.value}/areas`)
    .then(function (res) {
        if(res.data.message=='ok'){
            option_warehouse.value = res.data.data;
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
            option_class.value=res.data.data;
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
</script>

<style>
.el-table .warning-row {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}
.el-table .success-row {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}
</style>