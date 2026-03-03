# Tasks

- [x] Task 1: Update Assistant and StreamingAssistant Interfaces
    - [x] Modify `Assistant.java` to accept `@MemoryId String sessionId`.
    - [x] Modify `StreamingAssistant.java` to accept `@MemoryId String sessionId`.
    - [x] Modify `TravelAssistant.java` to accept `@MemoryId String sessionId`.
- [x] Task 2: Configure ChatMemoryProvider
    - [x] In `AssistantConfiguration.java`, replace `ChatMemory` bean with `ChatMemoryProvider` bean.
    - [x] Implement `ChatMemoryProvider` using `ConcurrentHashMap` to store `MessageWindowChatMemory` instances.
- [x] Task 3: Update Controllers to Accept Session ID
    - [x] Update `AssistantController.java` to accept `@RequestParam(defaultValue = "default") String sessionId` and pass it to services.
    - [x] Update `TravelController.java` to accept `@RequestParam(defaultValue = "default") String sessionId` and pass it to services.
- [x] Task 4: Update Tests and Example Calls
    - [x] Update `TestAssistant.java` and `TravelControllerTest.java` to match new interface signatures.
    - [x] Verify `test.http` with session IDs.

# Task Dependencies
- Task 2 and Task 3 depend on Task 1 (Interfaces must be updated first).
- Task 4 depends on all previous tasks.
