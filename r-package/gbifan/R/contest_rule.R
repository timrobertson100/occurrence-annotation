#' Contest a rule
#'
#' @param id id of rule to contest. 
#'
#' @return A `list` information about the rule contested. 
#' @export
#'
#' @examples
#' \dontrun{
#' contest_rule(1)
#' }
contest_rule <- function(id) {
  url <- paste0(gbifan_url("rule/"),id,"/contest")
  gbifan_post(url,body=NULL)
}