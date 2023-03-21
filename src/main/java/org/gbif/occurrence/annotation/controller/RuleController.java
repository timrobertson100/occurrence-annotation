package org.gbif.occurrence.annotation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import javax.validation.Valid;
import org.gbif.occurrence.annotation.mapper.CommentMapper;
import org.gbif.occurrence.annotation.mapper.RuleMapper;
import org.gbif.occurrence.annotation.model.Comment;
import org.gbif.occurrence.annotation.model.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/occurrence/annotation/rule")
public class RuleController {
  @Autowired private RuleMapper ruleMapper;
  @Autowired private CommentMapper commentMapper;

  @Operation(
      summary =
          "List all rules that are not deleted, optionally filtered by contextType, contextKey and projectId")
  @Parameter(name = "contextType", description = "Filters by context type")
  @Parameter(name = "contextKey", description = "Filters by context key")
  @Parameter(name = "projectId", description = "Filters by the given project")
  @GetMapping
  public List<Rule> list(
      @RequestParam(required = false) String contextType,
      @RequestParam(required = false) String contextKey,
      @RequestParam(required = false) Integer projectId) {
    return ruleMapper.list(contextType, contextKey, projectId);
  }

  @Operation(summary = "Get a single rule (may be deleted)")
  @GetMapping("/{id}")
  public Rule get(@PathVariable(value = "id") int id) {
    return ruleMapper.get(id);
  }

  @Operation(summary = "Create a new rule")
  @PostMapping
  public Rule create(@Valid @RequestBody Rule rule) {
    rule.setCreatedBy("TODO:Auth");
    ruleMapper.create(rule); // id set by mybatis
    return ruleMapper.get(rule.getId());
  }

  @Operation(summary = "Logical delete a rule")
  @DeleteMapping("/{id}")
  public Rule delete(@PathVariable(value = "id") int id) {
    String username = "TODO:Auth";
    ruleMapper.delete(id, username);
    return ruleMapper.get(id);
  }

  @Operation(summary = "Adds support for a rule (removes any existing contest entry for the user)")
  @PostMapping("/{id}/support")
  public Rule addSupport(@PathVariable(value = "id") int id) {
    String username = "TODO:Auth";
    ruleMapper.addSupport(id, username);
    ruleMapper.removeContest(id, username); // contest and support are mutually exclusive
    return ruleMapper.get(id);
  }

  @Operation(summary = "Removes support for a rule for the user")
  @PostMapping("/{id}/removeSupport")
  public Rule removeSupport(@PathVariable(value = "id") int id) {
    String username = "TODO:Auth";
    ruleMapper.removeSupport(id, username);
    return ruleMapper.get(id);
  }

  @Operation(summary = "Record that the user contests a rule (removes any support from the user)")
  @PostMapping("/{id}/contest")
  public Rule contest(@PathVariable(value = "id") int id) {
    String username = "TODO:Auth";
    ruleMapper.addContest(id, username);
    ruleMapper.removeSupport(id, username); // contest and support are mutually exclusive
    return ruleMapper.get(id);
  }

  @Operation(summary = "Removes the user contest list for the rule")
  @PostMapping("/{id}/removeContest")
  public Rule removeContest(@PathVariable(value = "id") int id) {
    String username = "TODO:Auth";
    ruleMapper.removeContest(id, username);
    return ruleMapper.get(id);
  }

  @Operation(summary = "Lists all non-deleted comments for a rule")
  @GetMapping("/{id}/comment")
  public List<Comment> listComment(@PathVariable(value = "id") int ruleId) {
    return commentMapper.list(ruleId);
  }

  @Operation(summary = "Adds a comment")
  @PostMapping("/{id}/comment")
  public Comment addComment(
      @PathVariable(value = "id") int id, @Valid @RequestBody Comment comment) {
    String username = "TODO:Auth";
    comment.setCreatedBy(username);
    comment.setRuleId(id);
    commentMapper.create(comment); // id set by mybatis
    return commentMapper.get(comment.getId());
  }

  @Operation(summary = "Logical delete a comment")
  @DeleteMapping("/{id}/comment/{commentId}")
  public void deleteComment(@PathVariable(value = "commentId") int commentId) {
    String username = "TODO:Auth";
    commentMapper.delete(commentId, username);
  }
}
