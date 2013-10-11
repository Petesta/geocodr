puts "Checking Scala and SBT versions ..."
if `sbt sbt-version` =~ /0\.13\.0/ && `scala -version` =~ /2\.10\.3/
  puts "Updating Scala and SBT ..."
  `brew update && brew upgrade scala sbt`
end

puts "Checking for SASS ..."
unless `which sass` =~ /sass/
  puts "Installing Sass ..."
  `gem install sass`
end

puts "Setup Complete."



