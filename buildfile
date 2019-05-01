require 'buildr/git_auto_version'
require 'buildr/gpg'
require 'buildr/top_level_generate_dir'

desc 'GWT Cache Filter'
define 'gwt-cache-filter' do
  project.group = 'org.realityforge.gwt.cache-filter'
  compile.options.source = '1.8'
  compile.options.target = '1.8'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  pom.add_apache_v2_license
  pom.add_github_project('realityforge/gwt-cache-filter')
  pom.add_developer('realityforge', 'Peter Donald')
  pom.add_developer('aliakhtar', 'Ali')
  pom.provided_dependencies.concat [:javax_servlet]

  compile.with :javax_servlet

  package(:jar)
  package(:sources)
  package(:javadoc)

  iml.add_jruby_facet
end
