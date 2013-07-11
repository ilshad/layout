# Layout

Ring middleware that allows developer to specify base HTML template.

## Installation

Leiningen coordinates:

```clojure
[ilshad/layout "x.x.x"]
```

!NOTE: yet not released.

## Simplest option

Define `wrap-layout` middleware for your Ring application:

```clojure
(require '[ilshad.layout :refer [wrap-layout]])
; ... define your compojure routes here
(def app
  (-> app*
      ; ... some middlewares
      (wrap-layout layout-template)
	  ; ... other middlewares
	  ))
```

where `layout-template` is your function (for example, build with
`enlive-html/deftemplate`). For example:

```clojure
(require '[net.cgrand.enlive-html :as html))
(html/deftemplate layout-template "layout.html"
  [request content params]
  ; compose response from handlers with base template
  [:#main] (if (string? content)
             (html/html-content content)
             (html/content content))
  [:#menu] (html/content (myapp/build-menu request))
  [:#flash] (html/content (:flash request)))
  [:title] (:title params "Cool site")
```

This function is taking 3 arguments:

- Ring request,
- Response's body from your Ring handler,
- map of custom params you might want to pass into the layout
template. They are under key `:layout` in Ring handler's response.

For example, how to add `title` layout param form Ring handler:

```clojure
(defn frontpage
  [req]
  {:status 200
   :headers {}
   :body "<h1>This is front page</h1>"
   :layout {:title "Welcome!"}})
```

Any custom params (under `:layout`) will be passed into your layout
template function. Notice, there is reserved keys in the params -
`:template` which is used for named templates, and `:prevent` to
prevent layout, see below.

## Named layout templates

You can define multiple layout templates with the middleware and
then select it by passing templates' name into layout params form
custom handler. There are 2 rules:

- names are keywords;
- `:default` template is used by default (if you do not pass `:template`
layout param from handler).

In example below, we define 2 base templates: `:default` and `:admin`:

```clojure
(require '[ilshad.layout :refer [wrap-layout]])
; ... define your compojure routes here
(def app
  (-> app*
      ; ... some middlewares
      (wrap-layout {:templates {:default layout-template-1
	                            :admin layout-template-2}})
	  ; ... other middlewares
	  ))
```

And in handler, let's say we call this with admin layout:

```clojure
(defn admin-page
  [req]
  {:status 200
   :headers {}
   :body "<h1>Admin console</h1>"
   :layout {:template :admin}})
```

## Prevent layout

Some handlers must be called without wrapping their response's body
into layout. There are 2 options how to do this:

- pass `:prevent true` into layout params from ring handler:

```clojure
(defn ajax-view
  [req]
  {:status 200
   :headers {}
   :body "foo bar baaz"
   :layout {:prevent true}})
```

- or define patterns for URI with middleware. This is `:prevent` with
vector of regexps:

```clojure
(require '[ilshad.layout :refer [wrap-layout]])
; ... define your compojure routes here
(def app
  (-> app*
      ; ... some middlewares
      (wrap-layout {:prevent [#"^/static" #"^/api"]
                    :templates {:default layout-template}})
	  ; ... other middlewares
	  ))
```

## Summary

Middleware `wrap-template` can be used with:

- symbol argument (sole default template)
- or map argument to define multiple templates and prevent-layout patterns.

Handlers can specify `:layout` slot in Ring response with map. This
map can contain:

- `:template` with name of template (also keyword) instead of default.
- `:prevent` with value `true`
- arbitrary fields to pass into layout template.

## License

Copyright © 2013 [Ilshad Khabibullin](http://ilshad.com).

Distributed under the Eclipse Public License, the same as Clojure.
