#' Remove contest of a rule
#'
#' @param id id of rule to contest. 
#'
#' @return A `list` information about the rule contested. 
#' @export
#'
#' @examples
rm_contest_rule <- function(id) {
  url <- paste0(gbifan_url("rule/"),id,"/removeContest")
  gbifan_post(url,body=NULL)
}