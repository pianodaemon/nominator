BASE            := $(shell /bin/pwd)
OPERATIONAL     := $(BASE)
DEPLOY_STUFF    := $(BASE)/dev-tools/deployment

all:    deploy_infra

deploy_infra:   render_infra
	(cd  $(DEPLOY_STUFF) && ./incept-infra-stack.sh "${SUBSCRIPTOR}");

render_infra:   compile_fiscal
	(export OPERATIONAL=$(OPERATIONAL) &&          \
	mv $(OPERATIONAL)/fiscal/factory/build/distributions/factory-1-SNAPSHOT.zip  $(DEPLOY_STUFF)/ &&    \
	aws cloudformation package --template-file  $(DEPLOY_STUFF)/cf-template.yaml                    \
	--s3-bucket cfdi-datalake-${SUBSCRIPTOR} --output-template-file $(DEPLOY_STUFF)/infra-stack.yaml);

compile_fiscal:
	(export OPERATIONAL=$(OPERATIONAL) &&          \
	cd $(OPERATIONAL)/fiscal && ./gradlew build);
