package com.example.websocket_demo.repository;

import com.example.websocket_demo.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findTop20ByOrderByTimestampDesc();

    List<Message> findByMessageContaining(String keyword);

    void deleteById(Long id);

    List<Message> findByIdLessThanOrderByTimestampDesc(Long lastMessageId, Pageable pageable);
}
