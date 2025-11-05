pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Spotless Check') {
            steps {
                echo 'Running Spotless format check...'
                dir('bank') {
                    sh './gradlew spotlessCheck'
                }
            }
        }

        stage('Build') {
            steps {
                dir('bank') {
                    sh './gradlew build'
                }
            }
        }
    }

    post {
        failure {
            echo 'Spotless/Build/Test failed. Merge not allowed!'
        }
    }
}