package com.sprint.mission.guestbook;

public record GuestBookRequestDto(
        String name,
        String title,
        String content
) {}
