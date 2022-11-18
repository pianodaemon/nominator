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

__expected_queue "cfdi-inqueue-${SUBSCRIPTOR}"
if [[ $? != 0 ]]; then
    echo "Expected queue was not found"
    exit 1
fi
