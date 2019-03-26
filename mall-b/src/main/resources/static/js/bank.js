layui.use('table', function(){
    var table = layui.table;

    //第一个实例
    table.render({
        elem: '#demo'
        ,height: 312
        ,url: '/pay/bank' //数据接口
        ,toolbar: 'default'
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "data":res.data
            }
        }
        ,cols: [[ //表头
            {type: 'checkbox', fixed: 'left'}
            ,{field: 'id', title: 'ID', width:80, sort: true}
            ,{field: 'appID', title: 'appID', width:200,edit: 'text'}
            ,{field: 'appPrivateKey', title: 'app_private_key', width:200}
            ,{field: 'gateway', title: 'gateway', width:200}
            ,{field: 'payType', title: 'pay_type', width: 200}
            ,{field: 'spare', title: 'spare', width: 200}
        ]]
    });

    table.on('edit(test)', function(obj){
        var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field; //得到字段
        layer.msg('[ID: '+ data.id +'] ' + field + ' 字段更改为：'+ value);
        $.ajax({
            url: '',
            type: 'post',
            dataType: 'json',
            data: obj.data,
            success: function (res) {
                console.log(res.data);
                layer.msg(res.msg)
            },
            error: function (res) {
                alert(res);
            }
        })
    });

    table.on('toolbar(test)',function (res) {
        var checkStatus = table.checkStatus(res.config.id)
            ,data = checkStatus.data;
        switch(res.event){
            case 'add':
               var index = layer.open({
                type: 2,
                content: '/admin/pay/layer/bank',
                area: ['500px', '800px'],
                maxmin: true
            });
            layer.full(index);
            break;
            case 'update':
                if(data.length === 0){
                    layer.msg('请选择一行');
                } else if(data.length > 1){
                    layer.msg('只能同时编辑一个');
                } else {
                    var index = layer.open({
                        type: 2,
                        content: '/admin/pay/layer/bank',
                        area: ['500px', '800px'],
                        maxmin: true,
                        success: function (layero, index) {
                            var iframe = window['layui-layer-iframe' + index];
                            iframe.child(data)
                        }
                    });
                    layer.full(index);
                }
            break;
            case 'delete':
                layer.msg("暂不支持删除");
        }
    });

});