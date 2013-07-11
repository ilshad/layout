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
  
  ; using custom layout params
  [:title] (:title params "Cool site")
```

This function is taking 3 arguments:

- Ring request,
- Response's body from your Ring handler,
- map of custom params you might want to pass into the layout template.

Custom params are created with key `:layout` in Ring handler's response.
Say we want to add `title` param:

```clojure
(defn frontpage
  [req]
  {:status 200
   :headers {}
   :body "<h1>This is front page</h1>"
   :layout {:title "Welcome!"}})
```

Any custom params (under `:layout`) will be passed into your layout
template function. Notice, there is 2 reserved keys in the params -
`:template` and `:prevent`, see below.

## Named layout templates

You can define multiple layout templates with middleware and then select
them by passing their's names into response (under `:layout` map). There
are 2 rules:

- names are keywords;
- `:default` template is used by default.

Let's define 2 base templates: `:default` and `:admin`:

```clojure
(def app
  (-> app*
      ; ... some middlewares
      (wrap-layout {:templates {:default layout-template-1
	                            :admin layout-template-2}})
	  ; ... other middlewares
	  ))
```

In handler, let's call this with admin layout:

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

- pass `:prevent true` from ring handler:

```clojure
(defn ajax-handler
  [req]
  {:status 200
   :headers {}
   :body "foo bar baaz"
   :layout {:prevent true}})
```

- or define patterns for URI in middleware (regexp):

```clojure
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

Handlers can pass `:layout` slot into response with map. This map can
contain:

- `:template` with name of template (also keyword) instead of default.
- `:prevent` with value `true`
- arbitrary fields to pass into layout template.

## License

Copyright © 2013 [Ilshad Khabibullin](http://ilshad.com).

Distributed under the Eclipse Public License, the same as Clojure.
