pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK'
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Run Tests') {
            steps {
                bat 'mvn clean test -DsuiteXmlFiles=testng.xml,testng_E2E.xml'

            }
        }
    }

    post {
        always {
            echo 'Build completed'
        }
        success {
            echo 'Build successful'
        }
        failure {
            echo 'Build failed'
        }
    }
}
