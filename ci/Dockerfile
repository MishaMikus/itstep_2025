FROM jenkins/jenkins:lts

USER root

# ---------- Install utilities ----------
USER root
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    unzip \
    gnupg2 \
    xvfb \
    --no-install-recommends

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


# ============
# Install JMeter
# ============
ARG JMETER_VERSION=5.6.2
ENV JMETER_HOME=/opt/apache-jmeter-${JMETER_VERSION}
ENV PATH=$JMETER_HOME/bin:$PATH

RUN wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz -P /tmp \
    && tar -xzf /tmp/apache-jmeter-${JMETER_VERSION}.tgz -C /opt \
    && rm /tmp/apache-jmeter-${JMETER_VERSION}.tgz

# ---------- Install Google Chrome ----------
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable \

# Switch back to Jenkins user
USER jenkins

# Preinstall recommended plugins (optional)
RUN jenkins-plugin-cli --plugins \
    "git workflow-aggregator blueocean allure-jenkins-plugin"

