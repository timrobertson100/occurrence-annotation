package org.gbif.occurrence.annotation.repository;

import org.gbif.occurrence.annotation.model.Project;
import org.gbif.occurrence.annotation.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
}
