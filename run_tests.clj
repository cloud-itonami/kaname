#!/usr/bin/env bb
(require '[clojure.test :as t])

(def suites
  '[kaname.tests.test-bridge
    kaname.tests.test-centrality
    kaname.tests.test-coverage
    kaname.tests.test-energy-join
    kaname.tests.test-gates
    kaname.tests.test-graph
    kaname.tests.test-ie-flow
    kaname.tests.test-ingest
    kaname.tests.test-join
    kaname.tests.test-kotoba
    kaname.tests.test-leverage-concentration
    kaname.tests.test-osekkai
    kaname.tests.test-route
    kaname.tests.test-repository-contract
    kaname.tests.test-social
    kaname.tests.test-sos])

(apply require suites)
(let [{:keys [fail error] :as result} (apply t/run-tests suites)]
  (println (select-keys result [:test :pass :fail :error]))
  (when (pos? (+ fail error))
    (System/exit 1)))
