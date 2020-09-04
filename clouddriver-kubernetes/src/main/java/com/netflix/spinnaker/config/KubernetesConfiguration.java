/*
 * Copyright 2015 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.config;

import com.netflix.spectator.api.Registry;
import com.netflix.spinnaker.cats.module.CatsModule;
import com.netflix.spinnaker.clouddriver.kubernetes.caching.KubernetesProvider;
import com.netflix.spinnaker.clouddriver.kubernetes.caching.KubernetesProviderSynchronizable;
import com.netflix.spinnaker.clouddriver.kubernetes.caching.agent.KubernetesCachingAgentDispatcher;
import com.netflix.spinnaker.clouddriver.kubernetes.config.KubernetesConfigurationProperties;
import com.netflix.spinnaker.clouddriver.kubernetes.health.KubernetesHealthIndicator;
import com.netflix.spinnaker.clouddriver.kubernetes.security.KubernetesCredentials;
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsProvider;
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties
@EnableScheduling
@ConditionalOnProperty("kubernetes.enabled")
@ComponentScan({"com.netflix.spinnaker.clouddriver.kubernetes"})
public class KubernetesConfiguration {
  @Bean
  @RefreshScope
  @ConfigurationProperties("kubernetes")
  public KubernetesConfigurationProperties kubernetesConfigurationProperties() {
    return new KubernetesConfigurationProperties();
  }

  @Bean
  public KubernetesHealthIndicator kubernetesHealthIndicator(
      Registry registry, AccountCredentialsProvider accountCredentialsProvider) {
    return new KubernetesHealthIndicator(registry, accountCredentialsProvider);
  }

  @Bean
  public KubernetesProvider kubernetesProvider() {
    return new KubernetesProvider();
  }

  @Bean
  public KubernetesProviderSynchronizable kubernetesProviderSynchronizable(
      KubernetesProvider kubernetesProvider,
      AccountCredentialsRepository accountCredentialsRepository,
      KubernetesCachingAgentDispatcher kubernetesCachingAgentDispatcher,
      KubernetesConfigurationProperties kubernetesConfigurationProperties,
      KubernetesCredentials.Factory credentialFactory,
      CatsModule catsModule) {
    return new KubernetesProviderSynchronizable(
        kubernetesProvider,
        accountCredentialsRepository,
        kubernetesCachingAgentDispatcher,
        kubernetesConfigurationProperties,
        credentialFactory,
        catsModule);
  }
}
