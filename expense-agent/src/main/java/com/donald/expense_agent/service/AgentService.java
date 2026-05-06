package com.donald.expense_agent.service;

import com.donald.expense_agent.tools.ExpenseTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public AgentService(ChatClient.Builder builder, ExpenseTools expenseTools) {
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();

        this.chatClient = builder
                .defaultSystem("""
                          You are a personal expense assistant for a Kenyan user.
                          You help users track, query, and manage their expenses.
                          Always use the available tools to fetch or store data — never guess.
                          When adding expenses, confirm the details with the user before saving.
                          When displaying expenses, format amounts in Kenyan Shillings (KES).
                          If a category is not recognized, ask the user to pick from:
                          FOOD, TRANSPORT, UTILITIES, ENTERTAINMENT, HEALTH, OTHER.
                          If you cannot find relevant data, say so honestly.
                          """)
                .defaultTools(expenseTools)
                .build();
    }

    public String chat(String sessionId, String userMessage) {
        return this.chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(sessionId)
                        .build())
                .user(userMessage)
                .call()
                .content();
    }
}
