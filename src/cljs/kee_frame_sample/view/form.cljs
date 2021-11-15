(ns kee-frame-sample.view.form
  (:require [kee-frame.fsm.beta :as fsm]
            [re-frame.core :as f]
            [reagent.core :as r]))

(def validation-fsm
  {:id      :validation
   :initial ::clean
   :states  {::clean   {:on {::edit-started ::editing}}
             ::editing {:on {::edit-ended ::dirty}}
             ::dirty   {:on {::edit-started ::editing}}}})

(defn form []
  (fsm/with-fsm validation-fsm
    (r/with-let [state       (f/subscribe [::fsm/state :validation])
                 text        (r/atom "")
                 update-text #(reset! text (-> % .-target .-value))]
      [:div
       [:input {:type      :text
                :style     {:border-color (when (and (= @state ::dirty)
                                                     (not (seq @text)))
                                            "red")}
                :on-change #(do (f/dispatch [:transition-fsm :validation ::edit-started])
                                (update-text %))
                :on-blur   #(f/dispatch [:transition-fsm :validation ::edit-ended])}]])))
