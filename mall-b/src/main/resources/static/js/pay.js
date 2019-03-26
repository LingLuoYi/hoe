layui.config({
    version: '1551352891258' //为了更新 js 缓存，可忽略
});

layui.use([ 'laypage', 'layer', 'table', 'element', 'slider'], function(){
    var layer = layui.layer //弹层
        ,table = layui.table;

    //执行一个 table 实例
    table.render({
        elem: '#demo'
        ,height:  'full-70'
        ,url: '/pay/pay_admin_all' //数据接口
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,limit: 17
        ,toolbar: 'accept'
        ,page: true //开启分页
        ,cols: [[ //表头
             {field: 'id', title: '序号', width: 74, sort: true, fixed: 'left'}
            ,{field: 'payId', title: '支付订单ID', width: 274, sort: true}
            ,{field: 'payOrderId', title: '订单ID', width: 300, sort: true}
            ,{field: 'payCommodityId', title: '商品ID', width: 265, sort: true}
            ,{field: 'payTitle', title: '支付标题', width: 102,sort: true}
            ,{field: 'payCommodityName', title: '商品名称', width: 102, sort: true}
            ,{field: 'payCommodityUnitPrice', title: '商品单价', width: 103, sort: true}
            ,{field: 'payNum', title: '商品数量', width: 103, sort: true}
            ,{field: 'payCommodityMoney', title: '订单金额', width: 110,sort: true}
            ,{field: 'payMode', title: '支付方式', width: 107, sort: true}
            ,{field: 'PayTypeRate', title: '支付方式手续费', width: 144, sort: true}
            ,{field: 'PayCouponMoney', title: '优惠金额', width: 102, sort: true}
            ,{field: 'payReceipts', title: '实收款', width: 88, sort: true}
            ,{field: 'PayTypeId', title: '第三方支付ID',width: 130, sort: true}
            ,{field: 'payUserId', title: '支付人ID', width: 175, sort: true}
            ,{field: 'payName', title: '支付人姓名', width: 116, sort: true}
            ,{field: 'payPhone', title: '支付人手机号', width: 130, sort: true}
            ,{field: 'payEmail', title: '支付人邮箱', width: 200, sort: true}
            ,{field: 'payState', title: '订单状态', width: 105, sort: true,templet: function (res) {
                    if (res.payState === '14'){
                        return '<em>未上传支付凭证</em>'
                    }else if (res.payState === '0'){
                        return '<em>完成</em>'
                    }else if (res.payState === '18') {
                        return '<em>订单取消</em>'

                    }else {
                        return res.payState;
                    }
                }}
            ,{field: 'voucherState', title: '付款凭证状态', width: 200, sort: true,templet: function (res) {
                    if (res.voucherState === "2") {
                        return '<em>审核未通过</em>'
                    }else if (res.voucherState === "1") {
                        return '<em>等待审核</em>'
                    }else if (res.voucherState === '0'){
                        return '<em>审核完成</em>'
                    }
                }}
            ,{field: 'voucherUrl', title: '凭证url', width: 200, sort: true}
            ,{field: 'payTime', title: '支付订单生成时间', width: 240, sort: true}
            ,{field: 'userId', title: '审核者ID', width: 200, sort: true}
            ,{field: 'examineTime', title: '审核时间', width: 200, sort: true}
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
            content: '/admin/pay/update',
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