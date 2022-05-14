package com.cbd.teammate;

public class DistanceCalculator {

    public DistanceCalculator() {

    }

    public Double calculateDistance(Double lat1, Double lat2, Double lon1, Double lon2) {
        Double res;

        double dLon = deg2rad(lon2 - lon1);
        double dLat = deg2rad(lat2 - lat1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        res = 6370 * c;

        return res;
    }

    private double deg2rad(double value) {
        return value * (Math.PI / 180);
    }
}
