document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus();
});


function checkLoginStatus() {
    fetch('/api/v1/member/login-status', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + sessionStorage.getItem('accessToken')
        }
    })
    .then(response => {
        if (response.status === 401) {
            // 토큰 갱신을 위한 요청
            window.refreshAccessToken();
        }
        return response.json();
    })
    .then(data => {
        if (data && data.status) {
            document.getElementById('user-email').textContent = data.email;
            document.querySelector('.auth-button').textContent = '로그아웃';
        } else {
            document.querySelector('.auth-button').textContent = '로그인';
            document.getElementById('user-email').textContent = ''; // 텍스트 제거
        }
    })
    .catch(error => console.error('Error:', error));
}

function refreshAccessToken() {
    return fetch('/api/v1/member/refresh', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + sessionStorage.getItem('accessToken')
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to refresh token');
        }
        return response.json();
    })
    .then(data => {
        sessionStorage.setItem('accessToken', data.newAccessToken);
        return checkLoginStatus(); // 토큰 갱신 후 로그인 상태 재확인
    });
}


document.addEventListener('DOMContentLoaded', function() {
    var authButton = document.querySelector('.auth-button');
    if (authButton) {
        authButton.addEventListener('click', function() {
            // 버튼 클릭 이벤트 로직
            var buttonText = this.textContent;

                if (buttonText === '로그아웃') {
                    // 로그아웃 요청
                    fetch('/api/v1/member/logout', {
                        method: 'POST',
                        headers: {
                            'Authorization': 'Bearer ' + sessionStorage.getItem('accessToken')
                        }
                    })
                    .then(response => {
                        if (response.ok) {
                            // 로그아웃 성공 시 처리, 예: 세션 정리, 로그인 페이지로 리다이렉트 등
                            sessionStorage.removeItem('accessToken');
                            alert('로그아웃 되었습니다');
                            window.location.href = '/itblog/index';
                        } else {
                            // 로그아웃 실패 처리
                            console.error('로그아웃 실패');
                        }
                    })
                    .catch(error => console.error('로그아웃 중 에러 발생:', error));
                } else {
                    // 로그인 페이지로 이동
                    window.location.href = '/itblog/login';
                }
        });
    }
});

