(ns atp-api.core)

; Constants
(def base-url "http://www.atpworldtour.com")

; Generic patterns
(def url-pattern #"<a href.+?>")

(def a-href-pattern #"<a href.+?>.+?</a>")

(def tournament-pattern #"atpworldtour\.com/Share/Event-Draws\.aspx\?e=\d+&y=\d{4}")

(def match-stats-pattern #"atpworldtour\.com/Share/Match-Facts-Pop-Up\.aspx\?t=\d+&y=\d{4}&r=\d{1}&p=")

; Calendar page patterns
(def calendar-dbl-row #"(?<=Doubles:).+?</a>.+?</a>")

; Tournament page patterns
(def tournament-draw #"(?<=<p><span>Draw: </span>)\d+?(?=</p>)")

; Player page patterns
(def player-current-rank #"(?<=Current</p><span class=\"bioGridRank\">).+?(?=</span>)")

(def player-high-rank #"(?<=High</p><span class=\"bioGridRank\">).+?(?=</span>)")

(def player-rank-change #"(?<=<td width=\"46\"><span ).+?(?=</span>)")

(def player-win-loss #"(?<=<td width=\"52\">).+?(?=</td>)")

(def player-titles #"(?<=<td width=\"33\">).+?(?=</td>)")

; General utils
(defn substring? [sub st]
  (not= (.indexOf st sub) -1))

(defn re-matches? [pattern st]
  (boolean (re-find pattern st)))

(defn load-url [url]
  (slurp url))

(defn calendar-url? [url]
  (substring? "atpworldtour.com/Scores/Archive-Event-Calendar.aspx" url))

(defn tournament-url? [url]
  (re-matches? tournament-pattern url))

(defn double-tournament-url? [url]
  (and (re-matches? tournament-pattern url) (substring? "&t=d" url)))

(defn match-stats-url? [url]
  (re-matches? match-stats-pattern url))

(defn player-url? [url]
  (or (substring? "atpworldtour.com/Tennis/Players/" url)
      (substring? "atpworldtour.com/tennis/players/" url)))

(defn get-text [tag]
  (clojure.string/replace tag #"<[^>]*>" ""))

(defn neg-subs [s start end]
  (if (nil? s) ""
    (subs s start (- (count s) end))))

(defn checked-subs [s start]
  (if (nil? s) nil
    (subs s start)))

; Calendar page utils
(defn clean-dbl-names [row]
  (if (nil? row) ""
    (map #(get-text %) (re-seq a-href-pattern row))))

(defn clean-dbl-urls [row]
  (if (nil? row) ""
    (map #(str base-url (neg-subs % 9 2)) (re-seq url-pattern row))))

(defn indoor? [loc]
  (= loc "Indoor"))

(defn map-calendar-page [page]
  (let [blocks (re-seq #"(?s)<tr class=\"calendarFilterItem\">.+?</tr>" page)]
    (map #(into {} {:date (re-find #"\d{2}\.\d{2}\.\d{4}" %)
                    :name (re-find #"(?<=<td width=\"202\"><strong>)[^<]*" %)
                    :location (re-find #"(?<=<br /><strong>)[^<]*" %)
                    :series (re-find #"(?<=</strong><br />)[^<]*(?=</td>)" %)
                    :indoor (indoor? (re-find #"(?<=<td width=\"51\">)[^<]*" %))
                    :court (re-find #"(?<=<td width=\"51\">.{0,7}<br />)[^<]*" %)
                    :prize (re-find #"(?<=<span>)[^(]+(?=</span>)" %)
                    :financial-commitment (re-find #"(?<=<span>\().+?(?=\)</span>)" %)
                    :sgl-draw (re-find #"(?<=SGL )\d+" %)
                    :dbl-draw (re-find #"(?<=DBL )\d+" %)
                    :sgl-url (str base-url (re-find #"(?<=<a href=\").*(?=\">SGL)" %))
                    :dbl-url (str base-url (re-find #"(?<=<a href=\").*(?=\">DBL)" %))
                    :sgl-winner (re-find #"(?<=Singles:  <a href=\"/Tennis/Players/.{0,256}\">).+?(?=</a>)" %)
                    :dbl-winners (clean-dbl-names (re-find calendar-dbl-row %))
                    :sgl-winner-url (str base-url (re-find #"(?<=Singles:  <a href=\").+?(?=\")" %))
                    :dbl-winners-url (clean-dbl-urls (re-find calendar-dbl-row %))}) blocks)))

; Tournament page utils
(defn get-round [rd draw]
  (let [max-rd (/ (Math/log (Integer. draw)) (Math/log 2))]
    (case (- max-rd (Integer. rd) -1)
      0.0 "F"
      1.0 "SF"
      2.0 "QF"
      3.0 "R16"
      4.0 "R32"
      5.0 "R64"
      6.0 "R128")))

(defn map-tournament-page [page]
  (into {} {:name (get-text (re-find #"(?<=<h3>).+?(?=</h3>)" page))
            :location (re-find #"(?<=<p class=\"tournamentSubTitle\">).+?(?= -)" page)
            :start-date (re-find #"(?<= - )\d{2}\.\d{2}\.\d{4}" page)
            :end-date (re-find #"(?<=\d{4}-)\d{2}\.\d{2}\.\d{4}" page)
            :draw (re-find tournament-draw page)
            :surface (re-find #"(?<= <p><span>Surface: </span>).+?(?=</p>)" page)
            :prize-money (re-find #"(?<= <p><span>Prize Money: </span>).+?(?=</p>)" page)
            :financial-commitment (re-find #"(?<=Total Financial Commitment</a>: ).+?(?=\s)" page)
            :seeds (map #(into {} {:slot-number (re-find #"(?<=<p class=\"seedNumber\">)\d+?(?=</p>)" %)
                                   :seed (re-find #"(?<=<p class=\"highestRound\">).+?(?=</p>)" %)
                                   :player-name (re-find #"(?<=id=\"cphMain_phExtra_ctl00_ctl01_ctl\d{1,3}_Player1.{0,26}>).+?(?=</)" %)
                                   :player-url (re-find #"(?<=<a href=\").+?(?=\" id=\"cphMain_phExtra_ctl00_ctl01_ctl\d{1,3}_Player1)" %)})
                        (re-seq #"(?s)<div id=\"cph.+?class=\"drawItem.+?<p class=\"seedNumber\">.+?</div>" page))
            :matches (map #(into {} {:round (get-round (re-find #"(?<=id=\"cphMain_phExtra_ctl00_ctl0)[2-9](?=_ctl\d{1,3}_DrawNodeDiv\")" %)
                                                       (re-find tournament-draw page))
                                     :score (re-find #"(?<=ScoreLink\">).*?(?=</a>)" %)
                                     :winner (re-find #"(?<=class=\"(?:player winner|player)\">).+?(?=</a>)" %)
                                     :match-stats-url (str base-url (re-find #"(?<=javascript:openWin\(').*?(?=','Matchfacts')" %))})
                          (re-seq #"(?s)<div id=\"cphMain_phExtra_ctl00_ctl0[2-9]_ctl\d{1,3}_DrawNodeDiv\".+?</div>\s.+?</div>\s" page))}))

; Player page utils
(defn get-rank-change-value [s]
  (if (nil? s)
    nil
    (if (> (count s) 2)
      (if (substring? "playerGridWeekDown" s)
        (* (Integer. (re-find #"\d{1,4}" s)) -1)
        (re-find #"\d{1,4}" s))
      0)))

(defn seq-min-size [sq]
  (if (> (count sq) 1)
    (first sq)
    nil))

(defn map-player [page]
  (let [sgl-block (re-find #"(?s)<table id=\"bioGridSingles\".+?</table>" page)
        dbl-block (re-find #"(?s)<table id=\"bioGridDoubles\".+?</table>" page)
        sgl-wl (re-seq player-win-loss sgl-block)
        dbl-wl (re-seq player-win-loss dbl-block)
        sgl-titles (re-seq player-titles sgl-block)
        dbl-titles (re-seq player-titles dbl-block)]
    (into {} {:name (re-find #"(?<=<h1>).+?(?=</h1>)" page)
              :age (re-find #"(?<=Age:</span> ).+?(?= \()" page)
              :birthday (re-find #"(?<=\d{2} \()\d{2}\.\d{2}\.\d{4}(?=\))" page)
              :birthplace (re-find #"(?<=Birthplace:</span> ).+?(?=</li>)" page)
              :residence (re-find #"(?<=Residence:</span> ).+?(?=</li>)" page)
              :height (re-find #"(?<=Height:</span> \d'\d{1,2}\" \()\d{2,3} cm(?=\))" page)
              :weight (re-find #"(?<=Weight:</span> \d{2,3} lbs \()\d{2,3} kg(?=\))" page)
              :plays (re-find #"(?<=Plays:</span> ).+?(?=</li>)" page)
              :turned-pro (re-find #"(?<=Turned Pro:</span> )\d{4}(?=</li>)" page)
              :coach (re-find #"(?<=Coach:</span> ).+?(?=</li>)" page)
              :website (re-find #"(?<=Website:</span> <a href=\").+?(?=\")" page)
              :nationality (re-find #"(?<=height=\"48\"  title=\").+?(?=\")" page)
              :ytd-prize (re-find #"(?<=<td width=\"87\">).+?(?=[^>]</td>)" page)
              :career-prize (re-find #"(?<=<td width=\"87\">).+?(?=<p)" page)
              :singles {:this-year {:ranking (re-find player-current-rank sgl-block)
                                    :week-change (get-rank-change-value (re-find player-rank-change sgl-block))
                                    :win-loss (seq-min-size sgl-wl)
                                    :titles (seq-min-size sgl-titles)}
                        :career {:ranking (re-find player-high-rank sgl-block)
                                 :win-loss (last sgl-wl)
                                 :titles (last sgl-titles)}}
              :doubles {:this-year {:ranking (re-find player-current-rank dbl-block)
                                    :week-change (get-rank-change-value (re-find player-rank-change dbl-block))
                                    :win-loss (seq-min-size dbl-wl)
                                    :titles (seq-min-size dbl-titles)}
                        :career {:ranking (re-find player-high-rank dbl-block)
                                 :win-loss (last dbl-wl)
                                 :titles (last dbl-titles)}}})))

; Match stat utils
; Not all stats are necessary - a lot can be calculated (e.g. 1st serve return pts won = other player total serve - 1st serve pts won)
(defn map-match-stats [page]
  (let [name-matches (re-seq #"(?<=class=\"playerName\">).+?(?=</a>)" page)
        url-matches (re-seq #"(?<=openWin\(')/Tennis/Players/.+?(?=')" page)
        nationalities (re-seq #"(?<=height=\"48\"  title=\").+?(?=\")" page)
        aces (re-seq #"(?s)(?<=Aces</td>.{125,165}>)\d+?(?=</td>)" page)
        df (re-seq #"(?s)(?<=Double Faults</td>.{125,165}>)\d+?(?=</td>)" page)
        stats (re-seq #"\d+/\d+" page)
        service-games (re-seq #"(?s)(?<=Service Games Played</td>.{125,165}>)\d+?(?=</td>)" page)]
    (into {} {:tournament (re-find #"(?<=false;\">)[^<].+?(?=</a>)" page)
              :tournament-url (str base-url (re-find #"(?<=onclick=\"openWin\(')/Tennis/Tournaments/.+?(?=')" page))
              :round (re-find #"(?s)(?<=Round</td>.{127}colspan=\"2\">).+?(?=</td>)" page)
              :time (re-find #"(?s)(?<=Time</td>.{127}colspan=\"2\">).+?(?=</td>)" page)
              :winner (nth name-matches 0)
              :winner-url (str base-url (nth url-matches 0))
              :p1-name (nth name-matches 1)
              :p1-url (str base-url (nth url-matches 1))
              :p2-name (nth name-matches 2)
              :p2-url (str base-url (nth url-matches 2))
              :p1-nationality (first nationalities)
              :p2-nationality (seq-min-size nationalities)
              :p1-aces (first aces)
              :p2-aces (last aces)
              :p1-df (first df)
              :p2-df (last df)
              :p1-first-serve (first (clojure.string/split (nth stats 0) #"/"))
              :p1-total-serve (last (clojure.string/split (nth stats 0) #"/"))
              :p2-first-serve (first (clojure.string/split (nth stats 1) #"/"))
              :p2-total-serve (last (clojure.string/split (nth stats 1) #"/"))
              :p1-first-serve-won (first (clojure.string/split (nth stats 2) #"/"))
              :p2-first-serve-won (first (clojure.string/split (nth stats 3) #"/"))
              :p1-second-serve-won (first (clojure.string/split (nth stats 4) #"/"))
              :p2-second-serve-won (first (clojure.string/split (nth stats 5) #"/"))
              :p1-break-pts-saved (first (clojure.string/split (nth stats 6) #"/"))
              :p1-total-break-pts (last (clojure.string/split (nth stats 6) #"/"))
              :p2-break-pts-saved (first (clojure.string/split (nth stats 7) #"/"))
              :p2-total-break-pts (last (clojure.string/split (nth stats 7) #"/"))
              :p1-service-games (first service-games)
              :p2-service-games (last service-games)})))

; API calls
(defn parse-calendar [url]
  (if calendar-url?
    (let [page (load-url url)]
      (map-calendar-page page))
    "Invalid calendar URL"))

(defn parse-tournament [url]
  (if (double-tournament-url? url)
    (println "TODO double tournaments")
    (if (tournament-url? url)
      (let [page (load-url url)]
        (map-tournament-page page))
      "Invalid tournament URL")))

(defn parse-player [url]
  (if player-url?
    (let [page (load-url url)]
      (map-player page))
    "Invalid player URL"))

(defn parse-match-stats [url]
  (if match-stats-url?
    (let [page (load-url url)]
      (map-match-stats page))
    "Invalid match status URL"))
