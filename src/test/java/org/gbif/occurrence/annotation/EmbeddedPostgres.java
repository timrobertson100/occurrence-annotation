package org.gbif.occurrence.annotation;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

class EmbeddedPostgres implements BeforeAllCallback, AfterAllCallback {
  private static final PostgreSQLContainer postgres =
      new PostgreSQLContainer("postgres:15.2").withDatabaseName("annotations");

  @Override
  public void beforeAll(ExtensionContext context) {
    postgres.start();
  }

  @Override
  public void afterAll(ExtensionContext context) {
    postgres.stop();
  }

  public static PostgreSQLContainer getPostgres() {
    return postgres;
  }
}
