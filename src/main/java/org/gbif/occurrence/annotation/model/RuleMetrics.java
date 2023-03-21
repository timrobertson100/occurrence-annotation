package org.gbif.occurrence.annotation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleMetrics {
  private String username;
  private int ruleCount;
  private int contextCount;
  private int supportCount;
  private int contestCount;
}
