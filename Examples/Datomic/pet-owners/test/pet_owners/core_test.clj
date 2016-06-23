(ns pet-owners.core-test
  (:require [expectations :refer :all]
            [pet-owners.core :refer :all]
            [datomic.api :as d]))

(defn create-empty-in-memory-db []
  (let [uri "datomic:mem://pet-owners-test-db"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/Datomic/schema.edn")]
      (d/transact conn schema)
      conn)))


;; This is my test to add one owner to find an owner

(expect #{["John"]}
        (with-redefs [conn (create-empty-in-memory-db)]
        (do
          (add-pet-owner "John")
          (find-all-pet-owners))))

;;Adding multiple owners should allow us to find all those owners

(expect #{["John"] ["Paul"] ["George"]}
        (with-redefs [conn (create-empty-in-memory-db)]
        (do
          (add-pet-owner "John")
          (add-pet-owner "Paul")
          (add-pet-owner "George")
          (find-all-pet-owners))))


;; Adding one owner with one pet should allow us to find that pet for the owner

(expect #{["Salt"]}
        (with-redefs [conn (create-empty-in-memory-db)]
        (do
          (add-pet-owner "John")
          (add-pet "Salt" "John")
          (find-pets-for-owners "John"))))

;; Adding multiple owners and pets should allow us to find the pets for a particular owner

(expect #{["Martha"] ["Jet"]}
        (with-redefs [conn (create-empty-in-memory-db)]
        (do
          (add-pet-owner "John")
          (add-pet "Salt" "John")
          (add-pet "Pepper" "John")
          (add-pet-owner "Paul")
          (add-pet "Martha" "Paul")
          (add-pet "Jet" "Paul")
          (find-pets-for-owners "Paul"))))
