package com.example.drainadoption.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentRequest {
    private String text;
    private String imageUrl;
}
