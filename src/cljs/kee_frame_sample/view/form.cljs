(ns kee-frame-sample.view.form
  (:require [re-statecharts.core :as fsm]
            [re-frame.core :as f]
            [reagent.core :as r]))

(def validation-fsm
  {:id      :validation
   :initial ::clean
   :states  {::clean   {:on {::edit-started ::editing}}
             ::editing {:on {::edit-ended ::dirty}}
             ::dirty   {:on {::edit-started ::editing}}}})

(defn form []
  (fsm/with-fsm [state validation-fsm]
    (r/with-let [text        (r/atom "")
                 update-text #(reset! text (-> % .-target .-value))]
      [:div
       (fsm/match-state @state
                        ::editing [:div "User is editing..."]
                        ::clean [:div "No changes made yet"]
                        ::dirty [:div
                                 "Form has been modified and is "
                                 (if (seq @text)
                                   "valid"
                                   [:span {:style {:color :red}} "invalid"])]
                        nil [:div])
       [:input {:type      :text
                :on-change #(update-text %)
                :on-focus  #(f/dispatch [::fsm/transition :validation ::edit-started])
                :on-blur   #(f/dispatch [::fsm/transition :validation ::edit-ended])}]])))
