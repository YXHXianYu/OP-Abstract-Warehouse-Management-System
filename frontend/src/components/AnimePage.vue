<template>
    <div id="div" style="">
        <el-button @click="createGoodRest()">创建货架</el-button>
        <canvas id="canvas" ref="canvas" style="z-index:1"></canvas> 
    </div>
</template>

<script>
export default{
    data(){
        return{
            canvas: null,
            ctx: null,

            /**
             *  0-无状态
             *  1-创建货架
             *  2-选中货架
             *  3-货架拖动
             *  4-选中小车
             *  5-创建出入口
             *  6-选中出入口
             *  7-鼠标悬浮于货架
             */
            state:0,
            mouse:{x:0,y:0},
            nowID:-1,//当前选中的货架,进行颜色高亮
            preID:-1,//鼠标悬浮的货架，进行周边框修饰
            data:{
                goodrest:[],
            }
        }
    },
    mounted() {

      this.canvas = this.$refs.canvas;  
      this.ctx = this.canvas.getContext('2d');  
      this.ctx.imageSmoothingEnabled = true;
      this.ctx.imageSmoothingQuality = 'high';

      this.canvas.addEventListener('mousedown', this.handleMouseDown);
      this.canvas.addEventListener('dblclick', this.handleDbMouseDown);
      this.canvas.addEventListener('mouseup', this.handleMouseUp);
      this.canvas.addEventListener('mousemove', this.handleMouseMove);
      this.canvas.addEventListener('contextmenu', this.handleContextMenu);

      window.addEventListener('keydown', this.handleKey);

      window.addEventListener('keydown', this.saveContent)
      window.addEventListener('copy', this.handleCopy);
      window.addEventListener('paste', this.handlePaste);
      window.addEventListener('resize', this.resize)
      this.canvas.addEventListener("mousewheel", this.handleScroll, true);
      this.canvas.style.backgroundColor = "rgb(252,255,222)";

      this.initCanvasSize();
    //   this.refresh();

      
    //   window.addEventListener('wheel', this.handleMouseWheel, {
    //     passive: false,
    //   })
    //   // 禁止鼠标右键
    //   window.oncontextmenu = function () { return false; };
    },
    methods:{
        initCanvasSize(){
            var div = document.getElementById("div");
            this.canvas.width = div.clientWidth-10;
            // this.canvas.height = div.clientHeight-40;
            this.canvas.height = 0.7* window.innerHeight;
        },
        createGoodRest(){

            //创建货架
            this.data.goodrest.push({
                x:0,y:0,
                attr:{width:100,height:300},
            });

            this.state=1;
        },
        //鼠标移动
        handleMouseMove(event) {
            event.preventDefault();   
            const {
                offsetX,
                offsetY
            } = event;
            this.mouse.x=offsetX;this.mouse.y=offsetY;

            if(this.state==7)this.state=0;

            //货架悬浮
            if(this.state==0){
                for(let i=0;i<this.data.goodrest.length;i++){
                    if(offsetX>=this.data.goodrest[i].x-this.data.goodrest[i].attr.width/2
                    &&offsetX<=this.data.goodrest[i].x+this.data.goodrest[i].attr.width/2
                    &&offsetY>=this.data.goodrest[i].y-this.data.goodrest[i].attr.height/2
                    &&offsetY<=this.data.goodrest[i].y+this.data.goodrest[i].attr.height/2){
                        this.preID=i;
                        this.state=7;
                        break;
                    }
                    else{
                        this.state=0;
                    }
                }
            }

            //创建货架
            if(this.state==1){
                this.data.goodrest[this.data.goodrest.length-1].x=this.mouse.x;
                this.data.goodrest[this.data.goodrest.length-1].y=this.mouse.y;
            }
            this.draw();
        },

        //鼠标按下
        handleMouseDown(event) {
            const {
                offsetX,
                offsetY
            } = event;

            this.state=0;

            //货架选择
            if(this.state==0){
                for(let i=0;i<this.data.goodrest.length;i++){
                    if(offsetX>=this.data.goodrest[i].x-this.data.goodrest[i].attr.width/2
                    &&offsetX<=this.data.goodrest[i].x+this.data.goodrest[i].attr.width/2
                    &&offsetY>=this.data.goodrest[i].y-this.data.goodrest[i].attr.height/2
                    &&offsetY<=this.data.goodrest[i].y+this.data.goodrest[i].attr.height/2){
                        this.nowID=i;
                        this.state=2;
                    }
                }
                if(this.state!=2){
                    this.state=0;
                }
            }

            //货架创建
            if(this.state==1){
                this.state=0;
            }
            this.draw();
        },
        draw() {    
            // const startTime = new Date();
            // this.isDrawing=1;//渲染标识
            // // 清除画布
            this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);



            //货架渲染
            for(let i=0;i<this.data.goodrest.length;i++){

                this.ctx.fillStyle = 'rgb(108,171,207)';

                this.ctx.beginPath();
                this.ctx.fillRect(this.data.goodrest[i].x-this.data.goodrest[i].attr.width/2,this.data.goodrest[i].y-this.data.goodrest[i].attr.height/2,this.data.goodrest[i].attr.width,this.data.goodrest[i].attr.height);
                this.ctx.closePath();

                //选中框
                if(this.state==2 && i==this.nowID){
                    this.ctx.fillStyle = 'rgba(204,232,255,0.7)';
                    this.ctx.beginPath();
                    this.ctx.fillRect(this.data.goodrest[i].x-this.data.goodrest[i].attr.width/2-10,this.data.goodrest[i].y-this.data.goodrest[i].attr.height/2-10,this.data.goodrest[i].attr.width+20,this.data.goodrest[i].attr.height+20);
                    this.ctx.closePath();
                }

                //悬浮框
                if(this.state==7 && i==this.preID){
                    this.ctx.fillStyle = 'rgba(229,243,255,0.6)';
                    this.ctx.beginPath();
                    this.ctx.fillRect(this.data.goodrest[i].x-this.data.goodrest[i].attr.width/2-10,this.data.goodrest[i    ].y-this.data.goodrest[i].attr.height/2-10,this.data.goodrest[i].attr.width+20,this.data.goodrest[i].attr.height+20);
                    this.ctx.closePath();
                }

                //文字
                let str="货架 #"+(i+1);
                this.ctx.fillStyle = 'black';
                this.ctx.font = "16px 微软雅黑";
                this.ctx.fillText(str, this.data.goodrest[i].x-30, this.data.goodrest[i].y-10);
            }
            
            


            // if(state==0){

            // }
            // else if(state==1){

            // }
            // else if(state==2){

            // }

            // //绘制底图
            // if(this.myImg!=null && this.viewInfo.isViewImg){
            // this.ctxBuffer.drawImage(this.myImg,this.viewInfo.imgX0, this.viewInfo.imgY0, this.viewInfo.imgX1-this.viewInfo.imgX0, this.viewInfo.imgY1-this.viewInfo.imgY0);
            
            // }
            // if(this.viewInfo.isViewBorder){
            // this.ctxBuffer.strokeStyle = 'black';
            // this.ctxBuffer.strokeRect(this.viewInfo.imgX0-1,this.viewInfo.imgY0+1,this.viewInfo.imgX1-this.viewInfo.imgX0+2,this.viewInfo.imgY1-this.viewInfo.imgY0+2);
            // }


            // //绘制比例尺
            // this.ctxBuffer.font = '10px Arial';
            // this.ctxBuffer.strokeStyle = 'black'; // 设置线的颜色为蓝色
            // this.ctxBuffer.lineWidth = 1; // 设置线的宽度为2
            // this.ctxBuffer.beginPath();
            // this.ctxBuffer.moveTo(this.canvas.width-110, 15);
            // this.ctxBuffer.lineTo(this.canvas.width-110, 22);
            // this.ctxBuffer.lineTo(this.canvas.width-40, 22);
            // this.ctxBuffer.lineTo(this.canvas.width-40, 15);
            // this.ctxBuffer.stroke();
            // this.ctxBuffer.strokeText(0, this.canvas.width-110-5, 10, 20)
            // let a = this.viewInfo.sT
            // a = (a*1).toFixed(2)
            // this.ctxBuffer.strokeText(a+"m", this.canvas.width-40-10, 10, 40)

            // /**绘制选定框 */
            // this.ctxBuffer.fillStyle = "rgba(204, 232, 255,0.4)";
            // for(let i=0;i<this.ks.length;i++){
            // this.ctxBuffer.fillRect(this.ks[i].area.x0,this.ks[i].area.y0,this.ks[i].area.x1-this.ks[i].area.x0,this.ks[i].area.y1-this.ks[i].area.y0);
            // }

            // /**绘制房间类*/
            // //新建房间框拖动
            // if(this.TID==5 && this.createNewRoom==1){
            //     //外部框
            //     let j = this.rooms.length-1;
            //     this.ctxBuffer.fillStyle = "rgba(204, 232, 255,0.4)";
            //     this.ctxBuffer.beginPath();
            //     this.ctxBuffer.fillRect(this.rooms[j].lca.X0-10, this.rooms[j].lca.Y0-10,this.rooms[j].lca.X1-this.rooms[j].lca.X0+20, this.rooms[j].lca.Y1-this.rooms[j].lca.Y0+20);
            //     this.ctxBuffer.closePath();
            // }
            // //静态房间绘制
            // if(this.viewInfo.isViewRoom){
            //     //有房间被选中
            //     if(this.TID==1||this.TID==2||this.TID==3||this.TID==13||this.TID==14){
            //         this.ctxBuffer.fillStyle = "rgba(173, 216, 230,0.6)"
            //         this.ctxBuffer.lineWidth = 1; // 设置线的宽度为2
            //         let i=this.roomRule.currentID;

            //         this.ctxBuffer.beginPath()
            //         this.ctxBuffer.fillRect(this.rooms[i].lca.X0-10*this.nST.sTX, this.rooms[i].lca.Y0-10*this.nST.sTX,this.rooms[i].lca.X1-this.rooms[i].lca.X0+20*this.nST.sTX, this.rooms[i].lca.Y1-this.rooms[i].lca.Y0+20*this.nST.sTX);
            //         this.ctxBuffer.closePath()
            //     }
            //     //有房间被光标覆盖
            //     else if(this.TID==12){
            //         this.ctxBuffer.fillStyle = "rgba(204, 232, 255,0.4)";
            //         let i=this.roomRule.currentViewID;
            //         this.ctxBuffer.beginPath()
            //         this.ctxBuffer.fillRect(this.rooms[i].lca.X0-10*this.nST.sTX, this.rooms[i].lca.Y0-10*this.nST.sTX,this.rooms[i].lca.X1-this.rooms[i].lca.X0+20*this.nST.sTX, this.rooms[i].lca.Y1-this.rooms[i].lca.Y0+20*this.nST.sTX)
            //         this.ctxBuffer.closePath()
            //     }
            //     //房间主体
            //     for(let j=0;j<this.rooms.length;j++){
            //         //绘制背景
            //         this.ctxBuffer.beginPath();
            //         if(this.rooms[j].walls.length==0){
            //         continue;
            //         }
            //         this.ctxBuffer.moveTo(this.rooms[j].walls[0].x,this.rooms[j].walls[0].y);
            //         for (let i = 1; i < this.rooms[j].walls.length; i++) {
            //             // const point1 = this.rooms[j].walls[i - 1];
            //             const point2 = this.rooms[j].walls[i];
            //             if(point2.x==-10000){
            //                 continue;
            //             }
            //             this.ctxBuffer.lineTo(point2.x, point2.y);
            //         }
            //         this.ctxBuffer.closePath();
            //         this.ctxBuffer.strokeStyle="rgba(0, 0, 0, 0)";
            //         this.ctxBuffer.stroke();
            //         this.ctxBuffer.fillStyle = this.rooms[j].attr.color;
            //         this.ctxBuffer.fill();

            //         //房间墙壁
            //         if(this.drawConfig[0].state){
            //         for (let i = 1; i < this.rooms[j].walls.length; i++) {
            //             const point1 = this.rooms[j].walls[i - 1];
            //             const point2 = this.rooms[j].walls[i];
            //             if(point1.x==-10000 || point2.x==-10000){
            //                 continue;
            //             }
            //             if(this.TID==14&&this.roomRule.currentID==j&&this.roomRule.numLine===i){
            //                 this.ctxBuffer.strokeStyle = 'red';
            //             }
            //             else{
            //                 this.ctxBuffer.strokeStyle = this.drawConfig[0].color;
            //             }
            //             this.ctxBuffer.beginPath();
            //             this.ctxBuffer.moveTo(point1.x, point1.y);
            //             this.ctxBuffer.lineTo(point2.x, point2.y);
            //             this.ctxBuffer.closePath();
            //             this.ctxBuffer.lineWidth = this.drawConfig[0].r*this.nST.sTX;
            //             this.ctxBuffer.stroke();

            //             //墙壁点绘制
            //             if(this.drawConfig[1].state){
            //                 this.ctxBuffer.strokeStyle = this.drawConfig[1].color;
            //                 this.ctxBuffer.beginPath();
            //                 this.ctxBuffer.arc(point1.x, point1.y, this.drawConfig[1].r*this.nST.sTX, 0, Math.PI * 2);
            //                 this.ctxBuffer.closePath();
            //                 this.ctxBuffer.stroke();
            //                 this.ctxBuffer.fillStyle = this.drawConfig[1].color;
            //                 this.ctxBuffer.fill();
            //                 this.ctxBuffer.strokeStyle = this.drawConfig[1].color;
            //                 this.ctxBuffer.beginPath();
            //                 this.ctxBuffer.arc(point2.x, point2.y, this.drawConfig[1].r*this.nST.sTX, 0, Math.PI * 2);
            //                 this.ctxBuffer.closePath();
            //                 this.ctxBuffer.stroke();
            //                 this.ctxBuffer.fillStyle = this.drawConfig[1].color;
            //                 this.ctxBuffer.fill();
            //             }
            //         }
            //         }
                    

            //         //房间人群
            //         if(this.viewInfo.isViewPeos && this.TID==0 && this.drawConfig[2].state ){
            //             this.ctxBuffer.fillStyle = 'green';
            //             for (let i = 0; i < this.rooms[j].peos.length; i+=2) {
            //             this.ctxBuffer.beginPath();
            //             this.ctxBuffer.arc(this.rooms[j].peos[i].x, this.rooms[j].peos[i].y, this.drawConfig[2].r*this.nST.sTX, 0, Math.PI * 2);
            //             this.ctxBuffer.fill();
                        
            //             }
            //             this.ctxBuffer.fillStyle = 'blue';
            //             for (let i = 1; i < this.rooms[j].peos.length; i+=2) {
            //                 this.ctxBuffer.beginPath();
            //                 this.ctxBuffer.arc(this.rooms[j].peos[i].x, this.rooms[j].peos[i].y, this.drawConfig[2].r*this.nST.sTX, 0, Math.PI * 2);
            //                 this.ctxBuffer.fill();
            //             }
            //         }

            //         //房间编号+名称
            //         if(this.viewInfo.isViewRoomId&&this.viewInfo.isViewRoomName){
            //         this.ctxBuffer.fillStyle = 'black';
            //         this.ctxBuffer.font = this.viewInfo.fontSize*this.nST.sTX+'px 微软雅黑 Narrow';
            //         if (this.rooms[j].attr.id==undefined){continue;}
            //         let str = (this.rooms[j].attr.id).toString()+"#";
            //         let len = this.cal_font_len(str);
            //         this.ctxBuffer.fillText(str, (this.rooms[j].lca.X1+this.rooms[j].lca.X0)/2-len*20*this.nST.sTX/2, (this.rooms[j].lca.Y1+this.rooms[j].lca.Y0)/2-8*this.nST.sTX);
            //         if (this.rooms[j].attr.name==undefined){continue;}
            //         str = (this.rooms[j].attr.name).toString()+'('+this.rooms[j].peos.length+'人)';
            //         len = this.cal_font_len(str);
            //         this.ctxBuffer.fillText(str, (this.rooms[j].lca.X1+this.rooms[j].lca.X0)/2-len*20*this.nST.sTX/2, (this.rooms[j].lca.Y1+this.rooms[j].lca.Y0)/2+14*this.nST.sTX);
            //         }
            //         else{
            //         if(this.viewInfo.isViewRoomId){
            //             this.ctxBuffer.fillStyle = 'black';
            //             this.ctxBuffer.font = this.viewInfo.fontSize*this.nST.sTX+'px 微软雅黑 Narrow';
            //             if (this.rooms[j].attr.id==undefined){continue;}
            //             let str = (this.rooms[j].attr.id).toString()+"#";
            //             let len = this.cal_font_len(str);
            //             this.ctxBuffer.fillText(str, (this.rooms[j].lca.X1+this.rooms[j].lca.X0)/2-len*20*this.nST.sTX/2, (this.rooms[j].lca.Y1+this.rooms[j].lca.Y0)/2+5*this.nST.sTX);
            //         } 
            //         if(this.viewInfo.isViewRoomName){
            //             this.ctxBuffer.fillStyle = 'black';
            //             this.ctxBuffer.font = this.viewInfo.fontSize*this.nST.sTX+'px 微软雅黑 Narrow';
            //             if (this.rooms[j].attr.name==undefined){continue;}
            //             let str = (this.rooms[j].attr.name).toString()+'('+this.rooms[j].peos.length+'人)'  ;
            //             let len = this.cal_font_len(str);
            //             this.ctxBuffer.fillText(str, (this.rooms[j].lca.X1+this.rooms[j].lca.X0)/2-len*20*this.nST.sTX/2, (this.rooms[j].lca.Y1+this.rooms[j].lca.Y0)/2+5*this.nST.sTX);
            //         }
            //         }
            //         this.ctxBuffer.stroke();
            //     }
            // }

            // /**绘制导航点类 */
            // if(this.viewInfo.isViewNav){
            // //绘制导航点鼠标标识
            //     if(this.TID==6){
            //     this.ctxBuffer.fillStyle = this.drawConfig[3].color; // 设置点的颜色为深蓝色
            //     this.ctxBuffer.beginPath();
            //     this.ctxBuffer.arc(this.viewInfo.x, this.viewInfo.y, this.drawConfig[3].r*this.nST.sTX, 0, Math.PI * 2);
            //     this.ctxBuffer.fill();
            //     }
            //     // //绘制已放置导航点
            //     if(this.drawConfig[3].state){
            //     this.ctxBuffer.fillStyle = this.drawConfig[3].color; // 设置点的颜色为深蓝色
            //     for (const point of this.pointsNav) {
            //         this.ctxBuffer.beginPath();
            //         if(point.x==null)
            //             continue;
            //         this.ctxBuffer.arc(point.x, point.y, this.drawConfig[3].r*this.nST.sTX, 0, Math.PI * 2);
            //         this.ctxBuffer.fill();
            //     }
            //     }

            //     //绘制导航线条
            //     if(this.drawConfig[4].state){
            //     this.ctxBuffer.strokeStyle = this.drawConfig[4].color; 
            //     this.ctxBuffer.lineWidth = this.drawConfig[4].r*this.nST.sTX;
            //     for (let i = 0; i < this.pointsNavView.length; i++) {
            //         if(this.pointsNavView[i].a>=this.pointsNav.length || this.pointsNavView[i].b>=this.pointsNav.length)
            //             continue;
            //         const point1 = this.pointsNav[this.pointsNavView[i].a];
            //         const point2 = this.pointsNav[this.pointsNavView[i].b];
            //         if(point1.x==null||point2.x==null)
            //             continue;
            //         if(point1.x===-10000 || point2.x===-10000){
            //             continue;
            //         }
            //         this.ctxBuffer.beginPath();
            //         this.ctxBuffer.moveTo(point1.x, point1.y);
            //         this.ctxBuffer.lineTo(point2.x, point2.y);
            //         this.ctxBuffer.stroke();
            //     }
            //     }
                
            //     if(this.drawConfig[3].state){
            //     //被选中导航点
            //     this.ctxBuffer.fillStyle = 'blue';
            //     if(this.TID==15 || this.TID==4){
            //         this.ctxBuffer.beginPath();
            //         if(this.numMovingNav>this.pointsNav.length-1){
            //             // alert(this.numMovingNav)
            //             // alert(this.pointsNav.length)
            //         }
            //         this.ctxBuffer.arc(this.pointsNav[this.numMovingNav].x, this.pointsNav[this.numMovingNav].y, this.drawConfig[3].r*this.nST.sTX, 0, Math.PI * 2);
            //         this.ctxBuffer.fill();
            //     }
            //     }
            // }

            // /**绘制出口 */
            // //出口框指示
            // if(this.TID==16){
            //     //外部框
            //     this.ctxBuffer.fillStyle = "rgba(204, 232, 255,0.4)";
            //     this.ctxBuffer.lineWidth = 1*this.nST.sTX;
            //     this.ctxBuffer.beginPath();
            //     this.ctxBuffer.fillRect(this.exits[this.numMovingExit].x0-10*this.nST.sTX, this.exits[this.numMovingExit].y0-10*this.nST.sTX,this.exits[this.numMovingExit].x1-this.exits[this.numMovingExit].x0+20*this.nST.sTX, this.exits[this.numMovingExit].y2-this.exits[this.numMovingExit].y0+20*this.nST.sTX);
            //     this.ctxBuffer.closePath();
            //     this.ctxBuffer.fill();
            // }
            // //出口框选中
            // if(this.TID==17){
            //     this.ctxBuffer.fillStyle = "rgba(173, 216, 230,0.6)"
            //     this.ctxBuffer.lineWidth = 1*this.nST.sTX; // 设置线的宽度为2
            //     // let i=this.roomRule.currentID;

            //     this.ctxBuffer.beginPath()
            //     this.ctxBuffer.fillRect(this.exits[this.numMovingExit].x0-10*this.nST.sTX, this.exits[this.numMovingExit].y0-10*this.nST.sTX,this.exits[this.numMovingExit].x1-this.exits[this.numMovingExit].x0+20*this.nST.sTX, this.exits[this.numMovingExit].y2-this.exits[this.numMovingExit].y0+20*this.nST.sTX);
            //     this.ctxBuffer.closePath()

            //     this.ctxBuffer.beginPath()
            //     this.ctxBuffer.fillStyle = "rgb(255,165,0)";
            //     this.ctxBuffer.fillRect(this.exits[this.numMovingExit].x0-10*this.nST.sTX, this.exits[this.numMovingExit].y0-10*this.nST.sTX,this.exits[this.numMovingExit].x1-this.exits[this.numMovingExit].x0+20*this.nST.sTX, this.exits[this.numMovingExit].y2-this.exits[this.numMovingExit].y0+20*this.nST.sTX);
            //     this.ctxBuffer.closePath()
            //     this.ctxBuffer.fillStyle = "rgba(204, 232, 255,0.4)"
            // }
            // //出口本体
            // this.ctxBuffer.strokeStyle = 'blue'; // 设置线的颜色为蓝色
            // this.ctxBuffer.lineWidth = 1*this.nST.sTX; // 设置线的宽度为2
            // for (let i = 0; i < this.exits.length; i++) {
            //     this.ctxBuffer.beginPath();
            //     this.ctxBuffer.moveTo(this.exits[i].x0, this.exits[i].y0);
            //     this.ctxBuffer.lineTo(this.exits[i].x1, this.exits[i].y1);
            //     this.ctxBuffer.stroke();
            //     this.ctxBuffer.moveTo(this.exits[i].x1, this.exits[i].y1);
            //     this.ctxBuffer.lineTo(this.exits[i].x2, this.exits[i].y2);
            //     this.ctxBuffer.stroke();
            //     this.ctxBuffer.moveTo(this.exits[i].x2, this.exits[i].y2);
            //     this.ctxBuffer.lineTo(this.exits[i].x3, this.exits[i].y3);
            //     this.ctxBuffer.stroke();
            //     this.ctxBuffer.moveTo(this.exits[i].x3, this.exits[i].y3);
            //     this.ctxBuffer.lineTo(this.exits[i].x0, this.exits[i].y0);
            //     this.ctxBuffer.stroke();

            //     if(this.viewInfo.isViewExportId&&this.viewInfo.isViewExportName){
            //     this.ctxBuffer.fillStyle = 'black';
            //     this.ctxBuffer.font = this.viewInfo.fontSize*this.nST.sTX+'px Arial';
            //     let str = this.exits[i].id+"#";
            //     let len = this.cal_font_len(str);
            //     this.ctxBuffer.fillText(str, (this.exits[i].x0+this.exits[i].x1)/2-len*20*this.nST.sTX/2, (this.exits[i].y0+this.exits[i].y3)/2-8*this.nST.sTX);
            //     str = this.exits[i].name;
            //     len = this.cal_font_len(str);
            //     this.ctxBuffer.fillText(str, (this.exits[i].x0+this.exits[i].x1)/2-len*20*this.nST.sTX/2, (this.exits[i].y0+this.exits[i].y3)/2+14*this.nST.sTX);
            //     }
            //     else{
            //     if(this.viewInfo.isViewExportId){
            //         this.ctxBuffer.fillStyle = 'black';
            //         this.ctxBuffer.font = this.viewInfo.fontSize*this.nST.sTX+'px Arial';
            //         let str = this.exits[i].id+"#";
            //         let len = this.cal_font_len(str);
            //         this.ctxBuffer.fillText(str, (this.exits[i].x0+this.exits[i].x1)/2-len*20*this.nST.sTX/2, (this.exits[i].y0+this.exits[i].y3)/2+5*this.nST.sTX);
            //     }
            //     if(this.viewInfo.isViewExportName){
            //         this.ctxBuffer.fillStyle = 'black';
            //         this.ctxBuffer.font = this.viewInfo.fontSize*this.nST.sTX+'px Arial';
            //         let str = this.exits[i].name;
            //         let len = this.cal_font_len(str);
            //         this.ctxBuffer.fillText(str, (this.exits[i].x0+this.exits[i].x1)/2-len*20*this.nST.sTX/2, (this.exits[i].y0+this.exits[i].y3)/2+5*this.nST.sTX);
            //     }
            //     }
            // }
            // /**结果人群渲染 */
            // if((this.show.isMoving==0&&this.TID==19) || (this.show.isMoving==0&&this.TID==11)){
            // this.ctxBuffer.fillStyle = 'green';
            // for(let j=0;j<this.show.showPeople.length;j++){
            //     // 绘制人群点
            //     // 设置点的颜色为深蓝色
            //     if(this.show.showPeople[j].id%2==0)continue;
            //     this.ctxBuffer.beginPath();
            //     let x_=this.show.yx+(this.show.showPeople[j].x)/10*(this.show.yx2-this.show.yx);
            //     let y_=this.show.yy+(this.show.showPeople[j].y)/10*(this.show.yy2-this.show.yy);
            //     this.ctxBuffer.arc(x_, y_, this.drawConfig[2].r*this.nST.sTX, 0, Math.PI * 2);
            //     this.ctxBuffer.fill();
            // }
            // this.ctxBuffer.fillStyle = 'blue';
            // for(let j=1;j<this.show.showPeople.length;j++){
            //     if(this.show.showPeople[j].id%2==1)continue;
            //     // 设置点的颜色为深蓝色
            //     this.ctxBuffer.beginPath();
            //     let x_=this.show.yx+(this.show.showPeople[j].x)/10*(this.show.yx2-this.show.yx);
            //     let y_=this.show.yy+(this.show.showPeople[j].y)/10*(this.show.yy2-this.show.yy);
            //     this.ctxBuffer.arc(x_, y_, this.drawConfig[2].r*this.nST.sTX, 0, Math.PI * 2);
            //     this.ctxBuffer.fill();
            // }
            // //渲染热力图
            // if(!this.viewInfo.isViewHeat){
            //     this.ctx_heat.clearRect(0, 0, this.canvas_heat.width, this.canvas_heat.height);
            // }
            // if(this.show.nowTime%10==0 && this.TID==19 && this.viewInfo.isViewHeat && this.data.length>0){
            //     html2canvas
            //     this.drawHeatMap();
            // }
            // this.ctxBuffer.drawImage(this.canvas_heat,0,0); 
            // }
            // this.isDrawing=0;//渲染标识
            // const endTime = new Date();
            // const executionTime = endTime - startTime;
            // console.log(`代码执行时间：${executionTime} 毫秒`);
        },
    }
}

</script>
