#!/bin/bash -x

SUBSCRIPTOR="dummy"


__deployment_verification() {

    local verify_status=$(jq ".Stacks[0].StackStatus" \
	    <($(printf 'awslocal cloudformation describe-stacks --stack-name %s' "${1}"))  | sed -e 's/^"//' -e 's/"$//')

    [[ "CREATE_COMPLETE" == $verify_status ]]
}

__expected_queue() {

    local number_found=$(jq '.QueueUrls[0]' <(awslocal sqs list-queues --queue-name-prefix $1) | wc -l)
    [[ "1" -eq $number_found ]]
}

__expected_event_bus() {

    local number_found=$(jq '.EventBuses[0].Name' <(awslocal events list-event-buses --name-prefix  $1) | wc -l)
    [[ "1" -eq $number_found ]]
}

__expected_lambda() {

    local number_found=$(jq '.Code | length' <(awslocal lambda get-function --function-name $1))
    [[ "1" -eq $number_found ]]
}

sleep 6

__deployment_verification $SUBSCRIPTOR
if [[ $? != 0 ]]; then
    echo "Infra stack deployment failed"
    exit 1
fi

# Verification for expected buckets
{
    expected_buckets=(cfdi-datalake cfdi-datares)
    for b in "${expected_buckets[@]}"; do
        awslocal s3 ls s3://${b}-${SUBSCRIPTOR}
    done
}

# Verification for expected policies
{
    expected_policies=(cfdi-data-access cfdi-inqueue-access)
    for b in "${expected_policies[@]}"; do
        grep "${b}-${SUBSCRIPTOR}" <(awslocal iam list-user-policies --output text --user-name ${SUBSCRIPTOR})
        if [[ $? != 0 ]]; then
            echo "Expected policy was not found"
            exit 1
        fi
    done
}

__expected_queue "cfdi-inqueue-${SUBSCRIPTOR}"
if [[ $? != 0 ]]; then
    echo "Expected queue was not found"
    exit 1
fi

__expected_event_bus "cfdi-eventbus-${SUBSCRIPTOR}"
if [[ $? != 0 ]]; then
    echo "Expected event bus was not found"
    exit 1
fi

 __expected_lambda "cfdi-factoryfunc-${SUBSCRIPTOR}"
if [[ $? != 0 ]]; then
    echo "Expected lambda was not found"
    exit 1
fi
