ymaps.ready(initMap);

const points = [];
let map;
let line;
let polygon;
let route;

function initMap() {
    map = new ymaps.Map('map', {
        center: [56.315420929026374, 43.99029890743777],
        zoom: 12
    });

    line = new ymaps.Polyline([], {}, {
        strokeColor: '#ff0000',
        strokeWidth: 4,
        draggable: true
    });

    polygon = new ymaps.Polygon([[]], {}, {
        strokeColor: '#ff0000',
        strokeWidth: 4,
        fillColor: 'rgba(252,0,0,0.34)',
        draggable: true
    });

    map.events.add('click', onClickListenerHandler);

    function onClickListenerHandler(event) {
        var coords = event.get('coords');
        points.push(coords);

        var marker = new ymaps.Placemark(coords, {iconContent: String(points.length)});
        map.geoObjects.add(marker);

        if (points.length >= 2) {
            const buildRouteButton =  document.getElementById('routeButton')
            const buildLineButton =  document.getElementById('lineButton')

            buildRouteButton.style.display = 'block';
            buildRouteButton.addEventListener('click', buildRoute);
            buildLineButton.style.display = 'block';
            buildLineButton.addEventListener('click', buildLine);
        }
        if (points.length > 2) {
            const buildPolygonButton =  document.getElementById('polygonButton')

            buildPolygonButton.style.display = 'block';
            buildPolygonButton.addEventListener('click', buildPolygon);
        }
    }
}

function buildRoute() {
    if (points.length < 2) {
        console.error("Not enough points to build a route");
        return;
    }

    ymaps.route(points).then(function (route) {
        map.geoObjects.add(route);
        var wayPoints = route.getWayPoints(),
            lastPoint = wayPoints.getLength() - 1;
        // Задаем стиль метки - иконки будут красного цвета, и
        // их изображения будут растягиваться под контент.
        wayPoints.options.set('preset', 'islands#redStretchyIcon');
        // Задаем контент меток в начальной и конечной точках.
        wayPoints.get(0).properties.set('iconContent', 'Точка отправления');
        wayPoints.get(lastPoint).properties.set('iconContent', 'Точка прибытия');

        var way,
            segments;

        var allCoordinates = [];
        // Получаем массив путей.
        for (var i = 0; i < route.getPaths().getLength(); i++) {
            way = route.getPaths().get(i);
            segments = way.getSegments();
            for (var j = 0; j < segments.length; j++) {
                var segmentCoordinates = segments[j].getCoordinates();
                allCoordinates = allCoordinates.concat(segmentCoordinates);
            }
        }

        const sendRouteButton =  document.getElementById('sendRouteButton')
        sendRouteButton.style.display = 'block';
        sendRouteButton.addEventListener('click', async function () {
            window.sendLine(allCoordinates, "Route");
        });

        alert(window.javaCallback);
        window.javaCallback.addRoute(allCoordinates);
        window.javaCallback.log("Route was built");
    }, function (error) {
        alert(window.javaCallback);
        window.javaCallback.log("Error: " + error.message);
    });
}

function buildLine() {
    line.geometry.setCoordinates(points);
    map.geoObjects.add(line);

    const sendLineButton =  document.getElementById('sendLineButton')
    sendLineButton.style.display = 'block';
    sendLineButton.addEventListener('click', async function () {
        window.sendLine(line.geometry.getCoordinates());
    });

    alert(window.javaCallback);
    window.javaCallback.addLine(points);
    window.javaCallback.log("Line was built");
}

function buildPolygon() {
    polygon.geometry.setCoordinates([points]);
    map.geoObjects.add(polygon);

    const sendPolygonButton =  document.getElementById('sendPolygonButton')
    sendPolygonButton.style.display = 'block';
    sendPolygonButton.addEventListener('click', async function () {
        window.sendPolygon(polygon.geometry.getCoordinates())
    });

    alert(window.javaCallback);
    window.javaCallback.addPolygon(points);
    window.javaCallback.log("Polygon was built");
}