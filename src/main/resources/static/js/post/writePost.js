document.addEventListener('DOMContentLoaded', function() {
    var md = new markdownit();
    var toggleButton = document.getElementById('preview-button');
    var markdownContent = document.getElementById('markdown-content');
    var previewContent = document.getElementById('preview-content');
    var submitButton = document.getElementById('submit-button');
    var titleInput = document.getElementById('title');
    var isPreviewMode = false;

    toggleButton.addEventListener('click', function() {
        if (isPreviewMode) {
            // 프리뷰 모드에서 마크다운 모드로 전환
            markdownContent.style.display = 'block';
            previewContent.style.display = 'none';
            toggleButton.textContent = '프리뷰';
            isPreviewMode = false;
        } else {
            // 마크다운 모드에서 프리뷰 모드로 전환
            previewContent.innerHTML = md.render(markdownContent.value);
            markdownContent.style.display = 'none';
            previewContent.style.display = 'block';
            toggleButton.textContent = '마크다운';
            isPreviewMode = true;
        }
    });

    submitButton.addEventListener('click', function() {

        window.checkLoginStatus();
        /*
        var postData = {
            title: titleInput.value,
            content: md.render(markdownContent.value)
        };


        fetch('/api/v1/post/posts', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + sessionStorage.getItem('accessToken')
            },
            body: JSON.stringify(postData)
        })
        .then(response => {
            if (response.ok) {
                alert('게시글을 작성했습니다!');
                return response.json();
            } else {
                throw new Error('Network response was not ok.');
            }
        })
        .then(data => {
            console.log('Post created:', data);
            // 추가: 성공 시 작업, 예를 들어 페이지 리다이렉트
        })
        .catch(error => {
            console.error('Failed to create post:', error);
            // 추가: 실패 시 작업, 예를 들어 에러 메시지 표시
        });
        */
    });

});

