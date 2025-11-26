package com.example.myapplication.models;

import java.util.List;

public class MasterFilterParams {
    public Long startDate;
    public Long endDate;
    public List<String> selectedTriggers; // Crucial: List for multiple triggers
    public Boolean hasNightWaking;
    public Boolean hasActivityLimits;
    public Boolean hasCough;
    // Add other relevant parameters (e.g., minCoughScore, maxCoughScore)
}