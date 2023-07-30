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

function closeAndClearModal() {
    const modal = document.getElementById('userModal');
    modal.style.display = 'none';

    document.getElementById('username').value = '';
    document.getElementById('password').value = '';
}
