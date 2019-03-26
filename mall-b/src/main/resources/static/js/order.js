layui.use('table', function(){
    var table = layui.table;

    //方法级渲染
    table.render({
        elem: '#LAY_table_user'
        ,url: '/order/order_all_info'
        , where: {
            type: 'BTC'
        }
        ,toolbar: '#toolbar'
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,cols: [[
            {field:'userId', title: '购买者ID', sort: true,fixed: 'left',width: 175}
            ,{field:'orderId', title: '订单ID', sort: true,width:261}
            ,{field:'orderCommodityId', title: '商品ID', sort: true,width:271}
            ,{field:'orderCommodityName', title: '商品名称', sort: true,width:103}
            ,{field:'orderCommodityType', title: '商品类别', sort: true,width:105, templet:function (res) {
                    if (res.orderCommodityType === '1'){
                        return '<em>实物商品</em>'
                    } else {
                        return '<em>'+res.orderCommodityType+'</em>'
                    }
                }}
            ,{field:'orderName', title: '订单名称',sort: true,width:243}
            ,{field:'orderMoney', title: '订单金额', sort: true,width:104}
            ,{field:'orderNum', title: '商品数量',  sort: true,width:104}
            ,{field:'userCoupon', title: '订单使用优惠劵', sort: true,width:150}
            ,{field:'orderTerm', title: '订单期限', sort: true,width:105}
            ,{field:'orderStartTime', title: '订单开始时间', sort: true,width:130}
            ,{field:'orderStopTime', title: '订单结束时间', sort: true,width:130}
            ,{field:'orderState', title: '订单状态', sort: true,width:120,templet:function (res) {
                    if (res.orderState === '14') {
                        return '<em>未生成支付单</em>'
                    }else if (res.orderState === '13') {
                        return '<em>等待支付</em>'
                    }else if (res.orderState === '0') {
                        return '<em>支付完成</em>'
                    }else if (res.orderState === '15') {
                        return '<em>未发货</em>'
                    }else if (res.orderState === '18') {
                        return '<em>订单关闭</em>'
                    }else {
                        return res.orderState
                    }
                }}
            ,{field:'name', title: '购买人名字',  sort: true,width:120}
            ,{field:'email', title: '联系邮箱', sort: true,width:200}
            ,{field:'maintainPayType', title: '订单维护费支付方式', sort: true,width:171,templet: function(res){
                if (res.maintainPayType === 1) {
                    return '<em>预缴</em>'
                }else if (res.maintainPayType === 0) {
                    return '<em>扣除</em>'
                }else {
                    return res.maintainPayType
                }

                }}
            ,{field:'payStart', title: '支付状态', sort: true,width:105,templet: function (res) {
                    if (res.payStart === '14') {
                        return '<em>未支付</em>'
                    }else if (res.payStart === '20') {
                        return '<em>审核中</em>'
                    }else if (res.payStart === '0') {
                        return '<em>支付完成</em>'
                    }else {
                        return res.payStart
                    }
                }}
            ,{field:'expressNum', title: '订单物流信息',  sort: true,width:200}
            ,{field:'address', title: '订单收货地址', sort: true,width:200,templet: function (res) {
                var address = res.address;
                if (address != null) {
                    return '<em>' + address.name + '-' + address.phone + '-' + address.address + '</em>'
                }else {
                    return '<em>算力订单</em>'
                }
                }}
            ,{field:'orderTime', title: '订单生成时间', sort: true,width:240}
            ,{field:'cancelReason', title: '订单取消原因', sort: true,width:200}
        ]]
        ,id: 'user_id'
        ,page: true
        ,height: 'full-70'
        ,limit: 17
    });
    //监听头工具栏事件
    table.on('toolbar(order)', function(obj){
        var checkStatus = table.checkStatus(obj.config.id)
            ,data = checkStatus.data; //获取选中的数据
        if (obj.event === 'update') {
            if(data.length === 0){
                layer.msg('请选择一行');
            } else if(data.length > 1){
                layer.msg('只能同时编辑一个');
            } else {
                layer.alert('编辑 [id]：'+ checkStatus.data[0].id);
            }
        }else if (obj.event === 'noPay') {
            table.reload('user_id', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                ,where: {
                    state: '13'
                }
            });
        }

    });

    table.on('row(order)',function (res) {
        var index = layer.open({
            type: 2,
            content: '/admin/order/update',
            area: ['500px', '800px'],
            maxmin: true,
            success:function (layero,index) {
                var iframe = window['layui-layer-iframe' + index];
                iframe.child(res.data)
            }
        });
        layer.full(index);
    });

    var $ = layui.$, active = {
        reload: function () {
            var demoReload = $('#demoReload');

            //执行重载
            table.reload('user_id', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                        id: demoReload.val()
                }
            });
        }
    };

    $('.demoTable .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
});