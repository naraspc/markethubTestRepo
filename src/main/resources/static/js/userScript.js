document.addEventListener('DOMContentLoaded', function () {
    // JWT 토큰에서 이메일 가져오기
    const token = getToken();
    const email = getEmailFromToken(token);

    // 이메일을 사용하여 사용자 정보 가져오기
    fetchUserInfo(email);
});

function getToken() {
    const token = Cookies.get('token');
    return token ? token : '';
}

function getEmailFromToken(token) {
    if (!token) return '';
    try {
        const payload = parseJwt(token);
        return payload.email;
    } catch (error) {
        console.error('Error parsing JWT:', error);
        return '';
    }
}

function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

function fetchUserInfo(email) {
    if (!email) return;

    fetch('/api/user/info?email=' + email, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user information');
            }
            return response.json();
        })
        .then(data => {
            displayUserInfo(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function displayUserInfo(userInfo) {
    const userInfoDiv = document.getElementById('user-info');
    const userInfoHTML = `
        <h1>마이페이지</h1>
        <p>이메일: ${userInfo.email}</p>
        <p>이름: ${userInfo.name}</p>
        <p>전화번호 : ${userInfo.phone}</p>
        <p>주소 : ${userInfo.address}</p>
        <p>역할: ${userInfo.role}</p>
        <!-- 추가적인 사용자 정보 표시 -->
    `;
    userInfoDiv.innerHTML = userInfoHTML;
}