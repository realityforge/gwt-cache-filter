require 'buildr/git_auto_version'
require 'buildr/top_level_generate_dir'

desc 'GWT Cache Filter'
define 'gwt-cache-filter' do
  project.group = 'org.realityforge.gwt.cache-filter'
  compile.options.source = '1.6'
  compile.options.target = '1.6'
  compile.options.lint = 'all'

  project.version = ENV['PRODUCT_VERSION'] if ENV['PRODUCT_VERSION']

  compile.with :javax_servlet

  package(:jar)

  iml.add_jruby_facet
end
