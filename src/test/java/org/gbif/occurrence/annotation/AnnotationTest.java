package org.gbif.occurrence.annotation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.gbif.occurrence.annotation.controller.ProjectController;
import org.gbif.occurrence.annotation.model.Project;
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
