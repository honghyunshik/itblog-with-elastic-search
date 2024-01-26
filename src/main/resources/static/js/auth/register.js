document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('registerForm').addEventListener('submit', function(event) {
        event.preventDefault(); // 폼 기본 제출 동작 방지

        var formData = new FormData(this);
        var json = JSON.stringify(Object.fromEntries(formData.entries()));
        fetch('/api/v1/member/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: json
        })
        .then(response => {
            if (response.status === 400) {
                alert('유효성 검사 실패');
                return Promise.reject('Validation failed');
            } else if (response.status === 401) {
                alert('이미 존재하는 이메일입니다');
                return Promise.reject('Email already exists');
            } else if (response.ok) {
                alert('회원가입 성공! 로그인 해주세요');
                window.location.href = '/itblog/login'; // 성공 시 /index로 리다이렉트
            } else {
                // 기타 오류 처리
                alert('회원가입 실패');
                return Promise.reject('Failed to register');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });
});