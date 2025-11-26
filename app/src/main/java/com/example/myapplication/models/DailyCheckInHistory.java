// CheckInHistory.java
package com.example.myapplication.models;

import java.util.ArrayList;
import java.util.List;

public class DailyCheckInHistory {
    // A dynamic array (ArrayList) to store all the check-in entries
    private List<DailyCheckIn> historyLogs;

    public DailyCheckInHistory() {
        // Initialize the list when the object is created
        this.historyLogs = new ArrayList<>();
    }

    /**
     * Adds a new daily check-in entry to the history.
     * @param entry The completed AsthmaCheckIn object.
     */
    public void addEntry(DailyCheckIn entry) {
        this.historyLogs.add(entry);
    }

    /**
     * Returns the full list of check-in history.
     * This will be used to implement the history browser, filtering, and export.
     */
    public List<DailyCheckIn> getAllEntries() {
        return new ArrayList<>(historyLogs); // Return a copy to prevent external modification
    }


//    public List<DailyCheckIn> masterFilter(MasterFilterParams params){
//        List<DailyCheckIn> filteredEntries = new ArrayList<>(historyLogs);
//
//    }

    public List<DailyCheckIn> filterByDateRange(
            List<DailyCheckIn> entriesToFilter, long startTimestamp, long endTimestamp){
        List<DailyCheckIn> result = new ArrayList<>();
        for(DailyCheckIn entry : entriesToFilter){
            if(entry.getCheckInTimestamp() >= startTimestamp &&
                    entry.getCheckInTimestamp() <= endTimestamp){
                result.add(entry);
            }
        }
        return result;
    }
    public List<DailyCheckIn> getEntriesByDateRange(long startTimestamp, long endTimestamp){
        List<DailyCheckIn> entries = new ArrayList<>();
        for(DailyCheckIn entry : historyLogs){
            if(entry.getCheckInTimestamp() >= startTimestamp && entry.getCheckInTimestamp() <= endTimestamp){
                entries.add(entry);
            }
        }
        return entries;
    }

    public List<DailyCheckIn> filterByTrigger(String trigger) {
        List<DailyCheckIn> entries = new ArrayList<>();
        for (DailyCheckIn entry : historyLogs) {
            if (entry.getSelectedTriggers().contains(trigger)) {
                entries.add(entry);
            }
        }
        return entries;
    }

    public List<DailyCheckIn> filterBySymptomNightWaking() {
        List<DailyCheckIn> entries = new ArrayList<>();
        for (DailyCheckIn entry : historyLogs) {
            if (entry.getNightWaking()) {
                entries.add(entry);
            }
        }
        return entries;
    }

    public List<DailyCheckIn> filterBySymptomActivityLimits() {
        List<DailyCheckIn> entries = new ArrayList<>();
        for (DailyCheckIn entry : historyLogs) {
            if (entry.getActivityLimits() > 0) {
                entries.add(entry);
            }
        }
        return entries;
    }

    public List<DailyCheckIn> filterBySymptomCough() {
        List<DailyCheckIn> entries = new ArrayList<>();
        for (DailyCheckIn entry : historyLogs) {
            if (entry.getCough() > 0) {
                entries.add(entry);
            }
        }
        return entries;
    }
    // add methods here later for:
    // - getEntriesByDateRange(long startTimestamp, long endTimestamp)
    // - filterByTrigger(String trigger)
    // - filterBySymptom(int minCoughWheeze)
    // - exportToCSV(List<AsthmaCheckIn> entries)
}