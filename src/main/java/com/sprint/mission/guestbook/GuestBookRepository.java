package com.sprint.mission.guestbook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestBookRepository extends JpaRepository<GuestBookEntity, Long> {
}

