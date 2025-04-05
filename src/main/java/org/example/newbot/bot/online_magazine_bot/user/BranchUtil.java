package org.example.newbot.bot.online_magazine_bot.user;

import org.example.newbot.model.Branch;

import java.util.List;
public class BranchUtil {

    // Masofani metrda va kilometrda formatlab qaytaradi
    public static String formatDistance(double distanceInKm) {
        if (distanceInKm < 1) {
            // Agar masofa 1 km dan kichik bo'lsa, 3 xonali formatda qaytaradi
            return String.format("%.3f km", distanceInKm);
        } else {
            // Agar masofa 1 km dan katta bo'lsa, oddiy formatda (masalan 1.3 km)
            return String.format("%.1f km", distanceInKm);
        }
    }

    // Find the distance to the nearest branch and return formatted distance
    public static Double findNearBranchKm(List<Branch> branches, double userLat, double userLon) {
        Branch nearestBranch = findNearestBranch(branches, userLat, userLon);
        if (nearestBranch != null) {
            return haversine(userLat, userLon, nearestBranch.getLatitude(), nearestBranch.getLongitude()); // Format the distance and return
        } else {
            return null;
        }
    }

    // Calculate the distance using the Haversine formula (returns distance in kilometers)
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Returns distance in kilometers
    }

    // Find the nearest branch
    public static Branch findNearestBranch(List<Branch> branches, double userLat, double userLon) {
        Branch nearestBranch = null;
        double minDistance = Double.MAX_VALUE;

        for (Branch branch : branches) {
            if (branch.getLatitude() != null && branch.getLongitude() != null) {
                double distance = haversine(userLat, userLon, branch.getLatitude(), branch.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestBranch = branch;
                }
            }
        }

        return nearestBranch;
    }
}
