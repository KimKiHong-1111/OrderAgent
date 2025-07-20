FROM public.ecr.aws/lambda/java:17

# Java app JAR
COPY target/order-agent.jar ${LAMBDA_TASK_ROOT}/app.jar

# Optional: Chrome + chromedriver 설치
RUN yum install -y unzip curl \
 && curl -sSL https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm -o chrome.rpm \
 && yum install -y ./chrome.rpm \
 && curl -sSL https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip -o chromedriver.zip \
 && unzip chromedriver.zip \
 && mv chromedriver /usr/bin/ \
 && chmod +x /usr/bin/chromedriver

# Entrypoint
CMD ["api.orderagent.lambda.OrderAgentHandler::apply"]
