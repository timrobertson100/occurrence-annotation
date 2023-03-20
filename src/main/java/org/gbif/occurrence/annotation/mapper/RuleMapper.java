package org.gbif.occurrence.annotation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gbif.occurrence.annotation.model.Project;
import org.gbif.occurrence.annotation.model.Rule;
import java.util.List;

@Mapper
public interface RuleMapper {
  List<Rule> list();

  Rule get(int id);

  void create(Rule rule);
}
