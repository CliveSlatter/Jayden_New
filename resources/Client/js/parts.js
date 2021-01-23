"use strict";

function getPartsList() {
    //debugger;
    console.log("Invoked getPartsList()");     //console.log your BFF for debugging client side - also use debugger statement
    const url = "/Parts/list";    		// API method on web server will be in Users class, method list
    fetch(url, {
        method: "GET",				//Get method
    }).then(response => {
        return response.json();                 //return response as JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from the web server has an "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert (pop up window)
        } else {
            let dataHTML = `<table style="width: 50%"><tr><th>Part ID</th><th>Product Code</th><th>Description</th><th>Price</th></tr>`;

            for (let item of response.parts) {
                let option = `<select id="qty${item.PartID}">`;
                for(let i = 1; i <=10; i++){
                    option+=`<option value=`+i+`>`+i+`</option>`;
                }
                dataHTML += `<tr id="${item.PartID}"><td style="width: 10%">${item.PartID}</td><td style="width: auto">${item.ProductCode}</td><td style="width: auto">${item.PartDescription}</td><td>${item.Price}</td><td>`+option+`</td><td><button onclick="addToBasket(${item.PartID})">Add to basket</button></td></tr>`;
            }
            dataHTML+=`</table><div><a href="basket.html"><button>View Basket</button></div></a>`;
            document.getElementById("PartsTable").innerHTML = dataHTML;

        }
    });
}
function formatPartsList(myJSONArray){

}

function addToBasket(id){
    console.log("Invoked addToBasket()");     //console.log your BFF for debugging client side - also use debugger statement
    const url = "/BasketItems/add";    		// API method on web server will be in Users class, method list
    let formData = new FormData();

    let qtyID = "qty"+id;
    alert(document.getElementById(qtyID).value);
    formData.append("ItemID",id);
    formData.append("Qty", document.getElementById(qtyID).value)
    fetch(url, {
        method: "POST",				//Get method
        body: formData,
    }).then(response => {
        return response.json();                 //return response as JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from the web server has an "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert (pop up window)
        } else {
            alert(JSON.stringify(response));
            document.getElementById("basket").innerText = "Basket ("+response.Count+")";
        }
    });
}

function deleteItem(){

}