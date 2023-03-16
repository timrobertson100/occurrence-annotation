package org.gbif.occurrence.annotation.controller;

import org.gbif.occurrence.annotation.exception.ResourceNotFoundException;
import org.gbif.occurrence.annotation.model.Project;
import org.gbif.occurrence.annotation.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/occurrence/annotation/project")
public class ProjectController {
  @Autowired
  private ProjectRepository projectRepository;

  @GetMapping("")
  public List<Project> getAllProjects() {
    return projectRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Project> getProjectById(@PathVariable(value = "id") Long projectId)
      throws ResourceNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id :: " + projectId));
    return ResponseEntity.ok().body(project);
  }

  @PostMapping("")
  public Project createProject(@Valid @RequestBody Project project) {
    return projectRepository.save(project);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Project> updateProject(@PathVariable(value = "id") Long projectId,
                                                 @Valid @RequestBody Project projectDetails) throws ResourceNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id :: " + projectId));

    project.setName(projectDetails.getName());
    project.setDescription(projectDetails.getDescription());
    // creator and created are not updatable

    final Project updatedProject = projectRepository.save(project);
    return ResponseEntity.ok(updatedProject);
  }

  @DeleteMapping("/{id}")
  public Map<String, Boolean> deleteProject(@PathVariable(value = "id") Long projectId)
      throws ResourceNotFoundException {
    Project project = projectRepository.findById(projectId)
        .orElseThrow(() -> new ResourceNotFoundException("Project not found for this id :: " + projectId));

    projectRepository.delete(project);
    Map<String, Boolean> response = new HashMap<>();
    response.put("deleted", Boolean.TRUE);
    return response;
  }
}
