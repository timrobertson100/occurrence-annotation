package org.gbif.occurrence.annotation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class Project {
  private @Id @GeneratedValue(strategy = GenerationType.TABLE) Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "creator", nullable = false)
  private String creator;

  @Column(name="created")
  @CreationTimestamp
  private Date created;
}
