package ru.unus.sonus.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.unus.sonus.service.ClusterManagerService;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class AliveDataNodesChecker {

    @Value("${scheduler.boundary}")
    private int boundary;

    private final ClusterManagerService clusterManagerService;

    @Scheduled(fixedDelayString = "${scheduler.interval}")
    public void checkDataNodes() {
        log.info("Checking datanodes...");
        clusterManagerService.deleteDeathDataNodes(boundary);
    }
}
