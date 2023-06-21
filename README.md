# Frank OTEL Instrumentation

Provides instrumentation modules for the OpenTelemetry Agent to instrument a Frank Framework instance.


# Demo

- Start Zipkin with the following command`docker run -d -p 9411:9411 openzipkin/zipkin`, open http://localhost:9411/zipkin/ to access the UI.
- Download the OpenTelemetry Agent to a common folder.
- Build Frank OTEL Instrumentation module and place .jar in a common folder.
- Add the following JVM arguments: `-javaagent:path/to/common-folder/opentelemetry-javaagent.jar -Dotel.traces.exporter=zipkin -Dotel.resource.attributes=service.name=Frank -Dotel.javaagent.extensions=path/to/common-folder/frank-otel-instrumentation-1.0-SNAPSHOT.jar`
- Start JVM,  trigger some Adapters and check Zipkin!

![frank-otel-instrumentation-example](simple-example.png)