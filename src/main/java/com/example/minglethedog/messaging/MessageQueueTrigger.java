//package com.example.minglethedog.messaging;
//
//import com.example.minglethedog.dto.PostDto;
//import com.example.minglethedog.repository.PostQueryRepository;
//import com.example.minglethedog.repository.PostRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MessageQueueTrigger {
//    private final RedisTemplate<String, String> redisTemplate;
//    private final PostRepository postRepository;
//    private final PostQueryRepository postQueryRepository;
//    private final KafkaTemplate<String, PostDto> kafkaTemplate;
//    private static final String MESSAGE_QUEUE = "post-queue";
//    private static final String TOPIC = "post-feed";
//
//    @Scheduled(fixedDelay = 5000) // 5초마다
//    public void processQueue() {
//        while (Boolean.TRUE.equals(redisTemplate.hasKey(MESSAGE_QUEUE))) {
//            String postId = redisTemplate.opsForList().rightPop(MESSAGE_QUEUE);
//            log.info("Post id: {}", postId);
//            if (postId != null) {
//                postQueryRepository.findByIdWithAuthorId(Long.parseLong(postId)).ifPresent(post -> {
//                    PostDto postDto = PostDto.of(post);
//                    kafkaTemplate.send(TOPIC, postDto);
//                    log.info("Post sent to Kafka : {}", postDto);
//                });
//            }
//        }
//    }
//}
