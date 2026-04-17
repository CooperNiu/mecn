# CI/CD Configuration Guide

This document describes the Continuous Integration and Continuous Deployment configuration for the MECN project.

## 📋 Table of Contents

- [Overview](#overview)
- [Workflows](#workflows)
- [Local Testing](#local-testing)
- [Troubleshooting](#troubleshooting)

---

## Overview

The MECN project uses **GitHub Actions** to implement automated CI/CD, including three main workflows:

1. **CI (ci.yml)** - Automatically runs on every push and PR
2. **Release (release.yml)** - Automatically deploys when releasing a new version
3. **Docs (docs.yml)** - Automatically deploys when documentation is updated

---

## Workflows

### 1. Continuous Integration (ci.yml)

**Triggers**:
- Push to `main` or `develop` branch
- Create Pull Request to `main` branch

**Tasks**:

#### Build and Test
```yaml
✅  Checkout code
✅  Setup JDK 17
✅  Maven dependency cache
✅  Compile project
✅  Run unit tests (133 tests)
✅  Generate JaCoCo coverage report
✅  Upload test results
✅  Upload coverage report
```

#### Code Quality
```yaml
✅  Checkstyle code style check
✅  PMD static code analysis
```

#### Docker Build
```yaml
✅  Build Docker image
✅  Test Docker container
```

**View Results**:
- Visit GitHub Actions tab
- Download test reports and coverage reports

---

### 2. Release (release.yml)

**Triggers**:
- Create a new GitHub Release

**Tasks**:
```yaml
✅  Compile and package (skip tests)
✅  Publish to GitHub Packages
✅  Upload JAR file
✅  Auto-generate release notes
```

**Usage**:
1. Create a new Release on GitHub
2. Set version number (e.g., v1.0.0)
3. Workflow triggers automatically

---

### 3. Documentation Deployment (docs.yml)

**Triggers**:
- Push to `main` branch with documentation changes
- Manual trigger (workflow_dispatch)

**Tasks**:
```yaml
✅  Generate Javadoc
✅  Install MkDocs dependencies
✅  Build MkDocs site
✅  Deploy to GitHub Pages
```

**Access Documentation**:
- Javadoc: `https://cooperniu.github.io/mecn/apidocs/`
- MkDocs: `https://cooperniu.github.io/mecn/`

---

## Local Testing

### Run Complete Test Suite

```bash
# Run all tests
mvn clean test

# Generate coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Code Quality Checks

```bash
# Checkstyle check
mvn checkstyle:check

# PMD static analysis
mvn pmd:check

# Generate Javadoc
mvn javadoc:javadoc
```

### Docker Build Test

```bash
# Build Docker image
docker build -t mecn:test .

# Run container test
docker run --rm mecn:test java -version
```

---

## Configuration

### Maven Configuration

Add the following plugins to `pom.xml` to support CI/CD:

```xml
<!-- JaCoCo Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
</plugin>

<!-- Checkstyle Code Style -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>3.3.0</version>
</plugin>

<!-- PMD Static Analysis -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-pmd-plugin</artifactId>
    <version>3.21.0</version>
</plugin>
```

### GitHub Secrets

Configure the following secrets in GitHub repository settings:

| Secret Name | Purpose | Required |
|-------------|---------|----------|
| `GITHUB_TOKEN` | Auto-provided, used for publishing packages | ✅ |

---

## Troubleshooting

### Common Issues

#### 1. Test Failures

**Symptoms**: CI shows test failures

**Solution**:
```bash
# Reproduce locally
mvn test

# View detailed errors
cat target/surefire-reports/*.txt
```

#### 2. Dependency Download Failure

**Symptoms**: Maven build stuck on dependency download

**Solution**:
```bash
# Clean local repository
rm -rf ~/.m2/repository/com/mecn

# Rebuild
mvn clean install -U
```

#### 3. Coverage Not Meeting Target

**Symptoms**: JaCoCo check fails

**Solution**:
```bash
# View coverage report
open target/site/jacoco/index.html

# Add missing tests
# Ensure core logic has sufficient test coverage
```

#### 4. Docker Build Failure

**Symptoms**: Docker image build fails

**Solution**:
```bash
# Check Dockerfile
cat Dockerfile

# Local build test
docker build -t mecn:debug .
docker logs <container_id>
```

---

## Best Practices

### 1. Pre-commit Checks

```bash
# Run quick checks
mvn clean compile test

# Ensure all tests pass
# Ensure no compilation warnings
```

### 2. Writing Tests

- Each new feature must have corresponding unit tests
- Maintain test coverage > 80%
- Use meaningful test names

### 3. Code Review

- PRs must pass all CI checks
- At least one maintainer review
- Follow code style guidelines

### 4. Version Management

- Use Semantic Versioning (SemVer)
- Update CHANGELOG.md with each release
- Tag before creating Release

---

## Monitoring and Optimization

### Build Time Optimization

Current average build time: **~2 minutes**

Optimization suggestions:
- ✅ Use Maven dependency cache
- ✅ Execute independent tasks in parallel
- ✅ Incremental compilation

### Coverage Targets

| Metric | Target | Current |
|--------|--------|---------|
| Line Coverage | > 80% | ~75% |
| Branch Coverage | > 70% | ~65% |
| Method Coverage | > 85% | ~80% |

---

## Related Links

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Official Documentation](https://maven.apache.org/guides/)
- [JaCoCo User Guide](https://www.jacoco.org/jacoco/trunk/doc/)
- [GitHub Packages](https://docs.github.com/en/packages)

---

**Last Updated**: 2026-04-16  
**Maintainer**: MECN Development Team
