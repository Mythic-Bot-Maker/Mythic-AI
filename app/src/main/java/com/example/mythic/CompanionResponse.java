package com.example.mythic;

public class CompanionResponse {
    public final String message;
    public final String moodLabel;
    public final String confidenceLabel;

    public CompanionResponse(String message, String moodLabel, String confidenceLabel) {
        this.message = message;
        this.moodLabel = moodLabel;
        this.confidenceLabel = confidenceLabel;
    }
}
