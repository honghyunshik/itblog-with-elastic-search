package org.example.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepostiory extends JpaRepository<Post,Long> {
}
