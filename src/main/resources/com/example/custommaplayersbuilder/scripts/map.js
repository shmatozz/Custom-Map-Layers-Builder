ymaps.ready(initMap);

var points = [];

function initMap() {
    var map = new ymaps.Map('map', {
        center: [56.315420929026374, 43.99029890743777],
        zoom: 12
    });

    var line = new ymaps.Polyline([], {}, {
        strokeColor: '#ff0000',
        strokeWidth: 4,
        draggable: true
    });

    var polygon = new ymaps.Polygon([[]], {}, {
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
            document.getElementById('routeButton').style.display = 'block';
            document.getElementById('routeButton').addEventListener('click', buildRoute);
            document.getElementById('lineButton').style.display = 'block';
            document.getElementById('lineButton').addEventListener('click', buildLine);
        }
        if (points.length > 2) {
            document.getElementById('polygonButton').style.display = 'block';
            document.getElementById('polygonButton').addEventListener('click', buildPolygon);
        }
    }

    function buildRoute() {
        if (points.length < 2) {
            console.error("Not enough points to build a route");
            return;
        }


        const multiRoute = new ymaps.multiRouter.MultiRoute({
            referencePoints: points
        }, {
            boundsAutoApply: true
        });

        map.geoObjects.add(multiRoute);
    }

    function buildLine() {
        line.geometry.setCoordinates(points);
        map.geoObjects.add(line);
    }

    function buildPolygon() {
        polygon.geometry.setCoordinates([points]);
        map.geoObjects.add(polygon);
    }
}
