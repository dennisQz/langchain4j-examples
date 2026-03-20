package dev.langchain4j.example.configuration.repository;

import dev.langchain4j.example.configuration.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity> findBySessionIdOrderByMessageOrderAsc(String sessionId);

    @Transactional
    void deleteBySessionId(String sessionId);

    Long countBySessionId(String sessionId);
}