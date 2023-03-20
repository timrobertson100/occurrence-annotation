package org.gbif.occurrence.annotation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gbif.occurrence.annotation.model.Project;
import java.util.List;

@Mapper
public interface ProjectMapper {

  @Select("SELECT * FROM project WHERE id = #{id}")
  Project get(@Param("id") Long id);

  @Select("SELECT * FROM project")
  List<Project> list();
}
