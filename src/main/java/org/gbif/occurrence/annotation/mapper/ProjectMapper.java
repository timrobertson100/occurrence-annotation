package org.gbif.occurrence.annotation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gbif.occurrence.annotation.model.Project;
import java.util.List;

@Mapper
public interface ProjectMapper {
  List<Project> list();

  Project get(int id);

  void create(Project rule);

  void update(Project rule);
  void delete(int id, String username);
}
