let isPhoneLogin = false;
const toggleText = document.querySelector('.toggle-login-type');
const passwordGroup = document.getElementById('password-group');
const codeGroup = document.getElementById('code-group');
const loginKeyInput = document.getElementById('loginKey');
const loginValueInput = document.getElementById('loginValue');
const sendCodeBtn = document.getElementById('sendCodeBtn');

let config = {};

// 加载配置文件
fetch('/account/login/login.json')
    .then(response => response.json())
    .then(data => {
        config = data; // 将配置赋值给 config
    })
    .catch(error => {
        console.error('无法加载配置文件:', error);
    });

function toggleLoginType() {
    isPhoneLogin = !isPhoneLogin;
    if (isPhoneLogin) {
        toggleText.textContent = '使用用户名登录';
        loginKeyInput.placeholder = '请输入手机号';
        loginKeyLabel.textContent = '手机号'; // 修改 label 文本为“手机号”
        passwordGroup.style.display = 'none';
        codeGroup.style.display = 'block';
        document.getElementById('phone-login-tip').style.display = 'block'; // 显示提示信息
    } else {
        toggleText.textContent = '使用手机号登录/注册';
        loginKeyInput.placeholder = '请输入用户名';
        loginKeyLabel.textContent = '用户名'; // 修改 label 文本为“用户名”
        passwordGroup.style.display = 'block';
        codeGroup.style.display = 'none';
        document.getElementById('phone-login-tip').style.display = 'none'; // 隐藏提示信息
    }
}

function sendCode() {
    const phone = loginKeyInput.value;
    if (!phone) {
        alert('请输入手机号');
        return;
    }

    sendCodeBtn.classList.add('disabled');
    sendCodeBtn.textContent = '发送中...';

    fetch(`${config.domain}${config.sendCodeEndpoint}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({ phone })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('验证码请求失败');
            }
            return response.json();
        })
        .then(data => {
            sendCodeBtn.classList.remove('disabled');
            sendCodeBtn.textContent = '发送验证码';

            if (data.msg === 'success') {
                alert('验证码已发送');
            } else {
                alert(data.msg);
            }
        })
        .catch(error => {
            sendCodeBtn.classList.remove('disabled');
            sendCodeBtn.textContent = '发送验证码';
            console.error('发送验证码错误:', error);
        });
}

function submitSSOLogin() {
    const loginKey = loginKeyInput.value;
    const loginValue = isPhoneLogin ? document.getElementById('loginValueCode').value : loginValueInput.value;
    const loginType = isPhoneLogin ? 'PHONE' : 'USERNAME';

    if (!loginKey || !loginValue) {
        alert('请填写完整信息');
        return;
    }

    let appName = window.appName
    let redirectUrl = window.redirectUrl
    const requestData = {
        appName: appName,
        loginServer: appName, // 从配置中获取
        redirectUrl: redirectUrl, // 从配置中获取
        loginType: loginType,
        loginK: loginKey,
        loginV: loginValue,
        requestsTime: new Date().toISOString()
    };

fetch(`${config.domain}${config.ssoLoginEndpoint}`, {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    },
    body: JSON.stringify(requestData)
})
    .then(response => {
        if (!response.ok) { // 检查是否为成功的响应
            throw new Error('Network response was not ok');
        }
        return response.json(); // 解析 JSON 响应
    })
    .then(data => {
        if (data.code === 0) {
            localStorage.setItem('token', data.result.ssoToken);
            // 发送带有请求头的 AJAX 请求
            fetch(data.result.redirectUrl, {
                method: 'GET',
                headers: {
                    'token': `${localStorage.getItem('token')}`
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.text(); // 或者 response.json() 等其他响应处理方式
                })
                .then(text => {
                    alert('登录成功,准备跳转回：' + data.result.redirectUrl);
                    window.location.href = data.result.redirectUrl;
                    // window.open(data.result.redirectUrl, '_blank'); // 在新窗口中打开 URL
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('请求失败，请重试');
                });
        } else {
            alert(data.msg);
        }
    })
    .catch(error => {
        // 处理网络错误或其他错误
        console.error('Fetch error:', error);
        alert('登录失败,原因未知');
    });

}
