package org.gbif.occurrence.annotation.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import org.gbif.occurrence.annotation.mapper.ProjectMapper;
import org.gbif.occurrence.annotation.mapper.RuleMapper;
import org.gbif.occurrence.annotation.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/v1/occurrence/annotation/project")
public class ProjectController {
  @Autowired private ProjectMapper projectMapper;
  @Autowired private RuleMapper ruleMapper;

  @Operation(summary = "List all projects that are not deleted")
  @GetMapping
  public List<Project> list() {
    return projectMapper.list();
  }

  @Operation(summary = "Get a single project (may be deleted)")
  @GetMapping("/{id}")
  public Project get(@PathVariable(value = "id") int id) {
    return projectMapper.get(id);
  }

  @Operation(summary = "Create a new project")
  @PostMapping
  public Project create(@Valid @RequestBody Project project) {
    project.setCreatedBy("TODO:Auth");
    project.setMembers(new String[] {"TODO:Auth"});
    projectMapper.create(project); // mybatis sets id
    return projectMapper.get(project.getId());
  }

  @Operation(summary = "Update a project")
  @PutMapping("/{id}")
  public Project update(@PathVariable(value = "id") int id, @Valid @RequestBody Project project) {
    project.setModifiedBy("TODO:Auth");
    project.setId(id); // defensive
    projectMapper.update(project);
    return projectMapper.get(id);
  }

  @Operation(summary = "Logical delete a project and all associated rules")
  @DeleteMapping("/{id}")
  public void delete(@PathVariable(value = "id") int id) {
    String username = "TODO:Auth";
    projectMapper.delete(id, username);
    ruleMapper.deleteByProject(id, username);
  }
}
