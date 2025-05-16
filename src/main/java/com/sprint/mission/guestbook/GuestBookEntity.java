package com.sprint.mission.guestbook;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "guestbook")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuestBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String title;
    private String content;
    private String imageUrl;
    private String s3Url;
    private String s3Key;
    private LocalDateTime createdAt;

    public GuestBookEntity(String name, String title, String content, String imageUrl, String s3Url, String s3Key) {
        this.name = name;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.s3Url = s3Url;
        this.s3Key = s3Key;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
