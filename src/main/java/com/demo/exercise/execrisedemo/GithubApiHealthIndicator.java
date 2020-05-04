package com.demo.exercise.execrisedemo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.demo.exercise.execrisedemo.events.GithubClient;
import com.demo.exercise.execrisedemo.github.RepositoryEvent;

@Component
public class GithubApiHealthIndicator implements HealthIndicator{

	private final GithubClient githubClient;

	public GithubApiHealthIndicator(GithubClient githubClient) {
		this.githubClient = githubClient;
	}

	@Override
	public Health health() {
		try {
			ResponseEntity<RepositoryEvent[]> responseEntity = githubClient
					.fetchEvents("spring-projects", "spring-boot");
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				return Health.up()
						.withDetail("X-RateLimit-Remaining", responseEntity.getHeaders().getFirst("X-RateLimit-Remaining"))
						.build();
			}
			else {
				return Health.down().withDetail("status", responseEntity.getStatusCode()).build();
			}
		}
		catch (Exception e) {
			return Health.down(e).build();
		}
	}
}
