package com.example.minglethedog.messaging;

import com.example.minglethedog.dto.PostDto;
import com.example.minglethedog.service.FollowService;
import com.example.minglethedog.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostConsumer {
    private final RedisService redisService;
    private final RedisTemplate<String, Object> redisTemplate;


    @KafkaListener(topics = "post-feed", groupId = "newsfeed-group") // 어떤메시지를 소비할지 어떤 그룹에서 메시지 처리할지 결정하는 설정
    public void listen(PostDto postDto) {
        log.info("Received post: {}", postDto);

        //게시글 작성자의 팔로워 목록조회
        Set<String> followers = redisService.getFollowers(postDto.getAuthorId());

        for(String follower : followers) {
            String newsfeedKey = "newsfeed:"+follower;
            redisTemplate.opsForList().leftPush(newsfeedKey, postDto);
            redisTemplate.opsForList().trim(newsfeedKey, 0,9);
        }



    }

}
