package org.gbif.occurrence.annotation.controller;

import org.gbif.occurrence.annotation.exception.ResourceNotFoundException;
import org.gbif.occurrence.annotation.mapper.ProjectMapper;
import org.gbif.occurrence.annotation.mapper.RuleMapper;
import org.gbif.occurrence.annotation.model.Project;
import org.gbif.occurrence.annotation.model.Rule;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/occurrence/annotation/rule")
public class RuleController {
  @Autowired private RuleMapper mapper;

  @GetMapping
  public List<Rule> list() {
    return mapper.list();
  }
  @GetMapping("/{id}")
  public Rule get(@PathVariable(value = "id") int id) {
    return mapper.get(id);
  }

  @PostMapping
  public Rule create(@Valid @RequestBody Rule rule) {
    mapper.create(rule); // id set by mybatis
    return mapper.get(rule.getId());
  }
}
