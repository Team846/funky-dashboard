#!/bin/bash

set -e # exit with nonzero exit code if anything fails

# install ssh keys
openssl aes-256-cbc -K $encrypted_b6332ef3a6b3_key -iv $encrypted_b6332ef3a6b3_iv -in .repodeploykey.enc -out .repodeploykey -d
chmod go-rwx .repodeploykey
eval `ssh-agent -s`
ssh-add .repodeploykey

# clone the current gh-pages branch into the repo folder
git clone -b gh-pages --single-branch git@github.com:Team846/repo.git repo

sbt "publish"

# go to the out directory
cd repo

# inside this git repo we'll pretend to be a new user
git config user.name "Travis CI"
git config user.email "robot@lynbrookrobotics.com"

# The first and only commit to this new Git repo contains all the
# files present with the commit message "Deploy to GitHub Pages".
git add .
git commit -m "Automated deploy to GitHub Pages"

# Force push from the current repo's master branch to the remote
# repo's gh-pages branch. (All previous history on the gh-pages branch
# will be lost, since we are overwriting it.) We redirect any output to
# /dev/null to hide any sensitive credential data that might otherwise be exposed.
git push --force --quiet "git@github.com:Team846/repo.git" gh-pages:gh-pages > /dev/null 2>&1
