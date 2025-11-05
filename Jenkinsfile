pipeline {
    agent any

    environment {
        // Gradle 캐시 디렉토리 (속도 향상)
        GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
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
