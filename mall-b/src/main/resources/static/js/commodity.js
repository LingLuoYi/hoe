layui.use(['table','element'], function() {

    var table = layui.table;
    var element = layer.element;

    //第一个实例
     table.render({
        elem: '#productList'
        ,url: '/commodity/commodity_info_page' //数据接口
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.datasize,
                "data":res.data
            }
        }
        ,toolbar: '#toolbar'
        ,limit: 20
        ,cols: [[ //表头
             {field: 'id', title: '编号', sort: true,fixed: true}
            ,{field: 'commodityId', title: '商品编号', sort: true,width:271}
            ,{field: 'commodityName', title: '商品名称', sort: true}
            ,{field: 'commodityStock', title: '现有库存', sort: true}
            ,{field: 'commodityInitialStock', title: '总库存', sort: true}
            ,{field: 'commodityMoney', title: '单价', sort: true}
            ,{field: 'commodityType', title: '类型', sort: true, templet: function (res) {
                    if (res.commodityType === '1'){
                        return '<em>实物商品</em>'
                    } else {
                        return '<em>'+res.commodityType+'</em>'
                    }
                }}
            ,{field: 'commodityWatt', title: '算力功耗', sort: true}
            ,{field: 'commodityPowerRate', title: '单位电费', sort: true}
            ,{field: 'commodityTime', title: '交割时间', sort: true}
            ,{field: 'commodityTerm', title: '算力期限', sort: true}
            ,{field: 'commodityDescribe', title: '商品描述', sort: true}
            ,{field: 'commodityUrl', title: '商品图片地址', sort: true}
            ,{field: 'commodityState', title: '商品状态', sort: true, templet: function (res) {
                     if (res.commodityState === 0){
                         return "<em>正常出售</em>"
                     } else if (res.commodityState === 1) {
                         return "<em>预热商品</em>"
                     }else if (res.commodityState === 2) {
                         return "<em>商品下架</em>"
                     }else if (res.commodityState === 3) {
                         return "<em>商品删除</em>"
                     }else {
                         return res.commodityState
                     }
                 }}
        ]]
        ,id: 'testReload'
        ,page:true
        ,height: 'full'
    });
    //头工具栏事件
    table.on('toolbar(commodity)', function(obj){
        // var checkStatus = table.checkStatus(obj.config.id);
        if (obj.event === 'add'){
            window.parent.tabChange('addCommodity');
            window.parent.send("/admin/commodity/add");
        }
    });

    var $ = layui.$, active = {
        reload: function(){
            var demoReload = $('#demoReload');
            //执行重载
            table.reload('testReload',{
                where: {
                    id: demoReload.val()
                }
            })
        }
    };

    $('.demoTable .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    table.on('row(commodity)',function (res) {
        var index = layer.open({
            type: 2,
            content: '/admin/commodity/update',
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
