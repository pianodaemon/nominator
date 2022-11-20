import boto3
import json

client = boto3.client('events')
_bus = 'cfdi-eventbus-dummy'
#_bus = "test-bus"
payload=dict([("username", "j4nusx"), ("city", "SJO"), ("age", "42")])
detailJsonString = json.dumps(payload)

entry = {
    'Source':'cfdi-issuer-client',
    'DetailType':'user-preferences',
    'Detail': '{}'.format(detailJsonString),
    'EventBusName': '{}'.format(_bus),
}
print(entry)
response = client.put_events(
    Entries=[entry]
)

print(response)
