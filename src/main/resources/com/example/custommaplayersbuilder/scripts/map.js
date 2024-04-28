ymaps.ready(initMap);

const points = [];
const customPoints = [];
let searchPoints = [];
let map;
let line;
let polygon;

function initMap() {
    searchControl = new ymaps.control.SearchControl({
        options: {
            provider: 'yandex#search',
            noPopup: true,
            results: 10
        }
    });

    map = new ymaps.Map('map', {
        center: [56.315420929026374, 43.99029890743777],
        zoom: 12,
        controls: [searchControl]
    });

    line = new ymaps.Polyline([], {}, {
        strokeColor: '#ff0000',
        strokeWidth: 4
    });

    polygon = new ymaps.Polygon([[]], {}, {
        strokeColor: '#ff0000',
        strokeWidth: 4,
        fillColor: 'rgba(252,0,0,0.34)'
    });

    map.events.add('click', onClickListenerHandler);

    searchControl.events.add('clear', function () {
        const searchPointsButton =  document.getElementById('getSearchPointsButton')

        searchPointsButton.style.display = 'none';
    }, this);

    searchControl.events.add('load', function() {
        searchPoints = [];
        var geoResults = searchControl.getResultsArray();
        const request = searchControl.getRequestString();
        var coordResults = [];

        for (var i = 0; i < geoResults.length; i++) {
            var resultCoordinates = geoResults[i].geometry.getCoordinates();
            coordResults.push(resultCoordinates);

            searchPoints.push({
                coords: resultCoordinates,
                hint: request,
                header: request,
                body: "",
                color: ""
            });
        }

        const searchPointsButton =  document.getElementById('getSearchPointsButton');

        searchPointsButton.style.display = 'block';
        searchPointsButton.addEventListener('click', function () {
            window.sendPoints(customPoints, searchPoints, map.getBounds(), searchControl.getRequestString());
        });

        alert(window.javaCallback);
        window.javaCallback.addPoints(coordResults, request);
        window.javaCallback.log("Результаты поиска мест добавлены на карту.");
    });

    function onClickListenerHandler(event) {
        var coords = event.get('coords');
        points.push(coords);

        alert(window.javaCallback);
        window.javaCallback.openPointCreateDialog(coords);
    }
}

var pointIndex;
function processCustomPoint(jsonData) {
    var data = JSON.parse(jsonData);

    alert(window.javaCallback);
    var coords = data['coords'].toString().split(',').map(parseFloat);
    [coords[0], coords[1]] = [coords[1], coords[0]];

    var marker = new ymaps.Placemark(coords, {
        hintContent: data['hint'],
        balloonContentHeader: data['header'],
        balloonContentBody: data['body']
    }, {
        iconColor: '#' + data['color'].substring(2),
        draggable: true
    });

    marker.events.add('dragstart', function () {
        pointIndex = points.findIndex(function(point) {
            return point[0] === coords[0] && point[1] === coords[1];
        });
        pointIndex = customPoints.findIndex(function(point) {
            return point.coords[0] === coords[0] && point.coords[1] === coords[1];
        });
    });

    marker.events.add('dragend', function () {
        points[pointIndex] = marker.geometry.getCoordinates();
        customPoints[pointIndex].coords = marker.geometry.getCoordinates();
    });

    map.geoObjects.add(marker);

    customPoints.push({
        coords: coords,
        hint: data['hint'],
        header: data['header'],
        body: data['body'],
        color: data['color']
    });

    if (points.length < 2) {
        const searchPointsButton =  document.getElementById('getSearchPointsButton')

        searchPointsButton.style.display = 'block';
        searchPointsButton.addEventListener('click', function () {
            window.sendPoints(customPoints, searchPoints, map.getBounds());
        });
    }
    if (points.length === 2) {
        const buildRouteButton =  document.getElementById('routeButton')
        const buildLineButton =  document.getElementById('lineButton')

        buildRouteButton.style.display = 'block';
        buildRouteButton.addEventListener('click', buildRoute);
        buildLineButton.style.display = 'block';
        buildLineButton.addEventListener('click', buildLine);
    }
    if (points.length > 2) {
        const buildPolygonButton = document.getElementById('polygonButton')

        buildPolygonButton.style.display = 'block';
        buildPolygonButton.addEventListener('click', buildPolygon);
    }
}

function buildRoute() {
    ymaps.route(points).then(function (route) {
        map.geoObjects.add(route);

        var way, segments;
        var allCoordinates = [];

        for (var i = 0; i < route.getPaths().getLength(); i++) {
            way = route.getPaths().get(i);
            segments = way.getSegments();
            for (var j = 0; j < segments.length; j++) {
                var segmentCoordinates = segments[j].getCoordinates();
                allCoordinates = allCoordinates.concat(segmentCoordinates);
            }
        }

        var minLatitude = Infinity;
        var minLongitude = Infinity;
        var maxLatitude = -Infinity;
        var maxLongitude = -Infinity;

        for (let i = 0; i < allCoordinates.length; i++) {
            minLatitude = Math.min(minLatitude, allCoordinates[i][0]);
            minLongitude = Math.min(minLongitude, allCoordinates[i][1]);
            maxLatitude = Math.max(maxLatitude, allCoordinates[i][0]);
            maxLongitude = Math.max(maxLongitude, allCoordinates[i][1]);
        }

        var bounds = [[minLatitude, minLongitude], [maxLatitude, maxLongitude]];

        const sendRouteButton =  document.getElementById('sendRouteButton')
        sendRouteButton.style.display = 'block';
        sendRouteButton.addEventListener('click', function () {
            window.sendLine(allCoordinates, bounds, "Route");
        });

        alert(window.javaCallback);
        window.javaCallback.addRoute(allCoordinates);
        window.javaCallback.log("Маршрут построен.");
    }, function () {
        alert(window.javaCallback);
        window.javaCallback.log("Упс, что-то пошло не так...");
    });
}

function buildLine() {
    line.geometry.setCoordinates(points);
    map.geoObjects.add(line);

    const sendLineButton =  document.getElementById('sendLineButton')
    sendLineButton.style.display = 'block';
    sendLineButton.addEventListener('click', function () {
        window.sendLine(line.geometry.getCoordinates(), line.geometry.getBounds());
    });

    alert(window.javaCallback);
    window.javaCallback.addLine(points);
    window.javaCallback.log("Линия построена.");
}

function buildPolygon() {
    polygon.geometry.setCoordinates([points.concat([points[0]])]);
    map.geoObjects.add(polygon);

    const sendPolygonButton =  document.getElementById('sendPolygonButton')
    sendPolygonButton.style.display = 'block';
    sendPolygonButton.addEventListener('click', function () {
        window.sendPolygon(polygon.geometry.getCoordinates(), polygon.geometry.getBounds())
    });

    alert(window.javaCallback);
    window.javaCallback.addPolygon(polygon.geometry.getCoordinates());
    window.javaCallback.log("Полигон построен.");
}
