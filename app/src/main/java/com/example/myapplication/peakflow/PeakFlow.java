package com.example.myapplication.peakflow;

import java.time.LocalDateTime;

public class PeakFlow {
    int peakFlow;
    LocalDateTime time;
    int personalBest;

    /**
     * Constructor for PeakFlow
     * @param peakFlow
     * @param time of logging the peakflow value
     */
    public PeakFlow(int peakFlow, LocalDateTime time) {
        this.peakFlow = peakFlow;
        this.time = time;
    }

    /**
     * Getter for peakFlow
     * @return peakFlow
     */
    public int getPeakFlow() {
        return peakFlow;
    }

    public void setPersonalBest(int personalBest) {
        this.personalBest = personalBest;
    }


}
