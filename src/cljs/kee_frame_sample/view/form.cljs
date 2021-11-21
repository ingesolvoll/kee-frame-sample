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

(def global-validation-fsm
  (-> validation-fsm
      (with-meta {::fsm/open? true})
      (assoc :id :validation-open)))

(f/reg-event-fx ::edit-started (constantly nil))
(f/reg-event-fx ::edit-ended (constantly nil))

(defn form-state [state text]
  (fsm/match-state @state
                   ::editing [:div "User is editing..."]
                   ::clean [:div "No changes made yet"]
                   ::dirty [:div
                            "Form has been modified and is "
                            (if (seq @text)
                              "valid"
                              [:span {:style {:color :red}} "invalid"])]
                   nil [:div]))

(defn form []
  [:div
   "A form using local events mode"
   (fsm/with-fsm [state validation-fsm]
     (r/with-let [text        (r/atom "")
                  update-text #(reset! text (-> % .-target .-value))]
       [:div
        [form-state state text]
        [:input {:type      :text
                 :on-change #(update-text %)
                 :on-focus  #(f/dispatch [::fsm/transition :validation ::edit-started])
                 :on-blur   #(f/dispatch [::fsm/transition :validation ::edit-ended])}]
        [:button {:on-click #(f/dispatch [::fsm/restart :validation])} "Reset input FSM"]]))

   "A form using global events mode"
   (fsm/with-fsm [state global-validation-fsm]
     (r/with-let [text        (r/atom "")
                  update-text #(reset! text (-> % .-target .-value))]
       [:div
        [form-state state text]
        [:input {:type      :text
                 :on-change #(update-text %)
                 :on-focus  #(f/dispatch [::edit-started :validation-open])
                 :on-blur   #(f/dispatch [::edit-ended :validation-open])}]
        [:button {:on-click #(f/dispatch [::fsm/restart :validation-open])} "Reset input FSM"]]))])
