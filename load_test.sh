#!/bin/bash

for i in {1..100}
do
    r=$(curl -s http://localhost:8080)
    sleep 1
    echo "Request $i: $r"
    
done