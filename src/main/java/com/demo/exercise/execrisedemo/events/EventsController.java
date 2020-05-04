package com.demo.exercise.execrisedemo.events;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.exercise.execrisedemo.github.RepositoryEvent;
@Controller
public class EventsController {
	private final GithubClient githubClient;

	private final GithubProjectRepository repository;

	public EventsController(GithubClient githubClient, GithubProjectRepository repository) {
		this.githubClient = githubClient;
		this.repository = repository;
	}

	@GetMapping("/events/{projectName}")
	@ResponseBody
	public ResponseEntity<RepositoryEvent[]> fetchEvents(@PathVariable String projectName) {
		GithubProject project = this.repository.findByRepoName(projectName);
		/*if (project == null) {
			return ResponseEntity.notFound().build();
		}*/
		String orgName="spring-projects";
		String repoNmae=projectName;
		ResponseEntity<RepositoryEvent[]> response = this.githubClient
				.fetchEvents(orgName, repoNmae);
		return ResponseEntity.ok()
				.eTag(response.getHeaders().getETag())
				.body(response.getBody());
	}

	private List<GithubProject> getDummyList(){
		
		List<GithubProject> list=new ArrayList<>();
		GithubProject g1=new GithubProject();
		g1.setOrgName("spring-projects");
		g1.setRepoName("spring-boot");
		
		GithubProject g2=new GithubProject();
		g2.setOrgName("spring-io");
		g2.setRepoName("initializr");
		list.add(g1);
		list.add(g2);
		return list;
	}
	
	@GetMapping("/")
	public String dashboard(Model model) {
		
	List<GithubProject> list=getDummyList();
		List<DashboardEntry> entries = StreamSupport
				.stream(list.spliterator(), true)
				.map(p -> new DashboardEntry(p, githubClient.fetchEventsList(p.getOrgName(), p.getRepoName())))
				.collect(Collectors.toList());
		model.addAttribute("entries", entries);
		return "dashboard";
	}

	@GetMapping("/admin")
	public String admin(Model model) {
		model.addAttribute("projects", getDummyList());
		return "admin";
	}

}
