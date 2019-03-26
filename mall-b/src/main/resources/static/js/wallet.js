layui.use(['table','layer'], function(){
    var table = layui.table
        ,layer = 'layui.layer'
    //方法级渲染
    table.render({
        elem: '#LAY_table_user'
        ,url: '/order/order_all_info'
        , where: {
            type: '1'
        }
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,toolbar: '#toolbarDemo'
        ,cols: [[
            {checkbox: true, fixed: true}
            ,{field:'user_id', title: '购买者ID', width: true, sort: true, fixed: true}
            ,{field:'order_id', title: '订单ID', width:true, edit: 'text'}
            ,{field:'order_commodity_id', title: '商品ID', width:true, sort: true, edit: 'text'}
            ,{field:'order_Commodity_Name', title: '商品名称', width:true, edit: 'text'}
            ,{field:'order_Commodity_Type', title: '商品类别', width:true, sort: true, edit: 'text'}
            ,{field:'order_Name', title: '订单名称',  width:true, edit: 'text'}
            ,{field:'order_Money', title: '订单金额', sort: true, width:true, edit: 'text'}
            ,{field:'order_Num', title: '商品数量', width:true, sort: true, edit: 'text'}
            ,{field:'id', title: '订单使用优惠劵', width:true, edit: 'text'}
            ,{field:'order_Term', title: '订单期限', sort: true, width:true, edit: 'text'}
            ,{field:'order_Start_Time', title: '订单开始时间', sort: true, width:true, edit: 'text'}
            ,{field:'order_Stop_Time', title: '订单结束时间', sort: true, width:true, edit: 'text'}
            ,{field:'order_State', title: '完成，订单状态', sort: true, width:true, edit: 'text'}
            ,{field:'name', title: '购买人名字',  width:true, edit: 'text'}
            ,{field:'email', title: '联系邮箱', width:true, edit: 'text'}
            ,{field:'order_pay_start', title: '支付状态', sort: true, width:true, edit: 'text'}
            ,{field:'express_num', title: '订单物流信息',  width:true, edit: 'text'}
            ,{field:'address_id', title: '订单收货地址', width:true, edit: 'text'}
            ,{field:'order_time', title: '订单生成时间', sort: true, width:true, edit: 'text'}
            ,{field:'cancel_Reason', title: '订单取消原因',  width:true, edit: 'text'}
        ]]
        ,id: 'user_id'
        ,page: true
        ,height: 'full-70'
    });

    //搜索事件
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