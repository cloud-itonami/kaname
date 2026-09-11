(ns kaname.methods.social
  "Actor adapter over the shared social publication membrane."
  (:require [etzhayyim.social.publication :as publication]))

(def config {:actor-id "kaname" :display-name "要 — Kaname"})
(def DISCLAIMER (publication/disclaimer config))

(defn draft-observation-post
  ([subject body sources]
   (publication/draft-observation-post config subject body sources))
  ([subject body sources author]
   (publication/draft-observation-post config subject body sources author)))

(defn build-live [& args]
  (apply publication/build-live config args))
