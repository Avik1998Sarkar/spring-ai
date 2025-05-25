package com.learn.ai.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    private final ChatClient chatClient;

    @Value("classpath:prompts/celeb-details/celeb-details.st")
    private Resource celeb_detailsPrompt;

    @Value("classpath:prompts/sports-details/system-prompt.st")
    private Resource sports_systemPrompt;

    @Value("classpath:prompts/sports-details/user-prompt.st")
    private Resource sports_userPrompt;

    public HelloController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping
    public String prompt(@RequestParam String prompt) {
        try {
            return chatClient
                    .prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (NonTransientAiException e) {
            return "You exceeded your current quota, please check your plan and billing details!";
        }
    }

    @GetMapping("/celeb")
    public String celebDetails(@RequestParam String name) {
        PromptTemplate promptTemplate = new PromptTemplate(celeb_detailsPrompt);
        Prompt prompt = promptTemplate.create(Map.of("name", name));
        try {
            return chatClient
                    .prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (NonTransientAiException e) {
            return "You exceeded your current quota, please check your plan and billing details!";
        }
    }

    @GetMapping("/sports")
    public String sportsDetails(@RequestParam String name) {
        PromptTemplate userPromptTemplate = new PromptTemplate(sports_userPrompt);
        Prompt userPrompt = userPromptTemplate.create(Map.of("name", name));
        UserMessage userMessage = new UserMessage(userPrompt.getContents());

        SystemMessage systemMessage = new SystemMessage(sports_systemPrompt);

        Prompt prompt = new Prompt(systemMessage, userMessage);

        try {
            return chatClient
                    .prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (NonTransientAiException e) {
            return "You exceeded your current quota, please check your plan and billing details!";
        }
    }
}
