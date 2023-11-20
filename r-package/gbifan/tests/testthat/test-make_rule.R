with_mock_dir("fixtures/make_rule", {
  test_that("test make rule works as expected", {
    # projectId and rulesetId can be left NULL
    r <- make_rule(taxonKey = 1, geometry = "", annotation="NATIVE")
    expect_type(r,"list")
    expect_equal(r$taxonKey, 1)
    
    expect_equal(r$datasetKey, NULL)
    expect_equal(r$deleted, NULL)
    
    # can adde a rule to a project and ruleset 
    p <- make_project(name="test project",description="A test project.")
    rs <- make_ruleset(projectId=p$id,name="test ruleset",description = "A test ruleset.")
    r1 <- make_rule(projectId=p$id,rulesetId=rs$id,taxonKey = 1, geometry = "", annotation="NATIVE")
    
    expect_type(r1,"list")
    expect_equal(r1$projectId,p$id)
    expect_equal(r1$rulesetId,rs$id)
    expect_equal(r$taxonKey, 1)
    
    expect_error(make_rule(taxonKey=NULL,geometry="WKT",annotation="NATIVE"),
                 "please supply taxonKey")
    expect_error(make_rule(taxonKey=1,geometry=NULL,annotation="NATIVE"),
                 "please supply geometry")
    expect_error(make_rule(taxonKey=1,geometry="WKT",annotation=NULL),
                 "please supply annotation")
  })
})