package com.example.drainadoption.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private Long drainId;
    private Long userId;
    private String userName;
    private String text;
    private String imageUrl;
    private LocalDateTime createdAt;
}
