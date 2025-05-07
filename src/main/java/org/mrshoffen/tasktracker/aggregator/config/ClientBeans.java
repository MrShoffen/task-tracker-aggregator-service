package org.mrshoffen.tasktracker.aggregator.config;

import org.mrshoffen.tasktracker.aggregator.client.DeskClient;
import org.mrshoffen.tasktracker.aggregator.client.TaskClient;
import org.mrshoffen.tasktracker.aggregator.client.WorkspaceClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WorkspaceClient workspaceClient(WebClient.Builder webClientBuilder) {
        return new WorkspaceClient(webClientBuilder.baseUrl("http://workspace-manager-rs") .build());
    }

    @Bean
    public DeskClient deskClient(WebClient.Builder webClientBuilder) {
        return new DeskClient(webClientBuilder.baseUrl("http://desk-manager-rs") .build());
    }

    @Bean
    public TaskClient taskClient(WebClient.Builder webClientBuilder) {
        return new TaskClient(webClientBuilder.baseUrl("http://task-manager-rs") .build());
    }

}
