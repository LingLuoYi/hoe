layui.config({
    version: '1551352891258' //为了更新 js 缓存，可忽略
});

layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element', 'slider'], function(){
    var layer = layui.layer //弹层
        ,table = layui.table; //表格


    //执行一个 table 实例
    table.render({
        elem: '#demo'
        ,height: 'full-70'
        ,url: '/reflect/reflect_admin_all' //数据接口
        ,where: {
            state: '3'
        }
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,toolbar: 'accept'
        ,page: true //开启分页
        ,cols: [[ //表头
            {type: 'checkbox', fixed: 'left'}
            ,{field: 'id', title: '序号', width: 75, sort: true, totalRowText: '合计：'}
            ,{field: 'userid', title: '用户ID', width: 200}
            ,{field: 'assetsId', title: '资产ID', width: 275, sort: true}
            ,{field: 'name', title: '提现人名', width: 200, sort: true}
            ,{field: 'phone', title: '手机号', width: 132}
            ,{field: 'email', title: '邮箱', width: 200, sort: true}
            ,{field: 'IDCard', title: '身份证号码', width: 250}
            ,{field: 'assetsType', title: '资产类型', width: 200, sort: true}
            ,{field: 'num', title: '提现数量', width: 200}
            ,{field: 'wallet', title: '提现到的钱包', width: 200, sort: true}
            ,{field: 'state', title: '状态', width: 150, sort: true}
            ,{field: 'submitTime', title: '提交时间', width: 250, sort: true}
            ,{field: 'examineUserId', title: '审核人ID',width: 200, sort: true}
            ,{field: 'examinePhone', title: '审核联系方式', width: 200, sort: true}
            ,{field: 'examineTime', title: '审核时间', width: 250, sort: true}
            ,{field: 'actualNum', title: '实际转账数量', width: 200, sort: true}
            ,{field: 'brokerage', title: '转账手续费', width: 200, sort: true}
            ,{field: 'TransferUserId', title: '转账人ID', width: 200, sort: true}
            ,{field: 'completeTime', title: '转账时间', width: 250, sort: true}
            ,{field: 'hash', title: '转账hash', width: 300, sort: true}
            ,{field: 'remarks', title: '备注', width: 300, sort: true}
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

    table.on('row(test)',function (res) {
        var index = layer.open({
            type: 2,
            content: '/admin/reflect/layer/examine',
            area: ['500px', '800px'],
            maxmin: true,
            success:function (layero,index) {
                var iframe = window['layui-layer-iframe' + index];
                iframe.child(res.data)
            }
        });
        layer.full(index);
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

    //触发事件


});