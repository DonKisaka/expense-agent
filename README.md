# AI Expense Agent

A conversational AI agent that manages personal expenses through natural language. Instead of filling forms or clicking buttons, you just talk to it.

---

## The Problem

Expense tracking apps require manual data entry — open the app, fill in amount, pick category, set date, save. Nobody does this consistently. By the time you log an expense, you've already forgotten half the details.

The real problem is friction. If logging an expense takes more than one sentence, people stop doing it.

---

## The Solution

An AI agent you talk to naturally:

> *"Add lunch at KFC, KES 850, FOOD category, today"*
> *"How much did I spend on transport this week?"*
> *"Show me my 5 most recent expenses"*

The AI understands your intent, calls the right database operation, and responds in plain English. No forms. No clicks.

---

## What Makes This Different From a Chatbot

A regular chatbot answers questions. This agent **acts** — it reads from and writes to a real Postgres database based on what you ask. It decides on its own which tool to call, when to call it, and what arguments to pass.

```
You: "How much have I spent on food?"
Agent thinks: I need to call getTotalByCategory("FOOD")
Agent calls the tool → gets result from database
Agent responds: "You've spent KES 4,250 on FOOD so far."
```

You never wrote that logic. The AI figured it out from the tool descriptions.

---

## Architecture

```
POST /api/agent/chat
        │
        ▼
AgentController
        │
        ▼
AgentService
        │  message + session history + available tools
        ▼
Ollama AI (llama3.2)
        │  decides which tool to call
        ▼
ExpenseTools (@Tool methods)
        │  executes against Postgres
        ▼
Ollama AI (reasons over result)
        │
        ▼
Natural language response
```

---

## Tech Stack

| Technology | Role |
|------------|------|
| Spring Boot 4.0.6 | Application framework |
| Spring AI 2.0.0-M5 | AI integration + tool calling |
| Ollama (llama3.2) | Local LLM — no API costs |
| PostgreSQL | Expense storage |
| Docker | Postgres container |

---

## Project Structure

```
src/main/java/com/donald/expense_agent/
│
├── model/
│   └── Expense.java                  # Database entity
│
├── repository/
│   └── ExpenseRepository.java        # JPA queries
│
├── tools/
│   └── ExpenseTools.java             # Methods the AI can call
│
├── service/
│   └── AgentService.java             # Manages conversation + tools
│
└── controller/
    └── AgentController.java          # REST endpoint
```

---

## Available Tools

The AI can call any of these based on your message:

| Tool | What it does |
|------|-------------|
| `addExpense()` | Save a new expense to the database |
| `getAllExpenses()` | Fetch all stored expenses |
| `getExpensesByCategory()` | Filter expenses by category |
| `getExpensesByDateRange()` | Fetch expenses between two dates |
| `getTotalByCategory()` | Sum all spending in a category |
| `getRecentExpenses()` | Get the 5 most recent expenses |
| `deleteExpense()` | Remove an expense by ID |

---

## How Tool Calling Works

Each method in `ExpenseTools.java` is annotated with `@Tool` and a plain English description:

```java
@Tool(description = "Get total amount spent in a specific category.")
public String getTotalByCategory(String category) { ... }
```

Spring AI sends these descriptions to the LLM alongside your message. The LLM reads them and decides which method matches your intent — then Spring AI executes it and returns the result back to the LLM for a final response.

---

## Running Locally

### Prerequisites
- Java 25
- Docker
- Ollama installed and running

### Steps

**1. Pull the AI model:**
```bash
ollama pull llama3.2
```

**2. Start Postgres:**
```bash
docker-compose up -d
```

**3. Run the app:**
```bash
./mvnw spring-boot:run
```

---

## Testing

**POST** `http://localhost:8080/api/agent/chat`

Add header:
```
X-Session-Id: session-donald
```

Example messages:

```
Add an expense: lunch at KFC, KES 850, category FOOD, today
```
```
What are all my expenses?
```
```
How much have I spent on FOOD?
```
```
Show me my most recent expenses
```
```
Delete expense with ID 3
```

---

## Session Memory

The agent remembers context within a session. Send the same `X-Session-Id` header across requests and it will recall what was discussed earlier.

Different session ID = fresh conversation.

---

## Part of a Learning Series

| Level | Project | Concepts |
|-------|---------|----------|
| 1 | Invoice Processor | Spring AI basics, structured extraction |
| 2 | RAG Document Assistant | Embeddings, pgvector, document Q&A |
| 3 | Fraud Detection Pipeline | Kafka, async AI, event-driven architecture |
| 4 | **Expense Agent** | Tool calling, function execution, session memory |
