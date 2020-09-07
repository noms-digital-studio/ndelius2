.PHONY: all ecr-login sbt-clean sbt-assembly build tag test push clean-remote clean-local

aws_region := eu-west-2
image := hmpps/new-tech-web
# fixes issue with compatible issue with v12.18.3 and graceful-fs webjar
sbt_builder_image := circleci/openjdk@sha256:0ab7db4ecdc5966baaa44f64075725c22a53dc63922492c26cabd98b7c961ae0
# newtechweb_version should be passed from command line
all:
	$(MAKE) ecr-login
	$(MAKE) sbt-clean
	$(MAKE) sbt-assembly
	$(MAKE) build
	$(MAKE) push
	$(MAKE) clean-remote
	$(MAKE) clean-local

sbt-clean: build_dir = $(shell pwd)
sbt-clean:
	$(Info Running sbt clean task)
	docker run --rm -v $(build_dir):/build -w /build $(sbt_builder_image) bash -c "sbt -v clean ;"

sbt-assembly: build_dir = $(shell pwd)
sbt-assembly:
	$(Info Running sbt assembly task)
	docker run --rm -e CIRCLE_BUILD_NUM=${jenkins_build} -v $(build_dir):/home/circleci/build -w /home/circleci/build $(sbt_builder_image) bash -c "sudo npm install; sudo chmod -R 0777 /home/circleci/build; sbt -v 'set test in assembly := {}' 'set target in assembly := file(\"/home/circleci/build/target/scala-2.12/\")' assembly; sudo chmod -R 0777 project/ target/"

ecr-login:
	$(shell aws ecr get-login --no-include-email --region ${aws_region})
	aws --region $(aws_region) ecr describe-repositories --repository-names "$(image)" | jq -r .repositories[0].repositoryUri > ecr.repo

build: ecr_repo = $(shell cat ./ecr.repo)
build:
	$(info Build of repo $(ecr_repo))
	docker build -t $(ecr_repo) --build-arg NEWTECHWEB_VERSION=${newtechweb_version}  -f docker/Dockerfile.aws .

tag: ecr_repo = $(shell cat ./ecr.repo)
tag:
	$(info Tag repo $(ecr_repo) $(newtechweb_version))
	docker tag $(ecr_repo) $(ecr_repo):$(newtechweb_version)


push: ecr_repo = $(shell cat ./ecr.repo)
push:
	docker tag  ${ecr_repo} ${ecr_repo}:${newtechweb_version}
	docker push ${ecr_repo}:${newtechweb_version}

clean-remote: untagged_images = $(shell aws ecr list-images --region $(aws_region) --repository-name "$(image)" --filter "tagStatus=UNTAGGED" --query 'imageIds[*]' --output json)
clean-remote:
	if [ "${untagged_images}" != "[]" ]; then aws ecr batch-delete-image --region $(aws_region) --repository-name "$(image)" --image-ids '${untagged_images}' || true; fi

clean-local: ecr_repo = $(shell cat ./ecr.repo)
clean-local:
	-docker rmi ${ecr_repo}:latest
	-docker rmi ${ecr_repo}:${newtechweb_version}
	-rm -f ./ecr.repo
	-rm -f ./src/test/resources/client*.key 
	-rm -f ./src/test/resources/client.pub