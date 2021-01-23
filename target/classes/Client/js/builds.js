"use strict";
function getBuildsList() {
    //debugger;
    console.log("Invoked getPartsList()");     //console.log your BFF for debugging client side - also use debugger statement

    const url = "/Builds/list";    		// API method on web server will be in Users class, method list
    fetch(url, {
        method: "GET",				//Get method
    }).then(response => {
        return response.json();                 //return response as JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from the web server has an "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert (pop up window)
        } else {
            let dataHTML = `Builds<br><table><tr><th>Build ID</th><th>Build Title</th></tr>`;
            for (let build of response.Build) {
                dataHTML += `<tr><td>${build.BuildID}</td><td>${build.Title}</td></td><td><button id="${build.BuildID}" onclick="getBuildParts(${build.BuildID})">View parts</button></td>`;
            }
            dataHTML += `</table>`
            document.getElementById("buildList").innerHTML = dataHTML;
            //formatBuildsList(response);          //this function will create an HTML table of the data (as per previous lesson)
        }
    });
}
function getBuildParts(id){
    let formData = new FormData();
    formData.append("buildID", id);
    const url = "/Builds/buildPartsList";    		// API method on web server will be in Users class, method list
    fetch(url, {
        method: "POST",				//Get method
        body: formData,
    }).then(response => {
        return response.json();                 //return response as JSON
    }).then(response => {
        if (response.hasOwnProperty("Error")) { //checks if response from the web server has an "Error"
            alert(JSON.stringify(response));    // if it does, convert JSON object to string and alert (pop up window)
        } else {
            let dataHTML = `Parts<br><table><tr><th>Part ID</th><th>Description</th><th>Category</th></tr>`;
            for (let part of response.parts) {
                dataHTML += `<tr><td>${part.PartID}</td><td>${part.PartDescription}</td><td>${part.Category}</td>`;
            }
            dataHTML += `</table><button onclick="clearPage()">Clear</button>`
            document.getElementById("partsList").innerHTML = dataHTML;
            //formatBuildsList(response);          //this function will create an HTML table of the data (as per previous lesson)
        }
    });


}

function clearPage(){
    document.getElementById('partsList').innerHTML="Parts";
}