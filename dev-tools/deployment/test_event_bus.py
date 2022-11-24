import boto3
import json

subscriptor="medica"
client = boto3.client('events')
_bus = 'cfdi-eventbus-{}'.format(subscriptor)
payload=dict([("kind", "fac"), ("req", "S3://mocos.json")])
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
