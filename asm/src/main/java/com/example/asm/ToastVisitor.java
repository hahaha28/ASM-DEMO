package com.example.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class ToastVisitor extends ClassVisitor {

    private final String MAIN_ACTIVITY_NAME = "com/example/asmdemo/MainActivity";


    private String className = null;


    public ToastVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = name;
        System.out.println("className="+className+", superName="+superName);
        if(interfaces.length != 0){
            System.out.println("实现了接口");
            for(String inter:interfaces){
                System.out.println(inter);
            }
        }
        if(className != null && className.equals(MAIN_ACTIVITY_NAME)){
            System.out.println("MainActivity!!!");
        }
    }



    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor =  super.visitMethod(access, name, descriptor, signature, exceptions);

        System.out.println(className+",visitMethod:"+name);

        if(className!= null && className.equals(MAIN_ACTIVITY_NAME)
         && methodVisitor != null && name.equals("onCreate")){
            MethodVisitor newMethodVisitor = new MethodVisitor(api,methodVisitor) {

                @Override
                public void visitEnd() {
                   super.visitEnd();

                }
            };
            return newMethodVisitor;
        }

        return methodVisitor;
    }
}
