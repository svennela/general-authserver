README for general_authserver
==========================

CURL

request oauth token

Token for different clients...


Web

curl -X POST -vu webclient:mySecretOAuthSecret http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=admin&username=admin&grant_type=password&scope=read%20write&client_secret=mySecretOAuthSecret&client_id=webclient"

Mobile

curl -X POST -vu mobileclient:mySecretOAuthSecret http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=admin&username=admin&grant_type=password&scope=read%20write&client_secret=mySecretOAuthSecret&client_id=mobileclient"


Admin

curl -X POST -vu adminclient:AdminsAreTheBest http://localhost:8080/oauth/token -H "Accept: application/json" -d "password=admin&username=admin&grant_type=password&scope=read%20write&client_secret=AdminsAreTheBest&client_id=adminclient"





request protected resource with issued token.

curl http://localhost:8080/api/account -H "Authorization: Bearer aca1af1a-c478-4f3a-bb45-94c2cea28866"

refresh an expired token

curl -X POST -vu general_authserverapp:mySecretOAuthSecret http://localhost:8080/oauth/token -H "Accept: application/json" -d "grant_type=refresh_token&refresh_token=da6a1913-ca6c-41db-80c1-8fde31a9d070&client_secret=mySecretOAuthSecret&client_id=general_authserverapp"






