# Layout

Ring middleware that allows developer to specify base HTML template.

## Installation

Leiningen coordinates:

```clojure
[com.ilshad/layout "x.x.x"]
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
response from your handlers. Something like this:

```clojure
(require '[net.cgrand.enlive-html :as html))

(html/deftemplate layout-template "layout.html"
  [request content]
  [:title] (html/content (myapp/build-title request))
  [:#menu] (html/content (myapp/build-menu request))
  [:#flash] (html/content (:flash request))
  [:#main] (html/content content))
```

and `layout.html` is your base template:

```html
<!doctype html>
<html>
  <head>
    <meta charset="utf-8" />
    <link rel="stylesheet" type="text/css" href="/static/css/screen.css" />
    <title></title>
  </head>
  <body>
    <div id="container">
      <div id="menu"></div>
      <div id="flash"></div>
      <div id="main"></div>
  </body>
</html>
```

Your Ring handlers return Ring response with body, or just body, as usual.
The body is:

* string containing HTML
* `enlive-html/html-snippet` output

The middleware will wrap this response into your layout template.

### Preventing layout wrapping

Some responses must not be wrapped into layout template. For this, you
can add `:layout` keyword with value `false` into your handler's response,
or utilize convenient function `ilshad.layout/prevent-layout`. See example
`defroutes` code above.

### Layout without middleware

Alternatively, `ilshad.layout/layout` function can be used explicitly
with Ring handler:

```clojure
(require '[ilshad.layout :refer [layout]])

(defn my-ring-handler
  [request]
  (let [content (; ... things here
        )]
    (layout request content myapp/layout-template)))
```

## TODO

- Hiccup support
- independent from template engine
- allow to define multiple named layouts

## License

Copyright © 2013 [Ilshad Khabibullin](http://ilshad.com).

Distributed under the Eclipse Public License, the same as Clojure.
