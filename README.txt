Work to be done:
* Documentation
* still need ignorelist support for dot, dir (do we?)
* Consolidate reports
* Support for relative paths (default)
* Currently we require BCEL to exist in the ANT_HOME/lib directory... a way around?
* If JUnit tests fail, fail on error but after code coverage is complete
* Unified Report View - Prettify the XML reports
* Determine minimum version for Java + ANT required
* Package-Archive Diagram
* Model missing dependencies (place holders by most general means)
* Focus - center view on a set of interesting classlistelements and only include boundary classes of
  uninteresting classlistelements
* Requires / Inject (for any parameter based constructor/method??)
* Why are there multiple arcs when composition exists?
* Comparing archives - equivalent? identify?
* Clickable diagrams (i.e. imagemaps with HTML), which can drill down
* Overlay Classpath on top of Classlist
* What about reflection?
* jaywalker report should be included in output (on fail)

* Incoporate Greg's work
* Dealing with classelement's files should be the same regardless of where it is (i.e. file in a directory vs file in an archive)
* Do we really need a separate FileFilter concept?
* JayWalkerDocs
* Archive dependencies report
* NPE Main single jar / file

* Other enhancements described in doc/index.html