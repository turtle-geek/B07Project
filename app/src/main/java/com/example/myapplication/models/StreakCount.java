package com.example.myapplication.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class StreakCount {
    private ArrayList<StreakDay> streakDays;
    private int controllerStreak;
    private int techniqueStreak;
    private int rescueCount;

    public StreakCount() {
        streakDays = new ArrayList<>();
        controllerStreak = 0;
        techniqueStreak = 0;
        rescueCount = 0;
    }

    public void addStreakDay(StreakDay day) {
        streakDays.add(day);
    }
    
    public int getControllerStreak() {
        return controllerStreak;
    }
    
    public int getTechniqueStreak() {
        return techniqueStreak;
    }

    public int getRescueCount() {
        return rescueCount;
    }

    private int getMonthlyControllerUsage() {
        // Calculate rescueCount: number of controllerUsage=true in the current month.
        // "Current month" is the month of the last entry in streakDays.
        LocalDate lastDayDate = streakDays.get(streakDays.size() - 1).getDate();
        int currentMonth = lastDayDate.getMonthValue();
        int currentYear = lastDayDate.getYear();

        int monthlyControllerUsage = 0;
        for (StreakDay day : streakDays) {
            if (day.getDate().getYear() == currentYear && day.getDate().getMonthValue() == currentMonth) {
                if (day.isControllerUsage()) {
                    monthlyControllerUsage++;
                }
            }
        }
        return monthlyControllerUsage;
    }

    private int getCurrentControllerStreak() {
        // Calculate the current streak of days where controllerUsage is true
        int currentControllerStreak = 0;
        LocalDate expectedControllerDate = streakDays.get(streakDays.size() - 1).getDate();

        for (int i = streakDays.size() - 1; i >= 0; i--) {
            StreakDay day = streakDays.get(i);

            if (!day.getDate().equals(expectedControllerDate)) {
                break;
            }

            if (day.isControllerUsage()) {
                currentControllerStreak++;
                expectedControllerDate = expectedControllerDate.minusDays(1);
            } else {
                break;
            }
        }
        return currentControllerStreak;
    }

    private int getCurrentTechniqueStreak() {
        // Calculate the current streak of days where techniqueQuality is HIGH
        int currentTechniqueStreak = 0;
        LocalDate expectedTechniqueDate = streakDays.get(streakDays.size() - 1).getDate();

        for (int i = streakDays.size() - 1; i >= 0; i--) {
            StreakDay day = streakDays.get(i);

            if (!day.getDate().equals(expectedTechniqueDate)) {
                break;
            }

            if (day.getTechniqueQuality() == TechniqueQuality.HIGH) {
                currentTechniqueStreak++;
                expectedTechniqueDate = expectedTechniqueDate.minusDays(1);
            } else {
                break;
            }
        }
        return currentTechniqueStreak;
    }
    
    public void countStreaks() {
        // Reset counters
        controllerStreak = 0;
        techniqueStreak = 0;
        rescueCount = 0;

        if (streakDays.isEmpty()) {
            return;
        }

        // Sort by date to ensure correct streak calculation
        streakDays.sort(Comparator.comparing(StreakDay::getDate));

        this.controllerStreak = getCurrentControllerStreak();
        this.techniqueStreak = getCurrentTechniqueStreak();
        this.rescueCount = getMonthlyControllerUsage();
    }
}
