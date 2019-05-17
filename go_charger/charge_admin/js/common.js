/*常量定义*****常量定义*****常量定义*****常量定义*****常量定义*****常量定义*****常量定义*/
//缓存key常量
var CACHE_USER_INFO = "userInfo";
var CACHE_RULE_COMBO_DATA = "ruleComboData";
/*常量定义*****常量定义*****常量定义*****常量定义*****常量定义*****常量定义*****常量定义*/

var _user = getCache(CACHE_USER_INFO);
var _stationList = null;    // 充电站列表
var _ruleList = null;       // 计费策略列表


(function(){  
    //combobox可编辑，自定义模糊查询 
    $.fn.combobox.defaults.editable = true;  
    $.fn.combobox.defaults.filter = function(q, row){  
        var opts = $(this).combobox('options');  
        return row["text0"].indexOf(q) >= 0;  
    };  
})(jQuery);

// 表单数据校验
function formValidate(id){
    var rt = true;
    var validate = $('#'+id+' .validatebox-invalid').length;
    if(validate > 0){
        $('#'+id+' .validatebox-invalid').first().focus();
        rt = false;
    }
    return rt;
}


//登录接口
function login(u,p){
    var rt = null;
    if(!u && !p){
        $.messager.alert("温馨提醒","用户和密码都不有为空");
    }else{
        var url = API_URL + 'mgr/user/login/';
        var pp = {userName:u,pwd:$.md5(p)};
        console.log(url,pp);
        $.ajax({
            type:"POST",
            url:url,
            async:false,
            data:JSON.stringify(pp),
            success:function(res){
                var res = JSON.parse(res);
                console.log(res);
                if(res.res == 0){
                    rt = res.data;
                }else{
                    // $.messager.alert("错误",res.data);
                }
            },
            error:function(res){
                $.messager.alert("错误","请求失败，请重新请求");
            }
        }); 
    }
    return rt;
}

// 用户登录检查
function checkUser(){
    // return true;   
    if(!_user){
        window.location=ROOT_PATH+"login.html";
    }
}

// 获取url中指定的参数
function getUrlParam(paramName) { 
    paramValue = "", isFound = !1; 
    if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=") > 1) { 
        arrSource = unescape(this.location.search).substring(1, this.location.search.length).split("&"), i = 0; 
        while (i < arrSource.length && !isFound) arrSource[i].indexOf("=") > 0 && arrSource[i].split("=")[0].toLowerCase() == paramName.toLowerCase() && (paramValue = arrSource[i].split("=")[1], isFound = !0), i++ 
    } 
    return paramValue == "" && (paramValue = null), paramValue 
}

//根据下拉选项数组返回KV数组
function getOptionsArray(p){
    var a = [];
    if(p.length > 0){
        p.forEach(function(v,i){
            a[v.value] = v.text;
        });
    }
    return a;
}

// 获取指定日期时间戳(秒)
function getTimestamp(dd){
    var dt;
    if(dd) {
        dt = new Date(dd);
    }else{
        dt = new Date();
    }
    return dt.getTime() / 1000;
}

// 时间戳转换成日期
function timestamp2date(ts){
    var dt = new Date();
    dt.setTime(ts * 1000)
    var y = dt.getFullYear();
    var m = dt.getMonth()+1;
    var d = dt.getDate();
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}

// 时间戳转换成日期
function timestamp2datetime(ts){
    var dt = new Date();
    dt.setTime(ts * 1000)
    var y = dt.getFullYear();
    var m = dt.getMonth()+1;
    var d = dt.getDate();
    var h = dt.getHours(); 
    var mi = dt.getMinutes(); 
    var s = dt.getSeconds();
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d)+" "+(h<10?('0'+h):h)+":"+(mi<10?('0'+mi):mi)+":"+(s<10?('0'+s):s);
}

// 获取指定日期
function getDate(dd){
    var dt;
    if(dd) {
        dt = new Date(dd);
    }else{
        dt = new Date();
    }
    var y = dt.getFullYear();
    var m = dt.getMonth()+1;
    var d = dt.getDate();
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}

// 获取指定日期时间
function getDateTime(dd){
    var dt;
    if(dd) {
        dt = new Date(dd);
    }else{
        dt = new Date();
    }
    var y = dt.getFullYear();
    var m = dt.getMonth()+1;
    var d = dt.getDate();
    var h = dt.getHours(); 
    var mi = dt.getMinutes(); 
    var s = dt.getSeconds();
    return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d)+" "+(h<10?('0'+h):h)+":"+(mi<10?('0'+mi):mi)+":"+(s<10?('0'+s):s);
}

// 从时间字符串中获取时数值
function getHour(t){
    console.log(t);
    var ta = t.split(':');
    return parseInt(ta[0]);
}

// 从时间字符串中获取分数值
function getMinute(t){
    var ta = t.split(':');
    return parseInt(ta[1]);
}

// 从时间字符串中获取秒数值
function getSecond(t){
    var ta = t.split(':');
    return parseInt(ta[2]);
}

// 设置表单数据
function setFormData(p,t){
    var bt = null;
    var bta = null;
    for(k in p){
        if($("#"+t+"-"+k).length == 0){
            continue;
        }
        var bt = $("#"+t+"-"+k)[0]['className'];        
        // console.log($("#"+t+"-"+k)[0]['className']);
        if(bt){
            bta = bt.split(' ');
            if(bta[0] == "easyui-textbox"){
                $("#"+t+"-"+k).textbox('setValue',p[k]);
            }else if(bta[0] == "easyui-combobox"){
                $("#"+t+"-"+k).combobox('setValue',p[k]);
            }else if(bta[0] == "easyui-numberbox"){
                $("#"+t+"-"+k).numberbox('setValue',p[k]);
            }else if(bta[0] == "easyui-datebox"){
                $("#"+t+"-"+k).datebox('setValue',p[k]);
            }else if(bta[0] == "easyui-datetimebox"){
                $("#"+t+"-"+k).datetimebox('setValue',p[k]);
            }else if(bta[0] == "easyui-calendar"){
                $("#"+t+"-"+k).calendar('setValue',p[k]);
            }else if(bta[0] == "easyui-numberspinner"){
                $("#"+t+"-"+k).numberspinner('setValue',p[k]);
            }else if(bta[0] == "easyui-timespinner"){
                $("#"+t+"-"+k).timespinner('setValue',p[k]);
            }else if(bta[0] == "easyui-combogrid"){
                $("#"+t+"-"+k).combogrid('setValue',p[k]);
            }else if(bta[0] == "easyui-passwordbox"){
                $("#"+t+"-"+k).passwordbox('setValue',p[k]);
            }else if(bta[0] == "easyui-tagbox"){
                $("#"+t+"-"+k).tagbox('setValue',p[k]);
            }else{
                $("#"+t+"-"+k).val(p[k]);
            }          
        }else{
            $("#"+t+"-"+k).val(p[k]);
        }
    }
}

// 清空表单控件值
function clearFormData(p, t){  
    var bt = null;
    var bta = null;
    for(k in p){
        var bt = $("#"+t+"-"+k)[0]['className'];        
        // console.log($("#"+t+"-"+k)[0]['className']);
        if(bt){
            bta = bt.split(' ');
            if(bta[0] == "easyui-textbox"){
                $("#"+t+"-"+k).textbox('setValue',"");
            }else if(bta[0] == "easyui-combobox"){
                $("#"+t+"-"+k).combobox('setValue',0);
            }else if(bta[0] == "easyui-numberbox"){
                $("#"+t+"-"+k).numberbox('setValue',0);
            }else if(bta[0] == "easyui-datebox"){
                $("#"+t+"-"+k).datebox('setValue',"");
            }else if(bta[0] == "easyui-datetimebox"){
                $("#"+t+"-"+k).datetimebox('setValue',"");
            }else if(bta[0] == "easyui-calendar"){
                $("#"+t+"-"+k).calendar('setValue',"");
            }else if(bta[0] == "easyui-numberspinner"){
                $("#"+t+"-"+k).numberspinner('setValue',0);
            }else if(bta[0] == "easyui-timespinner"){
                $("#"+t+"-"+k).timespinner('setValue',"");
            }else if(bta[0] == "easyui-combogrid"){
                $("#"+t+"-"+k).combogrid('setValue',"");
            }else if(bta[0] == "easyui-passwordbox"){
                $("#"+t+"-"+k).passwordbox('setValue',"");
            }else if(bta[0] == "easyui-tagbox"){
                $("#"+t+"-"+k).tagbox('setValue',"");
            }else{
                $("#"+t+"-"+k).val("");
            }           
        }else{
            $("#"+t+"-"+k).val("");
        }
    } 
}

// 获取表单数据json,ta是参数数据类型及参数筛选（其中不存在的参数将被丢弃）
function getFormData(p,pt){
    console.log(p,pt);
    var rt = false;
    if(p.length > 0){
        rt = {};
        for(k in p){
            if(pt[p[k]['name']] == "int"){
                rt[p[k]['name']] = parseInt(p[k]['value']);
            }else if(pt[p[k]['name']] == "float"){                
                rt[p[k]['name']] = parseFloat(p[k]['value']);
            }else if(pt[p[k]['name']] == "boolean"){
                rt[p[k]['name']] = Boolean(p[k]['value']);
            }else if(pt[p[k]['name']] == "string"){
                rt[p[k]['name']] = p[k]['value'];
            }
        }
    }else{
        console.log("获取表单数据，数据格式错误");
    }
    return rt;
}

// 获取表单指定数据数组(获取数组数据)
function getFormArrayDataByKey(p,key){
    console.log(p,key);
    var rt = false;
    if(p.length > 0){
        rt = [];
        for(k in p){
            if(p[k]['name'] == key+"[]"){
                rt.push(p[k]['value']);
            }
        }
    }else{
        console.log("获取表单指定数据数组，数据格式错误");
    }
    return rt;
}

// 获取状态数组
function getStatus(str){
    return [
        {"value":"正常","text":"正常","text0":"1-t-正常"},
        {"value":"异常","text":"异常","text0":"2-f-异常"}
    ];
}

// 不分页         
function list0(url,page){
    p = parseInt(page);
    p = isNaN(p) ? 0 :(p<0?0:p);
    url = API_URL + url + p;
    console.log(url);
    var rt = false;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        // data: JSON.stringify(p),
        success: function (res) {
            // console.log('接口返回', res);
            var res = JSON.parse(res);
            console.log('获取列表成功', res);
            if(res.res == 0){
                rt= res.data;
            }else{
                console.log('获取列表失败',res.data);
                // $.messager.alert("错误",'获取列表失败');
            }          
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;
}

// 不分页,post方式请求         
function list0_post(url,param){
    url = API_URL + url;
    console.log(url,param);
    var rt = false;
    $.ajax({
        type: 'POST',
        url: url,
        async: false,
        data: JSON.stringify(param),
        success: function (res) {
            // console.log('接口返回', res);
            var res = JSON.parse(res);
            console.log('获取列表成功', res);
            if(res.res == 0){
                rt= res.data;
            }else{
                console.log('获取列表失败',res.data);
                // $.messager.alert("错误",'获取列表失败');
            }          
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;
}

// 分页            
function list(url,page){
    p = parseInt(page);
    p = isNaN(p) ? 0 :(p<0?0:p);
    url = API_URL + url + p;
    console.log(url);
    var rt = false;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        // data: JSON.stringify(p),
        success: function (res) {
            // console.log('接口返回', res);
            var res = JSON.parse(res);
            console.log('获取列表成功', res);
            if(res.res == 0){
                rt = {"rows":[],"total":0,"pageSize":20};
                rt.rows = res.data.rows;
                rt.total = res.data.count;
                rt.pageSize = res.data.pagenum;
            }else{
                console.log('获取列表失败',res.data);
                // $.messager.alert("错误",'获取列表失败');
            }          
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;
}

function one(url,id){
    url= API_URL + url + id;
    console.log(url,id);
    var rt = false;
    $.ajax({
        type: 'GET',
        url: url,
        async: false,
        // data: JSON.stringify(p),
        success: function (res) {
            var res = JSON.parse(res);
            console.log('获取数据成功', res);
            if(res.res == 0){
                rt = res.data;
            }else{                
                console.log('获取数据失败',res.data);
                // $.messager.alert("错误",'获取数据失败');
            }
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;
}

function one_post(url,param){
    url= API_URL + url;
    console.log(url,param);
    var rt = false;
    $.ajax({
        type: 'POST',
        url: url,
        async: false,
        data: JSON.stringify(param),
        success: function (res) {
            var res = JSON.parse(res);
            console.log('获取数据成功', res);
            if(res.res == 0){
                rt = res.data;
            }else{                
                console.log('获取数据失败',res.data);
                // $.messager.alert("错误",'获取数据失败');
            }
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;
}

function add(url,param){    
    url= API_URL + url;
    console.log(url,param); 
    var rt = false;   
    $.ajax({
        type: 'POST',
        url: url,
        async: false,
        data: JSON.stringify(param),
        success: function (res) {
            var res = JSON.parse(res);
            console.log('接口返回JSON', res);
            if(res.res == 0){
                console.log('添加成功');
                rt = true;
            }else{                
                console.log('添加失败',res.data);
                // $.messager.alert("错误",'添加失败');
            }
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;

}

function put(url,param){    
    url= API_URL + url;
    console.log(url,param); 
    var rt = false;   
    $.ajax({
        type: 'PUT',
        url: url,
        async: false,
        data: JSON.stringify(param),
        success: function (res) {
            var res = JSON.parse(res);
            console.log('接口返回JSON', res);
            if(res.res == 0){
                console.log('添加成功');
                rt = true;
            }else{                
                console.log('添加失败',res.data);
                // $.messager.alert("错误",'添加失败');
            }
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;

}

function edit(url,id,param){
    url= API_URL + url + id;
    console.log(url, param);
    var rt = false;
    $.ajax({
        type: 'PUT',
        url: url,
        async: false,
        data: JSON.stringify(param),
        success: function (res) {
            var res = JSON.parse(res);
            console.log('接口返回JSON', res);
            if(res.res == 0){
                console.log('修改成功');
                rt = true;
            }else{                
                console.log('修改失败',res.data);
                // $.messager.alert("错误",'修改失败');
            }
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;
}

function del(url,id){
    url= API_URL + url + id;
    console.log(url);
    var rt = false;
    $.ajax({
        type: 'DELETE',
        url: url,
        async: false,
        // data: JSON.stringify(p),
        success: function (res) {
            var res = JSON.parse(res);
            console.log('接口返回JSON', res);
            if(res.res == 0){
                console.log('删除成功');
                rt = true;
            }else{                
                console.log('删除失败',res.data);
                // $.messager.alert("错误",'删除失败');
            }
        },
        error: function (res) {
            console.log('请求失败', res);
            $.messager.alert("错误","请求失败，请重新请求");
        }
    });
    return rt;
}

function getRuleComboData(){
    // var rt = getCache(CACHE_RULE_COMBO_DATA);
    var rt = null;
    if(!rt){
        rt = [];
        if(!_ruleList){
            _ruleList = list("mgr/bill/all/",-1);
            _ruleList = _ruleList.rows;
        }
        if(_ruleList){
            _ruleList.forEach(function(v, i){
                rt.push({"value":v.id,"text":v.name,"text0":v.id+"-"+v.name});
            });
            // setCache(CACHE_RULE_COMBO_DATA, rt);
        }
    }
    return rt;
}

function getRulesArray(){
    return getOptionsArray(getRuleComboData());
}

function getStationComboData(){
    // var rt = getCache(CACHE_STATION_COMBO_DATA);
    var rt = null;
    if(!rt){
        rt = [];
        if(!_stationList){
            _stationList = list("mgr/station/all/",-1);
            _stationList = _stationList.rows;
        }
        if(_stationList){
            _stationList.forEach(function(v, i){
                rt.push({"value":v.id,"text":v.name,"text0":v.id+"-"+v.name});
            });
            // setCache(CACHE_STATION_COMBO_DATA, rt);
        }
    }
    return rt;
}

function getStationsArray(){
    return getOptionsArray(getStationComboData());
}

// 获取充电站名
function getStationName(id){
    var rt = "无";
    if(id > 0){
        var rs = getStationsArray();
        if(rs && rs[id]){
            rt = rs[id];
        }else{
            rt = "无";
        }
    }
    return rt;
}

function cent2yuan(cent){
    return cent/100;
}

// input maxlength
function maxlength(){
    var elements = $('form .easyui-textbox:input[maxlength],form .easyui-numberbox:input[maxlength]');
    $(elements).each(function(i,v){
        var len = $(v).attr('maxlength');
        $(v).next().find('.textbox-text:input').attr('maxlength',len);
    });
}

// input min max
function minmax(){
    $('form input[max],form input[min]').each(function(i,v){
        console.log(v);
        $(v).unbind('keyup');
        $(v).bind('keyup',function(){
            var min = parseInt($(v).attr('min'));
            var max = parseInt($(v).attr('max'));
            var val = parseInt($(v).val());
            if(val && val < min){
                $(v).val(min);
            }else if(val && val > max){
                $(v).val(max);
            }            
        });
    });
}