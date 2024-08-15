# HTTP Header Server

## Running

1. `kotlinc Main.kt -include-runtime -d example.jar` to generate .jar.
2. `java -jar example.jar` to execute jar.
3. In separate terminal, enter `curl 127.0.0.1:8888` to call server.
4. You should receive the HTTP headers in response.