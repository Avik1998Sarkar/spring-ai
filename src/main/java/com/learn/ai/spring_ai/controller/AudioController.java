package com.learn.ai.spring_ai.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class AudioController {

    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;
    private final OpenAiAudioSpeechModel audioSpeechModel;

    public AudioController(OpenAiAudioTranscriptionModel audioTranscriptionModel, OpenAiAudioSpeechModel audioSpeechModel) {
        this.audioTranscriptionModel = audioTranscriptionModel;
        this.audioSpeechModel = audioSpeechModel;
    }

    @GetMapping("/transcribe-audio")
    private String transcribeAudio(String audioFilePath) {
        OpenAiAudioTranscriptionOptions options = OpenAiAudioTranscriptionOptions
                .builder()
                .temperature(0.5f)
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .language("en")
                .build();
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(
                new ClassPathResource("audios/Wake Up To Reality! - Madara's Speech.mp3"),
                options
        );
        return audioTranscriptionModel.call(prompt).getResult().getOutput();
    }

    @GetMapping("/generate-audio/{prompt}")
    public ResponseEntity<Resource> generateAudio(@PathVariable String prompt) throws IOException {
        OpenAiAudioSpeechOptions options = OpenAiAudioSpeechOptions
                .builder()
                .model(OpenAiAudioApi.TtsModel.TTS_1_HD.getValue())
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ONYX.getValue())
                .speed(1.0f)
                .build();
        SpeechPrompt prompt1 = new SpeechPrompt(prompt, options);
        byte[] speechOutputByteArray = audioSpeechModel.call(prompt1)
                .getResult()
                .getOutput();

        // Save the audio output to a file
        try (FileOutputStream fos = new FileOutputStream("C:\\Users\\avik\\Codes\\2\\spring-ai\\src\\main\\resources\\audios\\output\\output.mp3")) {
            fos.write(speechOutputByteArray);
        }

        // If we hit the URL in a browser, it will download the audio file
        ByteArrayResource byteArrayResource = new ByteArrayResource(speechOutputByteArray);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .header("Content-Disposition",
                        ContentDisposition.attachment()
                        .filename("output.mp3")
                        .build()
                        .toString())
                .body(byteArrayResource);
    }
}
