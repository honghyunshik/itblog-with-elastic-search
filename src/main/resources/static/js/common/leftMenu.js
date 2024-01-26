document.addEventListener('DOMContentLoaded', function() {
    var searchIcon = document.getElementById('search-icon');
    var searchBox = document.getElementById('search-box');
    var writeButton = document.querySelector('.fa-pen');

    // 검색 아이콘 클릭 이벤트
    searchIcon.addEventListener('click', function(event) {
        var searchBox = document.getElementById('search-box');
        searchBox.style.display = searchBox.style.display === 'none' ? 'block' : 'none';
        event.stopPropagation(); // 이벤트 전파 방지
    });

    // 다른 곳 클릭 시 검색창 닫기
    document.addEventListener('click', function() {
        var searchBox = document.getElementById('search-box');
        if (event.target.id !== 'search-icon' && event.target.id !== 'search-box' && event.target.id !== 'search-box-input') {
            searchBox.style.display = 'none';
        }
    });

    // 글 쓰기 버튼 클릭 시 로그인 중 -> 글쓰기 페이지로 이동, 로그아웃 중 -> 로그인 페이지로 이동
    writeButton.addEventListener('click', function(event) {
        checkLoginStatus().then(loginData => {
            if(loginData.status){
                return fetch('/itblog/post/new', {
                    headers: {
                        'Authorization': 'Bearer ' + sessionStorage.getItem('accessToken')
                    }
                    // 여기에 필요한 데이터를 추가할 수 있습니다.
                }).then(response => {
                  if (response.ok) {
                      return response.text(); // 응답 본문을 텍스트로 변환
                  } else {
                      throw new Error('Failed to load /main');
                  }
                }).then(html => {
                  document.documentElement.innerHTML = html;
                  history.pushState(null, '', '/itblog/post/new'); // URL 변경
                });
            }else{
                alert('로그인 후 이용 가능합니다');
                window.location.href = '/itblog/login'; // 로그아웃 상태일 때 로그인 페이지로 리다이렉트
            }
        })
    });
});