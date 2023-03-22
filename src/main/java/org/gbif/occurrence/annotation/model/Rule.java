package org.gbif.occurrence.annotation.model;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule {

  // The type of "view" the user had when making the annotation
  public enum CONTEXT {
    TAXON,
    DATASET
  }

  // The type of error the user is reporting
  public enum ERROR_TYPE {
    IDENTIFICATION,
    LOCATION,
    OTHER
  }

  // Basic forms of enrichment annotation that may be applied
  public enum ENRICHMENT_TYPE {
    INTRODUCED,
    NATIVE,
    VAGRANT,
    CAPTIVITY
  }

  private Integer id;
  @NotNull private CONTEXT contextType;
  @NotNull private String contextKey;
  @NotNull private String geometry;
  private ERROR_TYPE errorType;
  private ENRICHMENT_TYPE enrichmentType;
  private Integer projectId;
  private String[] supportedBy;
  private String[] contestedBy;
  private Date created;
  private String createdBy;
  private Date deleted;
  private String deletedBy;
}
