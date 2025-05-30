package org.mrshoffen.tasktracker.aggregator;

import org.mrshoffen.tasktracker.aggregator.client.CommentsClient;
import org.mrshoffen.tasktracker.aggregator.client.PermissionsClient;
import org.mrshoffen.tasktracker.aggregator.client.DeskClient;
import org.mrshoffen.tasktracker.aggregator.client.StickerClient;
import org.mrshoffen.tasktracker.aggregator.client.TaskClient;
import org.mrshoffen.tasktracker.aggregator.client.UserClient;
import org.mrshoffen.tasktracker.aggregator.client.WorkspaceClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AggregationBeans {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WorkspaceClient workspaceClient(WebClient.Builder webClientBuilder) {
        return new WorkspaceClient(webClientBuilder.baseUrl("http://workspace-manager-rs").build());
    }

    @Bean
    public DeskClient deskClient(WebClient.Builder webClientBuilder) {
        return new DeskClient(webClientBuilder.baseUrl("http://desk-manager-rs").build());
    }

    @Bean
    public TaskClient taskClient(WebClient.Builder webClientBuilder) {
        return new TaskClient(webClientBuilder.baseUrl("http://task-manager-rs").build());
    }

    @Bean
    public PermissionsClient permissionsClient(WebClient.Builder webClientBuilder) {
        return new PermissionsClient(webClientBuilder.baseUrl("http://user-permission-rs").build());
    }

    @Bean
    public UserClient userClient(WebClient.Builder webClientBuilder) {
        return new UserClient(webClientBuilder.baseUrl("http://user-profile-ws").build());
    }

    @Bean
    public StickerClient stickerClient(WebClient.Builder webClientBuilder) {
        return new StickerClient(webClientBuilder.baseUrl("http://sticker-manager-rs").build());
    }

    @Bean
    public CommentsClient commentsClient(WebClient.Builder webClientBuilder) {
        return new CommentsClient(webClientBuilder.baseUrl("http://task-comments-rs").build());
    }

}
