package com.sprint.mission.guestbook;

import java.time.LocalDateTime;

public record GuestBookResponseDto(
        Long id,
        String name,
        String title,
        String content,
        String imageUrl,
        String s3Url,
        LocalDateTime createdAt
) {}
