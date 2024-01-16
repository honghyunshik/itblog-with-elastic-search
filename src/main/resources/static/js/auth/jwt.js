function refreshAccessToken() {
    return fetch('/api/v1/member/refresh', {
        method: 'POST',
    })
    .then(response => {
        if (response.ok) {
            const newAccessToken = response.headers.get('Authorization');
            sessionStorage.setItem('accessToken', newAccessToken);
            return true; // 토큰 갱신 성공 시 true 반환
        }
        return false; // 토큰 갱신 실패 시 false 반환
    });
}

function checkLoginStatus() {
    let refreshTokenPromise = null;

    return fetch('/api/v1/member/login-status', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + sessionStorage.getItem('accessToken')
        }
    })
    .then(response => {
        if (response.status === 401) {
            // 401 상태 코드가 떴으므로 토큰 갱신을 시도하고,
            // refreshAccessToken의 실행 결과를 refreshTokenPromise에 저장
            return refreshAccessToken() // refreshAccessToken을 호출하여 토큰을 갱신하고
                    .then(refreshResponse => {
                        if (refreshResponse === true) {
                            return checkLoginStatus(); // 토큰 갱신 성공 시 checkLoginStatus 다시 호출
                        }
                    });
        } else {
            // 401 상태 코드가 아닌 경우, 로그인 상태 확인 후 이메일 업데이트
            return response.json()
                .then(data => {
                    return data;
                });
        }
    })
    .catch(error => {
        console.error('Failed to check login status:', error);
        return false; // 에러 발생 시 로그인 상태가 아닌 것으로 간주
    })
    .finally(() => {
        if (refreshTokenPromise !== null) {
            // refreshTokenPromise이 존재하면, 토큰 갱신이 완료된 후에 실행
            return refreshTokenPromise;
        }
    });
}

window.checkLoginStatus = checkLoginStatus;