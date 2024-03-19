<template>
    <el-card style="max-width: 680px">
        <template #header>
        <div class="card-header" >
            <span>个人中心(待开发...)</span>
        </div>
        </template>
        <el-descriptions
            class="margin-top"
            title="With border"
            :column="3"
            :size="size"
            border
        >
            <template #extra>
            <el-button type="primary">Operation</el-button>
            </template>
            <el-descriptions-item>
            <template #label>
                <div class="cell-item">
                <el-icon :style="iconStyle">
                    <user />
                </el-icon>
                Username
                </div>
            </template>
            {{ name }}
            </el-descriptions-item>
            <el-descriptions-item>
            <template #label>
                <div class="cell-item">
                <el-icon :style="iconStyle">
                    <iphone />
                </el-icon>
                Telephone
                </div>
            </template>
            {{ phone }}
            </el-descriptions-item>
            <el-descriptions-item>
            <template #label>
                <div class="cell-item">
                <el-icon :style="iconStyle">
                    <location />
                </el-icon>
                Email
                </div>
            </template>
            {{ email }}
            </el-descriptions-item>
            <el-descriptions-item>
            <template #label>
                <div class="cell-item">
                <el-icon :style="iconStyle">
                    <tickets />
                </el-icon>
                Remarks
                </div>
            </template>
            <el-tag size="small">School</el-tag>
            </el-descriptions-item>
            <el-descriptions-item>
            <template #label>
                <div class="cell-item">
                <el-icon :style="iconStyle">
                    <office-building />
                </el-icon>
                Address
                </div>
            </template>
            No.1188, Wuzhong Avenue, Wuzhong District, Suzhou, Jiangsu Province
            </el-descriptions-item>
        </el-descriptions>
        <template #footer>该账号注册于:{{ time }}</template>
    </el-card>
</template>

<script lang="ts" setup>
    import { onMounted } from 'vue';
    import { ref } from 'vue'

    const name = ref('');
    const phone = ref('');
    const email = ref('');
    const time = ref('');


    onMounted(() => {
        
    });
</script>