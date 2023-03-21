package org.gbif.occurrence.annotation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.gbif.occurrence.annotation.model.Rule;

@Mapper
public interface RuleMapper {
  List<Rule> list();

  Rule get(int id);

  void create(Rule rule);

  void delete(int id, String username);

  void deleteByProject(int projectId, String username);

  void addSupport(int id, String username);

  void removeSupport(int id, String username);

  void addContest(int id, String username);

  void removeContest(int id, String username);
}
