#!/bin/bash

if [ ! -f $(pwd)/lib/otelcol-contrib ]; then
    wget https://github.com/open-telemetry/opentelemetry-collector-releases/releases/download/v0.108.0/otelcol-contrib_0.108.0_darwin_arm64.tar.gz ## for macos arm64 chips, change as needed
    gunzip otelcol-contrib_0.108.0_darwin_arm64.tar.gz
    tar -xvf otelcol-contrib_0.108.0_darwin_arm64.tar
    mv otelcol-contrib_0.108.0_darwin_arm64 lib/otelcol
    ls -ltr
fi
if [ -z "${LS_TOKEN}" ]; then
    echo "Please set the LS_TOKEN environment variable"
    echo "LS_TOKEN=<your access token>"
    exit 1
fi
# Start the OpenTelemetry Collector with the configuration file
echo "starting otel collector with config: "
cat $(pwd)/conf/config.yaml
./lib/otelcol-contrib --config ./conf/config.yaml
