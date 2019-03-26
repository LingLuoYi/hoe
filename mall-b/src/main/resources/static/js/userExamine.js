layui.config({
    version: '1551352891258' //为了更新 js 缓存，可忽略
});

layui.use(['layer', 'table'], function(){
    var layer = layui.layer //弹层
        ,table = layui.table; //表格

    //执行一个 table 实例
    table.render({
        elem: '#demo'
        ,height: 'full-70'
        ,url: '/user/user_all_data' //数据接口
        ,toolbar: 'accept'
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,where:{
            userStart: 1
        }
        ,page: true //开启分页
        ,cols: [[ //表头
            {type: 'checkbox', fixed: 'left'}
            ,{field:'id', title: '序号',  sort: true, fixed: true}
            ,{field:'userId', title: '用户id',sort: true}
            ,{field:'name', title: '昵称',sort: true }
            ,{field:'phone', title: '手机号',  sort: true}
            ,{field:'email', title: '邮箱', sort: true}
            ,{field:'imgUrl', title: '头像', sort: true, templet: '#imgUrl'}
            ,{field:'IDCardNo', title: '身份证号', sort: true}
            ,{field:'userStart', title: '用户状态', sort: true}
            ,{field:'Roles', title: '角色', sort: true, templet: '#Roles'}
            ,{field:'Wallet', title: '钱包', sort: true , templet: '#Wallet'}
            ,{field:'address', title: '收货地址', sort: true,templet: '#address'}
            ,{field:'profit', title: '累计收益', sort: true}
        ]]
        ,id: 'testReload'
    });


    var $ = layui.$, active = {
        reload: function(){
            var demoReload = $('#demoReload');

            //执行重载
            table.reload('testReload', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                ,where: {
                    id: demoReload.val()
                }
            });
        }
    };

    $('.demoTable .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    //监听头工具栏事件
    table.on('toolbar(test)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id)
            ,data = checkStatus.data; //获取选中的数据
        switch(obj.event){
            case 'add':
                layer.msg('添加');
                break;
            case 'update':
                if(data.length === 0){
                    layer.msg('请选择一行');
                } else if(data.length > 1){
                    layer.msg('只能同时编辑一个');
                } else {
                    layer.alert('编辑 [id]：'+ checkStatus.data[0].id);
                }
                break;
            case 'delete':
                if(data.length === 0){
                    layer.msg('请选择一行');
                } else {
                    layer.msg('删除');
                }
                break;
        };
    });

    //监听行工具事件
    table.on('tool(test)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
        var data = obj.data //获得当前行数据
            ,layEvent = obj.event; //获得 lay-event 对应的值
        if(layEvent === 'detail'){
            layer.msg('查看操作');
        } else if(layEvent === 'del'){
            layer.confirm('真的删除行么', function(index){
                obj.del(); //删除对应行（tr）的DOM结构
                layer.close(index);
                //向服务端发送删除指令
            });
        } else if(layEvent === 'edit'){
            layer.msg('编辑操作');
        }
    });

    table.on('row(test)',function (res) {
        var index = layer.open({
            type: 2,
            content: '/admin/user/layer/examine',
            area: ['500px', '800px'],
            maxmin: true,
            success:function (layero,index) {
                var iframe = window['layui-layer-iframe' + index];
                iframe.child(res.data);
            }
        });
        layer.full(index);
    })


});