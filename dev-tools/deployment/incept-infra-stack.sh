#!/bin/bash -x

# XXX: Before run this
# Don't forget to set AWS_PROFILE with the respective entitled profile

declare -A AWS_STACK_PARAMS

__prompt_passwd() {

        local tpwd=''

        echo -n Password:
        read -s tpwd
        echo
        AWS_STACK_PARAMS[SubscriptorPwd]=$tpwd
}

__prompt_env() {

        local tenv=''

        echo -n Enviroment:
        read -s tenv
        echo
        AWS_STACK_PARAMS[SubscriptorEnv]=$tenv
}

__render_params() {

        local params=''

        for i in "${!AWS_STACK_PARAMS[@]}"; do
                if [[ -z $params ]]; then
                        params="ParameterKey=$i,ParameterValue=${AWS_STACK_PARAMS[$i]}"
                else
                        params="$params ParameterKey=$i,ParameterValue=${AWS_STACK_PARAMS[$i]}"
                fi
        done

        echo $params
}

# Deploys the subscriptor stack
__deploy_stack_cmd() {

        __prompt_env
        __prompt_passwd

        local temp="infra-stack.yaml"

        # Verification of presence
        if [[ ! -f $temp ]]; then
                echo "Cloudformation template not found"
                exit 1
        fi

        # Verification of content
        if [[ ! -s $temp ]]; then
                echo "Emptyness at Cloudformation template's content"
                exit 1
        fi

        local deploy_cmd=$(printf 'awslocal cloudformation create-stack --stack-name %s --template-body file://%s  --capabilities CAPABILITY_NAMED_IAM --parameters %s' "${1}" "${temp}" "$(__render_params)")

        echo $deploy_cmd
}

__deploy_stack_cmd $1
