# Git Commands Cheat Sheet
## For Student Management System Project

---

## 🚀 Quick Start Commands

### Initial Setup (One-Time)
```bash
# Clone repository
git clone https://github.com/Adi-Sin101/Student_Management.git
cd Student_Management

# Configure user info
git config user.name "Your Name"
git config user.email "your.email@example.com"

# View configuration
git config --list
```

---

## 🌿 Branch Management

### Creating the Testing Branch

```bash
# Method 1: Create and switch in one command
git checkout -b testing/unit-integration-tests

# Method 2: Create then switch
git branch testing/unit-integration-tests
git checkout testing/unit-integration-tests

# Verify current branch
git branch
# Output: * testing/unit-integration-tests

# List all branches (including remote)
git branch -a
```

### Switching Between Branches

```bash
# Switch to main
git checkout main

# Switch back to testing branch
git checkout testing/unit-integration-tests

# Create and switch to new branch from current location
git checkout -b feature/new-feature
```

### Deleting Branches

```bash
# Delete local branch (safe - prevents if unmerged)
git branch -d testing/unit-integration-tests

# Force delete local branch
git branch -D testing/unit-integration-tests

# Delete remote branch
git push origin --delete testing/unit-integration-tests
```

---

## 📝 Making Changes

### Checking Status

```bash
# See modified files
git status

# Short status
git status -s

# Show diff of unstaged changes
git diff

# Show diff of staged changes
git diff --staged
```

### Adding Files

```bash
# Add specific file
git add src/test/java/StudentServiceTest.java

# Add all files in directory
git add src/test/java/

# Add all Java files
git add **/*.java

# Add all changes
git add .

# Add with interactive mode
git add -i
```

### Committing Changes

```bash
# Basic commit
git commit -m "test: add unit tests for StudentService"

# Commit with detailed message
git commit -m "test: add integration tests for StudentRepository

- Add @DataJpaTest for database operations
- Test CRUD operations with H2 in-memory database
- Cover custom query methods
- Ensure transactional behavior"

# Amend last commit (change message or add files)
git commit --amend -m "test: add comprehensive unit tests for StudentService"

# Commit all modified tracked files (skip git add)
git commit -am "test: update existing tests"
```

### Viewing History

```bash
# View commit history
git log

# One-line format
git log --oneline

# Graph view
git log --graph --oneline --all

# Last 5 commits
git log -5

# Show commits by author
git log --author="Your Name"

# Show commits for specific file
git log -- src/test/java/StudentServiceTest.java
```

---

## 🔄 Syncing with Remote

### Pushing Changes

```bash
# Push to remote for first time (set upstream)
git push -u origin testing/unit-integration-tests

# After first push
git push

# Force push (DANGEROUS - use with caution)
git push --force

# Safer force push (checks remote wasn't updated)
git push --force-with-lease
```

### Pulling Changes

```bash
# Pull latest from current branch
git pull

# Pull from specific branch
git pull origin main

# Fetch without merging
git fetch origin

# Pull with rebase instead of merge
git pull --rebase
```

### Fetching Updates

```bash
# Fetch all branches from all remotes
git fetch --all

# Fetch specific remote
git fetch origin

# Fetch and prune deleted remote branches
git fetch --prune
```

---

## 🔀 Merging and Rebasing

### Merging

```bash
# Merge main into current branch
git checkout testing/unit-integration-tests
git merge main

# Merge with no fast-forward (creates merge commit)
git merge --no-ff main

# Abort merge if conflicts occur
git merge --abort
```

### Rebasing

```bash
# Rebase current branch onto main
git checkout testing/unit-integration-tests
git rebase main

# Continue after resolving conflicts
git add <resolved-file>
git rebase --continue

# Skip current commit
git rebase --skip

# Abort rebase
git rebase --abort

# Interactive rebase (squash commits)
git rebase -i HEAD~3
```

---

## 🔧 Resolving Conflicts

### When Conflict Occurs

```bash
# 1. See which files have conflicts
git status

# 2. Open conflicted file and look for:
<<<<<<< HEAD
Your changes
=======
Their changes
>>>>>>> branch-name

# 3. Manually edit to resolve

# 4. Add resolved files
git add <resolved-file>

# 5. Complete merge
git commit -m "merge: resolve conflicts in README.md"

# OR if rebasing
git rebase --continue
```

### Conflict Resolution Tools

```bash
# Use merge tool
git mergetool

# Accept all changes from current branch
git checkout --ours <file>

# Accept all changes from merging branch
git checkout --theirs <file>
```

---

## 📤 Creating Pull Requests

### Via GitHub CLI

```bash
# Install GitHub CLI
# Windows: winget install GitHub.cli
# Mac: brew install gh

# Authenticate
gh auth login

# Create PR
gh pr create --base main --head testing/unit-integration-tests \
  --title "Add comprehensive unit and integration tests" \
  --body "Description of changes"

# Create PR interactively
gh pr create

# List PRs
gh pr list

# View PR status
gh pr status

# Checkout PR locally
gh pr checkout 123

# Review PR
gh pr review --approve
gh pr review --request-changes --body "Please fix XYZ"

# Merge PR
gh pr merge --squash --delete-branch
```

### Via Web (Manual Steps)

1. Push branch: `git push -u origin testing/unit-integration-tests`
2. Go to GitHub repository
3. Click "Pull requests" → "New pull request"
4. Select branches: base `main` ← compare `testing/unit-integration-tests`
5. Fill in title and description
6. Create pull request

---

## 🎯 Conventional Commits

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation
- **style**: Code formatting
- **refactor**: Code restructuring
- **test**: Adding/updating tests
- **chore**: Maintenance tasks
- **ci**: CI/CD changes
- **build**: Build system changes
- **perf**: Performance improvements

### Examples

```bash
# Simple test addition
git commit -m "test: add unit tests for StudentService"

# Feature with scope
git commit -m "feat(auth): implement JWT authentication"

# Bug fix with body
git commit -m "fix(student): resolve duplicate email validation

Previously, the system allowed duplicate emails due to
case-sensitive comparison. Now using case-insensitive check.

Fixes #123"

# Breaking change
git commit -m "feat(api)!: change student endpoint structure

BREAKING CHANGE: The /api/students endpoint now returns
paginated results instead of full list."
```

---

## 🧹 Cleaning Up

### Undoing Changes

```bash
# Discard changes in working directory
git checkout -- <file>

# Unstage file (keep changes)
git reset HEAD <file>

# Undo last commit (keep changes staged)
git reset --soft HEAD~1

# Undo last commit (keep changes unstaged)
git reset HEAD~1

# Undo last commit (discard changes) - DANGEROUS
git reset --hard HEAD~1

# Revert a commit (creates new commit)
git revert <commit-hash>
```

### Stashing Changes

```bash
# Stash current changes
git stash

# Stash with message
git stash save "WIP: working on tests"

# List stashes
git stash list

# Apply latest stash
git stash apply

# Apply specific stash
git stash apply stash@{2}

# Pop stash (apply and remove)
git stash pop

# Delete stash
git stash drop stash@{0}

# Clear all stashes
git stash clear
```

---

## 🔍 Searching and Debugging

### Finding Things

```bash
# Search for text in files
git grep "StudentService"

# Search in commit messages
git log --grep="test"

# Find who changed a line
git blame <file>

# Show what revision changed a file
git log -p <file>
```

### Debugging

```bash
# Show file at specific commit
git show <commit>:<file>

# Compare branches
git diff main..testing/unit-integration-tests

# Show commits in branch A but not in B
git log main..testing/unit-integration-tests
```

---

## 📊 Working with Tags

### Creating Tags

```bash
# Lightweight tag
git tag v1.0.0

# Annotated tag (recommended)
git tag -a v1.0.0 -m "Release version 1.0.0"

# Tag specific commit
git tag -a v1.0.0 <commit-hash> -m "Release 1.0.0"
```

### Managing Tags

```bash
# List tags
git tag

# Show tag details
git show v1.0.0

# Push tag to remote
git push origin v1.0.0

# Push all tags
git push --tags

# Delete local tag
git tag -d v1.0.0

# Delete remote tag
git push origin --delete v1.0.0
```

---

## 🚨 Emergency Commands

### Oh No, I Committed to Main!

```bash
# If not pushed yet:
git reset --soft HEAD~1
git stash
git checkout testing/unit-integration-tests
git stash pop
git add .
git commit -m "test: add tests (moved from main)"
```

### I Need to Update My PR

```bash
# Make changes
git add .
git commit -m "test: address review comments"
git push
# PR automatically updates!
```

### I Pushed Sensitive Data!

```bash
# Remove file from all history (DANGEROUS)
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch path/to/sensitive/file' \
  --prune-empty --tag-name-filter cat -- --all

# Then force push
git push --force --all
```

---

## 📋 Daily Workflow

### Morning Routine

```bash
# 1. Switch to main and update
git checkout main
git pull origin main

# 2. Create or switch to feature branch
git checkout testing/unit-integration-tests

# 3. Merge main into feature (stay up to date)
git merge main
```

### During Development

```bash
# 1. Make changes
# (code, code, code...)

# 2. Check what changed
git status
git diff

# 3. Stage and commit
git add .
git commit -m "test: add new test cases"

# 4. Push regularly
git push
```

### Before Creating PR

```bash
# 1. Update from main
git checkout main
git pull
git checkout testing/unit-integration-tests
git merge main

# 2. Run tests locally
mvn clean test

# 3. Push and create PR
git push
gh pr create
```

---

## 🔐 Security Best Practices

### Protecting Credentials

```bash
# Never commit .env files
echo ".env" >> .gitignore

# Never commit application.properties with passwords
# Use application-example.properties instead

# Check for sensitive data before committing
git diff --staged
```

### SSH Setup (Recommended)

```bash
# Generate SSH key
ssh-keygen -t ed25519 -C "your.email@example.com"

# Start SSH agent
eval "$(ssh-agent -s)"

# Add key to agent
ssh-add ~/.ssh/id_ed25519

# Copy public key (add to GitHub settings)
cat ~/.ssh/id_ed25519.pub

# Test connection
ssh -T git@github.com
```

---

## 📖 Help and Documentation

```bash
# Get help for any command
git help <command>
git <command> --help

# Examples:
git help commit
git help rebase

# Quick help
git <command> -h
```

---

## 🎯 Quick Reference Table

| Task | Command |
|------|---------|
| Create branch | `git checkout -b branch-name` |
| Switch branch | `git checkout branch-name` |
| Stage changes | `git add .` |
| Commit | `git commit -m "message"` |
| Push | `git push` |
| Pull | `git pull` |
| View status | `git status` |
| View history | `git log --oneline` |
| Merge main | `git merge main` |
| Create PR | `gh pr create` |
| Resolve conflict | Edit → `git add` → `git commit` |
| Undo changes | `git checkout -- file` |
| Stash changes | `git stash` |

---

## 🎓 Pro Tips

1. **Commit often**: Small commits are easier to review
2. **Pull before push**: Avoid conflicts
3. **Use branches**: Never work directly on main
4. **Write good messages**: Follow conventional commits
5. **Review before commit**: Use `git diff --staged`
6. **Test before push**: Run `mvn clean test`
7. **Keep branches short-lived**: Merge within days
8. **Use .gitignore**: Don't commit generated files
9. **Learn to rebase**: Keeps history clean
10. **Use SSH**: More secure than HTTPS

---

**Last Updated**: February 2026  
**For**: Student Management System Project
