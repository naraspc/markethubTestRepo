let host = 'http://' + window.location.host;

document.addEventListener('DOMContentLoaded', function () {
    const auth = getToken();
    if (auth === '') {
        window.location.href = `${host}/api/user/login-page`;
    } else {
        document.getElementById('login-true').style.display = 'block';
        document.getElementById('login-false').style.display = 'none';
    }
});


function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', { path: '/' });
    window.location.href = host + "/api/user/login-page";
}

function getToken() {
    let auth = Cookies.get('Authorization');

    if(auth === undefined) {
        return '';
    }

    return auth;
}