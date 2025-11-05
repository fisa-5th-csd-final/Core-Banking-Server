pipeline {
    agent any

    tools {
        jdk 'temurin-17'
        gradle 'gradle-8'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                sh 'chmod +x ./gradlew'
                checkout scm
            }
        }

        stage('Spotless Check') {
            steps {
                echo "Running Spotless format check..."
                sh './gradlew spotlessCheck'
            }
        }

        stage('Build') {
            steps {
                echo "Building project..."
                sh './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                echo "Running tests..."
                sh './gradlew test'
            }
        }
    }

    post {
        success {
            echo "All checks passed! Merge allowed"
        }
        failure {
            echo "Spotless/Build/Test failed. Merge not allowed!"
        }
    }
}
