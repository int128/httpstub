DOCKER_REPOSITORY := ghcr.io/int128/slack-docker

# determine the version from ref
ifeq ($(GITHUB_REF), refs/heads/master)
  VERSION := latest
else
  VERSION ?= $(notdir $(GITHUB_REF))
endif

.PHONY: docker-build
docker-build: Dockerfile
	docker buildx build . \
		--output=type=image,push=false \
		--cache-from=type=local,src=/tmp/buildx \
		--cache-to=type=local,mode=max,dest=/tmp/buildx.new
	rm -fr /tmp/buildx
	mv /tmp/buildx.new /tmp/buildx

.PHONY: docker-build-push
docker-build-push: Dockerfile
	docker buildx build . \
		--build-arg=VERSION=$(VERSION) \
		--tag=$(DOCKER_REPOSITORY):$(VERSION) \
		--cache-from=type=local,src=/tmp/buildx
		--push
