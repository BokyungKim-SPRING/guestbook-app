package com.sprint.mission.guestbook;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/guestbooks")
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookService guestBookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GuestBookResponseDto> create(
            @ModelAttribute GuestBookRequestDto request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        GuestBookResponseDto response = guestBookService.create(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<GuestBookResponseDto> pageResult = guestBookService.list(PageRequest.of(page, size));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("size", pageResult.getSize());
        response.put("number", pageResult.getNumber());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestBookResponseDto> get(@PathVariable Long id){
        return ResponseEntity.ok(guestBookService.get(id));
    }
}
