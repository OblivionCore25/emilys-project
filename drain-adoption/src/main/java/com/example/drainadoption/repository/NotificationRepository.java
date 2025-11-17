package com.example.drainadoption.repository;

import com.example.drainadoption.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByOrderByCreatedAtDesc();
    Long countByReadFalse();
    
    @Transactional
    void deleteByDrainId(Long drainId);
}
