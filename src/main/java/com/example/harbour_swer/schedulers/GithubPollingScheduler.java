package com.example.harbour_swer.schedulers;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.example.harbour_swer.data.github.Repository;
import com.example.harbour_swer.services.ActivityService;
import com.example.harbour_swer.services.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GithubPollingScheduler {
    private static final Logger logger = LoggerFactory.getLogger(GithubPollingScheduler.class);
    private static final int BATCH_SIZE = 100;

    private final RepositoryService repositoryService;
    private final ActivityService activityService;

    @Autowired
    public GithubPollingScheduler(RepositoryService repositoryService, ActivityService activityService) {
        this.repositoryService = repositoryService;
        this.activityService = activityService;
    }

    @Scheduled(fixedRateString = "${github.polling.interval:60000}")
    public synchronized void pollGitHubRepositories() {
        logger.info("Starting scheduled GitHub polling task");

        try {
            List<Repository> localList = new ArrayList<>(repositoryService.getRepositoriesToUpdate(BATCH_SIZE));

            for (int i = 0; i < localList.size(); i++) {
                try {
                    activityService.fetchAndSaveActivities(localList.get(i));
                    repositoryService.updateLastChecked(localList.get(i));
                } catch (Exception e) {
                    System.out.println(e);
                    logger.error("Error polling repository {}: {}", localList.get(i).getName(), e.getMessage());
                }
            }

            logger.info("Completed GitHub polling task");
        } catch (Exception e) {
            logger.error("Error in GitHub polling scheduler: {}", e.getMessage());
        }
    }
}