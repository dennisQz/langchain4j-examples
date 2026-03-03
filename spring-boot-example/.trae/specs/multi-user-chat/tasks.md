# Tasks

- [ ] Task 1: Add Caffeine Dependency (Optional/Verify)
    - Check if `caffeine` is available. If not, add it to `pom.xml` or use `ConcurrentLinkedHashMap` / `Collections.synchronizedMap` with a size limit for the example. *Correction: For this example, I will use a simple `ConcurrentHashMap` with a size check or just a standard `MessageWindowChatMemory` which limits messages per user, but to strictly "avoid OOM", I will implement a `ChatMemoryStore` that removes old entries.*
    - Actually, `MessageWindowChatMemory` only limits messages *per chat*. We need to limit *number of chats*.
    - Decision: Implement a simple `InMemoryChatMemoryStore` with a capacity limit.

- [ ] Task 2: Refactor Assistant Interface
    - [ ] Update `Assistant.java`: Add `@MemoryId String sessionId` to `chat` method.

- [ ] Task 3: Configure ChatMemoryProvider
    - [ ] Modify `AssistantConfiguration.java`:
        - Remove `@Bean ChatMemory chatMemory()`.
        - Add `@Bean ChatMemoryProvider chatMemoryProvider()`.
        - Implement `ChatMemoryStore` (inner class or bean) with a simple map and eviction policy (e.g., remove oldest if size > N).

- [ ] Task 4: Update Controller
    - [ ] Modify `AssistantController.java`:
        - Add `String sessionId` parameter to endpoints (default to a random UUID if missing, or require it).
        - Pass `sessionId` to `assistant.chat()`.
        - Add `Semaphore` for concurrency limiting (Queuing).

- [ ] Task 5: Verification
    - [ ] Verify that different `sessionId`s get different responses/context.
    - [ ] Verify that memory does not grow indefinitely (mock test or manual check).
