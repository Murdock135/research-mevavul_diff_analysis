docker container stop -f $(docker ps -aq --filter "name=megavul-analysis") 2>/dev/null || true
docker container rm -f $(docker ps -aq --filter "name=megavul-analysis") 2>/dev/null || true

# find all research-lm_error_analysis-megavul-analysis images and delete them
docker image rm -f $(docker images -q research-lm_error_analysis-megavul-analysis) 2>/dev/null || true
