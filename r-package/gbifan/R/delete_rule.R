#' Delete a rule
#'
#' @param id the id of the rule
#'
#' @return list of information about deleted rule.
#' @export
#'
#' @examples
#' \dontrun{
#' delete_rule(1)
#' }
delete_rule <- function(id) {
  url <- paste0(gbifan_url("rule/"),id)
  gbifan_delete(url)  
}