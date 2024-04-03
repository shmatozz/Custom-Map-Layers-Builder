const firebaseConfig = {
    apiKey: "AIzaSyDPLQT0elrwdpO_YjB4Ojl7Twc8C4dNav0",
    authDomain: "custommaplayers.firebaseapp.com",
    databaseURL: "https://custommaplayers-default-rtdb.europe-west1.firebasedatabase.app",
    projectId: "custommaplayers",
    storageBucket: "custommaplayers.appspot.com",
    messagingSenderId: "808761174607",
    appId: "1:808761174607:web:796a372a36f6999609583a",
    measurementId: "G-GYC6VT46MH"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const db = firebase.firestore();

window.sendLine = function(coordinates, docName = "line") {
    db.collection("geofiles").doc(docName).set({
        type: "Feature",
        geometry: {
            type: "LineString",
            coordinates: coordinates.toString()
        },
        properties: {
            name: docName
        }
    }).then(() => {
        console.log("Document successfully written!");
    }).catch((error) => {
        console.error("Error writing document: ", error);
    });
}

window.sendPolygon = function(coordinates) {
    db.collection("geofiles").doc("polygon").set({
        type: "Feature",
        geometry: {
            type: "Polygon",
            coordinates: coordinates.toString()
        },
        properties: {
            name: "polygon"
        }
    }).then(() => {
        console.log("Document successfully written!");
    }).catch((error) => {
        console.error("Error writing document: ", error);
    });
}