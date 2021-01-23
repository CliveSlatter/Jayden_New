function showBasket(){
    let url = "/Baskets/list";

    fetch(url, {
        method: "GET",				//Get method
    }).then(response => {
        return response.json();                 //return response as JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from the web server has an "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert (pop up window)
        } else {
            let userHTML = `<h3>Basket for ${response.firstName}`+" "+`${response.lastName}</h3>`;
            document.getElementById("user").innerHTML = userHTML;
            // displays items as a table, creating it based on the results from the API
            let dataHTML = `Parts<br><table><tr><th>Description</th><th>Quantity</th><th>Price</th><td>Unit Total</td></tr>`;
            for (let part of response.Items) {
                dataHTML += `<tr id="${part.PartID}"><td>${part.Description}</td><td>${part.Quantity}</td><td>${part.Price}</td><td>`+(part.Quantity*part.Price)+`</td><td><button onclick="deleteItem(${part.PartID})">Remove</button></td>`;
            }
            dataHTML += `</table><button onclick="clearPage()">Clear</button><button onclick="buyNow()">Buy Now</button>`
            document.getElementById("items").innerHTML = dataHTML;
            //formatBuildsList(response);          //this function will create an HTML table of the data (as per previous lesson)
        }
    });
}

function deleteItem(partID){
    console.log("Invoked deleteItem()");
    let url = "/Baskets/removeItem/";
    let itemData = new FormData();
    itemData.append("PartID",partID)
    fetch(url, {
        method: "POST",				//Get method
        body: itemData,
    }).then(response => {
        return response.json();                 //return response as JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from the web server has an "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert (pop up window)
        } else {
            alert(JSON.stringify(response));
            showBasket();
        }
    })
}

function buyNow(){
    let url = "/Baskets/buyNow"
    fetch(url, {
        method: "get",
    }).then(response => {
        return response.json();
    }).then(response => {
        if(response.hasOwnProperty("Error")){
            alert(JSON.stringify(response));
        }else{
            alert(JSON.stringify(response));
            showBasket();
        }
    });
}