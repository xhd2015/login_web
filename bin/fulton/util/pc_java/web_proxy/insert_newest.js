

var rsaUsercode = RSAUtils.encryptedString(key, "%s");
var rsaPassword = RSAUtils.encryptedString(key, "%s");
$.ajax({
            type: "post",
            url: "http://jwts.hit.edu.cn/loginLdap",

            data: {
                usercode: rsaUsercode,
                password: rsaPassword,
                code: ""
            },
            success: function (text) {
                console.log(text);
                window.open("http://jwts.hit.edu.cn/loginLdap","_self");
            }
});