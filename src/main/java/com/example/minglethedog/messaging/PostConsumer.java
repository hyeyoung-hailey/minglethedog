//package com.example.minglethedog.messaging;
//
//import com.example.minglethedog.dto.PostDto;
//import com.example.minglethedog.dto.PostResponse;
//import com.example.minglethedog.entity.Post;
//import com.example.minglethedog.service.FollowService;
//import com.example.minglethedog.service.PostService;
//import com.example.minglethedog.service.RedisService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Component;
//
//import java.util.Set;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class PostConsumer {
//    private final RedisService redisService;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final PostService postService;
//
//
//    @KafkaListener(topics = "post-topic", groupId = "newsfeed-group") // 어떤메시지를 소비할지 어떤 그룹에서 메시지 처리할지 결정하는 설정
//    public void listen(ConsumerRecord<String, String> record) {
//        log.info("Received post: {}", record.value());
//
//        Post post = postService.getPost(Long.parseLong(record.value()));
//
//        Long authorId = post.getAuthor().getId();
//
//        Set<String> followers = redisService.getFollowers(authorId);
//
//        for (String follower : followers) {
//            redisService.updateCache(Long.valueOf(follower), post.getId());
//        }
//
//
//    }
//
//}
