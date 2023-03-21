package org.gbif.occurrence.annotation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.gbif.occurrence.annotation.model.Project;

@Mapper
public interface ProjectMapper {
  List<Project> list();

  Project get(int id);

  void create(Project rule);

  void update(Project rule);

  void delete(int id, String username);
}
