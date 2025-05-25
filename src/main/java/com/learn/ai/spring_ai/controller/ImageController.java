package com.learn.ai.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    private final ChatModel chatModel;
    private final ImageModel imageModel;

    public ImageController(ChatModel chatModel, ImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    @GetMapping("/image-to-text")
    public String describeImage() {
        String describeTheImage = ChatClient.create(chatModel)
                .prompt()
                .user(userSpec -> userSpec
                        .text("Describe the image")
                        .media(MimeTypeUtils.IMAGE_JPEG, new ClassPathResource("images/horse-image.jpg")))
                .call()
                .content();
        return describeTheImage;
    }

    @GetMapping("/image/{prompt}")
    public String generateImage(@PathVariable String prompt) {
        ImageResponse imageResponse = imageModel.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions
                                .builder()
                                .N(1)
                                .width(1024)
                                .height(1024)
                                .quality("hd")
                                .build()));
        return imageResponse.getResult().getOutput().getUrl();
    }
}