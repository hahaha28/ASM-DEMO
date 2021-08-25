package com.example.asm;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MyPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("plugin execute [write by java]");
        project.getExtensions().getByType(AppExtension.class).registerTransform(new MyTransform());
//        extension.registerTransform(new MyTransform());
    }
}
