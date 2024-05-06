def calculateVersion(latestTag) {
    def (major, minor, patch) = latestTag.tokenize('.')

    // Fetch the latest commit message
    def commitMessage = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()

    // Increment the version based on the commit message
    if (commitMessage.contains('major')) {
        major = major.toInteger() + 1
        minor = '0'
        patch = '0'
    } else if (commitMessage.contains('minor')) {
        minor = minor.toInteger() + 1
        patch = '0'
    } else {
        patch = patch.toInteger() + 1
    }

    return "${major}.${minor}.${patch}"
}

def cleanGit() {
    sh 'git fetch --all'
    sh 'git reset --hard'
    sh 'git clean -fdx'
}

def calculateSnapshotVersion(latestReleaseTag) {
    def (major, minor, patch) = latestReleaseTag.tokenize('.')
    patch = patch.toInteger() + 1
    sh "mvn versions:set -DnewVersion=${major}.${minor}.${patch}-SNAPSHOT"
    return "${major}.${minor}.${patch}-SNAPSHOT"
}

pipeline {
    agent any
    environment {
        APP_NAME = 'campaign_controller_api'
        SONAR_SERVER = 'LabSonarQube'
        SONAR_PROJECT_NAME = 'campaign_controller_api'
        SONAR_PROJECT_KEY = 'campaign_controller_api'
        SONAR_SOURCES = './src'
        SONAR_SONAR_LOGIN = 'adam-stegienko'
        DOCKER_REGISTRY = 'registry.stegienko.com:8443'
        DUPLICATED_TAG = 'false'
    }
    options {
        timestamps()
    }
    tools {
        maven 'Maven'
        jdk 'JDK'
        dockerTool '26.1.1'
    }
    stages {

        stage('Clean Workspace') {
            steps {
                sshagent(['jenkins_github_np']) {
                    cleanGit()
                }
            }
        }

        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    doGenerateSubmoduleConfigurations: 'false',
                    extensions: [],
                    submoduleCfg: [],
                    userRemoteConfigs: [[
                        credentialsId: 'jenkins_github_np',
                        url: 'git@github.com:adam-stegienko/campaign_controller_api.git'
                    ]]
                ])
            }
        }

        stage('Calculate Version') {
            steps {
                script {
                    def latestTag = '0.0.0'
                    try {
                        latestTag = sh(returnStdout: true, script: 'git tag | sort -Vr | head -n 1').trim()
                    } catch (Exception e) {}
                    env.APP_VERSION = calculateVersion(latestTag)

                    // Check if the latest commit already has a tag
                    def latestCommitTag = ''
                    try {
                        latestCommitTag = sh(returnStdout: true, script: 'git tag --contains HEAD').trim()
                        echo "Latest commit tag: ${latestCommitTag}"
                    } catch (Exception e) {}
                    if (latestCommitTag) {
                        env.DUPLICATED_TAG = 'true'
                        sh "echo 'Tag ${latestCommitTag} already exists for the latest commit. DUPLICATED_TAG env var is set to: '${env.DUPLICATED_TAG}"
                    } else {
                        sh "echo ${latestTag} '->' ${env.APP_VERSION}"
                        sh "echo ${env.DUPLICATED_TAG}"
                    }
                }
            }
        }

        stage('SonarQube analysis') {
            steps {
                withMaven() {
                    withSonarQubeEnv(env.SONAR_SERVER) {
                        sh "mvn versions:set -DnewVersion=${env.APP_VERSION}"
                        sh "mvn clean verify sonar:sonar -Dsonar.projectKey=${env.SONAR_PROJECT_KEY} -Dsonar.projectName='${env.SONAR_PROJECT_NAME}'"
                    }
                }
            }
        }

        stage('Docker Build') {
            when {
                expression {
                    return currentBuild.currentResult == 'SUCCESS'
                }
            }
            steps {
                sh "docker build --build-arg APP_VERSION=${env.APP_VERSION} -t ${env.DOCKER_REGISTRY}/${env.APP_NAME}:${env.APP_VERSION} ."
            }
        }

        stage('Docker Image Security Scan') {
            when {
                expression {
                   return currentBuild.currentResult == 'SUCCESS'
                }
            }
            steps {
                sh "trivy image --severity HIGH,CRITICAL --exit-code 1 ${env.DOCKER_REGISTRY}/${env.APP_NAME}:${env.APP_VERSION}"
            }
        }

        stage('Docker Push') {
            when {
                expression {
                    return currentBuild.currentResult == 'SUCCESS' && env.DUPLICATED_TAG == 'false'
                }
            }
            // steps {
            //     script {
            //         withDockerRegistry([credentialsId: "docker_registry_credentials", url: "https://${env.DOCKER_REGISTRY}"]) {
            //             sh "docker push ${env.DOCKER_REGISTRY}/${env.APP_NAME}:${env.APP_VERSION}"
            //             sh "docker tag ${env.DOCKER_REGISTRY}/${env.APP_NAME}:${env.APP_VERSION} ${env.DOCKER_REGISTRY}/${env.APP_NAME}:latest"
            //             sh "docker push ${env.DOCKER_REGISTRY}/${env.APP_NAME}:latest"
            //         }
            //     }
            // }
            steps {
                script {
                    docker.withRegistry("https://${env.DOCKER_REGISTRY}", "docker_registry_credentials") {
                        def appImage = docker.image("${env.DOCKER_REGISTRY}/${env.APP_NAME}:${env.APP_VERSION}")
                        appImage.push()
                        appImage.push('latest')
                    }
                }
            }
        }

        stage('Archive') {
            when {
                expression {
                    return currentBuild.currentResult == 'SUCCESS' && env.DUPLICATED_TAG == 'false'
                }
            }
            steps {
                archiveArtifacts artifacts: "**/target/${env.APP_NAME}*.jar", fingerprint: true
            }
        }

        stage('Maven Deploy') {
            when {
                expression {
                    return currentBuild.currentResult == 'SUCCESS' && env.DUPLICATED_TAG == 'false'
                }
            }
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'ABORTED') {
                    withMaven() {
                        sh "mvn versions:set -DnewVersion=${env.APP_VERSION}"
                        sh 'mvn clean deploy'
                    }
                }
            }
        }

        stage('Update pom.xml version, Tag, and Push to Git') {
            when {
                expression {
                    return currentBuild.currentResult == 'SUCCESS' && env.DUPLICATED_TAG == 'false'
                }
            }
            steps {
                script {
                    sshagent(['jenkins_github_np']) {
                        cleanGit()
                        sh "git config --global user.email 'adam.stegienko1@gmail.com'"
                        sh "git config --global user.name 'Adam Stegienko'"
                        def snapshotVersion = calculateSnapshotVersion(env.APP_VERSION)
                        sh "git fetch -all"
                        sh "git checkout master"
                        sh "git merge origin/master"
                        sh "git add pom.xml"
                        sh "git commit -m '[skip ci] Dev app version: ${snapshotVersion}'"
                        sh "git push origin master"
                        sh "git tag ${env.APP_VERSION}"
                        sh "git push origin tag ${env.APP_VERSION}"
                    }
                }
            }
        }
    }
    post {
        always {
            emailext body: "Build ${currentBuild.currentResult}: Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}\nMore info at: ${env.BUILD_URL}",
                 from: 'jenkins+blueflamestk@gmail.com',
                 subject: "${currentBuild.currentResult}: Job '${env.JOB_NAME}' (${env.BUILD_NUMBER})",
                 to: 'adam.stegienko1@gmail.com'
        }
    }
}