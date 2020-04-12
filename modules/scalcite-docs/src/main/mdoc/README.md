# Scalcite

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=HE7K7HLJJBVWN&currency_code=EUR&source=url)
[![Build Status](https://travis-ci.com/mvillafuertem/scalcite.svg?branch=master)](https://travis-ci.com/mvillafuertem/scalcite)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.mvillafuertem/scalcite_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.mvillafuertem/scalcite_2.12)

****

Scalcite is a library to query 💬 and update ✏️ JSON data 📄

This library use https://github.com/apache/calcite

****


## Scalcite Example


### Backend

Is an Akka Microservices using DDD with ZIO ZStreams Tapir

```bash


sbt scalcite-example-backend/run

http://0.0.0.0:8080/api/v1.0/docs


```


### Console

Is an util project to play with json through SQL

```bash

sbt clean compile

./modules/scalcite-example/console/sqlline

!connect jdbc:calcite:model=modules/scalcite-example/console/target/scala-2.13/classes/model.json admin admin

!tables

!describe PERSON

SELECT "favoriteFruit" FROM scalcite;

SELECT "personalinfo.address" FROM scalcite;

```


### Frontend

Is a UI project created with React using Slinky 


## Monitoring

![APM](modules/scalcite-docs/src/main/resources/kibana.png)