(ns lt.plugins.html
  (:require [lt.object :as object]
            [lt.objs.eval :as eval]
            [lt.objs.editor :as ed]
            [lt.objs.command :as cmd]
            [lt.objs.editor :as editor]
            [lt.objs.editor.pool :as pool]
            [lt.objs.clients :as clients]
            [lt.util.dom :refer [$ append]])
  (:require-macros [lt.macros :refer [behavior defui]]))

(defn start-browser [path]
  (cmd/exec! :add-browser-tab (str "file://" path)))

(behavior ::on-eval
          :triggers #{:eval
                      :eval.one}
          :reaction (fn [editor]
                      (eval/get-client! {:command :editor.eval.html
                                         :origin editor
                                         :create (fn [] (start-browser (-> @editor :info :path)))
                                         :info (:info @editor)})
                      (object/raise editor :save)))

(behavior ::eval-on-save
          :triggers #{:save}
          :reaction (fn [editor]
                      (when (and (-> @editor :client :default)
                                 (not (clients/placeholder? (-> @editor :client :default))))
                        (object/raise html-lang :eval! {:origin editor
                                                        :info (assoc (@editor :info)
                                                                :code (ed/->val (:ed @editor)))}))))

(behavior ::eval!
          :triggers #{:eval!}
          :reaction (fn [this event]
                      (let [{:keys [info origin]} event]
                        (clients/send (eval/get-client! {:command :editor.eval.html
                                                         :origin origin
                                                         :info info})
                                      :editor.eval.html
                                      info
                                      :only origin))))

(object/object* ::html-lang
                :tags #{:html.lang}
                :behaviors [::eval!]
                :triggers #{:eval!})

(def html-lang (object/create ::html-lang))

(cmd/command {:command :html.jump-to-matching-tag
              :desc "HTML: Jump to matching tag"
              :exec (fn []
                      (let [cm (editor/->cm-ed (pool/last-active))
                            _ (js/CodeMirror.commands.toMatchingTag cm)
                            cursor (.getCursor cm)]
                        ;; Decrement to keep cursor in tag and enable jumping back and forth between matching tags
                        (set! (.-ch cursor) (dec (.-ch cursor)))
                        ;; Unselect selection. Not a problem in CM demo
                        (.setSelection cm cursor cursor)))})
