# Start from the latest TimescaleDB image
FROM timescale/timescaledb:latest-pg16

# Switch to the root user
USER root

# Create a script to create the extension
RUN echo "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";" > /docker-entrypoint-initdb.d/create_extension.sql

# Switch back to the postgres user
USER postgres

