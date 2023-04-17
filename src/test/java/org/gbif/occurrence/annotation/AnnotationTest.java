/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.occurrence.annotation;

import org.gbif.api.vocabulary.UserRole;
import org.gbif.occurrence.annotation.controller.AuthAdvice;
import org.gbif.occurrence.annotation.controller.ProjectController;
import org.gbif.occurrence.annotation.controller.RuleController;
import org.gbif.occurrence.annotation.model.Comment;
import org.gbif.occurrence.annotation.model.Project;
import org.gbif.occurrence.annotation.model.Rule;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
  @WithMockUser(
      username = "tim",
      authorities = {"USER"})
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
  @WithMockUser(
      username = "tim",
      authorities = {"USER"})
  void testRuleLifecycle() {
    Project p1 = projectController.create(Project.builder().name("1").description("1").build());
    Project p2 = projectController.create(Project.builder().name("2").description("2").build());

    ruleController.create(
        Rule.builder()
            .taxonKey(1)
            .geometry("geom1")
            .annotation(Rule.ANNOTATION_TYPE.NATIVE)
            .build());

    ruleController.create(
        Rule.builder()
            .datasetKey("2")
            .geometry("geom2")
            .projectId(p1.getId())
            .annotation(Rule.ANNOTATION_TYPE.NATIVE)
            .build());

    ruleController.create(
        Rule.builder()
            .taxonKey(1)
            .geometry("geom3")
            .projectId(p1.getId())
            .annotation(Rule.ANNOTATION_TYPE.VAGRANT)
            .build());

    Rule deleteRule =
        ruleController.create(
            Rule.builder()
                .taxonKey(1)
                .geometry("geom4")
                .annotation(Rule.ANNOTATION_TYPE.INTRODUCED)
                .build());
    ruleController.delete(deleteRule.getId());

    assertEquals(
        "A deleted rule can still be accessed",
        "geom4",
        ruleController.get(deleteRule.getId()).getGeometry());
    assertNotNull(
        "A deleted rule should have a deleted timestamp", ruleController.get(deleteRule.getId()));
    assertEquals(
        "3 active rules were just created", 3, ruleController.list(null, null, null, null).size());
    assertEquals(
        "2 non-deleted rules were about taxon 1",
        2,
        ruleController.list(1, null, null, null).size());
    assertEquals(
        "2 non-deleted rules are in project 1",
        2,
        ruleController.list(null, null, p1.getId(), null).size());
    assertEquals(
        "0 non-deleted rules are in project 2",
        0,
        ruleController.list(null, null, p2.getId(), null).size());
  }

  @Test
  @WithMockUser(
      username = "tim",
      authorities = {"USER"})
  void testFindByComment() {
    Rule r1 =
        ruleController.create(
            Rule.builder()
                .taxonKey(1)
                .geometry("geom1")
                .annotation(Rule.ANNOTATION_TYPE.NATIVE)
                .build());

    Rule r2 =
        ruleController.create(
            Rule.builder()
                .taxonKey(1)
                .geometry("geom1")
                .annotation(Rule.ANNOTATION_TYPE.NATIVE)
                .build());

    ruleController.addComment(
        r1.getId(), Comment.builder().comment("They do though don't they though").build());
    ruleController.addComment(r1.getId(), Comment.builder().comment("I divvina, pet").build());
    ruleController.addComment(
        r2.getId(), Comment.builder().comment("Are ye gannin yem already?").build());
    ruleController.addComment(r1.getId(), Comment.builder().comment("Am gannin doon toon").build());
    Comment c1 =
        ruleController.addComment(r1.getId(), Comment.builder().comment("Delete me").build());
    ruleController.deleteComment(c1.getId());

    assertEquals(
        "Gannin should match 2 rules", 2, ruleController.list(null, null, null, "gannin").size());
    assertEquals(
        "Deleted comments should be skipped",
        0,
        ruleController.list(null, null, null, "delete").size());
    assertEquals("Pet is only on one rule", 1, ruleController.list(null, null, null, "pet").size());
  }

  @Test
  @WithMockUser(
      username = "tim",
      authorities = {"USER"})
  void testRuleSupportContest() {
    Rule r =
        ruleController.create(
            Rule.builder()
                .taxonKey(1)
                .geometry("geom1")
                .annotation(Rule.ANNOTATION_TYPE.NATIVE)
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
  @WithMockUser(
      username = "tim",
      authorities = {"USER"})
  void testComments() {
    Rule r =
        ruleController.create(
            Rule.builder()
                .taxonKey(1)
                .geometry("geom1")
                .annotation(Rule.ANNOTATION_TYPE.NATIVE)
                .build());

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

  @Test
  void testCommentAuth() {
    setAuthenticatedUser("tim", UserRole.USER);
    Rule r =
        ruleController.create(
            Rule.builder()
                .taxonKey(1)
                .geometry("geom1")
                .annotation(Rule.ANNOTATION_TYPE.NATIVE)
                .build());
    Comment c =
        ruleController.addComment(r.getId(), Comment.builder().comment("comment 1").build());

    setAuthenticatedUser("thomas", UserRole.USER);
    try {
      ruleController.deleteComment(c.getId());
      fail("Should not be able to delete other users comments");
    } catch (AuthAdvice.NotAuthorisedException e) {
    }

    setAuthenticatedUser("marie", UserRole.REGISTRY_ADMIN);
    try {
      ruleController.deleteComment(c.getId());
    } catch (AuthAdvice.NotAuthorisedException e) {
      fail("Admin should be able to delete any user comments");
    }
  }

  private static void setAuthenticatedUser(String name, UserRole role) {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                name,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority(role.toString()))));
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

    @Override
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
