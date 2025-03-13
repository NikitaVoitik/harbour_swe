package com.example.harbour_swer.services;

import com.example.harbour_swer.data.github.Activity;
import com.example.harbour_swer.data.github.Repository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GithubApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiBaseUrl = "https://api.github.com";

    @Value("${github.api.token:}")
    private String apiToken;

    public GithubApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Repository fetchRepositoryInfo(String repoName) {
        String url = apiBaseUrl + "/repos/" + repoName;

        try {
            ResponseEntity<String> response = makeApiRequest(url);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());

                Repository repository = new Repository();
                repository.setName(root.get("name").asText());
                repository.setLastChecked(LocalDateTime.now());

                return repository;
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error fetching repository info: " + e.getMessage());
            return null;
        }
    }

    public List<Activity> fetchCommits(String repoName, LocalDateTime since) {
        String url = apiBaseUrl + "/repos/" + repoName + "/commits";
        if (since != null) {
            url += "?since=" + since.format(DateTimeFormatter.ISO_DATE_TIME);
        }

        List<Activity> activities = new ArrayList<>();

        try {
            ResponseEntity<String> response = makeApiRequest(url);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode commitsArray = objectMapper.readTree(response.getBody());

                for (JsonNode commit : commitsArray) {
                    Activity activity = new Activity();
                    activity.setType(Activity.ActivityType.COMMIT);
                    activity.setActivityId(commit.get("sha").asText());

                    JsonNode commitData = commit.get("commit");
                    String message = commitData.get("message").asText();
                    activity.setTitle(message.split("\\n")[0]);
                    activity.setDescription(message);

                    if (commit.has("author") && !commit.get("author").isNull()) {
                        activity.setAuthor(commit.get("author").get("login").asText());
                    } else {
                        activity.setAuthor(commitData.get("author").get("name").asText());
                    }

                    String dateStr = commitData.get("author").get("date").asText();
                    activity.setCreatedAt(LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME));

                    activities.add(activity);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching commits: " + e.getMessage());
        }

        return activities;
    }

    public List<Activity> fetchPullRequests(String repoName, String state) {
        String url = apiBaseUrl + "/repos/" + repoName + "/pulls?state=" + state;
        List<Activity> activities = new ArrayList<>();

        try {
            ResponseEntity<String> response = makeApiRequest(url);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode prsArray = objectMapper.readTree(response.getBody());

                for (JsonNode pr : prsArray) {
                    Activity activity = new Activity();
                    activity.setType(Activity.ActivityType.PULL_REQUEST);
                    activity.setActivityId(pr.get("number").asText());
                    activity.setTitle(pr.get("title").asText());

                    if (pr.has("body") && !pr.get("body").isNull()) {
                        activity.setDescription(pr.get("body").asText());
                    }

                    activity.setAuthor(pr.get("user").get("login").asText());
                    activity.setCreatedAt(LocalDateTime.parse(pr.get("created_at").asText(),
                            DateTimeFormatter.ISO_DATE_TIME));
                    activity.setUpdatedAt(LocalDateTime.parse(pr.get("updated_at").asText(),
                            DateTimeFormatter.ISO_DATE_TIME));

                    activities.add(activity);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching pull requests: " + e.getMessage());
        }

        return activities;
    }

    public List<Activity> fetchIssues(String repoName, String state) {
        String url = apiBaseUrl + "/repos/" + repoName + "/issues?state=" + state;
        List<Activity> activities = new ArrayList<>();

        try {
            ResponseEntity<String> response = makeApiRequest(url);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode issuesArray = objectMapper.readTree(response.getBody());

                for (JsonNode issue : issuesArray) {
                    if (issue.has("pull_request")) {
                        continue;
                    }

                    Activity activity = new Activity();
                    activity.setType(Activity.ActivityType.ISSUE);
                    activity.setActivityId(issue.get("number").asText());
                    activity.setTitle(issue.get("title").asText());

                    if (issue.has("body") && !issue.get("body").isNull()) {
                        activity.setDescription(issue.get("body").asText());
                    }

                    activity.setAuthor(issue.get("user").get("login").asText());
                    activity.setCreatedAt(LocalDateTime.parse(issue.get("created_at").asText(),
                            DateTimeFormatter.ISO_DATE_TIME));
                    activity.setUpdatedAt(LocalDateTime.parse(issue.get("updated_at").asText(),
                            DateTimeFormatter.ISO_DATE_TIME));

                    activities.add(activity);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching issues: " + e.getMessage());
        }

        return activities;
    }

    public List<Activity> fetchReleases(String repoName) {
        String url = apiBaseUrl + "/repos/" + repoName + "/releases";
        List<Activity> activities = new ArrayList<>();

        try {
            ResponseEntity<String> response = makeApiRequest(url);
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode releasesArray = objectMapper.readTree(response.getBody());

                for (JsonNode release : releasesArray) {
                    Activity activity = new Activity();
                    activity.setType(Activity.ActivityType.RELEASE);
                    activity.setActivityId(release.get("id").asText());

                    if (release.has("name") && !release.get("name").isNull()) {
                        activity.setTitle(release.get("name").asText());
                    } else {
                        activity.setTitle(release.get("tag_name").asText());
                    }

                    if (release.has("body") && !release.get("body").isNull()) {
                        activity.setDescription(release.get("body").asText());
                    }

                    activity.setAuthor(release.get("author").get("login").asText());
                    activity.setCreatedAt(LocalDateTime.parse(release.get("created_at").asText(),
                            DateTimeFormatter.ISO_DATE_TIME));

                    if (release.has("published_at") && !release.get("published_at").isNull()) {
                        activity.setUpdatedAt(LocalDateTime.parse(release.get("published_at").asText(),
                                DateTimeFormatter.ISO_DATE_TIME));
                    }

                    activities.add(activity);
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching releases: " + e.getMessage());
        }

        return activities;
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
}
