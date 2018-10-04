package io.pivotal.pal.tracker.allocations;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {

    private ConcurrentMap<Long, ProjectInfo> cacheMap = new ConcurrentHashMap<Long, ProjectInfo>();
    private final RestOperations restOperations;
    private final String registrationServerEndpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo retVal = restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        cacheMap.put(projectId,retVal);
        return retVal;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        ProjectInfo retVal = cacheMap.get(projectId);
        return retVal;
    }

}

