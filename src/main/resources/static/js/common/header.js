document.addEventListener('DOMContentLoaded', function() {
    checkLoginStatus().then(loginData => {
        updateHeaderButtons(loginData);
    });

    var logoutButton = document.querySelector('.logout-button');
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogoutClick);
    }
});


function updateHeaderButtons(loginData) {
    var loginButton = document.querySelector('.login-button');
    var logoutButton = document.querySelector('.logout-button');
    var profileButton = document.querySelector('.profile-button');
    var myPostsButton = document.querySelector('.my-posts-button');
    var myCommentsButton = document.querySelector('.my-comments-button');

    if (loginData.status) {
        // 로그인 상태일 경우
        if (loginButton) loginButton.style.display = 'none';
        if (logoutButton) logoutButton.style.display = 'block';
        if (profileButton) profileButton.style.display = 'block';
        if (myPostsButton) myPostsButton.style.display = 'block';
        if (myCommentsButton) myCommentsButton.style.display = 'block';

        // 사용자 이메일을 업데이트
        var userEmailElement = document.getElementById('user-email');
        if (userEmailElement) {
            userEmailElement.textContent = loginData.email;
        }
    } else {
        // 로그아웃 상태일 경우
        if (loginButton) loginButton.style.display = 'block';
        if (logoutButton) logoutButton.style.display = 'none';
        if (profileButton) profileButton.style.display = 'none';
        if (myPostsButton) myPostsButton.style.display = 'none';
        if (myCommentsButton) myCommentsButton.style.display = 'none';

        // 사용자 이메일을 숨김
        var userEmailElement = document.getElementById('user-email');
        if (userEmailElement) {
            userEmailElement.textContent = '';
        }
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
                    window.location.href = '/itblog/index';
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