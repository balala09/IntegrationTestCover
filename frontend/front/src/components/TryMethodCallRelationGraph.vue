<template>
    <!-- <el-header>Method Call Relation Graph</el-header> -->
    <el-container>
        <!-- <el-aside width="350px">
                <el-card :body-style="{ padding: '0px' }" class="card">
                    <el-upload class="upload" action="/apiurl/uploadJar" accept="application/jar" :before-upload="onBeforeUpload" ref="upload" :file-list="fileList" :auto-upload="false">
                        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
                        <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button>
                        <div slot="tip" class="el-upload__tip">只能上传jar文件</div>
                    </el-upload>
                </el-card>
                <el-card :body-style="{ padding: '0px' }" class="card">
                    <el-container class="formbody">
                        <el-form ref="form" :model="form" label-width="80px">
                            <el-form-item label="Jar包选择">
                                <el-select v-model="form.selectedjar" placeholder="请选择jar包" @visible-change="showfilelist">
                                    <el-option
                                            v-for="item in uploadedFiles"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="包范围">
                                <el-input type="textarea" v-model="form.packages" placeholder="请输入包范围"></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" size="small" @click="generateGraph()">立即创建</el-button>
                            </el-form-item>
                        </el-form>
                    </el-container>
                </el-card>
                <el-card :body-style="{ padding: '0px' }" class="card">
                    <el-container class="formbody">
                        <el-form ref="form" :model="adjustform" label-width="80px">
                            <el-form-item label="类选择">
                                <el-select filterable  v-model="adjustform.selectedClass" placeholder="请选择类" @change="getClass($event)">
                                    <el-option
                                            v-for="item in classes"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="方法选择">
                                <el-select filterable  v-model="adjustform.selectedMethod" placeholder="请选择类" @visible-change="showfilelist">
                                    <el-option
                                            v-for="item in methods"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" size="small" @click="goToNode()">立即定位</el-button>
                            </el-form-item>
                        </el-form>
                    </el-container>
                </el-card>
        </el-aside> -->
        <el-main style="padding:0;">
            <div id="container" class="container">
                <svg id="mainsvg" class="svg">

                </svg>
            </div>
            <div id="thumb-container">
                <svg id="thumb" xmlns:xlink="http://www.w3.org/1999/xlink" viewBox="0 0 1000 1000">
                    <use xlink:href="#mainsvg" />
                </svg>
            </div>
        </el-main>
        <div class="myfoot">
                <!-- <el-upload class="upload" action="/apiurl/uploadJar" accept="application/jar" :before-upload="onBeforeUpload" ref="upload" :file-list="fileList" :auto-upload="false">
                    <el-button slot="trigger" size="small" type="primary">选取jar包</el-button>
                    <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">上传到服务器</el-button>
                </el-upload> -->
                <el-container>
                        <el-form :inline="true" ref="form" :model="form" label-width="80px">
                            <el-form-item label="Jar包选择">
                                <el-select v-model="form.selectedjar" placeholder="请选择jar包" @visible-change="showfilelist" filterable @change="generateGraph()">
                                    <el-option
                                            v-for="item in uploadedFiles"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item >
                                <el-input v-model="form.packages" placeholder="请输入包范围"></el-input>
                            </el-form-item>
                            <!-- <el-form-item>
                                <el-button type="primary" size="small" @click="generateGraph()">立即创建</el-button>
                            </el-form-item> -->
                        </el-form>

                        <el-form :inline="true" ref="form" :model="adjustform" label-width="80px">
                            <el-form-item >
                                <el-select filterable  v-model="adjustform.selectedClass" placeholder="请选择类" @change="getClass($event)">
                                    <el-option
                                            v-for="item in classes"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item >
                                <el-select filterable  v-model="adjustform.selectedMethod" placeholder="请选择方法" @visible-change="showfilelist">
                                    <el-option
                                            v-for="item in methods"
                                            :key="item"
                                            :label="item"
                                            :value="item">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="goToNode()" icon="el-icon-location-outline" plain>立即定位</el-button>
                            </el-form-item>
                        </el-form>
                </el-container>
        </div>
    </el-container>
</template>

<script>
    import ElContainer from "element-ui/packages/container/src/main";
    import ElButton from "element-ui/packages/button/src/button";
    import { getUploadedFileList, getRelationByFileName } from '@/api/methodcallrelationgraph.js'
    import * as d3 from 'd3'
    export default {
        components: {
            ElButton,
            ElContainer},
        name: "method-call-relation-graph",
        data () {
            return {
                relation: {},
                cname: '',
                fileList:[],
                form: {
                    selectedjar: '',
                    packages:''
                },
                adjustform: {
                    selectedClass: '',
                    selectedMethod: ''

                },
                uploadedFiles:[],
                classes:["a", "b"],
                methods:[],
                classMethodMap:{"a": ["a","b"], "b": ["a","c"]},
                g:{},
                tempTrans:{},
                lastNode: {x: 510, y: 300}
            }
        },
        methods: {
            goToNode() {
                var node = this.findNodeByName(this.adjustform.selectedClass + ":" + this.adjustform.selectedMethod)
                var trans = this.tempTrans
                
                trans.k = 1;
                this.g.attr('transform',trans);

                trans.x = 510 - node.x
                trans.y = 300 - node.y
                // console.log(trans)
                // console.log(this.relation.nodes)
                this.g.attr('transform', trans)
            },
            findNodeByName(name) {
                for(let index in this.relation.nodes) {
                    if(this.relation.nodes[index].name == name) {
                        console.log(this.relation.nodes[index])
                        return this.relation.nodes[index]
                    }
                }
            },
            getClass(prov) {
                this.methods = this.classMethodMap[prov]
            },
            showfilelist(open){
                if(open) {
                    let _this = this
                    getUploadedFileList().then(response => {
                        _this.uploadedFiles = response.result
                    })
                }
            },
            generateGraph(){
                let _this = this
                this.$nextTick(() => {
                    getRelationByFileName(this.form.selectedjar, this.form.packages).then(response => {
                        _this.relation.nodes = response.nodes
                        _this.relation.links = response.links
                        _this.classes = response.classes
                        _this.classMethodMap = response.classMethodMap
                        console.log(response.classMethodMap)
                        _this.showd3()
                    })
                })

            },
            submitUpload() {
                this.$refs.upload.submit();
            },
            onBeforeUpload(file) {

            },
            showd3 () {
                //        获取body高度和宽度
                let height = document.body.clientHeight
                let width = document.body.clientWidth

                //移动端设备横竖屏重新加载页面
                function change () {
                    // window.location.reload()
                }

                window.addEventListener('onorientationchange' in window ? 'orientationchange' : 'resize', change, false)
                //        节点大小（圆圈大小）
                const nodeSize = 35
                //        初始化时连接线的距离长度
                const linkDistance = 130
                //        赋值数据集
                var nodes = this.relation.nodes
                var links = this.relation.links
                
                var svg = d3.select('#mainsvg');
                
                // 移除上一个画布（如果有的话）
                if(svg.selectAll("g").size() > 0){
                    svg.selectAll("g").remove();
                }

                    //        设置力布局，使用d3 v4版本的力导向布局
                var force = d3.forceSimulation()
                    .force('center', d3.forceCenter(width / 2 - 200, height / 2 -100))//设置力导向布局的中心点，创建一个力中心，设置为画布长宽的一半，所以拓扑图会在画布的中心点
                    .force('charce', d3.forceManyBody().strength(-300))//节点间的作用力
                    .force('collide', d3.forceCollide().radius(() => 30))//使用30的半径创建一个碰撞作用力

                    //        设置缩放
                    //        svg下嵌套g标签，缩放都在g标签上进行
                var g = svg.append('g')
                this.g = g
                var _this = this
                //        d3.zoom是设置缩放，pc端是滚轮进行缩放，在移动端可以通过两指进行缩放
                var zoomObj = d3.zoom()
                    .scaleExtent([0.5, 1.2]) // 设置缩放范围
                    .on('zoom', () => {
                        //监听zoom事件，zoom发生时，调用该方法
                        const transform = d3.event.transform //获取缩放和偏移的数据，不懂得同学可以自行通过console.log(d3.event.transform)滑动滚轮查看数据变化
                        _this.tempTrans = d3.event.transform
                        console.log(d3.event.transform.toString());
                        g.attr('transform', transform)   // 设置缩放和偏移量 transform对象自带toString()方法
                    })
                    .on('end', () => {
                    //            该方法在缩放时间结束后回调
                        // code
                    })
                svg.call(zoomObj).on("dblclick.zoom", null)
                //        绘制箭头
                //箭头
                // eslint-disable-next-line no-unused-vars
                var marker =
                    g.append('marker')
                        .attr('id', 'resolved')
                        //.attr("markerUnits","strokeWidth")//设置为strokeWidth箭头会随着线的粗细发生变化
                        .attr('markerUnits', 'userSpaceOnUse')//用于确定marker是否进行缩放。取值strokeWidth和userSpaceOnUse，
                        .attr('viewBox', '0 -5 10 10')//坐标系的区域
                        .attr('refX', 39)//箭头坐标
                        .attr('refY', 0)
                        .attr('markerWidth', 12)//标识的大小
                        .attr('markerHeight', 12)
                        .attr('orient', 'auto')//绘制方向，可设定为：auto（自动确认方向）和 角度值
                        .attr('stroke-width', 2)//箭头宽度
                        .append('path')
                        .attr('d', 'M0,-5L10,0L0,5')//箭头的路径
                        .attr('fill', '#ff7438')//箭头颜色

                    //        设置连线
                var edgesLine = g.selectAll('line')
                    .data(links)
                    .enter()
                    .append('path')
                    .attr('class', 'edgelabel')//添加class样式
                    .style('stroke', '#ff7438')//添加颜色
                    .style('stroke-width', 1)//连接线粗细度
                    .attr('marker-end', 'url(#resolved)')//设置线的末尾为刚刚的箭头
                    //        设置连接线中间关系文本
                var edgesText = g.selectAll('.linetext')
                    .data(links)
                    .enter()
                    .append('text')
                    .attr('class', 'linetext')
                    .text((d) => {
                        //          设置关系文本
                        return d.relation
                    })
                        //        设置拖拽
                var drag = d3.drag()
                    .on('start', (d, i) => {
                        if (!d3.event.active) {
                            //              拖拽开始回调
                            force.alphaTarget(0.1).restart() // 这个方法可以用在在交互时重新启动仿真，比如拖拽了某个节点，重新进行布局。这个必须要进行设置不然会拖动不了。
                        }
                        //d.fixed = true //偏移后固定不动
                        if(!d.fixed)
                        {
                            d.fx = null//记录当前默认位置（x - 节点当前的 x-位置，如果要为某个节点设置默认的位置，则需要为该节点设置如下两个属性:fx =x位置）
                            d.fy = null
                        }
                    })
                    .on('drag', (d, i) => {
                        //            拖动时，设置拖动后默认位置的x，y
                        d.fx = d3.event.x
                        d.fy = d3.event.y
                    })
                    .on('end', (d, i) => {
                        //            拖动结束后
                        if (!d3.event.active) {
                            force.alphaTarget(0)
                        }
                        d.fixed = true;
                    })

                        //        设置节点
                var node = g.selectAll('circle')
                    .data(nodes)
                    .enter()
                    .append('circle')
                    .attr('r', nodeSize)
                    .attr('class','node')
                    .attr('fill',(d,i) => {
                        return stringToColour(d.name.split(":")[0]);
                    })
                    .attr('id', (d, i) => {
                        //            为每个节点设置不同的id
                        return 'node' + i
                    })
                    .on('touchmove', (d, i) => {
                        //            设置鼠标监听时间，当移动端手指移动时,设置关系文本透明度
                        edgesText.style('fill-opacity', function (edge) {
                            if (edge.source === d || edge.target === d) {
                                return 1.0
                            } else {
                                return 0
                            }
                        })
                    })
                    .on('touchend', (d, i) => {
                        //            手指移开后，所有关系文本设置透明度为1
                        edgesText.style('fill-opacity', 1.0);
                    })
                    .on('click', (d, i) => {
                        edgesText.style('fill-opacity', function (edge) {
                            if (edge.source === d || edge.target === d) {
                                return 1.0
                            } else {
                                return 0
                            }
                        })
                        d3.select('#node' + i).raise()
                        d3.select('#nodetext' + i).raise()
                        console.log(d)
                    })
                    .on('mouseout', (d, i) => {
                        edgesText.style('fill-opacity',1.0);
                        d3.select('#nodetext' + i).classed('highlighted',false);
                    })
                    .on('dblclick', (d, i) => {
                        d.fx = null
                        d.fy = null
                    })
                    .on('mouseover',(d,i) => {
                        d3.select('#nodetext' + i).classed('highlighted',true);
                    })
                    .call(drag)//监听拖动事件

                    // 节点文字
                var nodeText = g.selectAll('.nodetext')
                    .data(nodes)
                    .enter()
                    .append('text')
                    .attr('text-anchor', 'middle')
                    .attr('class', 'nodetext')
                    .attr('id', (d, i) => {
                        return 'nodetext' + i
                    })
                    .attr('x', function (d, i) {
                        /**
                         * 由于svg的text不能进行换行，所以下面文字使用了tspan进行换行操作
                         */
                            //正则表达式
                        var reEn = /[a-zA-Z]+/g
                        //如果全英文则不换行
                        if (d.name.match(reEn)) {
                            d3.select(this).append('tspan')
                                .attr('class', 'nodetext')
                                .attr('fill', '#ff7438')
                                .text(function () { return d.name })
                        } else if (d.name.length <= 4) {
                            //文中小于4个字不换行
                            d3.select(this).append('tspan')
                                .attr('class', 'nodetext')
                                .attr('fill', '#ff7438')
                                .text(function () { return d.name })
                        } else {
                            if (d.name.length <= 8) {
                                //中文小于八个字，则分段进行换行
                                let top = d.name.substring(0, 4)
                                let bot = d.name.substring(4, 8)
                                //这里的this指代text dom，不懂的可以自行打印this查看
                                d3.select(this).append('tspan')
                                    .text(function () { return top })
                                d3.select(this).append('tspan')
                                    .attr('dy', '1.2em')//设置偏移
                                    .text(function () { return bot })
                            } else {
                                //中文大于8个字，分段并用...代替后面的字符
                                let top = d.name.substring(0, 4)
                                let bot = d.name.substring(4, 7) + '...'
                                d3.select(this).append('tspan')
                                    .text(function () { return top })
                                d3.select(this).append('tspan')
                                    .attr('dy', '1.2em')
                                    .text(function () { return bot })
                            }
                        }
                    });

                    //        设置node和edge
                force.nodes(nodes)
                    .force('link', d3.forceLink(links).distance(linkDistance).strength(0.1))
                    .restart()
                    //        tick 表示当运动进行中每更新一帧时
                force.on('tick', function () {
                    //          //更新连接线的位置
                    edgesLine.attr('d', function (d) {
                        var path = 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y
                        return path
                    })

                    //更新连接线上文字的位置
                    edgesText.attr('x', function (d) {
                        return (d.source.x + d.target.x) / 2
                    })
                    edgesText.attr('y', function (d) { return (d.source.y + d.target.y) / 2 })

                    //更新结点图片和文字
                    node.attr('cx', function (d) {
                        return d.x
                    })
                    node.attr('cy', function (d) { return d.y })

                    nodeText.attr('x', function (d) { return d.x })
                    nodeText.attr('y', function (d) { return d.y })
                    //动态更新sptan 的x的坐标
                    nodeText.selectAll('tspan')
                        .attr('x', function (d) {
                            return d.x
                        })
                })
            }
        },
        created () {
            this.$nextTick(() => {
                // this.relation = JSON.parse('{"nodes":[{"name":"BetterVicky.method1","type":0},{"name":"BetterVicky.method2","type":1},{"name":"MyTestClass.method1","type":1},{"name":"MyRunner.methodhaha","type":1},{"name":"VideoCapture.methodxixi","type":2},{"name":"Render.Methodhehe","type":2}],"links":[{"source":0,"target":1,"relation":"对外投资"},{"source":0,"target":2,"relation":"对外投资"},{"source":0,"target":3,"relation":"对外投资"},{"source":4,"target":0,"relation":"投资"},{"source":5,"target":0,"relation":"投资"}],"code":200,"message":"请求成功"}');
                this.showd3();
            })
        }
    }

    function stringToColour(str) {
        var hash = 0;
        for (var i = 0; i < str.length; i++) {
            hash = str.charCodeAt(i) + ((hash << 5) - hash);
        }
        var colour = '#';
        for (var i = 0; i < 3; i++) {
            var value = (hash >> (i * 8)) & 0xFF;
            colour += ('00' + value.toString(16)).substr(-2);
        }
        return colour;
    }
</script>

<style scoped lang="less">
    .myfoot{
        position:absolute;
        bottom: 0;
    }

    .card {
        margin:25px 0px 25px 0px;
    }
    .upload {
        margin:25px 0px 25px 0px;
    }

    #thumb {
        width: 160px;
        height: 160px;
    }

    #thumb-container {
        position: absolute;
        right: 0;
        bottom: 0;
        margin: 20px;
        border: 1px solid silver;
        background-color: white;
        box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2);
    }


    .container {
        height: 600px;
        .exit {
            position: absolute;
            top: 20px;
            left: 20px;
            width: 60px;
            height: 25px;
            background: #0583f2;
            border: none;
            border-radius: 2px;
            color: #000;
            z-index: 200;
            &:hover {
                 background: #1e82d9;
            }
        };
        overflow-y: scroll;
    }

    .container::-webkit-scrollbar { 
        display: none;
    }

    .formlabelfont {
        font-size: 12px;
    }

    .labeltext {
        font-size: 16px;
        font-family: SimSun;
        fill: #ff7438;
    }

    .nodetext {
        font-size: 12px;
        font-family: SimSun;
        fill: #000;
        position: relative;
        pointer-events: none;
    }

    .highlighted {
        font-weight: bold;
        font-size: 15px;
        color:black;
    }

    .linetext {
        font-size: 12px;
        font-weight: bold;
        font-family: SimSun;
        fill: #000 !important;
        color: #000;
        fill-opacity: 1;
    }

    .svg {
        position: relative;
        width: 100%;
        height: 100%;
    }

    .edgepath {
        pointer-events: none;
        stroke-width: 0.5px;
    }

    .node{
        position: relative;
    }

    .node:hover{
        cursor: pointer;
    }
</style>