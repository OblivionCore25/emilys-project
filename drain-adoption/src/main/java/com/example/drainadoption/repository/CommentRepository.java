package com.example.drainadoption.repository;

import com.example.drainadoption.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDrainIdOrderByCreatedAtDesc(Long drainId);
    
    @Transactional
    void deleteByDrainId(Long drainId);
}
