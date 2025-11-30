package com.example.myapplication.health;

import android.os.Build;

import com.example.myapplication.models.TechniqueQuality;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MedicineUsageLog {
    private InventoryItem medicine;
    private double dosageAmount;
    private LocalDateTime timestamp;
    private TechniqueQuality techniqueQuality;
    private String rating;

    // No-arg constructor for Firebase
    public MedicineUsageLog() {
        // Firebase needs this
    }

    // ========== BACKWARD COMPATIBILITY CONSTRUCTORS (Accept String) ==========

    /**
     * Constructor with String timestamp (for backward compatibility)
     * Converts String to LocalDateTime
     */
    public MedicineUsageLog(InventoryItem medicine, double dosageAmount, String timestamp, TechniqueQuality techniqueQuality) {
        this.medicine = medicine;
        this.dosageAmount = dosageAmount;
        this.timestamp = parseStringToLocalDateTime(timestamp);
        this.techniqueQuality = techniqueQuality;
        this.rating = null;
    }

    /**
     * Constructor with String timestamp and rating (for backward compatibility)
     * Converts String to LocalDateTime
     */
    public MedicineUsageLog(InventoryItem medicine, double dosageAmount, String timestamp, TechniqueQuality techniqueQuality, String rating) {
        this.medicine = medicine;
        this.dosageAmount = dosageAmount;
        this.timestamp = parseStringToLocalDateTime(timestamp);
        this.techniqueQuality = techniqueQuality;
        this.rating = rating;  // FIX: Actually save the rating!
    }

    // ========== MODERN CONSTRUCTORS (Accept LocalDateTime) ==========

    /**
     * Constructor with LocalDateTime (no rating)
     */
    public MedicineUsageLog(InventoryItem medicine, double dosageAmount, LocalDateTime timestamp, TechniqueQuality techniqueQuality) {
        this.medicine = medicine;
        this.dosageAmount = dosageAmount;
        this.timestamp = timestamp;
        this.techniqueQuality = techniqueQuality;
        this.rating = null;
    }

    /**
     * Constructor with LocalDateTime and rating (RECOMMENDED)
     */
    public MedicineUsageLog(InventoryItem medicine, double dosageAmount, LocalDateTime timestamp, TechniqueQuality techniqueQuality, String rating) {
        this.medicine = medicine;
        this.dosageAmount = dosageAmount;
        this.timestamp = timestamp;
        this.techniqueQuality = techniqueQuality;
        this.rating = rating;
    }

    // ========== HELPER METHOD ==========

    /**
     * Converts String timestamp to LocalDateTime
     * Supports multiple formats for backward compatibility
     */
    private LocalDateTime parseStringToLocalDateTime(String timestampStr) {
        if (timestampStr == null || timestampStr.isEmpty()) {
            return LocalDateTime.now();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // Try ISO format first (e.g., "2025-12-01T10:30:00")
                return LocalDateTime.parse(timestampStr);
            } catch (Exception e1) {
                try {
                    // Try custom format (e.g., "Dec 01, 2025 10:30 AM")
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");
                    return LocalDateTime.parse(timestampStr, formatter);
                } catch (Exception e2) {
                    // If all parsing fails, use current time
                    return LocalDateTime.now();
                }
            }
        } else {
            // For older Android versions, just use current time
            return LocalDateTime.now();
        }
    }

    // ========== GETTERS ==========

    public InventoryItem getMedicine() {
        return medicine;
    }

    public double getDosageAmount() {
        return dosageAmount;
    }

    /**
     * Returns LocalDateTime timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns timestamp as formatted string
     * For backward compatibility with code expecting String
     */
    public String getTimestampAsString() {
        if (timestamp == null) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a");
            return timestamp.format(formatter);
        } else {
            return timestamp.toString();
        }
    }

    /**
     * Helper to get formatted timestamp string
     */
    public String getFormattedTimestamp() {
        return getTimestampAsString();
    }

    /**
     * Helper to get just the date
     */
    public LocalDate getDate() {
        if (timestamp == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return timestamp.toLocalDate();
        }
        return null;
    }

    public TechniqueQuality getTechniqueQuality() {
        return techniqueQuality;
    }

    public String getRating() {
        return rating;
    }

    // ========== SETTERS ==========

    public void setMedicine(InventoryItem medicine) {
        this.medicine = medicine;
    }

    public void setDosageAmount(double dosageAmount) {
        this.dosageAmount = dosageAmount;
    }

    /**
     * Set timestamp as LocalDateTime
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set timestamp as String (for backward compatibility)
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = parseStringToLocalDateTime(timestamp);
    }

    public void setTechniqueQuality(TechniqueQuality techniqueQuality) {
        this.techniqueQuality = techniqueQuality;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    // ========== DEPRECATED METHODS (For backward compatibility) ==========

    /**
     * @deprecated Use getDate() instead
     */
    @Deprecated
    public LocalDateTime parseTimestamp() {
        return timestamp;
    }

    /**
     * @deprecated Use getDate() instead
     */
    @Deprecated
    public LocalDate parseDate() {
        if (timestamp == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return timestamp.toLocalDate();
        }
        return null;
    }

    // ========== toString ==========

    @Override
    public String toString() {
        String ratingText = (rating != null) ? ", Rating: " + rating : "";
        String timestampText = (timestamp != null) ? getFormattedTimestamp() : "Unknown time";

        if (techniqueQuality == TechniqueQuality.NA) {
            return "Medicine: " + medicine.toString() +
                    ", Dosage: " + dosageAmount +
                    ratingText +
                    ", Timestamp: " + timestampText;
        } else {
            return "Medicine: " + medicine.toString() +
                    ", Dosage: " + dosageAmount +
                    ratingText +
                    ", Timestamp: " + timestampText +
                    ", Controller Quality: " + techniqueQuality;
        }
    }
}