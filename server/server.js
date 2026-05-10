import http from "node:http";
import { existsSync, readFileSync } from "node:fs";

loadLocalEnv();

const port = Number(process.env.PORT || 3000);
const apiKey = process.env.OPENAI_API_KEY;
const model = process.env.OPENAI_MODEL || "gpt-5-mini";

const instructions = [
  "You are Mythic, a Samsung-first personal AI companion.",
  "You help with planning, messages, routines, learning, and friendly conversation.",
  "You are warm and friend-like, but you do not pretend to be human.",
  "When the user discusses stress, sadness, fear, or loneliness, be grounding and supportive.",
  "You can suggest phone tasks, but you must ask before claiming to send messages, access private data, or change device settings.",
  "Treat wellness signals as possibilities, not facts."
].join(" ");

const server = http.createServer(async (request, response) => {
  setCorsHeaders(response);

  if (request.method === "OPTIONS") {
    response.writeHead(204);
    response.end();
    return;
  }

  if (request.method === "GET" && request.url === "/health") {
    sendJson(response, 200, { ok: true, name: "Mythic AI Server" });
    return;
  }

  if (request.method !== "POST" || request.url !== "/ask") {
    sendJson(response, 404, { error: "Not found" });
    return;
  }

  if (!apiKey) {
    sendJson(response, 500, { error: "Missing OPENAI_API_KEY on the server." });
    return;
  }

  try {
    const body = await readJson(request);
    const message = String(body.message || "").trim();

    if (!message) {
      sendJson(response, 400, { error: "Message is required." });
      return;
    }

    const aiResponse = await fetch("https://api.openai.com/v1/responses", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${apiKey}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        model,
        instructions,
        input: message,
        max_output_tokens: 450
      })
    });

    const result = await aiResponse.json();

    if (!aiResponse.ok) {
      sendJson(response, aiResponse.status, {
        error: result.error?.message || "The AI request failed."
      });
      return;
    }

    sendJson(response, 200, { reply: extractText(result) });
  } catch (error) {
    sendJson(response, 500, { error: "Mythic server failed to answer." });
  }
});

server.listen(port, () => {
  console.log(`Mythic AI server running on port ${port}`);
});

function setCorsHeaders(response) {
  response.setHeader("Access-Control-Allow-Origin", "*");
  response.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
  response.setHeader("Access-Control-Allow-Headers", "Content-Type");
}

function sendJson(response, statusCode, data) {
  response.writeHead(statusCode, { "Content-Type": "application/json" });
  response.end(JSON.stringify(data));
}

async function readJson(request) {
  let raw = "";
  for await (const chunk of request) {
    raw += chunk;
  }
  return raw ? JSON.parse(raw) : {};
}

function extractText(result) {
  for (const item of result.output || []) {
    if (item.type !== "message") {
      continue;
    }

    for (const content of item.content || []) {
      if (content.type === "output_text" && content.text) {
        return content.text.trim();
      }
    }
  }

  return "I am here, but I could not read the AI response clearly.";
}

function loadLocalEnv() {
  if (!existsSync(".env")) {
    return;
  }

  const lines = readFileSync(".env", "utf8").split(/\r?\n/);
  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith("#")) {
      continue;
    }

    const equalsIndex = trimmed.indexOf("=");
    if (equalsIndex === -1) {
      continue;
    }

    const key = trimmed.slice(0, equalsIndex).trim();
    const value = trimmed.slice(equalsIndex + 1).trim();

    if (key && process.env[key] === undefined) {
      process.env[key] = value;
    }
  }
}
