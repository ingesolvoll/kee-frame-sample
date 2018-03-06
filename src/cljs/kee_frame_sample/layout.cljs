(ns kee-frame-sample.layout
  (:require [re-frame.core :refer [subscribe dispatch reg-sub reg-event-fx]]
            [kee-frame.core :as k]
            [cljs-react-material-ui.icons :as ic]
            [cljs-react-material-ui.reagent :as ui]
            [cljs-react-material-ui.core :refer [get-mui-theme color]]
            [reagent.core :as r]
            [kee-frame-sample.views :as views]))

(defn drawer []
  [ui/drawer
   {:width             250
    :docked            true
    :open              true
    :on-request-change #(dispatch [:toggle-drawer false])}
   [:div.logo]
   [ui/menu-item
    {:href (k/path-for :live)}
    "Today's matches"]
   [ui/divider]
   (map (fn [{:strs [id caption]}]
          ^{:key caption}
          [ui/menu-item
           {:href (k/path-for :league :id id :tab :table)}
           caption])
        @(subscribe [:leagues]))])

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
  [ui/app-bar {:style              {:font-family "Broader View"
                                    :color       :white}
               :title              (r/as-element [:a.title-link {:href (k/path-for :live)} "Live football"])
               :icon-element-right (r/as-element
                                     [:a {:href "settings-todo"}
                                      [ic/action-settings]])}])

(defn main-panel []
  [ui/mui-theme-provider
   {:mui-theme (mui-theme)}
   [:div {:style {:padding-left 250}}
    [app-bar "Title" "url here"]
    [drawer]
    [:div.row.around-xs
     [ui/paper {:class "col-xs-11 col-md-7 col-lg-7 mar-top-20"}
      [views/dispatch-main]]]]])