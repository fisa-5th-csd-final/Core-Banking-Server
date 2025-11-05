pipeline {
        agent {
            docker {
                // 일관된 빌드 환경을 위해 특정 도커 이미지를 사용합니다.
                image 'eclipse-temurin:17-jdk-jammy'
                // Gradle 의존성을 캐시하여 빌드 속도를 향상시킵니다.
                // 호스트의 $HOME/.gradle을 컨테이너의 /root/.gradle에 마운트합니다.
                // 참고: 컨테이너 이미지의 사용자 설정에 따라 '/root' 경로는 달라질 수 있습니다.
                args '-v $HOME/.gradle:/root/.gradle'
            }
        }

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
