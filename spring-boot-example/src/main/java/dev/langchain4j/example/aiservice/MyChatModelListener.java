package dev.langchain4j.example.aiservice;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyChatModelListener implements ChatModelListener {

    private static final Logger log = LoggerFactory.getLogger(MyChatModelListener.class);

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        requestContext.attributes().put("start_time", System.nanoTime());
        log.info("onRequest(): [Request ID: {}] Model: {}, Request: {}", 
            requestContext.chatRequest().hashCode(), 
            requestContext.chatRequest().modelName(),
            requestContext.chatRequest());
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        long startTime = (long) responseContext.attributes().get("start_time");
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        log.info("onResponse(): [Request ID: {}] Total Duration: {} ms", 
            responseContext.chatRequest().hashCode(),
            String.format("%.2f", durationMs));
        
        log.info("Response: {}", responseContext.chatResponse());
        
        if (responseContext.chatResponse().tokenUsage() != null) {
            log.info("Token Usage: {}", responseContext.chatResponse().tokenUsage());
        }

        if (responseContext.chatResponse().metadata() != null) {
            log.info("Response Metadata: {}", responseContext.chatResponse().metadata());
        }
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        long startTime = (long) errorContext.attributes().get("start_time");
        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        log.error("onError(): [Request ID: {}] Duration until error: {} ms, Error: {}", 
            errorContext.chatRequest().hashCode(),
            String.format("%.2f", durationMs),
            errorContext.error().getMessage(), 
            errorContext.error());
    }
}
