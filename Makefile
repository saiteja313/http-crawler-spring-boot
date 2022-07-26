
#VERSION is the commit id
VERSION ?= $(shell git describe --tags --always --dirty || echo "unknown")

IMAGE = saiteja313/http-crawler-spring-boot
IMAGE_NAME = $(IMAGE):$(VERSION)

all: docker-run docker-push k8s-deploy

build:
	@echo "Maven build ..."
	mvn clean package

run: build
	@echo "Running src/app.py. \n Requires Python 3.7.4 or greater. \n Tip: You can run app inside a docker container using 'make docker-run' \n"
	java -jar "target/http-crawler-0.0.1-SNAPSHOT.jar"

docker-build: build
	docker build \
		-t "$(IMAGE_NAME)" \
		.
	@echo "Build complete for Docker image \"$(IMAGE_NAME)\""

docker-run:	docker-build
	@echo "Executing docker run -it \"$(IMAGE_NAME)\""
	docker run -it "$(IMAGE_NAME)"

docker-runt: docker-build
	@echo "Executing docker run -it \"$(IMAGE_NAME)\""
	docker run -it --entrypoint "" "$(IMAGE_NAME)" sh

# tag commit id as latest tag and push to docker hub.
docker-push:
	docker tag "$(IMAGE_NAME)" "$(IMAGE)":latest
	docker push "$(IMAGE_NAME)"
	docker push "$(IMAGE_NAME)"
	@echo "Push successful for \"$(IMAGE_NAME)\""
	docker push "$(IMAGE)":latest
	@echo "Push successful for \"$(IMAGE_NAME)\":latest"
	@echo "push complete for Docker image \"$(IMAGE_NAME)\""

monitor:
	watch -n 1 kubectl get pods

logs:
	kubectl logs -f deployment/http-crawler-spring-boot

k8s-deploy:
	kubectl delete -f k8s-deployment.yml --ignore-not-found=true
	kubectl apply -f k8s-deployment.yml
	kubectl get pods -l app=http-crawler-spring-boot
	kubectl wait pod --for=condition=Ready -l app=http-crawler-spring-boot
	kubectl get pods -l app=http-crawler-spring-boot
	kubectl logs deployment/http-crawler-spring-boot

clean:
	rm -rf target/
	@echo "clean completed"