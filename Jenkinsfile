def get_newtechweb_version() {
    sh """
    #!/bin/bash +x
    # App version prefix is set in config and completed by appending the build job number at build time
    BUILD_PREFIX="\$(grep "app.version=" conf/application.conf | awk -F = '{print \$2}' | sed 's/\\"//g')${BUILD_NUMBER}"
    branch=\$(echo ${GIT_BRANCH} | sed 's/\\//_/g')
    if [ \\"\$branch\\" = \\"master\\" ] || [ \\"\$branch\\" = \\"origin_master\\" ]; then
        echo "Master Branch build detected"
        echo "\$BUILD_PREFIX" > ./newtechweb.version
    else
        echo "Non Master Branch build detected"
        echo "\$BUILD_PREFIX-\$branch" > newtechweb.version;
    fi
    """
    return readFile("./newtechweb.version")
}
 
pipeline {
    agent { label "jenkins_slave" }

    environment {
        docker_image = "hmpps/new-tech-web"
        aws_region = 'eu-west-2'
        ecr_repo = ''
        newtechweb_VERSION = get_newtechweb_version()
    }

    options { 
        disableConcurrentBuilds() 
    }

    stages {
        stage ('Notify build started') {
            steps {
                slackSend(message: "Build Started - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace('http://', 'https://').replace(':8080', '')}|Open>)")
            }
        }

        stage ('Initialize') {
            steps {
                sh '''
                    #!/bin/bash +x
                    echo "PATH = ${PATH}"
                    echo "newtechweb_VERSION = ${newtechweb_VERSION}"
                '''
            }
        }

        stage('Verify Prerequisites') {
            steps {
                sh '''
                    #!/bin/bash +x
                    echo "Testing AWS Connectivity and Credentials"
                    aws sts get-caller-identity
                '''
            }
        }
        
        stage('SBT Assembly') {
            steps {
                sh '''
                    #!/bin/bash +x
                    make sbt-assembly jenkins_build=${BUILD_NUMBER};
                '''
            }
        }

        stage('Get ECR Login') {
            steps {
                sh '''
                    #!/bin/bash +x
                    make ecr-login
                '''
                // Stash the ecr repo to save a repeat aws api call
                stash includes: 'ecr.repo', name: 'ecr.repo'
            }
        }
        stage('Build Docker image') {
           steps {
                unstash 'ecr.repo'
                sh '''
                    #!/bin/bash +x
                    make build newtechweb_version=${newtechweb_VERSION}
                '''
            }
        }
        stage('Image Tests') {
            steps {
                // Run dgoss tests
                sh '''
                    #!/bin/bash +x
                    make test
                '''
            }
        }
        stage('Push image') {
            steps{
                unstash 'ecr.repo'
                sh '''
                    #!/bin/bash +x
                    make push newtechweb_version=${newtechweb_VERSION}
                '''
                
            }            
        }
        stage ('Remove untagged ECR images') {
            steps{
                unstash 'ecr.repo'
                sh '''
                    #!/bin/bash +x
                    make clean-remote
                '''
            }
        }
        stage('Remove Unused docker image') {
            steps{
                unstash 'ecr.repo'
                sh '''
                    #!/bin/bash +x
                    make clean-local newtechweb_version=${newtechweb_VERSION}
                '''
            }
        }
    }
    post {
        always {
            // Add a sleep to allow docker step to fully release file locks on failed run
            sleep(time: 3, unit: "SECONDS")
            deleteDir()
        }
        success {
            slackSend(message: "Build successful -${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace('http://', 'https://').replace(':8080', '')}|Open>)", color: 'good')
        }
        failure {
            slackSend(message: "Build failed - ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL.replace('http://', 'https://').replace(':8080', '')}|Open>)", color: 'danger')
        }
    }
}
