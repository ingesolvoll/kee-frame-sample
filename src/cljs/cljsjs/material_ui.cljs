(ns cljsjs.material-ui
  (:require ["material-ui" :as mui]
            ["material-ui/svg-icons" :as icons]
            ["material-ui/styles" :as styles]
            ["react" :as react]))

(js/goog.exportSymbol "MaterialUI" mui)
(js/goog.exportSymbol "MaterialUISvgIcons" icons)
(js/goog.exportSymbol "MaterialUIStyles" styles)
(js/goog.exportSymbol "React" react)
