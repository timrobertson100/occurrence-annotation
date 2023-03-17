package org.gbif.occurrence.annotation.controller;

import org.gbif.occurrence.annotation.exception.ResourceNotFoundException;
import org.gbif.occurrence.annotation.model.Rule;
import org.gbif.occurrence.annotation.repository.RuleRepository;
import org.gbif.occurrence.annotation.repository.RuleRepository;
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
@RequestMapping("/v1/occurrence/annotation/rule")
public class RuleController {
  @Autowired
  private RuleRepository ruleRepository;

  @GetMapping("")
  public List<Rule> getAllRules() {
    return ruleRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Rule> getRuleById(@PathVariable(value = "id") Long ruleId)
      throws ResourceNotFoundException {
    Rule rule = ruleRepository.findById(ruleId)
        .orElseThrow(() -> new ResourceNotFoundException("Rule not found for this id :: " + ruleId));
    return ResponseEntity.ok().body(rule);
  }

  @PostMapping("")
  public Rule createRule(@Valid @RequestBody Rule rule) {
    System.out.println("Rule: " + rule.getErrorType());
    return ruleRepository.save(rule);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Rule> updateRule(@PathVariable(value = "id") Long ruleId,
                                                 @Valid @RequestBody Rule ruleDetails) throws ResourceNotFoundException {
    Rule rule = ruleRepository.findById(ruleId)
        .orElseThrow(() -> new ResourceNotFoundException("Rule not found for this id :: " + ruleId));

    rule.setComment(ruleDetails.getComment());
    rule.setContextType(ruleDetails.getContextType());
    rule.setContextKey(ruleDetails.getContextKey());
    rule.setEnrichmentType(ruleDetails.getEnrichmentType());
    rule.setErrorType(ruleDetails.getErrorType());
    rule.setGeometry(ruleDetails.getGeometry());

    // creator, created, project, etc. are not updated

    final Rule updatedRule = ruleRepository.save(rule);
    return ResponseEntity.ok(updatedRule);
  }

  @DeleteMapping("/{id}")
  public Map<String, Boolean> deleteRule(@PathVariable(value = "id") Long ruleId)
      throws ResourceNotFoundException {
    Rule rule = ruleRepository.findById(ruleId)
        .orElseThrow(() -> new ResourceNotFoundException("Rule not found for this id :: " + ruleId));

    ruleRepository.delete(rule);
    Map<String, Boolean> response = new HashMap<>();
    response.put("deleted", Boolean.TRUE);
    return response;
  }
}
