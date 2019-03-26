layui.use('table', function(){
    var table = layui.table

    //方法级渲染
    table.render({
        elem: '#LAY_table_user'
        ,url: '/maintain/maintain_admin_all'
        ,toolbar: '#toolbarDemo'
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,cols: [[
            {field:'id', title: 'ID', width: 60, sort: true, fixed: true}
            ,{field:'maintainId', title: '订单ID', width:166}
            ,{field:'userId', title: '用户ID', width:172, sort: true}
            ,{field:'commodityId', title: '商品ID', width:264}
            ,{field:'assetsId', title: '资产ID', width:265, sort: true}
            ,{field:'money', title: '总金额',  width:100}
            ,{field:'term', title: '缴纳天数', sort: true, width:103}
            ,{field:'time', title: '下单时间', width:234, sort: true}
            ,{field:'state', title: '状态', width:60}
        ]]
        ,id: 'user_id'
        ,page: true
        ,height: 'full-70'
        ,limit: 17
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

    table.on('toolbar(maintain)', function(obj){
        if (obj.event === 'noPay') {
            table.reload('user_id', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    state: '13'
                }
            });
        }else if(obj.event === "pay"){
            table.reload('user_id', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    state: '14'
                }
            });
        }
    });

    table.on('row(maintain)',function (res) {
        var index = layer.open({
            type: 2,
            content: '/admin/maintain/update',
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