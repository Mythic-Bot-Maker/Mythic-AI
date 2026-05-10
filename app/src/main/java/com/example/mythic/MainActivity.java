package com.example.mythic;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private LinearLayout messages;
    private EditText promptInput;
    private WellnessPanel wellnessPanel;
    private final MythicBrain brain = new MythicBrain();
    private final MythicApiClient apiClient = new MythicApiClient();
    private final ExecutorService assistantExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.app_name));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 28, 28, 28);
        root.setBackgroundColor(Color.rgb(13, 16, 25));

        TextView title = new TextView(this);
        title.setText("Mythic");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Your Samsung-first AI companion for tasks, wellness, and real conversation.");
        subtitle.setTextColor(Color.rgb(184, 194, 214));
        subtitle.setTextSize(15);
        subtitle.setPadding(0, 4, 0, 18);
        root.addView(subtitle);

        wellnessPanel = new WellnessPanel(this);
        root.addView(wellnessPanel.getView());

        ScrollView scrollView = new ScrollView(this);
        messages = new LinearLayout(this);
        messages.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(messages);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);
        inputRow.setGravity(Gravity.CENTER_VERTICAL);

        promptInput = new EditText(this);
        promptInput.setHint("Message Mythic");
        promptInput.setSingleLine(false);
        promptInput.setMinLines(1);
        promptInput.setMaxLines(4);
        promptInput.setImeOptions(EditorInfo.IME_ACTION_SEND);
        promptInput.setTextColor(Color.WHITE);
        promptInput.setHintTextColor(Color.rgb(145, 154, 176));
        promptInput.setBackgroundColor(Color.rgb(31, 36, 51));
        promptInput.setPadding(18, 12, 18, 12);
        inputRow.addView(promptInput, new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        ));

        Button sendButton = new Button(this);
        sendButton.setText("Send");
        inputRow.addView(sendButton);
        root.addView(inputRow);

        setContentView(root);

        addMessage("Mythic", "I am Mythic. I can talk like a friend, plan your day, draft messages, prepare routines, and check in when your watch signals that something may be off.");

        sendButton.setOnClickListener(view -> sendPrompt(scrollView));
        promptInput.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendPrompt(scrollView);
                return true;
            }
            return false;
        });
    }

    private void sendPrompt(ScrollView scrollView) {
        String prompt = promptInput.getText().toString().trim();
        if (prompt.isEmpty()) {
            return;
        }

        addMessage("You", prompt);
        promptInput.setText("");

        CompanionResponse localResponse = brain.answer(prompt, wellnessPanel.getLatestSignal());
        TextView thinkingBubble = addMessage("Mythic", "Thinking...");
        wellnessPanel.showMood(localResponse.moodLabel, localResponse.confidenceLabel);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));

        assistantExecutor.execute(() -> {
            String answer = apiClient.ask(prompt, localResponse.message);
            runOnUiThread(() -> {
                thinkingBubble.setText("Mythic\n" + answer);
                scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
            });
        });
    }

    private TextView addMessage(String sender, String text) {
        TextView bubble = new TextView(this);
        bubble.setText(sender + "\n" + text);
        bubble.setTextColor(Color.WHITE);
        bubble.setTextSize(16);
        bubble.setLineSpacing(4, 1);
        bubble.setPadding(18, 14, 18, 14);

        if ("You".equals(sender)) {
            bubble.setBackgroundColor(Color.rgb(63, 86, 152));
            bubble.setGravity(Gravity.END);
        } else {
            bubble.setBackgroundColor(Color.rgb(39, 45, 63));
            bubble.setGravity(Gravity.START);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        messages.addView(bubble, params);
        return bubble;
    }
}
