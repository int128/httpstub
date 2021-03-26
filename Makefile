DOCKER_REPOSITORY := ghcr.io/int128/slack-docker

# extract version from tag or default to latest
ifeq ($(dir $(GITHUB_REF)), refs/tags/)
  VERSION := $(notdir $(GITHUB_REF))
else
  VERSION := latest
endif

.PHONY: docker-build
docker-build: Dockerfile
	docker buildx build . \
		--output=type=image,push=false \
		--cache-from=type=local,src=/tmp/buildx

.PHONY: docker-build-push
docker-build-push: Dockerfile
	docker buildx build . \
		--push \
		--tag=$(DOCKER_REPOSITORY):$(VERSION) \
		--cache-from=type=local,src=/tmp/buildx \
		--cache-to=type=local,mode=max,dest=/tmp/buildx.new
	rm -fr /tmp/buildx
	mv /tmp/buildx.new /tmp/buildx
