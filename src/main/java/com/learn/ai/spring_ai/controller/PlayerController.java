package com.learn.ai.spring_ai.controller;

import com.learn.ai.spring_ai.model.Achievements;
import com.learn.ai.spring_ai.model.Player;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@RestController
public class PlayerController {

    private final ChatClient chatClient;

    @Value("classpath:prompts/player-sports-details/player-sports-prompt.st")
    private Resource playerSportsPrompt;

    @Value("classpath:prompts/player-sports-details/player-achievement-prompt.st")
    private Resource playerAchievementPrompt;

    public PlayerController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/player")
    public List<Player> getPlayerSportsDetails(@RequestParam String name) {
        BeanOutputConverter<List<Player>> converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<Player>>() {
                }
        );
        PromptTemplate promptTemplate = new PromptTemplate(playerSportsPrompt);
        Prompt prompt = promptTemplate.create(Map.of("sports", name, "format", converter.getFormat()));
        Generation result = chatClient
                .prompt(prompt)
                .call()
                .chatResponse()
                .getResult();
        List<Player> playerList = converter.convert(result.getOutput().getText());
        return playerList;
    }

    @GetMapping("/achievement/player")
    public List<Achievements> getAchievements(@RequestParam String name) {
        PromptTemplate promptTemplate = new PromptTemplate(playerAchievementPrompt);
        Prompt prompt = promptTemplate.create(Map.of("name", name));
        List<Achievements> result = chatClient
                .prompt(prompt)
                .call()
                .entity(new ParameterizedTypeReference<List<Achievements>>() {
                });
        return result;
    }
}
