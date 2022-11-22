ECHO = /bin/echo

BASE            := $(shell /bin/pwd)
OPERATIONAL     := $(BASE)
MACROS_INCLUDE  := make.macros
SUBSCRIPTOR     := dummy
DEPLOY_STUFF    := $(BASE)/dev-tools/deployment

all:    deploy_fiscal

deploy_fiscal:  compile_fiscal
        (export MACROS_INCLUDE=$(MACROS_INCLUDE)  &&  \
        export OPERATIONAL=$(OPERATIONAL) &&          \
        mv $(OPERATIONAL)/fiscal/factory/build/distributions/factory-1-SNAPSHOT.zip  $(DEPLOY_STUFF)/ &&    \
        aws cloudformation package --template-file  $(DEPLOY_STUFF)/sam2cf-template.yaml                    \
        --s3-bucket cfdi-datalake-$(SUBSCRIPTOR) --output-template-file $(DEPLOY_STUFF)/infra-stack.yaml);


compile_fiscal:
        (export MACROS_INCLUDE=$(MACROS_INCLUDE)  &&  \
        export OPERATIONAL=$(OPERATIONAL) &&          \
        cd $(OPERATIONAL)/fiscal && ./gradlew build);
