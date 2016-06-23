(defproject pet-owners "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.datomic/datomic-free "0.8.4159"]
                 [expectations "1.4.53"]
                 [lein-autoexpect "1.0"]]
  :datomic {:schemas ["resources/Datomic" ["schema.edn"]]}
  :profiles {:dev
             {:datomic {:config "resources/Datomic/free-transactor-template.properties"
                        :db-uri "datomic:free://localhost:4334/pet-owners-db"}}})

