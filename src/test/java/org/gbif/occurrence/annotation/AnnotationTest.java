package org.gbif.occurrence.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.gbif.occurrence.annotation.controller.ProjectController;
import org.gbif.occurrence.annotation.controller.RuleController;
import org.gbif.occurrence.annotation.model.Comment;
import org.gbif.occurrence.annotation.model.Project;
import org.gbif.occurrence.annotation.model.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Runs tests against the controllers bypassing HTTP. This requires Docker to be running for the
 * embedded postgres container.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ContextConfiguration(initializers = {AnnotationTest.ContextInitializer.class})
class AnnotationTest {
  @RegisterExtension protected static EmbeddedPostgres pgContainer = new EmbeddedPostgres();

  @Autowired private ProjectController projectController;
  @Autowired private RuleController ruleController;
  @Autowired private DataSource dataSource;

  @Test
  void testProjects() {
    assertTrue("Projects should be empty", projectController.list().isEmpty());
    projectController.create(
        Project.builder().name("Legumedata.org").description("A private group").build());

    assertEquals("Should contain the project we just created", 1, projectController.list().size());
    Project legume = projectController.get(1);
    legume.setMembers(ArrayUtils.add(legume.getMembers(), "TIM"));
    projectController.update(1, legume);
    assertEquals("Should have two members", 2, projectController.get(1).getMembers().length);
  }

  @Test
  void testRuleLifecycle() {
    Project p1 = projectController.create(Project.builder().name("1").description("1").build());
    Project p2 = projectController.create(Project.builder().name("2").description("2").build());

    ruleController.create(
        Rule.builder()
            .contextType(Rule.CONTEXT.TAXON)
            .contextKey("1")
            .geometry("geom1")
            .errorType(Rule.ERROR_TYPE.IDENTIFICATION)
            .build());

    ruleController.create(
        Rule.builder()
            .contextType(Rule.CONTEXT.DATASET)
            .contextKey("2")
            .geometry("geom2")
            .projectId(p1.getId())
            .errorType(Rule.ERROR_TYPE.IDENTIFICATION)
            .build());

    ruleController.create(
        Rule.builder()
            .contextType(Rule.CONTEXT.TAXON)
            .contextKey("1")
            .geometry("geom3")
            .projectId(p1.getId())
            .enrichmentType(Rule.ENRICHMENT_TYPE.INTRODUCED)
            .build());

    Rule deleteRule =
        ruleController.create(
            Rule.builder()
                .contextType(Rule.CONTEXT.TAXON)
                .contextKey("1")
                .geometry("geom4")
                .enrichmentType(Rule.ENRICHMENT_TYPE.INTRODUCED)
                .build());
    ruleController.delete(deleteRule.getId());

    assertEquals(
        "A deleted rule can still be accessed",
        "geom4",
        ruleController.get(deleteRule.getId()).getGeometry());
    assertNotNull(
        "A deleted rule should have a deleted timestamp", ruleController.get(deleteRule.getId()));
    assertEquals(
        "3 active rules were just created", 3, ruleController.list(null, null, null).size());
    assertEquals(
        "2 non-deleted rules were about taxon 1",
        2,
        ruleController.list("TAXON", "1", null).size());
    assertEquals(
        "2 non-deleted rules are in project 1",
        2,
        ruleController.list(null, null, p1.getId()).size());
    assertEquals(
        "0 non-deleted rules are in project 2",
        0,
        ruleController.list(null, null, p2.getId()).size());
  }

  @Test
  void testRuleSupportContest() {
    Rule r =
        ruleController.create(
            Rule.builder()
                .contextType(Rule.CONTEXT.TAXON)
                .contextKey("1")
                .geometry("geom1")
                .errorType(Rule.ERROR_TYPE.IDENTIFICATION)
                .build());

    ruleController.addComment(r.getId(), Comment.builder().comment("comment 1").build());
    Comment deleteComment =
        ruleController.addComment(r.getId(), Comment.builder().comment("comment 2").build());

    assertEquals("Two comments were added", 2, ruleController.listComment(r.getId()).size());
    ruleController.deleteComment(deleteComment.getId());
    assertEquals(
        "One non-deleted comment should remain", 1, ruleController.listComment(r.getId()).size());
  }

  @Test
  void testComments() {
    Rule r =
        ruleController.create(
            Rule.builder()
                .contextType(Rule.CONTEXT.TAXON)
                .contextKey("1")
                .geometry("geom1")
                .errorType(Rule.ERROR_TYPE.IDENTIFICATION)
                .build());

    // TODO Auth here
    ruleController.support(r.getId());
    ruleController.support(r.getId());
    assertEquals(
        "Duplicate supports should be ignored",
        1,
        ruleController.get(r.getId()).getSupportedBy().length);
    ruleController.removeSupport(r.getId());
    assertEquals(
        "Removing supports failing", 0, ruleController.get(r.getId()).getSupportedBy().length);
    ruleController.support(r.getId());
    ruleController.contest(r.getId()); // should remove the support
    ruleController.contest(r.getId());
    assertEquals(
        "Contesting should remove any previous support",
        0,
        ruleController.get(r.getId()).getSupportedBy().length);
    assertEquals(
        "Duplicate contesting should be ignored",
        1,
        ruleController.get(r.getId()).getContestedBy().length);
    ruleController.removeContest(r.getId());
    assertEquals(
        "Removing contest failing", 0, ruleController.get(r.getId()).getContestedBy().length);
    ruleController.contest(r.getId());
    ruleController.support(r.getId()); // should remove the contest
    assertEquals("Should have 1 support", 1, ruleController.get(r.getId()).getSupportedBy().length);
    assertEquals(
        "Supporting should remove any previous contest",
        0,
        ruleController.get(r.getId()).getContestedBy().length);
  }

  @BeforeEach
  void initialiseDatabase() throws SQLException {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement(); ) {
      statement.execute("DROP DATABASE IF EXISTS annotation");
      statement.execute("CREATE DATABASE annotation");
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("./schema.sql"));
    }
  }

  // Overwrite the main properties with the testcontainer ones
  static class ContextInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SneakyThrows
    public void initialize(ConfigurableApplicationContext context) {
      TestPropertyValues.of(
              "spring.datasource.url=" + pgContainer.getPostgres().getJdbcUrl(),
              "spring.datasource.username=" + pgContainer.getPostgres().getUsername(),
              "spring.datasource.password=" + pgContainer.getPostgres().getPassword())
          .applyTo(context.getEnvironment());
    }
  }
}
