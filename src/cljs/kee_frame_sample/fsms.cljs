(ns kee-frame-sample.fsms
  (:require [kee-frame.fsm :as fsm]))

(defn league-fsm [id]
  {:id    [:league-fsm id]
   :start ::loading-table
   :stop  ::loaded
   :fsm   {::loading-table           {[::fsm/on-enter]            {:dispatch [[:league/load-table id]]}
                                      [:league/table-received id] {:to ::loading-fixtures}
                                      [:default-on-failure]       {:to ::loading-table-failed}}
           ::loading-fixtures        {[::fsm/on-enter]               {:dispatch [[:league/load-fixtures id]]}
                                      [:league/fixtures-received id] {:to ::loaded}
                                      [:default-on-failure]          {:to ::loading-fixtures-failed}}
           ::loading-table-failed    {[::fsm/after 10000] {:to ::loading-table}}
           ::loading-fixtures-failed {[::fsm/after 10000] {:to ::loading-fixtures}}}})