(ns atp-api.core-test
  (:use midje.sweet
        atp-api.core))

; Basic patterns tests
(def a-href-url "<a href=\"www.asdf.com\">test</a>")
(def match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=F324")
(def derp-url "http://www.asdf.qewrty/")

(fact "regex correctly matches a url in a href tag"
      a-href-url => url-pattern
      derp-url =not=> url-pattern)

(fact "regex correctly matches an a href tag"
      a-href-url => a-href-pattern
      derp-url =not=> a-href-pattern)

(fact "regex correctly matches a match stats url"
      match-stats-url => match-stats-pattern
      derp-url =not=> match-stats-pattern)

; Utils tests
(fact "'substring?' returns whether or not there is a match of the substring"
      (substring? "moo" "moocow") => true
      (substring? "asdf" "moocow") => false)

(fact "'re-matches?' returns whether or not there's a regex match given a string and pattern"
      (re-matches? #"\d" "a5df") => true
      (re-matches? #"\d" "asdf") => false)

(fact "'load-url' correctly slurps up a url"
      (load-url "http://www.google.com/") => truthy
      (load-url "http://www.google.fakeone/") => (throws Exception))

(fact "tests various url checks (calendar, tournament, player, match stats)"
      (calendar-url? "http://www.atpworldtour.com/Scores/Archive-Event-Calendar.aspx?t=2&y=2014") => true
      (calendar-url? derp-url) => false
      (tournament-url? "http://www.atpworldtour.com/Share/Event-Draws.aspx?e=339&y=2014") => true
      (tournament-url? derp-url) => false
      (player-url? "http://www.atpworldtour.com/Tennis/Players/Top-Players/Roger-Federer.aspx") => true
      (player-url? derp-url) => false
      (match-stats-url? match-stats-url) => true
      (match-stats-url? derp-url) => false)

(fact "'get-text' returns text between html tags"
      (get-text "<p>asdf</p>") => "asdf"
      (get-text "<p><i>moocow</i></p>") => "moocow")

(fact "'neg-subs' returns the substring between the two indecies"
      (neg-subs "purple cow" 0 4) => "purple"
      (neg-subs "purple cow" 2 4) => "rple")

(fact "'checked-subs' returns the substring if there is no nil string (i.e. it does a check)"
      (checked-subs nil 4) => nil
      (checked-subs "poop" 1) => truthy)

; API call tests
(fact "returns data from the calendar page"
      (first (parse-calendar "http://www.atpworldtour.com/Scores/Archive-Event-Calendar.aspx?t=2&y=2014")) =>
      {:dbl-winners-url '("http://www.atpworldtour.com/Tennis/Players/Top-Players/Daniel-Nestor.aspx"
                          "http://www.atpworldtour.com/Tennis/Players/Top-Players/Mariusz-Fyrstenberg.aspx"),
       :date "29.12.2013",
       :sgl-winner "Lleyton Hewitt",
       :prize "$452,670",
       :sgl-winner-url "http://www.atpworldtour.com/Tennis/Players/Top-Players/Lleyton-Hewitt.aspx",
       :court "Hard",
       :name "Brisbane International presented by Suncorp",
       :location "Australia",
       :financial-commitment "$511,825",
       :sgl-draw "28",
       :dbl-draw "16",
       :sgl-url "http://www.atpworldtour.com/Share/Event-Draws.aspx?e=339&y=2014",
       :dbl-url "http://www.atpworldtour.com/Share/Event-Draws.aspx?e=339&y=2014&t=d",
       :indoor false,
       :series "ATP World Tour 250",
       :dbl-winners '("Daniel Nestor" "Mariusz Fyrstenberg")})

(fact "returns data from the tournament page")

(fact "returns data from the player page")

(fact "returns data from the match stats page")

;(println (parse-tournament "http://www.atpworldtour.com/Share/Event-Draws.aspx?e=339&y=2014"))
