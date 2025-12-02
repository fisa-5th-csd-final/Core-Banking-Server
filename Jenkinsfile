pipeline {
    agent any

    environment {
        // Gradle 캐시 디렉토리 (속도 향상)
        GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
        DOCKER_IMAGE = "wjdjoonim/core-bank-fisa"   // Docker Hub Repository
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📦 Checking out source code...'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${env.BRANCH_NAME}"]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/fisa-5th-csd-final/Core-Banking-Server.git',
                        credentialsId: 'github-access-token'  // 🔗 Jenkins Credentials ID
                    ]]
                ])
            }
        }

        stage('Spotless Check') {
            steps {
                echo '✨ Running Spotless format check...'
                dir('bank') {
                    sh './gradlew spotlessCheck --no-daemon'
                }
            }
        }

        stage('Build') {
            steps {
                echo '🏗️ Building project (tests skipped)...'
                dir('bank') {
                    sh './gradlew build -x test --no-daemon'
                }
            }
        }

        stage('Docker Build & Push'){
            steps{
                // Docker Hub id, pwd
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-hub-cred',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]){
                    sh """
                        echo '🚧 Docker 이미지 빌드 시작'
                        docker build -t "${DOCKER_IMAGE}:${env.BUILD_NUMBER}" -t "${DOCKER_IMAGE}:latest" -f Dockerfile .

                        echo '🔐 Docker Hub 로그인'
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        echo '🚀 Docker Push'
                        docker push "${DOCKER_IMAGE}:${env.BUILD_NUMBER}"
                        docker push ${DOCKER_IMAGE}:latest

                        echo '🎉 Docker Push 완료'
                    """
                }
            }
        }

        stage('Deploy to EC2') {
            when {
                branch 'develop'
            }
            steps {
                echo '🚀 Deploying to EC2...'
                withCredentials([
                    string(credentialsId: 'SSH_USER', variable: 'SSH_USER'),
                    string(credentialsId: 'DEPLOY_HOST_CORE', variable: 'DEPLOY_HOST_CORE')
                ]) {
                    sshagent(['from-jenkins-to-aws-ec2-access-key']) {
                        sh """
                            ssh -o StrictHostKeyChecking=yes $SSH_USER@$DEPLOY_HOST_CORE \\
                                'cd ~/Loan-Mate-Backend && ./deploy.sh ${env.BUILD_NUMBER}'
                        """
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
                    environment {
                        SONAR_SCANNER_HOME = tool 'SonarScanner'
                    }
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            dir('bank') {
                                sh "${SONAR_SCANNER_HOME}/bin/sonar-scanner \
                                    -Dsonar.projectKey=core-banking \
                                    -Dsonar.projectName='Core Banking System' \
                                    -Dsonar.sources=src/main/java \
                                    -Dsonar.java.binaries=build/classes/java/main \
                                    -Dsonar.sourceEncoding=UTF-8"
                            }
                        }
                    }
                }
    }

    post {
        success {
            echo 'Spotless & Build succeeded! Merge allowed.'
        }
        failure {
            echo 'Spotless or Build failed. Merge not allowed!'
        }
    }
}
