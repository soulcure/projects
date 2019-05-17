//判断字符串是否为json字符串
function isJSON(str) {
    if (typeof str == 'string') {
        try {
            var obj=JSON.parse(str);
            if(typeof obj == 'object' && obj ){
                return true;
            }else{
                return false;
            }
        } catch(e) {
            return false;
        }
    }
    return false;
}

// 保存缓存
function setCache(id, value){
    if(typeof(value) == "object"){
        value = JSON.stringify(value);
    }
    sessionStorage.setItem(id,value);
}

// 获取缓存
function getCache(id){
    var value = sessionStorage.getItem(id);
    if(isJSON(value)){
        value = JSON.parse(value);
    }
    return value;
}

// 删除缓存
function deleteCache(id){
    sessionStorage.removeItem(id);
}
