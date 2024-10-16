package com.example.chatserverchat.domain.facade;

import com.example.chatserverchat.domain.dto.GraphqlDTO;
import com.example.chatserverchat.domain.service.ChatGraphqlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLockFacade {

    private final RedissonClient redissonClient;
    private final ChatGraphqlService chatGraphqlService;

    public GraphqlDTO incrementPersonnel(String id) {

        RLock lock = redissonClient.getLock(id);

        try {
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.error("락 획득 실패");
                return null;
            }

            log.info("락 획득 성공");
            return chatGraphqlService.incrementPersonnel(id);
        } catch (InterruptedException e) {
            log.error("락 획득 예외 발생: {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            log.info("락 잠금 해제");
            lock.unlock();
        }
    }
}