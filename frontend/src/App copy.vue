<template>
  <div class="common-layout">
      <div ref="attrRef" :style="{'position':'absolute','margin-top':height+selectTop+'px','width':'100%'}">
        <el-menu 
          style="z-index: 2;"
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
          :ellipsis="false"
          @select="handleSelect">
          <el-menu-item index="0">
            <img
              style="width: 100px"
              src="https://element-plus.org/images/element-plus-logo.svg"
              alt="Element logo"
            />
          </el-menu-item>
          <div class="flex-grow" style="flex-grow: 1;" />
          <el-menu-item style="margin-right:20px;" index="1">产品</el-menu-item>
          <el-menu-item style="margin-right:80px;" index="2">加入我们</el-menu-item>
        </el-menu>
      </div>
      <!-- 最外层容器 -->
      <div style="margin-top: 0px;">
        <!-- 内层容器 -->
        <div :style="{'margin-top': indexTop+'px','width': width+'px','height': height+'px'}">
          <!-- 产品 -->
          <div>
            <video id="myVideo" style="z-index: -1;object-fit:fill;margin-top: 0px;" :height="height" :width="width" loop="loop" autoplay muted><source src="https://ys.mihoyo.com/main/_nuxt/videos/bg.3e78e80.mp4"></video> 
          </div>
          <!-- 加入我们 -->
          <div :style="{'margin-top': height_inner+'px'}">
            加入OP Abstract Studio
          </div>
        </div>
      </div>
  </div>
</template>

<style scoped>
</style>

<script>
import { RouterLink, RouterView } from 'vue-router'
export default {
  data() {
    return {
      indexTop: 0,
      selectTop: 0,

      width:0,
      height:0,
      height_inner:0,

      throttle:false,

      isShow:false,

      activeIndex:'1',
    };
  },
  mounted(){

    document.getElementById("myVideo").play();

    this.width=window.innerWidth-1;
    this.height=window.innerHeight-5.81;
    this.height_inner=this.$refs.attrRef.offsetHeight; 
    // alert(this.height_inner)

    window.addEventListener('mousewheel',this.handleScroll,{
        passive: false,
      });
  },
  methods: {
    handleSelect(index){
      if(index=='1'){
        this.activeIndex='1';
        this.up();
      }
      if(index=='2'){
        this.activeIndex='2';
      }
    },
    handleScroll(e){
      e.preventDefault();
      if(!this.throttle){
        this.throttle=true;
        if (e.wheelDelta || e.detail) {
          if (e.wheelDelta > 0 || e.detail < 0) {     //当鼠标滚轮向上滚动时
            this.up(); 
          }
          if (e.wheelDelta < 0 || e.detail > 0) {     //当鼠标滚轮向下滚动时
            this.down();
          }
        }
        let timer = setTimeout(() => {
          this.throttle=false;
        }, 800)
      }
      else return;
    },
    up(){
      this.isShow=false;
      let timer = setInterval(() => {
        if(Math.abs(this.indexTop)>this.height/8*7&&Math.abs(this.indexTop)<=this.height){
          this.indexTop+=3;
        }
        if(Math.abs(this.indexTop)>this.height/4*3&&Math.abs(this.indexTop)<=this.height/8*7){
          this.indexTop+=8;
          this.selectTop=-3000;
        }
        if(Math.abs(this.indexTop)>this.height/4 && Math.abs(this.indexTop)<=this.height/4*3){
          this.indexTop+=10;
        }
        if(Math.abs(this.indexTop)>this.height/8 && Math.abs(this.indexTop)<=this.height/4){
          this.indexTop+=6;
        }
        if(Math.abs(this.indexTop)>0 && Math.abs(this.indexTop)<=this.height/8){
          this.indexTop+=3;
        }
        if(this.indexTop>0){
          clearTimeout(timer);
          this.indexTop=0;
          
        }
      }, 1);
      
    },
    down(){
      let timer = setInterval(() => {
        if(Math.abs(this.indexTop)>=0 && Math.abs(this.indexTop)<this.height/8){
          this.indexTop-=3;
        }
        if(Math.abs(this.indexTop)>=this.height/8 && Math.abs(this.indexTop)<this.height/4){
          this.indexTop-=8;
        }
        if(Math.abs(this.indexTop)>=this.height/4 && Math.abs(this.indexTop)<this.height/4*3){
          this.indexTop-=10;
        }
        if(Math.abs(this.indexTop)>=this.height/4*3 && Math.abs(this.indexTop)<this.height/8*7){
          this.indexTop-=6;
        }
        if(Math.abs(this.indexTop)>=this.height/8*7 && Math.abs(this.indexTop)<this.height){
          this.isShow=true;
          this.indexTop-=3;
        }
        if(Math.abs(this.indexTop)>=this.height){
          clearTimeout(timer);
          this.indexTop=-this.height;
          this.selectTop=0;
          this.activeIndex='2';
        }
      }, 1);
    }
  }
};
</script>