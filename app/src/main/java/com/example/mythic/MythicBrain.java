package com.example.mythic;

import java.util.Locale;

public class MythicBrain {
    public CompanionResponse answer(String prompt, WellnessSignal signal) {
        String text = prompt.toLowerCase(Locale.US);

        if (containsAny(text, "stress", "stressed", "panic", "overwhelmed")) {
            return new CompanionResponse(
                    "I am here with you. Let us slow the moment down: breathe in for four, hold for two, breathe out for six. Then tell me the one thing that feels heaviest right now.",
                    "Stress support",
                    "High"
            );
        }

        if (containsAny(text, "sad", "lonely", "depressed", "upset")) {
            return new CompanionResponse(
                    "That sounds heavy. I can stay with you, help name what happened, draft a message to someone you trust, or make the next ten minutes easier.",
                    "Sadness support",
                    "Medium"
            );
        }

        if (containsAny(text, "happy", "excited", "proud", "great")) {
            return new CompanionResponse(
                    "I love that. Want me to help capture the moment, make a quick note, text someone, or turn this energy into a plan?",
                    "Positive mood",
                    "Medium"
            );
        }

        if (containsAny(text, "schedule", "remind", "calendar", "alarm")) {
            return new CompanionResponse(
                    "I can help organize that. In the full version, Mythic would ask permission, then create reminders, alarms, routines, and calendar plans across your Samsung phone, tablet, and watch.",
                    signal.label,
                    signal.confidence
            );
        }

        if (containsAny(text, "call", "text", "message", "email")) {
            return new CompanionResponse(
                    "Tell me who it is for and what tone you want. I can draft it first, then ask before sending so you stay in control.",
                    signal.label,
                    signal.confidence
            );
        }

        if (containsAny(text, "watch", "heart", "sleep", "mood", "emotion")) {
            return new CompanionResponse(
                    "On a Samsung watch, Mythic could use permitted wellness signals like heart rate trends, sleep, movement, and check-in history to notice possible stress or low mood. It should ask gently, never assume.",
                    "Wearable-aware",
                    "Prototype"
            );
        }

        if (containsAny(text, "friend", "talk", "listen")) {
            return new CompanionResponse(
                    "I can be a steady voice: honest, useful, and kind. I will remember what matters to you when you allow it, and I will help without pretending to replace real people.",
                    "Companion mode",
                    "High"
            );
        }

        return new CompanionResponse(
                "I can help with that. I can plan, draft, summarize, search your device with permission, suggest routines, and talk through what is going on like a companion.",
                signal.label,
                signal.confidence
        );
    }

    private boolean containsAny(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
