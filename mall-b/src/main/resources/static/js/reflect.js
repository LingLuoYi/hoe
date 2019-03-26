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
        ,url: '/reflect/reflect_admin_all' //数据接口
        ,toolbar: '#barDemo'
        ,parseData: function(res){
            console.log(res);
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,limit: 17
        ,page: true //开启分页
        ,cols: [[ //表头
             {field: 'id', title: '序号', width: 75, sort: true}
            ,{field: 'userId', title: '用户ID', width: 200}
            ,{field: 'assetsId', title: '资产ID', width: 275, sort: true}
            ,{field: 'name', title: '提现人名', width: 200, sort: true}
            ,{field: 'phone', title: '手机号', width: 132}
            ,{field: 'email', title: '邮箱', width: 200, sort: true}
            ,{field: 'idcard', title: '身份证号码', width: 250}
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
            ,{field: 'transferUserId', title: '转账人ID', width: 200, sort: true}
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
        if (obj.event === 'delete'){
            if(data.length === 0){
                layer.msg('请选择一行');
            } else if(data.length > 1){
                layer.msg('只能同时删除一个');
            } else {
                obj.del();
                layer.close(index);
                layer.alert('编辑 [id]：'+ checkStatus.data[0].id);
            }
        }
    });

    table.on('rowDouble(test)',function(res){
        var index = layer.open({
            type: 2,
            content: '/admin/reflect/update',
            area: ['500px', '800px'],
            maxmin: true,
            success:function (layero,index) {
                var iframe = window['layui-layer-iframe' + index];
                iframe.child(res.data)
            }
        });
        layer.full(index);
    })
});