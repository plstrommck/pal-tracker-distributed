package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {

    private ConcurrentMap<Long, ProjectInfo> cacheMap = new ConcurrentHashMap<>();
    private final RestOperations restOperations;
    private final String endpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo retVal = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        cacheMap.put(projectId,retVal);
        return retVal;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        ProjectInfo retVal = cacheMap.get(projectId);
        return retVal;
    }
}
