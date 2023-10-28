package com.example.azureopenai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.*;
import com.azure.core.credential.AzureKeyCredential;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@Slf4j
public class AIServiceController {
    @Value("${AZUREOPENAIKEY}")
    String azureOpenaiKey;
    @Value("${ENDPOINT}")
    String endpoint;
    @Value("${DEPLOYMENTORMODELID}")
    String deploymentOrModelId;

    @GetMapping("/openai")
    public ResponseEntity<String> get(){
        return ResponseEntity.ok("Post to this URL with request param inputText and promptText");
    }
    @CrossOrigin
    @PostMapping("/openai")
    public ResponseEntity<ResponseData> getOpenAIResponse(@RequestBody RequestData requestData) {
        try {
            log.info(requestData.toString());
            log.info("""
                    End Point: %s,
                    Deployment Model ID: %s,
                    """.formatted(endpoint, deploymentOrModelId));

            OpenAIClient client = new OpenAIClientBuilder()
                    .endpoint(endpoint)
                    .credential(new AzureKeyCredential(azureOpenaiKey))
                    .buildClient();


            ChatCompletions chatCompletions = client.getChatCompletions(deploymentOrModelId, new ChatCompletionsOptions(List.of(
                    new ChatMessage(ChatRole.SYSTEM, requestData.promptText),
                    new ChatMessage(ChatRole.USER, requestData.inputText)
            )));

            StringBuilder strResponse = new StringBuilder();
            System.out.printf("Model ID=%s is created at %s.%n", chatCompletions.getId(), chatCompletions.getCreatedAt());
            for (ChatChoice choice : chatCompletions.getChoices()) {
                strResponse.append(choice.getMessage().getContent());
                System.out.printf("Index: %d, Text: %s.%n", choice.getIndex(), choice.getMessage().getContent());
            }

            CompletionsUsage usage = chatCompletions.getUsage();
            System.out.printf("Usage: number of prompt token is %d, "
                            + "number of completion token is %d, and number of total tokens in request and response is %d.%n",
                    usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
            return ResponseEntity.ok(new ResponseData(strResponse.toString(), usage.getPromptTokens(), usage.getCompletionTokens()));

        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), e.getMessage());
        }
    }
}


@Data
@ToString
class RequestData {
    String inputText;
    String promptText;
}

@Data
@AllArgsConstructor
class ResponseData {
    String generatedText;
    int promptTokens;
    int completionTokens;
}