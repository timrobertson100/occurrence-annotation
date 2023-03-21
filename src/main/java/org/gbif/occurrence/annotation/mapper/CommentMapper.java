package org.gbif.occurrence.annotation.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.gbif.occurrence.annotation.model.Comment;

@Mapper
public interface CommentMapper {
  List<Comment> list(int ruleId);

  Comment get(int id);

  void create(Comment comment);

  void delete(int id, String username);
}
