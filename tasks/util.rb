WORKSPACE_DIR = File.expand_path(File.dirname(__FILE__) + '/..')

def in_dir(dir)
  current = Dir.pwd
  begin
    Dir.chdir(dir)
    yield
  ensure
    Dir.chdir(current)
  end
end

def derive_versions
  changelog = IO.read('CHANGELOG.md')
  ENV['PREVIOUS_PRODUCT_VERSION'] ||= changelog[/^### \[v(\d+\.\d+)\]/, 1] || '0.00'

  next_version = ENV['PRODUCT_VERSION']
  unless next_version
    version_parts = ENV['PREVIOUS_PRODUCT_VERSION'].split('.')
    next_version = "#{version_parts[0]}.#{sprintf('%d', version_parts[1].to_i + 1)}"
    ENV['PRODUCT_VERSION'] = next_version
  end
end
