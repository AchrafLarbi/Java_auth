function displayImageName(input) {
    const fileName = input.files[0].name;
    document.getElementById('image-name').value = fileName;
}

function addItem() {
    window.location.href = "list_of_items.html";
}