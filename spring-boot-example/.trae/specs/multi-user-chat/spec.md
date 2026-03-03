# Multi-User Chat Support Spec

## Why
The current implementation only supports a single chat memory instance, which means all users share the same conversation context. To support multiple concurrent users, we need to isolate chat sessions using a `sessionId`. Additionally, to prevent application server memory exhaustion (OOM), we need a strategy to manage chat memory storage efficiently. We also need to consider request queuing/throttling to handle high concurrency.

## What Changes

### 1. Assistant Interface Update
- Modify `Assistant.java` to accept `@MemoryId String sessionId` in the `chat` method.
- This allows LangChain4j to associate the conversation with a specific user/session.

### 2. Chat Memory Configuration
- **Replace** the existing single `ChatMemory` bean in `AssistantConfiguration.java`.
- **Add** a `ChatMemoryProvider` bean that creates/retrieves `ChatMemory` based on the `memoryId`.
- **Implement** a `ChatMemoryStore` using **Caffeine Cache** (in-memory with eviction) to:
    - Persist chat history per session.
    - Set a maximum size and expiration policy to prevent OOM (answering the user's concern about memory).
    - (Note: For production, this can be easily swapped with Redis/DB).

### 3. Controller Update
- Update `AssistantController.java` to accept `sessionId` (e.g., via `@RequestHeader("X-Session-Id")` or `@RequestParam`).
- Pass the `sessionId` to the `Assistant` service.

### 4. Concurrency Control (Queuing)
- Add a `Semaphore` or `RateLimiter` in the `AssistantController` to limit the number of concurrent requests.
- This addresses the user's question about "party waiting" (queuing) to prevent overloading the system or the LLM provider.

## Impact
- **Affected Code**:
    - `src/main/java/dev/langchain4j/example/aiservice/Assistant.java`
    - `src/main/java/dev/langchain4j/example/aiservice/AssistantController.java`
    - `src/main/java/dev/langchain4j/example/aiservice/AssistantConfiguration.java`
- **New Dependencies**: `com.github.ben-manes.caffeine:caffeine` (if not already present, or use standard Map with manual eviction, but Caffeine is better for memory management). *Check if Caffeine is available or just use a simple bounded map for the example.*

## Requirements

### Requirement: User Isolation
- The system SHALL use `sessionId` to isolate chat memory for different users.
- Users SHALL NOT see each other's conversation history.

### Requirement: Memory Management
- The system SHALL use a bounded storage mechanism (e.g., LRU Cache) for chat memory to prevent OOM.
- Old sessions SHALL be evicted if memory limit is reached.

### Requirement: Concurrency Control
- The system SHALL limit concurrent processing to a configurable number (e.g., 10) to avoid resource exhaustion.
- Excess requests SHALL wait (queue) or be rejected (fail-fast) based on configuration.
