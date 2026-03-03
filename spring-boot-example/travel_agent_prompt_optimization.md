# Travel Agent Prompt Optimization Plan

## Goal
Optimize the Travel Life Agent prompt to handle ambiguous user inputs by asking for clarification when the user's intent is unclear.

## Steps

1.  **Modify `TravelResponse` Model**
    *   File: `src/main/java/dev/langchain4j/example/aiservice/model/TravelResponse.java`
    *   Action: Add a `message` field (String) with getter and setter. This field will hold the clarification message from the agent.

2.  **Update `TravelAssistant` System Prompt**
    *   File: `src/main/java/dev/langchain4j/example/aiservice/TravelAssistant.java`
    *   Action: Update the `@SystemMessage` annotation.
        *   Add instruction: If the user's intent is unclear or the scene is ambiguous, return a JSON with a `message` field asking for clarification.
        *   Update the "Structure output" requirement to include the `message` field as an alternative to `phrases`.

3.  **Verify Changes**
    *   File: `src/test/java/dev/langchain4j/example/aiservice/TravelControllerTest.java`
    *   Action: Add a new test case `testChatWithAmbiguousScene` to verify that the controller correctly returns the `message` field when the assistant provides it.
    *   Run the tests to ensure everything works as expected.
