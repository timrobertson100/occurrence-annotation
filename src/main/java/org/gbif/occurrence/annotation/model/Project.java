package org.gbif.occurrence.annotation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {
  private Integer id;
  @NotNull private String name;
  @NotNull private String description;
  private String[] members;
  private Date created;
  private String createdBy;
  private Date modified;
  private String modifiedBy;
  private Date deleted;
  private String deletedBy;
}