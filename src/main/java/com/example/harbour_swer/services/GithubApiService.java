package com.example.harbour_swer.services;

import com.example.harbour_swer.data.github.Activity;
import com.example.harbour_swer.data.github.Repository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class GithubApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiBaseUrl = "https://api.github.com";
    private static final Logger logger = LoggerFactory.getLogger(GithubApiService.class);

    @Value("${github.api.token:}")
    private String apiToken;

    public GithubApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Repository fetchRepositoryInfo(String repoName) {
        String url = apiBaseUrl + "/repos/" + repoName;
        try {
            return executeApiRequest(url, jsonNode -> {
                Repository repository = new Repository();
                repository.setName(jsonNode.get("full_name").asText());
                repository.setLastChecked(LocalDateTime.now());
                return repository;
            });
        } catch (Exception e) {
            logger.error("Error fetching repository info: " + e.getMessage());
            return null;
        }
    }

    public List<Activity> fetchCommits(String repoName, LocalDateTime since) {
        String url = apiBaseUrl + "/repos/" + repoName + "/commits";
        if (since != null) {
            url += "?since=" + since.format(DateTimeFormatter.ISO_DATE_TIME);
        }
        System.out.println("Fetching commits for " + repoName);
        System.out.println(url);

        return fetchActivities(url, node -> {
            Activity activity = new Activity();
            activity.setType(Activity.ActivityType.COMMIT);
            activity.setActivityId(node.get("sha").asText());

            JsonNode commitData = node.get("commit");
            System.out.println(10);
            System.out.println(commitData);
            String message = commitData.get("message").asText();
            activity.setTitle(message.split("\\n")[0]);
            activity.setDescription(message);

            if (node.has("author") && !node.get("author").isNull()) {
                activity.setAuthor(node.get("author").get("login").asText());
            } else {
                activity.setAuthor(commitData.get("author").get("name").asText());
            }

            String dateStr = commitData.get("author").get("date").asText();
            activity.setCreatedAt(LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME));

            return activity;
        });
    }

    public List<Activity> fetchPullRequests(String repoName, String state) {
        String url = apiBaseUrl + "/repos/" + repoName + "/pulls?state=" + state;
        return fetchActivities(url, node -> createActivityFromIssueOrPR(node, Activity.ActivityType.PULL_REQUEST));
    }

    public List<Activity> fetchIssues(String repoName, String state) {
        String url = apiBaseUrl + "/repos/" + repoName + "/issues?state=" + state;
        return fetchActivities(url, node -> {
            if (node.has("pull_request")) {
                return null;
            }
            return createActivityFromIssueOrPR(node, Activity.ActivityType.ISSUE);
        });
    }

    public List<Activity> fetchReleases(String repoName) {
        String url = apiBaseUrl + "/repos/" + repoName + "/releases";
        return fetchActivities(url, node -> {
            Activity activity = new Activity();
            activity.setType(Activity.ActivityType.RELEASE);
            activity.setActivityId(node.get("id").asText());

            if (node.has("name") && !node.get("name").isNull()) {
                activity.setTitle(node.get("name").asText());
            } else {
                activity.setTitle(node.get("tag_name").asText());
            }

            if (node.has("body") && !node.get("body").isNull()) {
                activity.setDescription(node.get("body").asText());
            }

            activity.setAuthor(node.get("author").get("login").asText());
            activity.setCreatedAt(parseDateTime(node.get("created_at").asText()));

            if (node.has("published_at") && !node.get("published_at").isNull()) {
                activity.setUpdatedAt(parseDateTime(node.get("published_at").asText()));
            }

            return activity;
        });
    }

    private Activity createActivityFromIssueOrPR(JsonNode node, Activity.ActivityType type) {
        Activity activity = new Activity();
        activity.setType(type);
        activity.setActivityId(node.get("number").asText());
        activity.setTitle(node.get("title").asText());

        if (node.has("body") && !node.get("body").isNull()) {
            activity.setDescription(node.get("body").asText());
        }

        activity.setAuthor(node.get("user").get("login").asText());
        activity.setCreatedAt(parseDateTime(node.get("created_at").asText()));
        activity.setUpdatedAt(parseDateTime(node.get("updated_at").asText()));

        return activity;
    }

    private <T> List<T> fetchActivities(String url, Function<JsonNode, T> mapper) {
        List<T> results = new ArrayList<>();
        try {
            ResponseEntity<String> response = makeApiRequest(url);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode itemsArray = objectMapper.readTree(response.getBody());
                for (JsonNode item : itemsArray) {
                    T result = mapper.apply(item);
                    if (result != null) {
                        results.add(result);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching data from " + url + ": " + e.getMessage());
        }
        return results;
    }

    private <T> T executeApiRequest(String url, Function<JsonNode, T> mapper) {
        try {
            ResponseEntity<String> response = makeApiRequest(url);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return mapper.apply(root);
            }
        } catch (Exception e) {
            logger.error("Error executing API request: " + e.getMessage());
        }
        return null;
    }

    private ResponseEntity<String> makeApiRequest(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");

        if (apiToken != null && !apiToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + apiToken);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME);
    }
}