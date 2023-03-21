package org.gbif.occurrence.annotation;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.gbif.occurrence.annotation.controller.ProjectController;
import org.gbif.occurrence.annotation.model.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ContextConfiguration(initializers = {AnnotationTest.ContexInitializer.class})
class AnnotationTest {
  @RegisterExtension protected static EmbeddedPostgres pgContainer = new EmbeddedPostgres();

  @Autowired private ProjectController projectController;

  @Test
  void testProjects() {
    List<Project> projects = projectController.list();
    assertTrue("Projects should be empty", projects.isEmpty());
  }

  static class ContexInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
              "spring.datasource.url=" + pgContainer.getPostgres().getJdbcUrl(),
              "spring.datasource.username=" + pgContainer.getPostgres().getUsername(),
              "spring.datasource.password=" + pgContainer.getPostgres().getPassword())
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
