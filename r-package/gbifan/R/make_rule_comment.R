#' Make a comment on rule. 
#'
#' @param ruleId id of the rule you want to leave a comment on. 
#' @param comment comment. 
#'
#' @return
#' @export
#'
#' @examples
make_rule_comment = function(id=NULL,comment=NULL) {
  url <- paste0(gbifan_url("rule/"),id,"/comment")
  body <- gbifan_body(comment=comment)
  gbifan_post(url,body) 
  }
