Run locally with `gradle run`

Deploy with .\gradlew.bat appengineDeploy

Can deploy with docker to private registry using
`docker tag initiative-tracker <registry-url>/initiative-tracker` and
`docker push <registry-url>/initiative-tracker`
If that registry is insecure use https://docs.docker.com/registry/insecure/
