# Scalcite

[![Build Status](https://travis-ci.com/mvillafuertem/scalcite.svg?branch=master)](https://travis-ci.com/mvillafuertem/scalcite)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.mvillafuertem/scalcite_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.mvillafuertem/scalcite_2.12)

****
Scalcite is a library to query 💬 and update ✏️ JSON data 📄

This library use https://github.com/mvillafuertem/io.github.mvillafuertem.mapflablup and https://github.com/apache/calcite

See ScalciteApplication.scala
****


```bash

./scalcite-example/backend/sqlline

!connect jdbc:calcite:model=scalcite-example-backend/target/scala-2.13/test-classes/model.json admin admin

!tables

!describe PERSON

SELECT "favoriteFruit" FROM scalcite;

SELECT "personalinfo.address" FROM scalcite;

```