document.addEventListener('DOMContentLoaded', function () {

    const myPostsButton = document.querySelector('.my-posts-button');
    const allPostsButton = document.querySelector('.all-posts-button');

    loadDefaultPage();

    document.querySelector('.my-posts-button').addEventListener('click', function() {
        checkLoginStatus().then(loginData => {
            if(loginData.status){
                setActiveButton(myPostsButton);
                loadContent('myBlog');
            }else{
                alert('로그인 후 이용 가능합니다');
                window.location.href = '/itblog/login';
            }
        });
    });

    document.querySelector('.all-posts-button').addEventListener('click', function() {
        setActiveButton(allPostsButton);
        loadContent('allPost');
    });

    function setActiveButton(button) {
        // 모든 버튼의 'active-button' 클래스 제거
        myPostsButton.classList.remove('active-button');
        allPostsButton.classList.remove('active-button');

        // 클릭된 버튼에 'active-button' 클래스 추가
        button.classList.add('active-button');
    }

    function loadDefaultPage(){
        checkLoginStatus().then(loginData=>{
            if(loginData.status){
                setActiveButton(myPostsButton);
                loadContent('myBlog');
            }else{
                setActiveButton(allPostsButton);
                loadContent('allPost');
            }
        })
    }
});



function loadContent(fragment) {
    const contentContainer = document.querySelector('.dynamic-container');

    fetch(`/itblog/fragment/${fragment}`)
        .then(response => response.text())
        .then(html => {
            // 'content-container'의 내부를 업데이트
            contentContainer.innerHTML = html;
        })
        .catch(error => console.error('Error:', error));
}