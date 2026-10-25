package com.haiyun.meal.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于 Redis 的 AI 会话记忆存储。
 */
@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 给会话消息加命名空间：db10 里还住着 shop:status、用户端菜品缓存等 key，
    // 原来直接拿裸 memoryId 当 key 没有隔离，容易混。
    public static final String MEMORY_KEY_PREFIX = "chat:memory:";

    // 保留 7 天。要做「历史会话列表」就不能只留 1 小时，
    // 否则列表里能看到会话、点进去内容却已经过期。
    // 每次写入都会重设 TTL，所以活跃会话会自动续期。
    private static final Duration TTL = Duration.ofDays(7);

    /**
     * 读取指定会话的历史消息。
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = redisTemplate.opsForValue().get(key(memoryId));
        // 会话第一次被访问时 key 还不存在，这里必须兜底成空列表
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    /**
     * 覆盖保存指定会话的历史消息。
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        String json = ChatMessageSerializer.messagesToJson(list);
        redisTemplate.opsForValue().set(key(memoryId), json, TTL);
    }

    /**
     * 删除指定会话的历史消息。
     */
    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete(key(memoryId));
    }

    /**
     * 生成带命名空间的 Redis 会话键。
     */
    private String key(Object memoryId) {
        return MEMORY_KEY_PREFIX + memoryId;
    }
}
