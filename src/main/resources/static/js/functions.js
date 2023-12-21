document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(window.location.search);
    const bookSaved = urlParams.get('bookSaved');
    const authentication = urlParams.get('authentication');
    const categorySaved = urlParams.get('categorySaved');
    const libraryAccepted = urlParams.get('libraryAccepted');
    const libraryDeclined = urlParams.get('libraryDeclined');
    const commandSaved = urlParams.get('commandSaved');
    const commandRated = urlParams.get('commandRated');
    const acceptOrder = urlParams.get('acceptOrder');
    const declineOrder = urlParams.get('declineOrder');
    const accountCreated = urlParams.get('accountCreated');
    const libraryAccountCreated = urlParams.get('libraryAccountCreated');

    let divId;

    if (
        bookSaved === 'true'
        || categorySaved === 'true'
        || libraryAccepted === 'true'
        || commandRated === 'true'
        || acceptOrder === 'true'
        || accountCreated === 'true'
    ) {
        divId='toast-container-success';
    }
    if(
        bookSaved === 'false'
        || categorySaved === 'false'
        || libraryAccepted === 'false'
        || libraryDeclined === 'false'
        || commandSaved === 'false'
        || commandRated === 'false'
        || acceptOrder === 'false'
        || declineOrder === 'false'
        || accountCreated === 'false'
    ){
        divId='toast-container-failed';
    }
    if(libraryDeclined === 'true' || declineOrder === 'true'){
        divId='toast-container-success-decline';
    }
    if(authentication === 'false'){
        divId='authenticated-failed';
    }
    if(libraryAccountCreated === 'true'){
        divId='account-creation-library-success';
    }

    const div = document.getElementById(divId);
    div.style.display = 'block';

    // Wait
    setTimeout(function() {
        hideAlert(divId);
    }, 3000);
});

// Function to hide the div after a delay
function hideAlert(divId) {
    const div = document.getElementById(divId);
    let opacity = 1;
    function updateOpacity() {
        opacity -= 0.005; // Decrease opacity by 0.05 (adjust as needed)
        div.style.opacity = opacity;

        if (opacity <= 0 && divId!=='authenticated-failed') {
            div.style.display = 'none';
        } else {
            if(opacity > 0){
                requestAnimationFrame(updateOpacity); // Continue updating
            }
        }
    }
    if(divId!=='account-creation-library-success'){
        requestAnimationFrame(updateOpacity);
    }
}

function addToCart(bookId) {
    $.ajax({
        type: "GET",  // Change to GET since your endpoint is a @GetMapping
        url: "/add-book",
        data: { bookId: bookId },
        success: function (response) {
            const addButton = document.getElementById('add-'+bookId);
            addButton.classList.add("d-none");
            const addButtonPopUp = document.getElementById('add-from-popup-'+bookId);
            addButtonPopUp.classList.add("d-none");

            const removeButton = document.getElementById('remove-'+bookId);
            removeButton.classList.remove('d-none');
            const removeButtonPopUp = document.getElementById('remove-from-popup-'+bookId);
            removeButtonPopUp.classList.remove('d-none');
        },
        error: function (error) {
            alert("Error adding book to cart");
        }
    });
}

function removeFromCart(bookId,bookCost,method) {
    $.ajax({
        type: "GET",  // Change to GET since your endpoint is a @GetMapping
        url: "/remove-book",
        data: { bookId: bookId },
        success: function (response) {
            if(method===0){
                const addButton = document.getElementById('add-'+bookId);
                addButton.classList.remove("d-none");
                const addButtonPopUp = document.getElementById('add-from-popup-'+bookId);
                addButtonPopUp.classList.remove("d-none");

                const removeButton = document.getElementById('remove-'+bookId);
                removeButton.classList.add('d-none');
                const removeButtonPopUp = document.getElementById('remove-from-popup-'+bookId);
                removeButtonPopUp.classList.add('d-none');
            }
            if(method===1){
                const removeProductRow = document.getElementById('product-row-'+bookId);
                removeProductRow.parentNode.removeChild(removeProductRow);

                const totalCostContainer=document.getElementById('total-cost-container');
                let totalPrice = totalCostContainer.innerText;
                totalPrice=totalPrice-bookCost;
                totalCostContainer.innerText = totalPrice.toString();
                if(totalPrice<=0){
                    const empty=document.getElementById('empty-books');
                    const notEmpty=document.getElementById('not-empty-books');

                    empty.classList.remove("d-none");
                    notEmpty.classList.add('d-none');
                }
            }
        },
        error: function (error) {
            alert("Error removing book from cart");
        }
    });
}