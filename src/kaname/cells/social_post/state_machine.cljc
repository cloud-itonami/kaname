(ns kaname.cells.social-post.state-machine
  "Actor adapter over the shared publication state machine."
  (:require [etzhayyim.social.publication :as publication]
            [kaname.methods.social :as social]))

(def disclaimer publication/disclaimer-prefix)
(def phase-init publication/phase-init)
(def phase-drafted publication/phase-drafted)
(def phase-refused publication/phase-refused)
(def state-defaults publication/state-defaults)

(defn transition-to-drafted [state]
  (publication/transition-to-drafted social/config state))
