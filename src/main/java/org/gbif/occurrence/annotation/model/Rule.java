package org.gbif.occurrence.annotation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "rule")
@Getter
@Setter
@NoArgsConstructor
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

  private @Id @GeneratedValue(strategy = GenerationType.TABLE) Long id;

  @Column(name="context_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private CONTEXT contextType;

  @Column(name="context_key", nullable = false)
  private String contextKey;

  @Column(name="geometry", nullable = false)
  private String geometry;

  @Column(name="error_type", nullable = true)
  @Enumerated(EnumType.STRING)
  @JsonProperty("errorType")
  private ERROR_TYPE errorType;

  @Column(name="enrichment_type", nullable = true)
  @Enumerated(EnumType.STRING)
  private ENRICHMENT_TYPE enrichmentType;

  @Column(name="comment", nullable = true)
  private String comment;

  // Optional project (ruleset) that this rule belongs to
  @ManyToOne(optional = true)
  @JoinColumn(name = "project_id", nullable = true)
  private Project project;

  @Column(name = "creator", nullable = false)
  private String creator;

  @Column(name="created")
  @CreationTimestamp
  private Date created;
}