---
layout: post
title:  "Adding author attribution to the blog"
date:   2015-03-30 18:03:00
tags: blog meta
author: patrik_fredriksson
---

Having a way of adding a reference to the blog post author is nice, and as it turns out this is supported by default in Jekyll by adding a `author: Author Name` to the [Front Matter](http://jekyllrb.com/docs/frontmatter/) part of the post. But to make it a bit more interesting you can easily add a few tweaks.

With inspiration from [this post](http://blog.sorryapp.com/blogging-with-jekyll/2014/02/06/adding-authors-to-your-jekyll-site.html) I added a list of authors as laid out in the post, and then tweaked the `post.html` file in the [`_layouts`](http://jekyllrb.com/docs/datafiles/) directory to look like this:

{% highlight html %}
---
layout: default
---
{% raw %}
{% assign author = site.data.authors[page.author] %}
<div class="post">

  <header class="post-header">
    <h1 class="post-title">{{ page.title }}</h1>
    <p class="post-meta">{{ page.date | date: "%b %-d, %Y" }}{% if author %} • <a href="{{ author.web }}" target="_blank">{{ author.name }}</a>{% endif %}{% if page.meta %} • {{ page.meta }}{% endif %}</p>
  </header>

  <article class="post-content">
    {{ content }}
  </article>

</div>
{% endraw %}
{% endhighlight %}

And that was it. After rebuilding the site each post now shows the authors with a link to the author's web page!

The templating enigne used is Liquid, learn more (including how to temporarily disable template processing for the code above to be outputted correctly) on their [wiki](https://github.com/Shopify/liquid/wiki/Liquid-for-Designers)