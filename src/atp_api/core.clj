(ns atp-api.core)

; Constants
(def base-url "http://www.atpworldtour.com")

; Calendar page patterns
;TODO: optimize regex
(def tournament-block
  (re-pattern "(?s)<tr class=\"calendarFilterItem\">.+?</tr>"))

(def calendar-date
  (re-pattern "\\d{2}\\.\\d{2}\\.\\d{4}"))

(def calendar-tournament-name
  (re-pattern "<td width=\"202\"><strong>[^<]*"))

(def calendar-tournament-country
  (re-pattern "<br /><strong>[^<]*"))

(def calendar-tournament-series
  (re-pattern "</strong><br />[^<]*</td>"))

(def calendar-in-out
  (re-pattern "<td width=\"51\">[^<]*"))

(def calendar-court
  (re-pattern "<td width=\"51\">.*<br />[^<]*</td>"))

(def calendar-prize
  (re-pattern "<span>[^(]+</span>"))

(def calendar-financial-commitment
  (re-pattern "<span>\\(.+?\\)</span>"))

(def calendar-sgl-num
  (re-pattern "SGL \\d+"))

(def calendar-dbl-num
  (re-pattern "DBL \\d+"))

(def calendar-sgl-url
  (re-pattern "<a href=\".*\">SGL"))

(def calendar-dbl-url
  (re-pattern "<a href=\".*\">DBL"))

(def calendar-sgl-winner
  (re-pattern "Singles:.+?</a>"))

(def calendar-sgl-winner-url
  (re-pattern "Singles:.+?\">"))

(def calendar-dbl-row
  (re-pattern "Doubles:.+?</a>.+?</a>"))

(def url-pattern
  (re-pattern "<a href.+?>"))

(def a-href-pattern
  (re-pattern "<a href.+?>.+?</a>"))

; Tournament page patterns
(def tournament-name
  (re-pattern "<h3>.+?</h3>"))

(def tournament-location
  (re-pattern #"(?<=<p class=\"tournamentSubTitle\">).+?(?= -)"))

(def tournament-start-date
  (re-pattern #"(?<= - )\d{2}\.\d{2}\.\d{4}"))

(def tournament-end-date
  (re-pattern #"(?<=\d{4}-)\d{2}\.\d{2}\.\d{4}"))

(def tournament-draw
  (re-pattern #"(?<=<p><span>Draw: </span>)\d+?(?=</p>)"))

(def tournament-surface
  (re-pattern #"(?<= <p><span>Surface: </span>).+?(?=</p>)"))

(def tournament-prize
  (re-pattern #"(?<= <p><span>Prize Money: </span>).+?(?=</p>)"))

(def tournament-financial-commitment
  (re-pattern #"(?<=Total Financial Commitment</a>: ).+?(?=\s)"))

(def tournament-seed-blocks
  (re-pattern #"(?s)<div id=\"cph.+?class=\"drawItem.+?<p class=\"seedNumber\">.+?</div>"))

(def tournament-slot-num
  (re-pattern #"(?<=<p class=\"seedNumber\">)\d+?(?=</p>)"))

(def tournament-seed
  (re-pattern #"(?<=<p class=\"highestRound\">).+?(?=</p>)"))

(def tournament-seed-name
  (re-pattern #"(?<=id=\"cphMain_phExtra_ctl00_ctl01_ctl\d{1,3}_Player1.{0,26}>).+?(?=</)"))

(def tournament-seed-url
  (re-pattern #"(?<=<a href=\").+?(?=\" id=\"cphMain_phExtra_ctl00_ctl01_ctl\d{1,3}_Player1)"))

(def tournament-matches
  (re-pattern #"(?s)<div id=\"cphMain_phExtra_ctl00_ctl0[2-9]_ctl\d{1,3}_DrawNodeDiv\".+?</div>\s.+?</div>\s"))

(def tournament-ctl-rd
  (re-pattern #"(?<=id=\"cphMain_phExtra_ctl00_ctl0)[2-9](?=_ctl\d{1,3}_DrawNodeDiv\")"))

(def tournament-match-score
  (re-pattern #"(?<=ScoreLink\">).*?(?=</a>)"))

(def tournament-match-winner
  (re-pattern #"(?<=class=\"(?:player winner|player)\">).+?(?=</a>)"))

(def tournament-match-url
  (re-pattern #"(?<=javascript:openWin\(').*?(?=','Matchfacts')"))

; Player page patterns
(def player-name
  (re-pattern #"(?<=<h1>).+?(?=</h1>)"))

(def player-age
  (re-pattern #"(?<=Age:</span> ).+?(?=\()"))

(def player-bday
  (re-pattern #"(?<=\d{2} \()\d{2}\.\d{2}\.\d{4}(?=\))"))

(def player-birthplace
  (re-pattern #"(?<=Birthplace:</span> ).+?(?=</li>)"))

(def player-residence
  (re-pattern #"(?<=Residence:</span> ).+?(?=</li>)"))

(def player-height
  (re-pattern #"(?<=Height:</span> \d'\d{1,2}\" \()\d{3} cm(?=\))"))

(def player-weight
  (re-pattern #"(?<=Weight:</span> \d{2,3} lbs \()\d{2,3} kg(?=\))"))

(def player-plays
  (re-pattern #"(?<=Plays:</span> ).+?(?=</li>)"))

(def player-turned-pro
  (re-pattern #"(?<=Turned Pro:</span> )\d{4}(?=</li>)"))

(def player-coach
  (re-pattern #"(?<=Coach:</span> ).+?(?=</li>)"))

(def player-website
  (re-pattern #"(?<=Website:</span> <a href=\").+?(?=\")"))

(def player-nationality
  (re-pattern #"(?<=height=\"48\"  title=\").+?(?=\")"))

(def player-singles-block
  (re-pattern #"(?s)<table id=\"bioGridSingles\".+?</table>"))

(def player-doubles-block
  (re-pattern #"(?s)<table id=\"bioGridDoubles\".+?</table>"))

(def player-current-rank
  (re-pattern #"(?<=Current</p><span class=\"bioGridRank\">).+?(?=</span>)"))

(def player-high-rank
  (re-pattern #"(?<=High</p><span class=\"bioGridRank\">).+?(?=</span>)"))

(def player-rank-change
  (re-pattern #"(?<=<td width=\"46\"><span ).+?(?=</span>)"))

(def player-win-loss
  (re-pattern #"(?<=<td width=\"52\">).+?(?=</td>)"))

(def player-titles
  (re-pattern #"(?<=<td width=\"33\">).+?(?=</td>)"))

(def player-ytd-prize
  (re-pattern #"(?<=<td width=\"87\">).+?(?=[^>]</td>)"))

(def player-career-prize
  (re-pattern #"(?<=<td width=\"87\">).+?(?=<p)"))

; Match stats page patterns
(def match-tournament-name
  (re-pattern #"(?<=false;\">)[^<].+?(?=</a>)"))

(def match-tournament-url
  (re-pattern #"(?<=onclick=\"openWin\(')/Tennis/Tournaments/.+?(?=')"))

(def match-round
  (re-pattern #"(?s)(?<=Round</td>.{127}colspan=\"2\">).+?(?=</td>)"))

(def match-time
  (re-pattern #"(?s)(?<=Time</td>.{127}colspan=\"2\">).+?(?=</td>)"))

(def match-player-name
  (re-pattern #"(?<=class=\"playerName\">).+?(?=</a>)"))

(def match-player-url
  (re-pattern #"(?<=openWin\(')/Tennis/Players/.+?(?=')"))

(def match-player-nationality
  (re-pattern #"(?<=height=\"48\"  title=\").+?(?=\")"))

(def match-aces
  (re-pattern #"(?s)(?<=Aces</td>.{125,165}>)\d+?(?=</td>)"))

(def match-df
  (re-pattern #"(?s)(?<=Double Faults</td>.{125,165}>)\d+?(?=</td>)"))

(def match-stats
  (re-pattern #"\d+/\d+"))

(def match-service-games
  (re-pattern #"(?s)(?<=Service Games Played</td>.{125,165}>)\d+?(?=</td>)"))

; General utils
(defn substring? [sub st]
  (not= (.indexOf st sub) -1))

(defn re-matches? [pattern st]
  (boolean (re-find pattern st)))

(defn load-url [url]
  (slurp url))

(defn calendar-url? [url]
  (substring? "atpworldtour.com/Scores/Archive-Event-Calendar.aspx" url))

(def tournament-pattern
  (re-pattern "atpworldtour\\.com/Share/Event-Draws\\.aspx\\?e=\\d+&y=\\d{4}"))

(defn tournament-url? [url]
  (re-matches? tournament-pattern url))

(def match-stats-pattern
  (re-pattern "atpworldtour\\.com/Share/Match-Facts-Pop-Up\\.aspx\\?t=\\d+&y=\\d{4}&r=\\d{1}&p="))

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
  (if (nil? s) ""
    (subs s start)))

; Calendar page utils
(defn clean-court-tags [s]
  (if (nil? s) ""
    (clojure.string/replace (neg-subs s 0 5) #"<td width=\"51\">[^<]*<br />" "")))

(defn clean-draw [s]
  (if (nil? s) ""
    (subs s 4)))

(defn clean-tournament-url [url]
  (if (nil? url) ""
    (str base-url (neg-subs url 9 5))))

(defn clean-winner-name [s]
  (if (nil? s) ""
    (clojure.string/replace (neg-subs s 0 4) #"Singles:.+?\">" "")))

(defn clean-winner-url [url]
  (if (nil? url) ""
    (str base-url (neg-subs url 19 2))))

(defn clean-dbl-names [row]
  (if (nil? row) ""
    (map #(get-text %) (re-seq a-href-pattern row))))

(defn clean-dbl-urls [row]
  (if (nil? row) ""
    (map #(str base-url (neg-subs % 9 2)) (re-seq url-pattern row))))

(defn indoor? [loc]
  (= loc "Indoor"))

; TODO: optimize the regex
(defn map-calendar-page [page]
  (let [blocks (re-seq tournament-block page)]
    (map #(into {} {:date (re-find calendar-date %)
                    :name (subs (re-find calendar-tournament-name %) 24)
                    :location (subs (re-find calendar-tournament-country %) 14)
                    :series (neg-subs (re-find calendar-tournament-series %) 15 5)
                    :indoor (indoor? (subs (re-find calendar-in-out %) 15))
                    :court (clean-court-tags (re-find calendar-court %))
                    :prize (neg-subs (re-find calendar-prize %) 6 7)
                    :financial-commitment (neg-subs (re-find calendar-financial-commitment %) 7 8)
                    :sgl-draw (clean-draw (re-find calendar-sgl-num %))
                    :dbl-draw (clean-draw (re-find calendar-dbl-num %))
                    :sgl-url (clean-tournament-url (re-find calendar-sgl-url %))
                    :dbl-url (clean-tournament-url (re-find calendar-dbl-url %))
                    :sgl-winner (clean-winner-name (re-find calendar-sgl-winner %))
                    :dbl-winners (clean-dbl-names (re-find calendar-dbl-row %))
                    :sgl-winner-url (clean-winner-url (re-find calendar-sgl-winner-url %))
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
  (into {} {:name (get-text (re-find tournament-name page))
            :location (re-find tournament-location page)
            :start-date (re-find tournament-start-date page)
            :end-date (re-find tournament-end-date page)
            :draw (re-find tournament-draw page)
            :surface (re-find tournament-surface page)
            :prize-money (re-find tournament-prize page)
            :financial-commitment (re-find tournament-financial-commitment page)
            :seeds (map #(into {} {:slot-number (re-find tournament-slot-num %)
                                   :seed (re-find tournament-seed %)
                                   :player-name (re-find tournament-seed-name %)
                                   :player-url (re-find tournament-seed-url %)}) (re-seq tournament-seed-blocks page))
            :matches (map #(into {} {:round (get-round (re-find tournament-ctl-rd %) (re-find tournament-draw page))
                                     :score (re-find tournament-match-score %)
                                     :winner (re-find tournament-match-winner %)
                                     :match-stats-url (str base-url (re-find tournament-match-url %))}) (re-seq tournament-matches page))}))

; Player page utils
(defn get-rank-change-value [s]
  (if (nil? s)
    nil
    (if (> (count s) 2)
      (if (substring? "playerGridWeekDown" s)
        (* (Integer. (re-find #"\d{1,4}" s)) -1)
        (re-find #"\d{1,4}" s))
      0)))

; TODO: bug with retired players returning win-loss & titles for this year
(defn map-player [page]
  (let [sgl-block (re-find player-singles-block page)
        dbl-block (re-find player-doubles-block page)]
    (into {} {:name (re-find player-name page)
              :age (re-find player-age page)
              :birthday (re-find player-bday page)
              :birthplace (re-find player-birthplace page)
              :residence (re-find player-residence page)
              :height (re-find player-height page)
              :weight (re-find player-weight page)
              :plays (re-find player-plays page)
              :turned-pro (re-find player-turned-pro page)
              :coach (re-find player-coach page)
              :website (re-find player-website page)
              :nationality (re-find player-nationality page)
              :ytd-prize (re-find player-ytd-prize page)
              :career-prize (re-find player-career-prize page)
              :singles {:this-year {:ranking (re-find player-current-rank sgl-block)
                                    :week-change (get-rank-change-value (re-find player-rank-change sgl-block))
                                    :win-loss (re-find player-win-loss sgl-block)
                                    :titles (re-find player-titles sgl-block)}
                        :career {:ranking (re-find player-high-rank sgl-block)
                                 :win-loss (last (re-seq player-win-loss sgl-block))
                                 :titles (last (re-seq player-titles sgl-block))}}
              :doubles {:this-year {:ranking (re-find player-current-rank dbl-block)
                                    :week-change (get-rank-change-value (re-find player-rank-change dbl-block))
                                    :win-loss (re-find player-win-loss dbl-block)
                                    :titles (re-find player-titles dbl-block)}
                        :career {:ranking (re-find player-high-rank dbl-block)
                                 :win-loss (last (re-seq player-win-loss dbl-block))
                                 :titles (last (re-seq player-titles dbl-block))}}})))

; Match stat utils
; Not all stats are necessary - a lot can be calculated (e.g. 1st serve return pts won = other player total serve - 1st serve pts won)
(defn map-match-stats [page]
  (let [name-matches (re-seq match-player-name page)
        url-matches (re-seq match-player-url page)
        nationalities (re-seq match-player-nationality page)
        aces (re-seq match-aces page)
        df (re-seq match-df page)
        stats (re-seq match-stats page)
        service-games (re-seq match-service-games page)]
    (into {} {:tournament (re-find match-tournament-name page)
              :tournament-url (str base-url (re-find match-tournament-url page))
              :round (re-find match-round page)
              :time (re-find match-time page)
              :winner (nth name-matches 0)
              :winner-url (str base-url (nth url-matches 0))
              :p1-name (nth name-matches 1)
              :p1-url (str base-url (nth url-matches 1))
              :p2-name (nth name-matches 2)
              :p2-url (str base-url (nth url-matches 2))
              :p1-nationality (nth nationalities 0)
              :p2-nationality (if (> (count nationalities) 1)
                                (nth nationalities 1)
                                "")
              :p1-aces (nth aces 0)
              :p2-aces (nth aces 1)
              :p1-df (nth df 0)
              :p2-df (nth df 1)
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
              :p1-service-games (nth service-games 0)
              :p2-service-games (nth service-games 1)})))

; API calls
(defn parse-calendar [url]
  (if calendar-url?
    (let [page (load-url url)]
      (map-calendar-page page))
    "Invalid calendar URL"))

(defn parse-tournament [url]
  (if tournament-url?
    (let [page (load-url url)]
      (map-tournament-page page))
    "Invalid tournament URL"))

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
