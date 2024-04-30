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
        window.javaCallback.log(docName + " успешно отправлен на сервер.");
    }).catch(() => {
        alert(window.javaCallback);
        window.javaCallback.log("Упс, что-то пошло не так...");
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
        window.javaCallback.log("Polygon успешно отправлен на сервер.");
    }).catch(() => {
        alert(window.javaCallback);
        window.javaCallback.log("Упс, что-то пошло не так...");
    });
}

window.sendPoints = function(customPoints, searchPoints, bounds, docName = "Points") {
    const allPoints = customPoints.concat(searchPoints);

    const features = allPoints.map(function (point) {
        return {
            type: "Feature",
            geometry: {
                type: "Point",
                coordinates: point.coords
            },
            properties: {
                hint: point.hint,
                header: point.header,
                body: point.body,
                color: point.color
            }
        };
    });

    const featureCollection = {
        type: "FeatureCollection",
        features: features
    };

    db.collection("geofiles").doc(docName).set(featureCollection)
        .then(() => {
            alert(window.javaCallback);
            window.javaCallback.log("Points успешно отправлены на сервер.");
        }).catch(() => {
            alert(window.javaCallback);
            window.javaCallback.log("Упс, что-то пошло не так...");
    });
}

window.sendAll = function createFeatureCollection(customPoints, searchPoints, line, polygon, route, docName) {
    const allFeatures = [];

    const allPoints = customPoints.concat(searchPoints);
    allPoints.forEach(function(point) {
        const feature = {
            type: "Feature",
            geometry: {
                type: "Point",
                coordinates: point.coords
            },
            properties: {
                hint: point.hint,
                header: point.header,
                body: point.body,
                color: point.color
            }
        };
        allFeatures.push(feature);
    });

    if (line.geometry.getCoordinates().length > 0) {
        const feature = {
            type: "Feature",
            geometry: {
                type: "LineString",
                coordinates: line.geometry.getCoordinates().toString()
            },
            properties: {
                name: "Line"
            }
        };
        allFeatures.push(feature);
    }

    if (polygon.geometry.getCoordinates()[0].length > 0) {
        const feature = {
            type: "Feature",
            geometry: {
                type: "Polygon",
                coordinates: polygon.geometry.getCoordinates().toString()
            },
            properties: {
                name: "Polygon"
            }
        };
        allFeatures.push(feature);
    }

    if (route.coordinates.length > 0) {
        const feature = {
            type: "Feature",
            geometry: {
                type: "LineString",
                coordinates: route.coordinates.toString()
            },
            properties: {
                name: "Route"
            }
        };
        allFeatures.push(feature);
    }

    const featureCollection = {
        type: "FeatureCollection",
        features: allFeatures
    };

    db.collection("geofiles").doc(docName.toString()).set(featureCollection)
        .then(() => {
            alert(window.javaCallback);
            window.javaCallback.log("Все данные успешно отправлены на сервер.");
        }).catch(() => {
            alert(window.javaCallback);
            window.javaCallback.log("Упс, что-то пошло не так...");
    });
}
