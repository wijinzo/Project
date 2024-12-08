// 檢查輸入是否為空，並顯示 loading 提示
function validateForm() {
    const keywordInput = document.getElementById("keyword").value.trim();
    if (keywordInput === "") {
        alert("請輸入關鍵字！");
        return false;
    }
    document.getElementById("loading").style.display = "block";
    return true;
}
