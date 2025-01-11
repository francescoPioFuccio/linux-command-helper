package it.unisannio.historyservice.repository;

import it.unisannio.historyservice.entity.UserPromptResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPromptResponseRepository extends JpaRepository<UserPromptResponse, Integer> {

    List<UserPromptResponse> findAllByUserId(Integer userId);

    List<UserPromptResponse> findAllByUserIdOrderByTimestampAsc(Integer userId);
}
