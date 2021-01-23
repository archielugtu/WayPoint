const sonarqubeScanner =  require('sonarqube-scanner');
sonarqubeScanner(
    {
      // replace "X" hereunder to match your VM url
      serverUrl:  'https://csse-s302g2.canterbury.ac.nz/sonarqube/',
      token: "8cb4c6141bafc698f1d48fb43bd4f1128c59b2d1",
      options : {
        'sonar.projectKey': 'team-200-client',
        'sonar.projectName': 'Team 200 - Client',
        "sonar.sourceEncoding": "UTF-8",
        'sonar.sources': 'src',
        'sonar.tests': 'src',
        'sonar.inclusions': '**',
        'sonar.test.inclusions': 'src/**/*.spec.js,src/**/*.test.js,src/**/*.test.ts',
        'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
        'sonar.javascript.lcov.reportPaths': 'coverage/lcov.info',
        'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml'
      }
    }, () => {});
