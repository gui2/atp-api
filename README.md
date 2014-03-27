# ATP-API

A Clojure API to retrieve data from the [ATP World Tour website](http://www.atpworldtour.com/). Currently a work in progress, a lot of features remain to be added.

## Usage

```clj
; Step 1: add to your "project.clj" file
:dependencies [[atp-api "0.2.5"]]

; Step 2: Add the API to your .clj file
(require '[atp-api.core :as atp])

; Step 3: use the API call for the page you want to scrape
;         Supported calls below:
(atp/parse-calendar "http://www.atpworldtour.com/Scores/Archive-Event-Calendar.aspx?t=2&y=2014")

(atp/parse-tournament "http://www.atpworldtour.com/Share/Event-Draws.aspx?e=339&y=2014")

(atp/parse-player "http://www.atpworldtour.com/Tennis/Players/Top-Players/Roger-Federer.aspx")

(atp/parse-match-stats "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=F324")

(atp/parse-rankings "http://www.atpworldtour.com/Rankings/Doubles.aspx")
```

## License

Copyright © 2014 Nikola Peric

Distributed under the Eclipse Public License version 1.0.
