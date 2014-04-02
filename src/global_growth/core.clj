(ns global-growth.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(defn TODO [msg]
  (throw (Exception. msg)))

(defn parse-json [str]
  (json/parse-string str true))

;; WORLD BANK API CALLS
(defn get-api
  "Returns map representing API response."
  [path params]
  (let [base-path (str "http://api.worldbank.org" path)
        query-params (merge params {:format "json" :per_page 10000})
        response (parse-json (:body (client/get base-path {:query-params query-params})))
        metadata (first response)
        results (second response)]
    {:metadata metadata
     :results results}))

(defn get-values
  "Returns a relation of two keys from API response."
  [path query-params key1 key2]
  (let [response (get-api path query-params)]
    (for [item (:results response)]
      [(key1 item) (key2 item)])))

(defn remove-aggregate-countries
  "Remove all countries that aren't actually countries, but are aggregates."
  [countries]
  (remove (fn [country]
            (= (get-in country [:region :value]) "Aggregates")) countries))

(def indicators
  (delay (get-values "/topics/16/indicators" {} :name :id)))

;; Get set of country ids so we can filter out aggregate values.
(def country-ids
  (delay
    (let [countries (remove-aggregate-countries (:results (get-api "/countries" {})))]
      (set (map :iso2Code countries)))))

(defn get-indicators []
  "Gets vector of indicators.
  /topics/16/indicators:   All urban development
  --- Other possibilities ---
  /sources/2/indicators:   All world development indicators (about 1300)
  /indicators:             All Indicators (about 8800)"
  @indicators)

(defn get-indicator-values
  "Returns indicator values for a specified year for all countries."
  [indicator year list-size]
  (let [values (get-values (str "/countries/all/indicators/" indicator)
                           {:date (str year)}
                           :country :value)]
    (take list-size
        (sort-by second >
                 (for [[country value] values
                       :when (and (not (nil? value))
                                  (contains? @country-ids (:id country)))]
                   [(:value country) (read-string value)])))))

(defn -main
  [& args]
  (let [indicator-values (get-indicator-values "EN.POP.DNST" 2010 10)]
    (doseq [value indicator-values]
      (println (str (first value) " " (second value))))))
