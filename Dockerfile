FROM confluentinc/cp-kafka-connect:latest

# Confluent Hub CLI를 사용하여 Elasticsearch 및 Debezium 플러그인 설치
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-elasticsearch:latest \
    && confluent-hub install --no-prompt debezium/debezium-connector-mysql:latest

# Kafka Connect 실행
CMD ["bash", "-c", "echo 'Starting Kafka Connect'; /etc/confluent/docker/run"]