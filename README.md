# Azure OpenAI Project

### Setup
Create a .env file at the project root with following details



<code>azureOpenaiKey=&lt;your kep></code><br>
<code>endpoint=&lt;end point for your region></code><br>
<code>deploymentOrModelId=&lt;your model id></code>

Important:<br>
No spaces or double quotes in the env properties. Will create problem with docker run


### Build Deploy
<code>mvn clean install</code>

<code>docker build -t azureopenai .</code>

<code>docker run --env-file .env -p 9090:8080 azureopenai   
</code>

### Access
http://localhost:9090/api/openai

