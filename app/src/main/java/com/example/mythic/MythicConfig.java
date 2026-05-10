package com.example.mythic;

public class MythicConfig {
    public static final String MYTHIC_SERVER_URL = "https://mythic-ai.onrender.com";
    public static final String OPENAI_API_KEY = "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";
    public static final String MODEL = "gpt-5-mini";

    public static final String SYSTEM_INSTRUCTIONS =
            "You are Mythic, a Samsung-first personal AI companion. " +
            "You help with planning, messages, routines, learning, and friendly conversation. " +
            "When the user discusses stress, sadness, fear, or loneliness, be warm and grounding. " +
            "You can suggest phone tasks, but you must ask before claiming to send messages, access private data, or change device settings. " +
            "Treat wellness signals as possibilities, not facts.";

    public static boolean hasServerUrl() {
        return MYTHIC_SERVER_URL != null
                && !MYTHIC_SERVER_URL.trim().isEmpty()
                && !"https://mythic-ai.onrender.com".equals(MYTHIC_SERVER_URL);
    }

    public static boolean hasApiKey() {
        return OPENAI_API_KEY != null
                && !OPENAI_API_KEY.trim().isEmpty()
                && !"PASTE_YOUR_OPENAI_API_KEY_HERE".equals(OPENAI_API_KEY);
    }
}
