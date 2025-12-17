#!/usr/bin/env bash
set -euo pipefail

# coming -location /app/repogit4testv0/ \
#   -mode mineinstance \
#   -action INS \
#   -entitytype BinaryOperator \
#   -output /app/output

java -classpath /app/coming.jar fr.inria.coming.main.ComingMain \
  -location /app/repogit4testv0/ \
  -mode mineinstance \
  -action INS \
  -entitytype BinaryOperator \
  -output /app/output
