(ns jobs-api.repo)

(defonce db (atom {}))

(defn status [] "OK")

(defn jobs [] @db)

(defn store [id job]
  (swap! db assoc id job))

(defn delete [id]
    (swap! db dissoc id))
