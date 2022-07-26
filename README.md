# http-crawler-spring-boot
Welcome to the `Http Crawler Spring boot` repository.

This repository holds a Java Springboot program that makes it easy to crawl two HTTP endpoints at the moment. It offers easy way to build and deploy the Springboot program on Kubernetes and monitor Prometheus format metrics (emitted by Springboot application) for application scaling and observability purposes.


What can I do with `http-crawler-spring-boot` repository?
--- 

You can use this repository as a sample design to run a Spring application on Kubernetes Container based environment with advanced usescases and best practices. Below are few features this repository offers,

- [x] Simple application written in Springboot that queries 2 URL's (https://httpstat.us/503 & https://httpstat.us/200) every one second.
- [x] Checks if external URL's are up (based on http status code 200) and returns response code.
- [x] Integrated with micrometer to emit Prometheus format metrics on path `/actuator/prometheus` using Prometheus Java library.

Getting Started
---

## prerequisites

- Docker
- Make
- Kubernetes Cluster
- kubectl
- Helm version 3
- Java 1.8 (only if Docker is not installed)

## Run `http-crawler-spring-boot` on local machine

    make docker-run

    # OR use below command if Docker is not installed, Java is configured

    make run

## Deploy `http-crawler-spring-boot` to Kubernetes Cluster

- Configure kubectl configuration pointing to a Kubernetes Cluster
- Deploy `http-crawler-spring-boot` using make

    ```
    make k8s-deploy
    ```

## Setup Infrastructure

- Create a Kubernetes Cluster
- Install Prometheus

    ```
    kubectl create namespace prometheus

    helm install prometheus prometheus-community/prometheus \
    --namespace prometheus \
    --set alertmanager.persistentVolume.storageClass="gp2" \
    --set server.persistentVolume.storageClass="gp2"

    kubectl port-forward -n prometheus deploy/prometheus-server 8080:9090
    ```

- Install Grafana

    ```
    mkdir ${HOME}/environment/grafana

    cat << EoF > ${HOME}/environment/grafana/grafana.yaml
    datasources:
      datasources.yaml:
        apiVersion: 1
        datasources:
        - name: Prometheus
          type: prometheus
          url: http://prometheus-server.prometheus.svc.cluster.local
          access: proxy
          isDefault: true
    EoF

    kubectl create namespace grafana

    helm install grafana grafana/grafana \
        --namespace grafana \
        --set persistence.storageClassName="gp2" \
        --set persistence.enabled=true \
        --set adminPassword='EKS!sAWSome' \
        --values ${HOME}/environment/grafana/grafana.yaml \
        --set service.type=LoadBalancer

    export ELB=$(kubectl get svc -n grafana grafana -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')

    echo "http://$ELB"

    kubectl get secret --namespace grafana grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo
    ```

- Deploy `http-crawler-spring-boot` on Kubernetes

    ```
    kubectl apply -f k8s-deployment.yml

    #OR

    make k8s-deploy
    ```

--- 

Questions or Something not working?

Please feel free to raise a [GitHub issue](https://github.com/saiteja313/http-crawler-spring-boot/issues)
