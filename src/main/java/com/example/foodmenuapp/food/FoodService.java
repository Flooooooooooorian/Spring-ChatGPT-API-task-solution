package com.example.foodmenuapp.food;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class FoodService {

    private final FoodRepository foodRepository;
    private final WebClient webClient;

    public FoodService(FoodRepository foodRepository,
                       @Value("${app.openai-api-key}") String openaiApiKey,
                       @Value("${app.openai-api-organization}") String openaiApiOrganization) {

        this.foodRepository = foodRepository;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("OpenAI-Organization", openaiApiOrganization)
                .build();
    }

    public Iterable<Food> getFoods() {
        return foodRepository.findAll();
    }

    public Food getFood(String id) {
        return foodRepository.findById(id).orElseThrow();
    }

    public Food createFood(Food food) {
        if (food.category() == null) {
            food = food.withCategory(calculateCategory(food.name()));
        }
        return foodRepository.save(food);
    }

    private String calculateCategory(String name) {
        return webClient.post()
                .bodyValue(new ChatGPTRequest("categories: [vegan, vegetarisch, Hausmannskost, Asiatisch, Nudel, Fisch, Spanisch, Deutsch, Mediterran]; output: best fitting category as string; outputformat: ${category}; input: " + name))
                .retrieve()
                .toEntity(ChatGPTResponse.class)
                .block()
                .getBody()
                .text();
    }

    public Food updateFood(Food food) {
        return foodRepository.save(food);
    }

    public void deleteFood(String id) {
        foodRepository.deleteById(id);
    }
}
