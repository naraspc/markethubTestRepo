let host = 'http://' + window.location.host;

document.addEventListener('DOMContentLoaded', function () {
    const auth = getToken();
    if (auth === '') {
        document.getElementById('login-true').style.display = 'none';
        document.getElementById('login-false').style.display = 'block';
    } else {
        document.getElementById('login-true').style.display = 'block';
        document.getElementById('login-false').style.display = 'none';
    }
});


function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', { path: '/' });
    window.location.href = host + "/";
}

function getToken() {
    let auth = Cookies.get('Authorization');

    if(auth === undefined) {
        return '';
    }

    return auth;
}