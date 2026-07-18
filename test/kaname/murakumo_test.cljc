(ns kaname.murakumo-test
  (:require [clojure.test :refer [deftest is]]
            [kaname.murakumo :as kaname]))

(def full-attestations
  (into {}
        (map (fn [gate] [gate (str "attested-" (name gate))]))
        (distinct (mapcat :required-gates (vals kaname/cell-specs)))))

(deftest maps-all-legacy-kaname-cells
  (is (= #{"charter_attestation_request"
           "ethics_content_classifier"
           "land_donation_processing"
           "member_registry"
           "religious_corp_taxation"
           "religious_marriage"
           "tithe_routing"
           "treasury_rebalance"}
         (set (map :legacy-cell (vals kaname/cell-specs))))))

(deftest r0-gates-block-effects
  (let [plan (kaname/cell-plan :treasury-rebalance
                               {:treasury-epoch "2026-06"})]
    (is (= :blocked (:status plan)))
    (is (= [:council-ratification-baseline
            :kaname-baseline-review
            :charter-rider-scan-baseline
            :evidence-cid-baseline
            :non-fabrication-baseline
            :encrypted-pii-baseline
            :murakumo-only-inference-baseline
            :kotoba-only-substrate-baseline
            :transparent-force-audit-baseline
            :treasury-mirror-read-baseline
            :constitution-target-ratio-baseline
            :drift-threshold-baseline
            :governance-propose-only-baseline
            :seventy-two-hour-timelock-baseline
            :no-direct-fund-movement-baseline]
           (:missing-gates plan)))
    (is (empty? (:effects plan)))))

(deftest treasury-rebalance-is-proposal-only
  (let [plan (kaname/cell-plan :treasury-rebalance
                               {:attestations full-attestations
                                :treasury-epoch "2026-06"
                                :proposal-id "prop-001"})
        effect (first (:effects plan))]
    (is (= :ready (:status plan)))
    (is (= "com.etzhayyim.kaname.treasuryRebalanceProposal" (:collection effect)))
    (is (= false (get-in effect [:record :directFundMovement])))
    (is (= true (get-in effect [:record :governanceProposalOnly])))
    (is (= true (get-in effect [:record :timelockRequired])))))

(deftest taxation-is-internal-audit-only
  (let [plan (kaname/cell-plan :religious-corp-taxation
                               {:attestations full-attestations
                                :legal-counsel-cid "bafkreicounsel"
                                :council-cid "bafkreicouncil"})
        effect (first (:effects plan))]
    (is (= :ready (:status plan)))
    (is (= "com.etzhayyim.kaname.taxAuditView" (:collection effect)))
    (is (= true (get-in effect [:record :internalAuditOnly])))
    (is (= false (get-in effect [:record :stateTaxDischarged])))))

(deftest marriage-and-member-records-do-not-replace-civil-state
  (let [member (first (:effects (kaname/cell-plan :member-registry
                                                  {:attestations full-attestations
                                                   :member-did "did:example:member"})))
        marriage (first (:effects (kaname/cell-plan :religious-marriage
                                                    {:attestations full-attestations
                                                     :marriage-id "marriage-001"})))]
    (is (= false (get-in member [:record :civilRegistryReplacement])))
    (is (= false (get-in marriage [:record :civilMarriageReplacement])))
    (is (= true (get-in member [:record :religiousBoundaryOnly])))
    (is (= true (get-in marriage [:record :religiousBoundaryOnly])))))

(deftest land-donation-requires-imagery-and-successors
  (let [attestations (-> full-attestations
                         (dissoc :imagery-three-month-series-baseline)
                         (dissoc :successor-designation-baseline))
        plan (kaname/cell-plan :land-donation-processing
                               {:attestations attestations
                                :land-id "land-001"})]
    (is (= :blocked (:status plan)))
    (is (= [:imagery-three-month-series-baseline :successor-designation-baseline]
           (:missing-gates plan)))))

(deftest ethics-borderline-requires-council-path
  (let [attestations (dissoc full-attestations :borderline-council-deliberation-baseline)
        plan (kaname/cell-plan :ethics-content-classifier
                               {:attestations attestations
                                :request-id "ethics-001"})]
    (is (= :blocked (:status plan)))
    (is (= [:borderline-council-deliberation-baseline] (:missing-gates plan)))))

(deftest tithe-routing-requires-router-and-public-fund-receipt
  (let [attestations (-> full-attestations
                         (dissoc :tithe-router-tx-observed-baseline)
                         (dissoc :public-fund-receipt-baseline))
        plan (kaname/cell-plan :tithe-routing
                               {:attestations attestations
                                :payment-id "pay-001"})]
    (is (= :blocked (:status plan)))
    (is (= [:tithe-router-tx-observed-baseline :public-fund-receipt-baseline]
           (:missing-gates plan)))))

(deftest all-cell-plans-ready-when-attested
  (let [plans (kaname/all-cell-plans {:attestations full-attestations
                                      :request-id "req-001"
                                      :member-did "did:example:member"
                                      :land-id "land-001"
                                      :marriage-id "marriage-001"
                                      :payment-id "pay-001"
                                      :treasury-epoch "2026-06"
                                      :evidence-cid "bafkreievidence"
                                      :council-cid "bafkreicouncil"
                                      :legal-counsel-cid "bafkreicounsel"
                                      :encrypted-payload-cid "bafkreiencrypted"
                                      :proposal-id "prop-001"
                                      :computed-at "2026-06-29T00:00:00Z"})]
    (is (= (set (keys kaname/cell-specs)) (set (keys plans))))
    (is (every? #(= :ready (:status %)) (vals plans)))
    (is (= 8 (count (mapcat :effects (vals plans)))))))
