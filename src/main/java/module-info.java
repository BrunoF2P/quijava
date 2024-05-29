module org.quijava.quijava {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.core;
    requires spring.boot.autoconfigure;
    requires spring.boot;
    requires spring.context;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires spring.data.jpa;
    requires spring.beans;
    requires spring.tx;
    requires spring.jdbc;
    requires spring.orm;
    requires spring.security.crypto;
    requires atlantafx.base;
    requires java.prefs;
    requires java.desktop;
    requires spring.aop;
    requires jakarta.annotation;


    opens org.quijava.quijava to javafx.fxml, spring.core;
    opens org.quijava.quijava.utils to spring.core;
    opens  org.quijava.quijava.models;
    opens org.quijava.quijava.controllers;
    exports org.quijava.quijava;
    exports org.quijava.quijava.models;
    exports org.quijava.quijava.utils;
    exports org.quijava.quijava.controllers;
    exports org.quijava.quijava.repositories;
    exports org.quijava.quijava.services;
}