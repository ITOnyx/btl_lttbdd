package com.example.app.model;

import java.io.Serializable;
import java.util.Locale;

public class TienDoInfo implements Serializable {
    private int completedTasks;
    private int totalTasks;

    public TienDoInfo() {
        this.completedTasks = 0;
        this.totalTasks = 0;
    }

    public TienDoInfo(int completedTasks, int totalTasks) {
        this.completedTasks = completedTasks;
        this.totalTasks = totalTasks;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getPercent() {
        if (totalTasks <= 0) {
            return 0;
        }
        return (completedTasks * 100) / totalTasks;
    }

    public String getFormattedText() {
        return String.format(Locale.getDefault(), "%d/%d (%d%%)", completedTasks, totalTasks, getPercent());
    }
}
