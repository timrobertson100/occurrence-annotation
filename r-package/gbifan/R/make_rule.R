#' Make a rule 
#'
#' @param taxonKey GBIF taxonKey for which rule applies to.  
#' @param geometry WKT text of the rule. 
#' @param annotation One of the controlled vocabulary. 
#' @param ... A named list of parameters (geometry and annotation required).  
#'
#' @return
#' A `list` of information about the rule. 
#' 
#' @details
#' Annotation needs to be part of controlled vocab. 
#'  
#' @export
#'
#' @examples
#' \dontrun{
#' make_rule(1,geometry="POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))", annotation="NATIVE")
#' }
make_rule = function(taxonKey = NULL, geometry=NULL,annotation=NULL,...) {
 
 args <- list(taxonKey = taxonKey, geometry=geometry, annotation=annotation, ...)
 
 if(is.null(args$taxonKey)) stop("please supply taxonKey")
 if(is.null(args$geometry)) stop("please supply geometry")
 if(is.null(args$annotation)) stop("please supply annotation")

 url <- gbifan_url("rule")
 body <- gbifan_body(args)
 gbifan_post(url,body)
 
}

