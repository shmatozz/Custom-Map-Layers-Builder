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

db.collection("geofiles").get().then((querySnapshot) => {
    querySnapshot.forEach((doc) => {
        console.log(`${doc.id} => ${doc.data()}`);
    });
});
