package org.gbif.occurrence.annotation.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
  private int id;
  private int ruleId;
  @NotNull private String comment;
  private Date created;
  private String createdBy;
  private Date deleted;
  private String deletedBy;
}
