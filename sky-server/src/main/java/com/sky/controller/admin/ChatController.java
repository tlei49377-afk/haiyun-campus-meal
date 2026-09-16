package com.sky.controller.admin;

import com.sky.aiservice.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private ConsultantService consultantService;

    /**
     * AI 助手对话接口（流式返回）
     *
     * @param memoryId 会话 id，用于区分不同用户/会话的上下文
     * @param memory   用户输入的问题
     */
    @RequestMapping(value = "/admin/chat", produces = "text/plain;charset=UTF-8")
    public Flux<String> chat(@RequestParam("memoryId") String memoryId,
                             @RequestParam("memory") String memory) {
        return consultantService.chat(memoryId, memory);
    }
}
