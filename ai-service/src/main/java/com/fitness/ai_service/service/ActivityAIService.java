package com.fitness.ai_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.ai_service.model.Activity;
import com.fitness.ai_service.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptFromActivity(activity);
        String aiResponse = geminiService.getAnswerFromGemini(prompt);
        log.info("Response from AI: {}", aiResponse);
        return processAIResponse(activity, aiResponse);
    }

    private Recommendation processAIResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();

            log.info("PARSED RESPONSE FROM AI: {}", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");

            List<Map<String,String>> improvements = extractImprovements(analysisJson.path("improvements"));
            List<Map<String,String>> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            String safety = extractSafetyGuidelines(analysisJson.path("safety"));
            log.info("------------------RECOMMENDATION: {}", fullAnalysis.toString().trim());

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();
        }catch(Exception e){
            log.error("Error processing AI response: {}", e.getMessage());
            return createDefaultRecommendation(activity);
        }

    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList(Collections.singletonMap("improvement","Continue with your current routine")))
                .suggestions(Collections.singletonList(Collections.singletonMap("suggestion","Consider consulting a fitness professional")))
                .safety(String.join(", ", List.of(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                )))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new LinkedList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(node -> safety.add(node.asText()));
        }
        String delimitedString = String.join(", ", safety);
        return safety.isEmpty() ?
                "Follow general safety guidelines during workouts" :
                delimitedString;
    }

    private List<Map<String,String>> extractSuggestions(JsonNode suggestionsNode) {
        List<Map<String,String>> suggestions = new LinkedList<Map<String,String>>();
        Map<String,String> suggestionMap = new HashMap<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestionMap.put(workout, description);
            });
            suggestions.add(suggestionMap);
        }
        return suggestions.isEmpty() ?
                Collections.singletonList(Collections.singletonMap("suggestion1", "No specific suggestions available")) :
                suggestions;
    }

    private List<Map<String,String>> extractImprovements(JsonNode improvementsNode) {
        List<Map<String,String>> improvements = new LinkedList<Map<String,String>>();
        Map<String,String> improvementMap = new HashMap<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(improvement -> {
                String area = improvement.path("area").asText();
                String detail = improvement.path("recommendation").asText();
                improvementMap.put(area, detail);
            });
            improvements.add(improvementMap);
        }
        return improvements.isEmpty() ?
                Collections.singletonList(Collections.singletonMap("improvement1", "No specific improvements available")) :
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptFromActivity(Activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }
}
