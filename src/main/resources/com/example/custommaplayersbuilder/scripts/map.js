initMap();
const points = [];

async function initMap() {
    await ymaps3.ready;

    const {YMapDefaultMarker} = await ymaps3.import('@yandex/ymaps3-markers@0.0.1');

    const {
        YMap,
        YMapDefaultSchemeLayer,
        YMapListener,
        YMapDefaultFeaturesLayer,
        YMapFeature
    } = ymaps3;

    const map = new YMap(
        document.getElementById('map'),
        {
            location: {
                center: [43.99029890743777, 56.315420929026374],
                zoom: 12
            }
        },
        [
            new YMapDefaultSchemeLayer({}),
            new YMapDefaultFeaturesLayer({})
        ]
    );

    const listener = new YMapListener({
        onClick: onClickListenerHandler
    });
    map.addChild(listener);

    const line = new YMapFeature({
        geometry: {
            type: 'LineString',
            coordinates: points
        },
        style: {stroke: [{color: 'rgba(252,0,0,0.66)', width: 4}]}
    });

    const polygon = new YMapFeature({
        geometry: {
            type: 'Polygon',
            coordinates: [points]
        },
        style: {stroke: [{color: 'rgba(252,0,0,0.66)', width: 4}], fill: 'rgba(252,0,0,0.34)'}
    });

    map.addChild(line);
    map.addChild(polygon);

    function onClickListenerHandler(object, event) {
        const coords = event.coordinates;
        console.log(points)

        const marker = new YMapDefaultMarker({
            coordinates: coords,
            color: '#bd0000'
        });
        points.push(coords);
        if (points.length === 2) {
            document.getElementById('routeButton').style.display = 'block';
            document.getElementById('lineButton').style.display = 'block';
            document.getElementById('lineButton').addEventListener("click", () => {
                line.update({
                    geometry: {
                        type: 'LineString',
                        coordinates: points
                    }
                });
                console.log(line);
            });
        } if (points.length > 2) {
            document.getElementById('polygonButton').style.display = 'block';
            document.getElementById('polygonButton').addEventListener("click", () => {
                polygon.update({
                    geometry: {
                        type: 'Polygon',
                        coordinates: [points]
                    }
                });
                console.log(polygon);
            });
        }
        map.addChild(marker);

        alert(window.javaCallback)
        window.javaCallback.addPoint(coords[0], coords[1]);
    }
}

async function fetchRoute(startCoordinates, endCoordinates) {
    // Request a route from the Router API with the specified parameters.
    const routes = await ymaps3.route({
        points: [startCoordinates, endCoordinates],
        type: 'driving',
        bounds: true
    });

    // Check if a route was found
    if (!routes[0]) return;

    const route = routes[0].toRoute();

    // Check if a route has coordinates
    if (route.geometry.coordinates.length == 0) return;

    return route;
}

function buildRoute() {
    // Проверяем, что у нас есть как минимум две точки для построения маршрута
    if (points.length < 2) {
        console.error("Недостаточно точек для построения маршрута");
        return;
    }

    // Получаем начальную и конечную точки маршрута из массива точек
    const startPoint = points[0];
    const endPoint = points[1];

    // Запрашиваем маршрут между начальной и конечной точками
    fetchRoute(startPoint, endPoint).then(routeHandler);
}

// Обработчик полученного маршрута
function routeHandler(newRoute) {
    // If the route is not found, then we alert a message and clear the route line
    if (!newRoute) {
        alert('Route not found');
        routeLine.update({geometry: {type: 'LineString', coordinates: []}});
        return;
    }

    routeLine.update({...newRoute});
    if (newRoute.properties.bounds) {
        map.setLocation({bounds: newRoute.properties.bounds, duration: 300});
    }
}