document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus().then(loginData => {
        updateHeaderButtons(loginData);
    });

    var logoutButton = document.getElementById('logout-button');
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogoutClick);
    }
});


function updateHeaderButtons(loginData) {
    var loginButton = document.querySelector('.fa-right-to-bracket'); // 로그인 버튼 선택자 수정
    var logoutButton = document.querySelector('.fa-xmark'); // 로그아웃 버튼 선택자 수정
    var userEmailElement = document.getElementById('user-email');

    if (loginData.status) {
        // 로그인 상태일 경우
        if (loginButton) loginButton.style.display = 'none';
        if (logoutButton) logoutButton.style.display = 'block';
        if (userEmailElement) userEmailElement.textContent = loginData.email; // 사용자 이메일 업데이트
    } else {
        // 로그아웃 상태일 경우
        if (loginButton) loginButton.style.display = 'block';
        if (logoutButton) logoutButton.style.display = 'none';
        if (userEmailElement) userEmailElement.textContent = ''; // 사용자 이메일 숨김
    }
}


// 로그아웃 버튼 클릭 이벤트 핸들러
function handleLogoutClick() {
    // 로그아웃 버튼을 클릭했을 때, 먼저 로그인 상태를 다시 확인
    checkLoginStatus().then(loginData => {
        if (loginData.status) {
            // 로그인 상태인 경우에만 로그아웃 요청 보냄
            // 로그아웃 요청 보내는 부분을 추가해야 합니다.
            fetch('/api/v1/member/logout', {
                method: 'POST', // 로그아웃 요청 메서드에 따라 수정
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem('accessToken')
                }
            })
            .then(response => {
                if (response.ok) {
                    // 로그아웃 성공 시 처리
                    alert('로그아웃 되었습니다');
                    // 추가로 필요한 로직을 여기에 작성하세요.
                    sessionStorage.removeItem('accessToken');
                    window.location.href = '/itblog/main';
                } else {
                    // 로그아웃 실패 시 처리
                    console.error('로그아웃 실패');
                    // 추가로 필요한 로직을 여기에 작성하세요.
                }
            })
            .catch(error => {
                console.error('로그아웃 요청 실패:', error);
                // 추가로 필요한 로직을 여기에 작성하세요.
            });
        }
    });
}

function updateSearchBoxStyle(isLoggedIn) {
    const searchBox = document.querySelector('.search-box');

    if (isLoggedIn) {
        // 로그인 상태일 때 검색창 가운데 정렬
        searchBox.style.textAlign = 'center';
        searchBox.style.marginLeft = 'auto';
        searchBox.style.marginRight = 'auto';
    } else {
        // 로그아웃 상태일 때 검색창 오른쪽 정렬
        searchBox.style.textAlign = 'right';
        searchBox.style.marginLeft = 'unset';
        searchBox.style.marginRight = '10px';
    }
}