package com.pathfinder.config;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.internal.GraphDAO;
import com.pathfinder.internal.GraphDAOStub;
import com.pathfinder.internal.GraphTraversalServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathfinderApplicationContext {

    private GraphDAO graphDAO() {
        return new GraphDAOStub();
    }

    @Bean
    public GraphTraversalService graphTraversalService() {
        return new GraphTraversalServiceImpl(graphDAO());
    }
}