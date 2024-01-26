function refreshAccessToken() {
    return fetch('/api/v1/member/refresh', {
        method: 'POST',
        // POST 요청에는 일반적으로 Authorization 헤더가 필요하지 않을 수 있습니다.
        // 이 부분은 백엔드 API의 요구사항에 따라 달라질 수 있습니다.
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

window.refreshAccessToken = refreshAccessToken;