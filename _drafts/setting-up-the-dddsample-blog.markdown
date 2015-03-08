---
layout: post
title:  "Setting up the "
date:   2015-03-08 22:37:00
categories: blog meta
---
So, when we decided to resurrect the DDD Sample app, we also decided to create a blog to go with it. We expect to learn a bunch of new stuff during this exercise, and thought it would be fun to share some of these with you as we
went along.

We decide early on to go with GitHub pages for the blog. We wanted something that required minimal effort to set up and use. We considered using a separate blog host, such as Blogger, but using GitHub Pages just seemed most lean; requiring minimal tooling, allowing us to keep the blog content in the same repository as the code, and host it directly from GitHub.

GitHub pages comes with a static page generator called Jekyll. Per default it takes simple markdown formatted posts and turns them into nicely formatted HTML pages. Setting this up to work with your GitHub project is quite simple, here's how we did it:

##1. Create a place to store you blog entries
We write blog entries as simple markdown files, and they need to live somewhere. If you want to create a blog to go with a particular project, like we wanted, the files are stored in the same repository as you source code, but in a branch called `gh-pages`. So first, set up this branch:

1. Clone your repo: `git clone github.com/<user>/<project-repository>.git`
1. In your newly cloned repo, create the gh-pages branch: `git checkout --orphan gh-pages`
1. Remove the code stuff from here, so you can replace it with your new blog stuff: `git rm -rf .`

More details on the above steps can be found in the [GitHub docs](https://help.github.com/articles/creating-project-pages-manually/).

Now we're ready to add some new content here and push it to GitHub to get it published. We'll use Jekyll for that, so setting up Jekyll is next on our list!
 
##Set up Jekyll on you local computer
While content you push to your gh-pages branch in your GitHub repo will get automatically processed and published, it's nice to be able to preview you work before you push it. Installing Jekyll locally on your computer will let you do just that.

###Set up Ruby
Jekyll requires Ruby. I'm sure there are all sorts of more or less complicated ways of doing this, but if you're on a recent version of OS X, as I happen to be, you're already good to go. Verify your Ruby installation by doing `ruby -v` on the command line; if it returns something like `ruby 2.0.0` you should be fine.

###Install Bundler
Bundler is a package manager for your Ruby projects that supposedly makes your Jekyll life a bit easier. It comes as a Gem and you can install it by doing `gem install bundler`. You may have to `sudo` for this to work depending on your setup.

###Bundle!
Bundler packages are specified in a Gemfile. We'll create one of those for our blog project; GitHub has a special Gem that will give us what we need. Open a file and enter:

{% highlight ruby %}
source 'https://rubygems.org'
   
require 'json'
require 'open-uri'
versions = JSON.parse(open('https://pages.github.com/versions.json').read)
   
gem 'github-pages', versions['github-pages']
{% endhighlight %}

Save the file with the nam `Gemfile`.

Run `bundle install`, this will down load and install a shitload of stuff in a secret location on your computer.

Note 1: The script may ask you for your password to execute as sudo, so your user must be allowed to do that (i.e. be on the sudoers list).
Note 2: On my Mac, the bundle installation fails since the "Xcode Command Line Tools" wasn't (correctly) installed. If this happens to you, do: `xcode-select --install` (this will download and install XCode, if you don't have it already), then rerun `bundle install`.

###Generate the blog skeleton
In you `gh-pages` branch directory do: `jekyll new .`, this will create your site.

On GitHub the blog will be available under the sub path `/project-repository` name, for the site to render correctly you need to set the `baseurl` option in `_config.yml` in you new Jekyll generated site to match this. In our case we set `baseurl` to `/dddsample-core` 

To generate the site and serve it locally, do: `bundle exec jekyll serve --baseurl ''` This should make your site available locally on port 4000, overriding the baseurl setting in `_config.yml`.

If you are happy with with the result. Add and commit the entire blog file tree, and push it to GitHub. When that is done, point your browser to http://<user>github.io/<project-repository>, e.g. [http://citerus.github.io/dddsample-core](http://citerus.github.io/dddsample-core) and view your new blog in all its glory.

If you want to see the source code for this blog, you'll find it in our [repo](https://github.com/citerus/dddsample-core/tree/gh-pages).

Additional docs: 
* [Jekyll](http://jekyllrb.com/)
* [GitHub Pages Basics](https://help.github.com/categories/github-pages-basics/)