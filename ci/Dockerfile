FROM jenkins/jenkins:lts

USER root

# Install required tools
RUN apt-get update && apt-get install -y \
    curl unzip zip git wget \
    && rm -rf /var/lib/apt/lists/*

# Install Maven
ARG MAVEN_VERSION=3.9.9
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -P /tmp \
    && tar xzvf /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && ln -s /opt/apache-maven-${MAVEN_VERSION} /opt/maven \
    && ln -s /opt/maven/bin/mvn /usr/bin/mvn \
    && rm /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz

ENV MAVEN_HOME=/opt/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# Install Allure CLI
ARG ALLURE_VERSION=2.29.0
RUN wget https://github.com/allure-framework/allure2/releases/download/${ALLURE_VERSION}/allure-${ALLURE_VERSION}.zip -P /tmp \
    && unzip /tmp/allure-${ALLURE_VERSION}.zip -d /opt \
    && ln -s /opt/allure-${ALLURE_VERSION}/bin/allure /usr/bin/allure \
    && rm /tmp/allure-${ALLURE_VERSION}.zip

ENV ALLURE_HOME=/opt/allure-${ALLURE_VERSION}
ENV PATH=$ALLURE_HOME/bin:$PATH

# Switch back to Jenkins user
USER jenkins

# Preinstall recommended plugins (optional)
RUN jenkins-plugin-cli --plugins \
    "git workflow-aggregator blueocean allure-jenkins-plugin"

