package dev.langchain4j.example.configuration.repository;

import dev.langchain4j.example.configuration.entity.ChatMemoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatMemoryRepository extends JpaRepository<ChatMemoryEntity, Long> {

    Optional<ChatMemoryEntity> findBySessionId(String sessionId);
}
