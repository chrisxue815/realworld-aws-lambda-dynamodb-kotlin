# Work In Progress

# ![RealWorld Example App](logo.png)

> ### AWS Lambda + DynamoDB + Kotlin codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/Kotlinthinkster/realworld) spec and API.

### [Demo](https://chrisxue815.github.io/realworld/build/#/)

[![Build Status](https://travis-ci.org/chrisxue815/realworld-aws-lambda-dynamodb-kotlin.svg?branch=master)](https://travis-ci.org/chrisxue815/realworld-aws-lambda-dynamodb-kotlin)

This codebase was created to demonstrate a fully fledged fullstack application built with **AWS Lambda + DynamoDB + Kotlin** including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **AWS Lambda + DynamoDB + Kotlin** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Getting started

## Prerequisite

* Install Kotlin, Node.js, Serverless CLI
* In `angularjs-realworld-example-app`, run `npm install`

## Build and deploy backend

In the root directory of this project:

* `./gradlew build`
* `sls deploy`

## Build and serve frontend

In `angularjs-realworld-example-app`:

* `npx gulp`

# How it works

Routes and their handlers are defined in `serverless.yml`.

For example, the following section means `POST /users` is handled by `com.serverless.route.usersPost.Handler`.

```
  users-post:
    handler: com.serverless.route.usersPost.Handler
    events:
      - http:
          path: users
          method: post
          cors: true
```

# Design choices
* Scrypt-based password hashing
* Input validation
* Data consistency with DynamoDB transactions

These tradeoffs were made for simpler code:
* Hardcoded Scrypt secret. Downside: tokens can't be invalidated
* Shared states (like DB and RNG) are singletons, no dependency injections used. Downside: lifecycles of shared states are not controllable. Potential memory leak. Unit-test-unfriendly
* Usernames are not changeable
* Usernames are case-sensitive
* Performance bottleneck in global secondary indices with a single hash-key value, like ArticleTable.CreatedAt and TagTable.ArticleCount
* Performance bottleneck in fan-in-based article feed aggregation
