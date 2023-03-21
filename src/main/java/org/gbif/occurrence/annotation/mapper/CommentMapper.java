package org.gbif.occurrence.annotation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.gbif.occurrence.annotation.model.Comment;
import org.gbif.occurrence.annotation.model.Rule;
import java.util.List;

@Mapper
public interface CommentMapper {
  List<Comment> list(int ruleId);
  Comment get(int id);
  void create(Comment comment);
  void delete(int id, String username);
}
