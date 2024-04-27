package com.example.custommaplayersbuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Converter {
    private final String outputPath;

    Converter(String outputPath) {
        this.outputPath = outputPath;
    }

    public void allToFeatureCollection(
            double[][] route,
            double[][] line,
            double[][] polygon,
            ArrayList<JSONObject> points,
            ArrayList<JSONObject> customPoints
    ) throws IOException {
        /* Creating feature collection */
        JSONObject featureCollection = new JSONObject();
        JSONArray features = new JSONArray();

        /* Add existing features to collections */
        if (!points.isEmpty() || !customPoints.isEmpty()) {
            features.put(createPointsFeature(points, customPoints));
        }
        if (line.length > 0) {
            features.put(createFeature("LineString", line));
        }
        if (polygon.length > 0) {
            features.put(createFeature("Polygon", polygon));
        }
        if (route.length > 0) {
            features.put(createFeature("LineString", route));
        }

        /* Put created fields to JSON Object */
        featureCollection.put("type", "FeatureCollection");
        featureCollection.put("features", features);

        /* Write created object to file */
        writeToFile(featureCollection);
    }

    public void convertLine(double[][] line) throws IOException {
        /* Creating JSON object of LINE feature */
        JSONObject lineObject = createFeature("LineString", line);

        /* Write created object to file */
        writeToFile(lineObject);
    }

    public void convertPolygon(double[][] polygon) throws IOException {
        /* Creating JSON object of POLYGON feature */
        JSONObject polygonObject = createFeature("Polygon", polygon);

        /* Write created object to file */
        writeToFile(polygonObject);
    }

    public void convertPoints(ArrayList<JSONObject> points, ArrayList<JSONObject> customPoints) throws IOException {
        /* Creating JSON object of POINTS feature */
        JSONObject pointsObject = createPointsFeature(points, customPoints);

        /* Write created object to file */
        writeToFile(pointsObject);
    }

    public void convertRoute(double[][] route) throws IOException {
        /* Creating JSON object of ROUTE (LineString) feature */
        JSONObject routeObject = createFeature("LineString", route);

        /* Write created object to file */
        writeToFile(routeObject);
    }

    private void writeToFile(JSONObject object) throws IOException {
        FileWriter file = new FileWriter(outputPath);
        file.write(object.toString(2));
        file.close();
    }

    /**
     * Create GeoJSON feature by type and coordinates
     * @param geometryType type of feature to build
     * @param coordinates coordinates array
     * @return JSONObject of built feature
     */
    private JSONObject createFeature(String geometryType, double[][] coordinates) {
        JSONObject feature = new JSONObject();

        /* Creating geometry */
        JSONObject geometry = new JSONObject();
        geometry.put("type", geometryType);
        if (Objects.equals(geometryType, "Polygon")) {
            geometry.put("coordinates", new double[][][]{coordinates});
        } else if (Objects.equals(geometryType, "Point")) {
            geometry.put("coordinates", coordinates[0]);
        } else {
            geometry.put("coordinates", coordinates);
        }

        /* Creating properties (name, bounding box) */
        JSONObject properties = new JSONObject();
        properties.put("name", geometryType);
        if (coordinates.length > 0) {
            properties.put("bbox", getBoundingBox(coordinates));
        }

        /* Put all fields to feature */
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", properties);

        return feature;
    }

    /**
     * Create GeoJSON points feature by arrays of Objects with properties
     * @param points array of places search results
     * @param customPoints array of custom points
     * @return JSONObject of built feature
     */
    private JSONObject createPointsFeature(ArrayList<JSONObject> points, ArrayList<JSONObject> customPoints) {
        JSONObject feature = new JSONObject();
        ArrayList<JSONObject> features = new ArrayList<>();

        feature.put("type", "FeatureCollection");

        if (!customPoints.isEmpty()) {
            for (JSONObject point : customPoints) {
                JSONObject pointFeature = createFeature("Point", new double[][]{ (double[]) point.get("coords") });

                pointFeature.getJSONObject("properties").put("header", point.get("header"));
                pointFeature.getJSONObject("properties").put("body", point.get("body"));
                pointFeature.getJSONObject("properties").put("hint", point.get("hint"));
                pointFeature.getJSONObject("properties").put("color", point.get("color"));

                features.add(pointFeature);
            }
        }

        if (!points.isEmpty()) {
            for (JSONObject point : points) {
                double[] pointCoords = new double[2];
                pointCoords[0] = point.getJSONArray("coords").getDouble(0);
                pointCoords[1] = point.getJSONArray("coords").getDouble(1);
                JSONObject pointFeature = createFeature("Point", new double[][] { pointCoords });

                pointFeature.getJSONObject("properties").put("header", point.get("header"));

                features.add(pointFeature);
            }
        }

        feature.put("type", "FeatureCollection");
        feature.put("features", features);

        return feature;
    }

    /**
     * Calculating bounding box by given coordinates
     * @param coordinates array of coordinates
     * @return array of 4 bounds as coordinates
     */
    private double[] getBoundingBox(double[][] coordinates) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (double[] coord : coordinates) {
            double x = coord[0];
            double y = coord[1];
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        return new double[]{minX, minY, maxX, maxY};
    }
}
