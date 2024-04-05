package com.example.custommaplayersbuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class Converter {
    public void allToFeatureCollection(
            double[][] route,
            double[][] line,
            double[][] polygon,
            double[][] points,
            String outputPath
    ) {
        JSONObject featureCollection = new JSONObject();
        featureCollection.put("type", "FeatureCollection");

        JSONArray features = new JSONArray();

        features.put(createFeature("MultiPoint", points));
        features.put(createFeature("LineString", line));
        features.put(createFeature("Polygon", polygon));
        features.put(createFeature("LineString", route));

        featureCollection.put("features", features);

        try (FileWriter file = new FileWriter(outputPath)) {
            file.write(featureCollection.toString(2));
            System.out.println("Successfully wrote GeoJSON to file: " + outputPath);
        } catch (IOException e) {
            System.err.println("Error writing GeoJSON to file: " + e.getMessage());
        }
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
            geometry.put("coordinates", new double[][][] { coordinates } );
        } else {
            geometry.put("coordinates", coordinates);
        }

        /* Creating properties (name, bounding box) */
        JSONObject properties = new JSONObject();
        properties.put("name", geometryType);
        properties.put("bbox", getBoundingBox(coordinates));

        /* Put all fields to feature */
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", properties);

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
