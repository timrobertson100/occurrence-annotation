#' Delete a project
#'
#' @param id the id of the project
#'
#' @return list of information about deleted project
#' @export
#'
#' @examples
delete_project = function(id) {
url <- paste0(gbifan_url("project/"),id)
gbifan_delete(url)  
}
