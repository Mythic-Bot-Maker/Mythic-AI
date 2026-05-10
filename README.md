# Mythic

Mythic is an Android Studio starter project for a Samsung-first AI companion.

It is designed as a personal assistant that can:

- Chat like a helpful companion
- Draft messages and help with planning
- Prepare reminders, alarms, and routines
- React to wellness signals from a watch or phone
- Ask gentle check-in questions when stress, sadness, happiness, or other mood patterns may be present

The current version is a prototype. It includes local Java response logic, plus a real AI server connection.

## Open in Android Studio

1. Open Android Studio.
2. Choose **Open**.
3. Select this folder.
4. Let Gradle sync.
5. Press **Run**.

## Important Files

- `app/src/main/java/com/example/mythic/MainActivity.java` - main chat screen
- `app/src/main/java/com/example/mythic/MythicBrain.java` - assistant response logic
- `app/src/main/java/com/example/mythic/MythicApiClient.java` - real AI API connection
- `app/src/main/java/com/example/mythic/MythicConfig.java` - server URL settings
- `app/src/main/java/com/example/mythic/WellnessPanel.java` - watch wellness signal UI
- `app/src/main/AndroidManifest.xml` - phone and wearable-related permissions
- `server/server.js` - private AI server that talks to OpenAI
- `server/.env.example` - server secret settings template

## Turn On The AI API

For a real app, use the included server instead of putting your API key inside the Android app.

1. Upload the `server` folder to a Node host like Render, Railway, or another Node 18+ server.
2. Set this environment variable on the server:

```text
OPENAI_API_KEY=your_real_api_key
```

3. Start the server with:

```text
npm start
```

4. Copy your server's public URL.
5. Open `app/src/main/java/com/example/mythic/MythicConfig.java`.
6. Replace `PASTE_YOUR_MYTHIC_SERVER_URL_HERE` with your server URL, for example:

```java
public static final String MYTHIC_SERVER_URL = "https://your-mythic-server.onrender.com";
```

7. Run the Android app again.

There is still a direct API-key fallback in `MythicConfig.java` for private testing, but do not use that in a public app.

For local server testing only, create `server/.env` using the same format as `server/.env.example`. The real `.env` file is ignored by Git so your key does not get uploaded with the project.

## Next Steps

To turn this into a full assistant, connect these areas:

- A real AI API for natural conversation
- Samsung Health or Health Connect for wellness data
- Android permissions for contacts, calendar, alarms, notifications, and device actions
- A permission screen so Mythic asks before accessing private data or performing actions
- Wear OS companion app support for Samsung watches

Mythic should be powerful, but still respectful: it should ask permission before acting, explain what it is doing, and treat emotional signals as possibilities rather than facts.
