package com.haiyun.meal.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于 Redis 的 AI 会话记忆存储。
 */
@Repository
@Slf4j
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /** Redis 不可用时，至少保证当前进程内的 AI 对话可以继续。 */
    private final Map<Object, List<ChatMessage>> localMessages = new ConcurrentHashMap<>();

    public static final String MEMORY_KEY_PREFIX = "chat:memory:";
    private static final Duration TTL = Duration.ofDays(7);

    /**
     * 读取指定会话的历史消息。
     */
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        try {
            String json = redisTemplate.opsForValue().get(key(memoryId));
            if (json != null && !json.isEmpty()) {
                return ChatMessageDeserializer.messagesFromJson(json);
            }
        } catch (Exception e) {
            log.warn("AI 对话记忆读取 Redis 失败，使用进程内记忆：{}", e.getMessage());
        }
        List<ChatMessage> local = localMessages.get(memoryId);
        return local == null ? new ArrayList<>() : new ArrayList<>(local);
    }

    /**
     * 保存指定会话的历史消息。
     */
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        localMessages.put(memoryId, new ArrayList<>(list));
        try {
            String json = ChatMessageSerializer.messagesToJson(list);
            redisTemplate.opsForValue().set(key(memoryId), json, TTL);
        } catch (Exception e) {
            log.warn("AI 对话记忆写入 Redis 失败，已保留进程内记忆：{}", e.getMessage());
        }
    }

    /**
     * 删除指定会话的历史消息。
     */
    @Override
    public void deleteMessages(Object memoryId) {
        localMessages.remove(memoryId);
        try {
            redisTemplate.delete(key(memoryId));
        } catch (Exception e) {
            log.warn("AI 对话记忆删除 Redis 失败：{}", e.getMessage());
        }
    }

    /**
     * 生成带命名空间的 Redis 会话键。
     */
    private String key(Object memoryId) {
        return MEMORY_KEY_PREFIX + memoryId;
    }
}
