(ns atp-api.core-test
  (:use midje.sweet
        atp-api.core))

; Basic patterns tests
(def a-href-url "<a href=\"www.asdf.com\">test</a>")
(def calendar-url "http://www.atpworldtour.com/Scores/Archive-Event-Calendar.aspx?t=2&y=2014")
(def tournament-url "http://www.atpworldtour.com/Share/Event-Draws.aspx?e=339&y=2014")
(def dbl-tournament-url "http://www.atpworldtour.com/Share/Event-Draws.aspx?e=339&y=2014&t=d")
(def player-url "http://www.atpworldtour.com/Tennis/Players/Le/I/Ivan-Lendl.aspx")
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
      (calendar-url? calendar-url) => true
      (calendar-url? derp-url) => false
      (tournament-url? tournament-url) => true
      (tournament-url? derp-url) => false
      (double-tournament-url? dbl-tournament-url) => true
      (double-tournament-url? tournament-url) => false
      (player-url? player-url) => true
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
      (first (parse-calendar calendar-url)) =>
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

(fact "returns data from the tournament page"
      (parse-tournament tournament-url) =>
      {:draw "32",
       :end-date "05.01.2014",
       :financial-commitment "$511,825",
       :location "Australia",
       :matches '({:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=F324", :round "R32", :score "", :winner "Federer, Roger"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=N289", :round "R32", :score "6-2, 6-3", :winner "Nieminen, Jarkko"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=ME05", :round "R32", :score "5-7, 6-4, 7-6(4)", :winner "Matosevic, Marinko"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=Q927", :round "R32", :score "7-5, 6-4", :winner "Querrey, Sam"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=H996", :round "R32", :score "", :winner "Herbert, Pierre-Hugues"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=G940", :round "R32", :score "7-6(3), 7-6(2)", :winner "Groth, Samuel"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=M873", :round "R32", :score "3-6, 6-4, 6-4", :winner "Mahut, Nicolas"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=CA12", :round "R32", :score "7-6(4), 7-5", :winner "Chardy, Jeremy"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=L397", :round "R32", :score "6-4, 6-4", :winner "Lopez, Feliciano"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=H432", :round "R32", :score "6-3, 7-5", :winner "Hewitt, Lleyton"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=CA99", :round "R32", :score "7-6(5), 6-7(2), 7-6(7)", :winner "Copil, Marius"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=SD32", :round "R32", :score "", :winner "Simon, Gilles"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=D875", :round "R32", :score "6-2, 6-3", :winner "Dimitrov, Grigor"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=C977", :round "R32", :score "6-7(3), 7-6(5), 6-4", :winner "Cilic, Marin"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=E690", :round "R32", :score "6-3, 6-4", :winner "Ebden, Matthew"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=3&p=N552", :round "R32", :score "", :winner "Nishikori, Kei"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=F324", :round "R16", :score "6-4, 6-2", :winner "Federer, Roger"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=ME05", :round "R16", :score "5-7, 7-6(3), 6-4", :winner "Matosevic, Marinko"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=G940", :round "R16", :score "4-6, 7-6(3), 7-6(5)", :winner "Groth, Samuel"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=CA12", :round "R16", :score "7-5, 6-7(4), 6-3", :winner "Chardy, Jeremy"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=H432", :round "R16", :score "7-5, 6-3", :winner "Hewitt, Lleyton"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=CA99", :round "R16", :score "7-5, 6-3", :winner "Copil, Marius"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=C977", :round "R16", :score "7-5, 7-5", :winner "Cilic, Marin"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=4&p=N552", :round "R16", :score "6-2, 6-4", :winner "Nishikori, Kei"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=5&p=F324", :round "QF", :score "6-1, 6-1", :winner "Federer, Roger"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=5&p=CA12", :round "QF", :score "7-5, 6-4", :winner "Chardy, Jeremy"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=5&p=H432", :round "QF", :score "6-4, 6-2", :winner "Hewitt, Lleyton"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=5&p=N552", :round "QF", :score "6-4, 5-7, 6-2", :winner "Nishikori, Kei"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=6&p=F324", :round "SF", :score "6-3, 6-7(3), 6-3", :winner "Federer, Roger"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=6&p=H432", :round "SF", :score "5-7, 6-4, 6-3", :winner "Hewitt, Lleyton"} {:match-stats-url "http://www.atpworldtour.com/Share/Match-Facts-Pop-Up.aspx?t=0339&y=2014&r=7&p=H432", :round "F", :score "6-1, 4-6, 6-3", :winner "Hewitt, Lleyton"}),
       :name "Brisbane International presented by Suncorp",
       :prize-money "$452,670",
       :seeds '({:player-name "Federer, Roger", :player-url "http://www.atpworldtour.com/tennis/players/F324.aspx", :seed "(1)", :slot-number "1"} {:player-name "Bye", :player-url nil, :seed nil, :slot-number "2"} {:player-name "Duckworth, James", :player-url "http://www.atpworldtour.com/tennis/players/D994.aspx", :seed "WC", :slot-number "3"} {:player-name "Nieminen, Jarkko", :player-url "http://www.atpworldtour.com/tennis/players/N289.aspx", :seed nil, :slot-number "4"} {:player-name "Matosevic, Marinko", :player-url "http://www.atpworldtour.com/tennis/players/ME05.aspx", :seed nil, :slot-number "5"} {:player-name "Benneteau, Julien", :player-url "http://www.atpworldtour.com/tennis/players/B747.aspx", :seed nil, :slot-number "6"} {:player-name "Querrey, Sam", :player-url "http://www.atpworldtour.com/tennis/players/Q927.aspx", :seed nil, :slot-number "7"} {:player-name "Tursunov, Dmitry", :player-url "http://www.atpworldtour.com/tennis/players/T315.aspx", :seed "(7)", :slot-number "8"} {:player-name "Herbert, Pierre-Hugues", :player-url "http://www.atpworldtour.com/tennis/players/H996.aspx", :seed "LL", :slot-number "9"} {:player-name "Bye", :player-url nil, :seed nil, :slot-number "10"} {:player-name "Groth, Samuel", :player-url "http://www.atpworldtour.com/tennis/players/G940.aspx", :seed "WC", :slot-number "11"} {:player-name "Harrison, Ryan", :player-url "http://www.atpworldtour.com/tennis/players/H940.aspx", :seed "Q", :slot-number "12"} {:player-name "Sijsling, Igor", :player-url "http://www.atpworldtour.com/tennis/players/SF36.aspx", :seed nil, :slot-number "13"} {:player-name "Mahut, Nicolas", :player-url "http://www.atpworldtour.com/tennis/players/M873.aspx", :seed nil, :slot-number "14"} {:player-name "Mannarino, Adrian", :player-url "http://www.atpworldtour.com/tennis/players/ME82.aspx", :seed nil, :slot-number "15"} {:player-name "Chardy, Jeremy", :player-url "http://www.atpworldtour.com/tennis/players/CA12.aspx", :seed "(8)", :slot-number "16"} {:player-name "Lopez, Feliciano", :player-url "http://www.atpworldtour.com/tennis/players/L397.aspx", :seed "(6)", :slot-number "17"} {:player-name "Kukushkin, Mikhail", :player-url "http://www.atpworldtour.com/tennis/players/K926.aspx", :seed nil, :slot-number "18"} {:player-name "Hewitt, Lleyton", :player-url "http://www.atpworldtour.com/tennis/players/H432.aspx", :seed nil, :slot-number "19"} {:player-name "Kokkinakis, Thanasi", :player-url "http://www.atpworldtour.com/tennis/players/KD46.aspx", :seed "Q", :slot-number "20"} {:player-name "Copil, Marius", :player-url "http://www.atpworldtour.com/tennis/players/CA99.aspx", :seed "Q", :slot-number "21"} {:player-name "Sugita, Yuichi", :player-url "http://www.atpworldtour.com/tennis/players/SE73.aspx", :seed "Q", :slot-number "22"} {:player-name "Bye", :player-url nil, :seed nil, :slot-number "23"} {:player-name "Simon, Gilles", :player-url "http://www.atpworldtour.com/tennis/players/SD32.aspx", :seed "(3)", :slot-number "24"} {:player-name "Dimitrov, Grigor", :player-url "http://www.atpworldtour.com/tennis/players/D875.aspx", :seed "(5)", :slot-number "25"} {:player-name "Haase, Robin", :player-url "http://www.atpworldtour.com/tennis/players/H756.aspx", :seed nil, :slot-number "26"} {:player-name "Istomin, Denis", :player-url "http://www.atpworldtour.com/tennis/players/I165.aspx", :seed nil, :slot-number "27"} {:player-name "Cilic, Marin", :player-url "http://www.atpworldtour.com/tennis/players/C977.aspx", :seed nil, :slot-number "28"} {:player-name "Kuznetsov, Alex", :player-url "http://www.atpworldtour.com/tennis/players/K737.aspx", :seed "LL", :slot-number "29"} {:player-name "Ebden, Matthew", :player-url "http://www.atpworldtour.com/tennis/players/E690.aspx", :seed nil, :slot-number "30"} {:player-name "Bye", :player-url nil, :seed nil, :slot-number "31"} {:player-name "Nishikori, Kei", :player-url "http://www.atpworldtour.com/tennis/players/N552.aspx", :seed "(2)", :slot-number "32"}),
       :start-date "30.12.2013",
       :surface "Hard"}
;      (parse-tournament dbl-tournament-url) =>
;      {:draw "32",
;       :end-date "05.01.2014"}
      )

; TODO: perhaps use static data for testing this
(fact "returns data from the player page"
      (parse-player player-url) => truthy)

(fact "returns data from the match stats page"
      (parse-match-stats match-stats-url) =>
      {:p1-aces "9",
       :p1-break-pts-saved "4",
       :p1-df "0",
       :p1-first-serve "34",
       :p1-first-serve-won "27",
       :p1-name "Roger Federer",
       :p1-nationality "Switzerland",
       :p1-second-serve-won "14",
       :p1-service-games "9",
       :p1-total-break-pts "4",
       :p1-total-serve "56",
       :p1-url "http://www.atpworldtour.com/Tennis/Players/Top-Players/Roger-Federer.aspx",
       :p2-aces "1",
       :p2-break-pts-saved "1",
       :p2-df "4",
       :p2-first-serve "35",
       :p2-first-serve-won "21",
       :p2-name "Jarkko Nieminen",
       :p2-nationality "Switzerland",
       :p2-second-serve-won "14",
       :p2-service-games "9",
       :p2-total-break-pts "4",
       :p2-total-serve "63",
       :p2-url "http://www.atpworldtour.com/Tennis/Players/Top-Players/Jarkko-Nieminen.aspx",
       :round "R16",
       :time "68&nbsp;minutes",
       :tournament "Brisbane",
       :tournament-url "http://www.atpworldtour.com/Tennis/Tournaments/Brisbane.aspx",
       :winner "Roger Federer",
       :winner-url "http://www.atpworldtour.com/Tennis/Players/Top-Players/Roger-Federer.aspx"})
