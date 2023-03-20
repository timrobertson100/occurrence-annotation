package org.gbif.occurrence.annotation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Comment {

  private Integer id;
  private String comment;
  private Long ruleId;
  private String creator;
  private Date created;
}