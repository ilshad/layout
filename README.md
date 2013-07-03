# Layout

Ring middleware that allows developer to specify base layout template
for entire web application.

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
(defroutes routes
  ; ... assemble routes ...
  
  ; some routes must not be wrapped into layout
  (ilshad.layout/prevent-layout (files "/static" {:root "resources/static"}))
  (not-found "<h1>Not Found</h1>"))

(def app
  (-> routes
      (ilshad.layout/wrap-layout myapp/layout-template)
      ; ... other middlewares
	  (compojure.handler/site ...)))
```

where `myapp/layout-template` is your function (in particular,
build with `enlive-html/deftemplate`) taking two arguments. First
argument is Ring request. Second argument is `content` - data structure
like produced from `enlive-html/html-snippet`. Something like this:

```clojure
(html/deftemplate layout-template "layout.html"
  [request content]
  [:title] (html-content (myapp/build-title request))
  [:#menu] (html/content (myapp/build-menu request))
  [:#flash] (html/content (:flash request))
  [:#main] (html/content content))
```

and `layout.html` is your "base" template:

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

Your Ring handlers can return Ring response, or just body, as usual and
Compose Them With Compojure. Body Can be strings with some HTML, or
`enlive-html/html-snippet` output. The middleware will wrap this response
into your layout template.

### Preventing layout wrapping

Some routes must not be wrapped into layout template. For this, you
can add `:layout` keyword with value `false` into your handler's response,
or just wrap the handler into `prevent-layout`. See example `defroutes`
code below.

### Layout without middleware

Alternatively, `layout` function can be used explicit with Ring handler:

```clojure
(defn my-ring-handler
  [request]
  (let [content (; ... things here
        )]
    (ilshad.layout/layout request content myapp/layout-template)))

## License

Copyright © 2013 [Ilshad Khabibullin](http://ilshad.com).

Distributed under the Eclipse Public License, the same as Clojure.
