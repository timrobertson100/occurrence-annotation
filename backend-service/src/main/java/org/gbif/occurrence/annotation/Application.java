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

import org.gbif.ws.remoteauth.RemoteAuthClient;
import org.gbif.ws.remoteauth.RemoteAuthWebSecurityConfigurer;
import org.gbif.ws.remoteauth.RestTemplateRemoteAuthClient;
import org.gbif.ws.server.filter.HttpServletRequestWrapperFilter;
import org.gbif.ws.server.filter.RequestHeaderParamUpdateFilter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@SpringBootApplication
@EnableConfigurationProperties
@Import({HttpServletRequestWrapperFilter.class, RequestHeaderParamUpdateFilter.class})
@ComponentScan(
    basePackages = {
      "org.gbif.occurrence.annotation",
      "org.gbif.occurrence.annotation.controller",
      "org.gbif.occurrence.annotation.model",
      "org.gbif.ws.remoteauth"
    },
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)})
@MapperScan("org.gbif.occurrence.annotation.mapper")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Configuration
  static class SpringSecurityConfig extends RemoteAuthWebSecurityConfigurer {
    public SpringSecurityConfig(
        ApplicationContext applicationContext, RemoteAuthClient remoteAuthClient) {
      super(applicationContext, remoteAuthClient);
    }
  }

  @Bean
  public RemoteAuthClient remoteAuthClient(
      RestTemplateBuilder builder, @Value("${registry.ws.url}") String gbifApiUrl) {
    return RestTemplateRemoteAuthClient.createInstance(builder, gbifApiUrl);
  }

  @Configuration
  @EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
  public static class RegistryMethodSecurityConfiguration
      extends GlobalMethodSecurityConfiguration {

    @Override
    protected AccessDecisionManager accessDecisionManager() {
      AffirmativeBased accessDecisionManager = (AffirmativeBased) super.accessDecisionManager();

      // Remove the ROLE_ prefix from RoleVoter for @Secured and hasRole checks on methods
      accessDecisionManager.getDecisionVoters().stream()
          .filter(RoleVoter.class::isInstance)
          .map(RoleVoter.class::cast)
          .forEach(it -> it.setRolePrefix(""));

      return accessDecisionManager;
    }
  }
}
