#' Support a rule
#'
#' @param id the id of the rule to updated.
#'
#' @return A `list` information about the rule supported. 
#' @export
#'
#' @examples
support_rule <- function(id=NULL) {
  url <- paste0(gbifan_url("rule/"),id,"/support")
  gbifan_post(url,body=NULL)
}