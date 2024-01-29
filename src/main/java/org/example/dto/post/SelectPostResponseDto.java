package org.example.dto.post;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelectPostResponseDto {

    private String title;
    private String content;
    private String author;
    private String email;
    private LocalDateTime saveDate;
}
