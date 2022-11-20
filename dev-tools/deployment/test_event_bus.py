import boto3
import json

subscriptor="medica"
client = boto3.client('events')
_bus = 'cfdi-eventbus-{}'.format(subscriptor)
payload=dict([("username", "j4nusx"), ("city", "SJO"), ("age", "42")])
detailJsonString = json.dumps(payload)

entry = {
    'Source':'{}'.format(subscriptor),
    'DetailType':'cfdi-issuer-client',
    'Detail': '{}'.format(detailJsonString),
    'EventBusName': '{}'.format(_bus),
}
print(entry)
response = client.put_events(
    Entries=[entry]
)

print(response)
