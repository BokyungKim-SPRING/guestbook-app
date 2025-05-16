package com.sprint.mission.guestbook;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class GuestBookService {
    private final S3Client s3Client;
    private final GuestBookRepository guestBookRepository;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    public GuestBookResponseDto create(GuestBookRequestDto request, MultipartFile image) throws IOException {
        String s3Key = null;
        String s3Url = null;
        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            s3Key = "guestbook/" + UUID.randomUUID() + extension;

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Key)
                            .contentType(image.getContentType())
                            .build(),
                    RequestBody.fromInputStream(image.getInputStream(), image.getSize())
            );

            s3Url = baseUrl + "/" + s3Key;
            imageUrl = s3Url;
        }

        GuestBookEntity entity = new GuestBookEntity(
                request.name(),
                request.title(),
                request.content(),
                imageUrl,
                s3Url,
                s3Key
        );
        entity.setCreatedAt(LocalDateTime.now());

        GuestBookEntity saved = guestBookRepository.save(entity);
        return toResponse(saved);
    }

    public Page<GuestBookResponseDto> list(Pageable pageable) {
        // 페이지가 존재하는지 확인 및 조정
        Page<GuestBookEntity> entityPage = guestBookRepository.findAll(pageable);

        if (pageable.getPageNumber() >= entityPage.getTotalPages() && entityPage.getTotalPages() > 0) {
            // 요청된 페이지가 범위를 벗어나면 마지막 페이지로 조정
            pageable = PageRequest.of(entityPage.getTotalPages() - 1, pageable.getPageSize());
            entityPage = guestBookRepository.findAll(pageable);
        }

        return entityPage.map(this::toResponse);
    }

    public GuestBookResponseDto get(Long id) {
        return guestBookRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Guestbook not found"));
    }

    public GuestBookResponseDto toResponse(GuestBookEntity e) {
        return new GuestBookResponseDto(
                e.getId(),
                e.getName(),
                e.getTitle(),
                e.getContent(),
                e.getImageUrl(),
                e.getS3Url(),
                e.getCreatedAt()
        );
    }
}
