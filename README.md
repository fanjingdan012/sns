# SNS
tool for integration with SNS platforms, playing with access tokens

## Package it
- rename configuration_sample.properties to configuration.properties and fill in the app id and app secrets
- `mvn package`

## Run it
- unzip `/target/sns-0.0.1-SNAPSHOT-distribution.zip`
- `java -jar sns-0.0.1-SNAPSHOT.jar -h` for help
> usage: Options  
>  -auth      oauth, please copy code and use -g to exchange access token  
>  -c <arg>   channel: twitter, facebook, instagram, pinterest, mailchimp  
>  -g <arg>   get access token, following with code  
>  -h         help  
>  -v <arg>   validate access token,following with access token to validate  


