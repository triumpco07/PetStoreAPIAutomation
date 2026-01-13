pipeline {
    agent any

    tools {
        maven 'Maven'
        jdk 'JDK'
    }

    parameters {
        string(
            name: 'SUITES',
            defaultValue: 'testng_E2E.xml',
            description: 'Comma-separated TestNG XML files to run'
        )
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Run Tests') {
            steps {
                bat "mvn clean test -DsuiteXmlFiles=${params.SUITES}"
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
