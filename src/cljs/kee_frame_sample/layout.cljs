(ns kee-frame-sample.layout
  (:require [re-frame.core :refer [subscribe dispatch]]
            [kee-frame.core :as k]
            [kee-frame.fsm.alpha :as fsm]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [reagent.core :as r]
            [kee-frame-sample.controller.leagues :as leagues-controller]
            [breaking-point.core :as bp]))

(defn drawer []
  (let [leagues-fsm-state (subscribe [::fsm/state :leagues])]
    [ui/drawer
     {:width             250
      :docked            @(subscribe [::bp/large-monitor?])
      :open              (or @(subscribe [:drawer-open?])
                             @(subscribe [::bp/large-monitor?]))
      :on-request-change #(dispatch [:toggle-drawer false])}
     [:div.logo]
     [ui/menu-item
      {:href (k/path-for [:live])}
      "Today's matches (" @(subscribe [:live-match-count]) ")"]
     [ui/divider]
     (if (#{::leagues-controller/loading-failed} @leagues-fsm-state)
       [ui/menu-item
        {:on-click #(dispatch [:leagues/transition :leagues/retry])}
        "Retry leagues loading"]
       (map (fn [{:keys [id name]}]
              ^{:key name}
              [ui/menu-item
               {:href (k/path-for [:league {:id id :tab "table"}])}
               name])
            @(subscribe [:leagues])))]))

(defn mui-theme []
  (get-mui-theme
   {:font-family "Avenir Next, sans-serif"
    :palette     {:primary1-color       (color :blue500)
                  :primary2-color       (color :green400)
                  :primary3-color       (color :green400)
                  :accent1-color        (color :pinkA200)
                  :accent2-color        (color :grey100)
                  :accent3-color        (color :grey500)
                  :text-color           (color :darkBlack)
                  :alternate-text-color (color :white)
                  :canvas-color         (color :white)
                  :border-color         (color :grey300)
                  :picker-header-color  (color :cyan500)}}))

(defn app-bar []
  [ui/app-bar {:id                            :app-bar
               :style                         {:font-family "Broader View"
                                               :color       :white}
               :title                         (r/as-element
                                               [:a.title-link {:href (k/path-for [:live])} "Live football"])
               :show-menu-icon-button         (not @(subscribe [:drawer-open?]))
               :on-left-icon-button-touch-tap #(dispatch [:toggle-drawer true])}])

(defn main-panel [main]
  [ui/mui-theme-provider
   {:mui-theme (mui-theme)}
   [:div {:style {:padding-left 0}}                         ;; (if @(subscribe [:drawer-open?]) 250 0)
    [app-bar]
    [drawer]
    [:div.row.around-xs
     [ui/paper {:class "col-xs-11 col-md-7 col-lg-7 mar-top-20 pad-top-10"}
      main]]]])