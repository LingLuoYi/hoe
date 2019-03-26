layui.use('table', function(){
    var table = layui.table;

    //方法级渲染
    table.render({
        elem: '#LAY_table_user'
        ,url: '/user/user_all_data'
        ,parseData: function(res){
            return {
                "code":res.state,
                "msg":res.msg,
                "count": res.data.size,
                "data":res.data.data
            }
        }
        ,toolbar: '#toolbar'
        ,limit: 17
        ,cols: [[{field:'id', title: '序号',  sort: true, fixed: true}
            ,{field:'userId', title: '用户id',sort: true,width:190}
            ,{field:'name', title: '昵称',sort: true }
            ,{field:'phone', title: '手机号',  sort: true}
            ,{field:'email', title: '邮箱', sort: true}
            ,{field:'imgUrl', title: '头像', sort: true, templet: '#imgUrl'}
            ,{field:'idcardNo', title: '身份证号', sort: true}
            ,{field:'userStart', title: '用户状态', sort: true,templet: function (res) {
                    if (res.userStart === 2){
                        return '<em>用户未实名</em>';
                    }else if (res.userStart === 1){
                        return '<em>实名审核中</em>'
                    }else if (res.userStart === 0){
                        return '<em>实名通过</em>'
                    } else {
                        return res.userStart;
                    }
                }}
            ,{field:'roles', title: '角色', sort: true}
            ,{field:'Wallet', title: '钱包', sort: true , templet: '#Wallet'}
            ,{field:'address', title: '收货地址', sort: true,templet: '#address'}
            ,{field:'profit', title: '累计收益', sort: true}
        ]]
        ,id: 'testReload'
        ,page: true
        ,height: 'full-70'
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

    table.on('toolbar(user)', function(obj){
        // var checkStatus = table.checkStatus(obj.config.id);
        if (obj.event === "add"){
            window.parent.send("/admin/user/add");
        }
    });

    table.on('row(user)', function(res){
        var index = layer.open({
            type: 2,
            content: '/admin/user/update',
            area: ['500px', '800px'],
            maxmin: true,
            success:function (layero,index) {
                var iframe = window['layui-layer-iframe' + index];
                iframe.child(res.data)
            }
        });
        layer.full(index);
    });

});

