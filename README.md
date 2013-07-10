# Layout

Ring middleware that allows developer to specify base HTML template.

## Installation

Leiningen coordinates:

```clojure
[ilshad/layout "x.x.x"]
```

!NOTE: yet not released.

## Usage

### Ring middleware

Define `wrap-layout` middleware with your layout template:

```clojure
(require '[compojure.core :refer [defroutes]])
(require '[compojure.route :refer [files]])
(require '[ilshad.layout :refer [wrap-layout
                                 prevent-layout]])

(defroutes routes
  ; ... assemble routes ...
  
  ; some routes must not be wrapped into layout
  (prevent-layout (files "/static" {:root "resources/static"}))
  
  ; etc
  )

(def app
  (-> routes
      (wrap-layout myapp/layout-template)

	  ; ... other middlewares
	  ))
```

where `myapp/layout-template` is your function (in particular,
build with `enlive-html/deftemplate`) taking two arguments. First
argument is Ring request. Second argument is `content` - data structure
like produced from `enlive-html/html-snippet`. Actually, `content` is
response from your handlers in html snippet view. This function should
return string or sequence of strings. Something like this:

```clojure
(require '[net.cgrand.enlive-html :as html))

(html/deftemplate layout-template "layout.html"
  [request content]

  ; compose response from handlers with base template
  [:#main] (html/content content)

  [:#menu] (html/content (myapp/build-menu request))
  [:#flash] (html/content (:flash request)))
```

Your Ring handlers return Ring response with body, or just body. The body is:

* string containing HTML
* or `enlive-html/html-snippet` output

The middleware will wrap this response into your layout template.

### Preventing layout

Some responses must not be wrapped into layout template. For this, you
have add `:layout` keyword with value `nil` into your handler's response,
or utilize convenient function `ilshad.layout/prevent-layout`. See example
`defroutes` code above.

!FIXME: :layout {:prevent true}

## TODO

- Hiccup support
- independent from template engine
- allow to define multiple named layouts

## License

Copyright © 2013 [Ilshad Khabibullin](http://ilshad.com).

Distributed under the Eclipse Public License, the same as Clojure.
