// 사용자 등록 모달 열기
function openModal() {
    const modal = document.getElementById('userModal');
    modal.style.display = 'block';
}

// 사용자 등록 API 요청 '/api/v1/users' POST 요청
function registerUser() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const apiUrl = '/api/v1/users';

    axios.post(apiUrl, {
        name: username,
        password: password
    })
        .then(function (response) {
            alert('사용자 등록이 완료되었습니다.');
            closeAndClearModal();
        })
        .catch(function (error) {
            alert(error.response.data.result);
        });
}

// 사용자 등록 모달창 닫기
function closeAndClearModal() {
    const modal = document.getElementById('userModal');
    modal.style.display = 'none';

    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
}


// 페이지 단위 조절
function changePageSize(size) {
    // 원하는 URL로 이동
    window.location.href = "?size="+size;
}


// 게시글 등록 모달
function openPostModal() {
    const postModal = document.getElementById('postModal');
    postModal.style.display = 'block';
}

// 게시글 등록 API 요청 '/api/v1/posts' POST 요청
function registerPost() {
    const username = document.getElementById('postUsername').value;
    const password = document.getElementById('postUserPassword').value;
    const postTitle = document.getElementById('postTitle').value;
    const postBody = document.getElementById('postBody').value;
    const hashtags = [];


    const hashtagInputs = document.querySelectorAll('#hashtags input');

    hashtagInputs.forEach(input => {
        if (input.value.trim() !== '') {
            hashtags.push(input.value.trim());
        }
    });

    const postData = {
        name: username,
        password: password,
        title: postTitle,
        body: postBody,
        hashtags: hashtags.slice(0, 5) // 최대 5개만 추출
    };

    // POST 요청 보내기
    axios.post('/api/v1/posts', postData)
        .then(function (response) {
            // 등록 성공 시, 모달 닫고 게시글 목록 갱신
            location.reload();
        })
        .catch(function (error) {
            console.log(error);
            alert(error.response.data.result);
        });
}

// 게시글 등록 모달 닫기
function closeAndClearPostModal() {
    const postModal = document.getElementById('postModal');
    postModal.style.display = 'none';

    document.getElementById('postUsername').value = '';
    document.getElementById('postUserPassword').value = '';
    document.getElementById('postTitle').value = '';
    document.getElementById('postBody').value = '';

    // 해시태그 입력 란 초기화
    const hashtagsDiv = document.getElementById('hashtags');
    hashtagsDiv.innerHTML = `
            <input type="text" class="form-control" placeholder="해시태그 입력">`;
    hashtagCount = 1;
}


let hashtagCount = 1;

// 해시태그 입력 란 추가하는 함수
function addHashtagInput() {
    if (hashtagCount >= 5) return; // 최대 5개까지만 추가

    const hashtagsDiv = document.getElementById('hashtags');
    const newInput = document.createElement('input');
    newInput.type = 'text';
    newInput.className = 'form-control mt-1';
    newInput.placeholder = '해시태그 입력';
    hashtagsDiv.appendChild(newInput);
    hashtagCount++;
}