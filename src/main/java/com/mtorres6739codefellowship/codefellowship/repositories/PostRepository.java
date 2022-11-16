package com.mtorres6739codefellowship.codefellowship.repositories;

import com.mtorres6739codefellowship.codefellowship.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
