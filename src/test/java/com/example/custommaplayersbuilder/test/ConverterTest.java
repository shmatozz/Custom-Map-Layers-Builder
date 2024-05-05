package com.example.custommaplayersbuilder.test;

import static org.junit.Assert.*;

import com.example.custommaplayersbuilder.Converter;
import org.json.JSONArray;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONObject;

public class ConverterTest{

    @Test
    public void testCreateFeature() {
        Converter converter = new Converter("test.json");
        double[] pointCoords = {1.0, 2.0};
        JSONObject pointFeature = converter.createFeature("Point", new double[][]{pointCoords});

        assertEquals("Point", pointFeature.getJSONObject("geometry").getString("type"));
        assertArrayEquals(pointCoords, ((double[]) pointFeature.getJSONObject("geometry").get("coordinates")), 0.001);
        assertEquals("Point", pointFeature.getJSONObject("properties").getString("name"));
    }


    @Test
    public void testAllToFeatureCollection() {
        Converter converter = new Converter("test.json");
        double[][] route = {{0.0, 0.0}, {1.0, 1.0}};
        double[][] line = {{0.0, 0.0}, {1.0, 1.0}};
        double[][] polygon = {{0.0, 0.0}, {1.0, 1.0}, {0.0, 1.0}};
        ArrayList<JSONObject> points = new ArrayList<>();
        ArrayList<JSONObject> customPoints = new ArrayList<>();

        try {
            converter.allToFeatureCollection(route, line, polygon, points, customPoints);
            File file = new File("test.json");
            assertTrue(file.exists());
            assertTrue(file.length() > 0);
            file.delete();
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testConvertLine() {
        Converter converter = new Converter("test.json");
        double[][] line = {{0.0, 0.0}, {1.0, 1.0}};

        try {
            converter.convertLine(line);
            File file = new File("test.json");
            assertTrue(file.exists());
            assertTrue(file.length() > 0);
            file.delete();
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testConvertPolygon() {
        Converter converter = new Converter("test.json");
        double[][] polygon = {{0.0, 0.0}, {1.0, 1.0}, {0.0, 1.0}};

        try {
            converter.convertPolygon(polygon);
            File file = new File("test.json");
            assertTrue(file.exists());
            assertTrue(file.length() > 0);
            file.delete();
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testConvertPoints() {
        Converter converter = new Converter("test.json");
        ArrayList<JSONObject> points = new ArrayList<>();
        ArrayList<JSONObject> customPoints = new ArrayList<>();
        JSONObject point = new JSONObject();
        point.put("coords", new JSONArray(new double[] { 1.0, 2.0 }));
        point.put("header", "Point Header");

        points.add(point);

        try {
            converter.convertPoints(points, customPoints);
            File file = new File("test.json");
            assertTrue(file.exists());
            assertTrue(file.length() > 0);
            file.delete();
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testGetBoundingBox() {
        Converter converter = new Converter("test.json");
        double[][] coordinates = {{0.0, 0.0}, {1.0, 1.0}, {0.0, 1.0}};
        double[] bbox = converter.getBoundingBox(coordinates);

        assertArrayEquals(new double[]{0.0, 0.0, 1.0, 1.0}, bbox, 0.001);
    }

    @Test
    public void testGetFeatureCollectionBbox() {
        Converter converter = new Converter("test.json");
        double[][] coordinates1 = {{0.0, 0.0}, {1.0, 1.0}, {0.0, 1.0}};
        double[][] coordinates2 = {{2.0, 2.0}, {3.0, 3.0}, {2.0, 3.0}};
        JSONArray features = new JSONArray();
        features.put(new JSONObject().put("bbox", converter.getBoundingBox(coordinates1)));
        features.put(new JSONObject().put("bbox", converter.getBoundingBox(coordinates2)));

        double[] bbox = converter.getFeatureCollectionBbox(features);

        assertArrayEquals(new double[]{0.0, 0.0, 3.0, 3.0}, bbox, 0.001);
    }
}
