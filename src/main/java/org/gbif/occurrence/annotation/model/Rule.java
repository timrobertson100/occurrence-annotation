package org.gbif.occurrence.annotation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

  // The type of "view" the user had when making the annotation
  public enum CONTEXT {
    TAXON, DATASET
  }

  // The type of error the user is reporting
  public enum ERROR_TYPE {
    IDENTIFICATION, LOCATION, OTHER
  }

  // Basic forms of enrichment annotation that may be applied
  public enum ENRICHMENT_TYPE {
    INTRODUCED, NATIVE, VAGRANT, CAPTIVITY
  }

  private Integer id;
  private CONTEXT contextType;
  private String contextKey;
  private String geometry;
  private ERROR_TYPE errorType;
  private ENRICHMENT_TYPE enrichmentType;
  private Integer projectId;
  private String creator;
  private Date created;
}