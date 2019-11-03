(ns jobs-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [jobs-api.repo :as repo]))

(defn status [request]
  (ring-resp/response
   {:status "Jobs API is online"
    :db (repo/status)
    :host (:server-name request)
    :status-page (route/url-for ::status)
    :clojure-version (clojure-version)}))

(defn list-jobs [request]
  (ring-resp/response (repo/jobs)))

(defn create-jobs [request]
  (let [body (get-in request [:json-params])
        new-id (str (java.util.UUID/randomUUID))
        company (:company body)
        title (:title body)
        description (:description body)]
    (if (and company title description)
      (do
        (repo/store new-id
                    {:company company
                     :title title
                     :description description})
        (ring-resp/response (repo/jobs)))
      (ring-resp/bad-request {:error "New jobs should be a JSON entity with company, title and description fields"}))))

(defn delete-job [request]
  (let [id (get-in request [:path-params :id])]
    (if (find (repo/jobs) id)
      (do
        (repo/delete id)
        (ring-resp/response (repo/jobs)))
      (ring-resp/bad-request {:error "Job id not found"}))))

;; Interceptors
(def common-interceptors [(body-params/body-params)
                          http/json-body])

;; Routes
(def routes #{["/status" :get (conj common-interceptors `status)]
              ["/jobs"  :get (conj common-interceptors `list-jobs)]
              ["/jobs"  :post (conj common-interceptors `create-jobs)]
              ["/jobs/:id" :delete (conj common-interceptors `delete-job)]})

;; Consumed by jobs-api.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; ::http/interceptors []
              ::http/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::http/allowed-origins ["scheme://host:port"]

              ;; Tune the Secure Headers
              ;; and specifically the Content Security Policy appropriate to your service/application
              ;; For more information, see: https://content-security-policy.com/
              ;;   See also: https://github.com/pedestal/pedestal/issues/499
              ;;::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
              ;;                                                          :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
              ;;                                                          :frame-ancestors "'none'"}}

              ;; Root for resource interceptor that is available by default.
              ::http/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ;;  This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
              ::http/type :jetty
              ;;::http/host "localhost"
              ::http/port 8080
              ;; Options to pass to the container (Jetty)
              ::http/container-options {:h2c? true
                                        :h2? false
                                        ;:keystore "test/hp/keystore.jks"
                                        ;:key-password "password"
                                        ;:ssl-port 8443
                                        :ssl? false
                                        ;; Alternatively, You can specify you're own Jetty HTTPConfiguration
                                        ;; via the `:io.pedestal.http.jetty/http-configuration` container option.
                                        ;:io.pedestal.http.jetty/http-configuration (org.eclipse.jetty.server.HttpConfiguration.)
                                        }})
