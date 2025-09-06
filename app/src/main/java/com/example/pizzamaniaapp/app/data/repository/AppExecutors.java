package com.example.pizzamaniaapp.app.data.repository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Executor DISK = Executors.newSingleThreadExecutor();
    public static Executor disk() { return DISK; }
}