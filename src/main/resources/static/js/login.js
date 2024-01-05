document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('loginForm').addEventListener('submit', function(event) {
        event.preventDefault(); // 기본 제출 동작 방지

        var formData = new FormData(this);
        var json = JSON.stringify(Object.fromEntries(formData.entries()));

        fetch('/api/v1/member/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: json
        }).then(response => {
            if (response.ok) {
                // Authorization 헤더 추출 및 저장
                const accessToken = response.headers.get('Authorization');
                if (accessToken) {
                    sessionStorage.setItem('accessToken', accessToken); // Bearer 제거
                    // 로그인 성공 처리: fetch 요청으로 /index 페이지 로드
                    return fetch('/index', {
                        headers: {
                            'Authorization': 'Bearer ' + accessToken
                        }
                    });
                }
            } else {
                // 로그인 실패 시 알림
                alert('로그인 실패: 아이디 혹은 비밀번호가 틀렸습니다.');
                return Promise.reject('Login failed');
            }
        }).then(response => {
            if (response.ok) {
                return response.text(); // 응답 본문을 텍스트로 변환
            } else {
                throw new Error('Failed to load /index');
            }
        }).then(html => {
            document.documentElement.innerHTML = html; // 현재 문서 내용을 /index 페이지로 교체
              history.pushState(null, '', '/index'); // URL 변경
        }).catch(error => {
            console.error('Error:', error);
        });
    });
});
