package com.example.minglethedog.messaging;

import com.example.minglethedog.service.PostService;
import com.example.minglethedog.service.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final RedisService redisService;
    private final PostService postService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "mingle.mydb.post", groupId = "post-consumer-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            JsonNode jsonNode = objectMapper.readTree(record.value());
            JsonNode payload = jsonNode.get("payload");

            if (payload == null || !payload.hasNonNull("op")) {
                log.warn("Kafka 메시지에 유효한 payload가 없음: {}", record.value());
                return;
            }

            String operation = payload.get("op").asText();
            JsonNode afterNode = payload.get("after");
            JsonNode beforeNode = payload.get("before");

            switch (operation) {
                case "c":
                    handleInsertEvent(afterNode);
                    break;
                case "u":
                    handleUpdateEvent(beforeNode, afterNode);
                    break;
                case "d":
                    handleDeleteEvent(beforeNode);
                    break;
                default:
                    log.warn("지원하지 않는 Kafka 이벤트 유형: {}", operation);
            }

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 새 게시글이 생성될 때 처리
     */
    private void handleInsertEvent(JsonNode afterNode) {
        if (afterNode == null || !afterNode.hasNonNull("post_id") || !afterNode.hasNonNull("author_id")) {
            log.warn("INSERT 이벤트에서 post_id 또는 author_id가 누락됨: {}", afterNode);
            return;
        }

        long postId = afterNode.get("post_id").asLong();
        long authorId = afterNode.get("author_id").asLong();
        updateRedisCache(authorId, postId);
        log.info(" [INSERT] 게시글 추가됨 - post_id: {}, author_id: {}", postId, authorId);
    }

    /**
     * 기존 게시글이 수정될 때 처리 (content가 변경된 경우만)
     */
    private void handleUpdateEvent(JsonNode beforeNode, JsonNode afterNode) {
        if (beforeNode == null || afterNode == null ||
                !beforeNode.hasNonNull("content") || !afterNode.hasNonNull("content")) {
            log.warn("UPDATE 이벤트에서 content 필드가 누락됨: before={}, after={}", beforeNode, afterNode);
            return;
        }

        boolean isContentChanged = !beforeNode.get("content").asText().equals(afterNode.get("content").asText());
        if (!isContentChanged) {
            return;
        }

        long postId = afterNode.get("post_id").asLong();
        long authorId = afterNode.get("author_id").asLong();
        updateRedisCache(authorId, postId);
        log.info("🔄 [UPDATE] 게시글 내용 변경 - post_id: {}, author_id: {}", postId, authorId);
    }

    /**
     * 게시글이 삭제될 때 처리
     */
    private void handleDeleteEvent(JsonNode beforeNode) {
        if (beforeNode == null || !beforeNode.hasNonNull("post_id") || !beforeNode.hasNonNull("author_id")) {
            log.warn("DELETE 이벤트에서 post_id 또는 author_id가 누락됨: {}", beforeNode);
            return;
        }

        long deletedId = beforeNode.get("post_id").asLong();
        long authorId = beforeNode.get("author_id").asLong();
        deleteRedisCache(authorId, deletedId);
        log.info(" [DELETE] 게시글 삭제됨 - post_id: {}, author_id: {}", deletedId, authorId);
    }

    /**
     * Redis 캐시에 게시글 추가
     */
    private void updateRedisCache(Long authorId, Long postId) {
        Set<String> followers = redisService.getFollowers(authorId);
        if (followers.isEmpty()) {
            log.info(" [Redis] authorId {} 에 대한 팔로워 없음, 업데이트 생략", authorId);
            return;
        }
        for (String follower : followers) {
            redisService.updateCache(Long.valueOf(follower), postId);
        }
    }

    /**
     * Redis 캐시에서 게시글 삭제
     */
    private void deleteRedisCache(Long authorId, Long postId) {
        Set<String> followers = redisService.getFollowers(authorId);
        if (followers.isEmpty()) {
            log.info(" [Redis] authorId {} 에 대한 팔로워 없음, 삭제 생략", authorId);
            return;
        }
        for (String follower : followers) {
            redisService.deleteCache(Long.valueOf(follower), postId);
        }
    }
}