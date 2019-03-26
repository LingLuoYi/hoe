layui.config({
    version: '1551352891258' //为了更新 js 缓存，可忽略
});

layui.use(['layer', 'table'], function(){
    var layer = layui.layer //弹层
        ,table = layui.table; //表格


    //执行一个 table 实例，分页参数 page 页码  limit 页面数量
    table.render({
        elem: '#assets'
        ,height: 'full-70'
        ,url: '/assets/assets_admin_all' //数据接口
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,limit: 17
        ,page: true //开启分页
        ,toolbar: '#toolbar' //开启工具栏
        ,cols: [[ //表头
             {field:'assetsPayId', title: '支付/资产ID', width:265, sort: true, fixed: true}
            ,{field:'assetsUserId', title: '所有者ID', width:172}
            ,{field:'assetsName', title: '资产名称', width:110, sort: true}
            ,{field:'assetsType', title: '资产类型', width:100, sort: true}
            ,{field:'assetsNum', title: '拥有数量', width:105, sort: true}
            ,{field:'assetsTerm', title: '资产周期', width:102, sort: true}
            ,{field:'maintainPayType', title: '维护费缴纳方式', width:147, sort: true,templet: function (res) {
                    if (res.maintainPayType === 1){
                        return '<em>预缴</em>'
                    } else if (res.maintainPayType === 0 ){
                        return '<em>扣除</em>'
                    }
                }}
            ,{field:'maintainDay', title: '已支付维护费天数', width:158, sort: true,templet: function (res) {
                    if (res.maintainDay === -1){
                        return '<em>扣除</em>'
                    } else {
                        return res.maintainDay;
                    }
                }}
            ,{field:'deductions', title: '收益扣除费用', width:136, sort: true}
            ,{field:'watt', title: '算力功耗', width:108, sort: true}
            ,{field:'powerRate', title: '电费', width:72, sort: true}
            ,{field:'assetsProfit', title: '资产浮动收益', width:150, sort: true}
            ,{field:'assetsAllProfit', title: '资产累计收益', width:150, sort: true}
            ,{field:'assetsFrozenProfit', title: '冻结收益', width:150, sort: true}
            ,{field:'assetsAvailableProfit', title: '已提收益', width:150, sort: true}
            ,{field:'assetsTime', title: '资产开始时间', width:250, sort: true}
            ,{field:'assetsDay', title: '资产持有天数', width:135, sort: true}
            ,{field:'assetsState', title: '资产状态', width:102, sort: true}
            ,{field:'assetsPhone', title: '资产联系人', width:123, sort: true}
            ,{field:'remark', title: '资产备注', width:200}
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
    table.on('toolbar(assets)', function(obj){
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
        }
    });

    table.on('row(assets)',function (res) {
        var index = layer.open({
            type: 2,
            content: '/admin/assets/update',
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