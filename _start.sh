
# Ask user if they want to rebuild the Docker image
read -p "Do you want to rebuild the Docker image? (y/n): " REBUILD
if [[ "$REBUILD" == "y" || "$REBUILD" == "Y" ]]; then
    docker compose up -d --force-recreate --build
else
    docker compose up -d
fi
sleep 2
docker compose exec primevul-analysis bash