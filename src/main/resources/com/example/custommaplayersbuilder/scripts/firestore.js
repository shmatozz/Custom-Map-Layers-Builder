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

window.sendLine = function(coordinates, bounds, docName = "Line") {
    console.log([bounds[0][0], bounds[0][1], bounds[1][0], bounds[1][1]])
    db.collection("geofiles").doc(docName).set({
        type: "Feature",
        geometry: {
            type: "LineString",
            coordinates: coordinates.toString()
        },
        properties: {
            name: docName,
            bbox: [bounds[0][1], bounds[0][0], bounds[1][1], bounds[1][0]]
        }
    }).then(() => {
        alert(window.javaCallback);
        window.javaCallback.log(docName + " successfully written!");
    }).catch((error) => {
        alert(window.javaCallback);
        window.javaCallback.log("Error writing document: ", error);
    });
}

window.sendPolygon = function(coordinates, bounds) {
    db.collection("geofiles").doc("Polygon").set({
        type: "Feature",
        geometry: {
            type: "Polygon",
            coordinates: coordinates.toString()
        },
        properties: {
            name: "Polygon",
            bbox: [bounds[0][1], bounds[0][0], bounds[1][1], bounds[1][0]]
        }
    }).then(() => {
        alert(window.javaCallback);
        window.javaCallback.log("Polygon successfully written!");
    }).catch((error) => {
        alert(window.javaCallback);
        window.javaCallback.log("Error writing document: ", error);
    });
}

window.sendPoints = function(coordinates, bounds, docName = "Points") {
    db.collection("geofiles").doc(docName).set({
        type: "Feature",
        geometry: {
            type: "MultiPoint",
            coordinates: coordinates.toString()
        },
        properties: {
            name: docName,
            bbox: [bounds[0][1], bounds[0][0], bounds[1][1], bounds[1][0]]
        }
    }).then(() => {
        alert(window.javaCallback);
        window.javaCallback.log("Points successfully written!");
    }).catch((error) => {
        alert(window.javaCallback);
        window.javaCallback.log("Error writing document: ", error);
    });
}
