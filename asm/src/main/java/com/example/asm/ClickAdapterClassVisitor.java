package com.example.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.HashSet;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IFGE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.LCMP;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static proguard.classfile.ClassConstants.ACC_FINAL;
import static proguard.classfile.ClassConstants.ACC_PRIVATE;

public class ClickAdapterClassVisitor extends ClassVisitor {

    private boolean isTargetClass = false;
    private boolean isTargetLambdaClass = false;
    private HashSet<String> targetMethodName = new HashSet<>();
    private String className = null;

    public ClickAdapterClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        isTargetClass = false;
        className = name;
        System.out.println("------------------------");
        System.out.println("visitClass :"+className);
        if(interfaces.length != 0){

            for (String inter : interfaces) {
                if (inter.equals("android/view/View$OnClickListener")) {
                    isTargetClass = true;
                    break;
                }
            }

        }
        if(className.contains("Test")){
            isTargetClass = false;
        }
    }



    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        System.out.println("visitMethod :"+name+",descriptor="+descriptor+",signature="+signature);
        // 处理匿名内部类的写法
        if(isTargetClass && methodVisitor != null && name.equals("onClick")){
            MethodVisitor newMethodVisitor = new MethodVisitor(api,methodVisitor) {
                @Override
                public void visitCode() {
                    mv.visitCode();

                    Label label0 = new Label();
                    methodVisitor.visitLabel(label0);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, className, "lastClickTime", "J");
                    mv.visitInsn(LSUB);
                    mv.visitLdcInsn(new Long(1000L));
                    mv.visitInsn(LCMP);

                    Label label1 = new Label();
                    methodVisitor.visitJumpInsn(IFGE,label1);

                    Label label2 = new Label();
                    methodVisitor.visitLabel(label2);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitFieldInsn(PUTFIELD, className, "lastClickTime", "J");
//
                    Label label3 = new Label();
                    methodVisitor.visitLabel(label3);
                    mv.visitInsn(RETURN);
//
                    methodVisitor.visitLabel(label1);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitFieldInsn(PUTFIELD, className, "lastClickTime", "J");



                }

                @Override
                public void visitLineNumber(int line, Label start) {
                    System.out.println("visit line number:"+line);
                    super.visitLineNumber(line, start);
                }
            };
            return newMethodVisitor;
        }

        // 处理Lambda的写法
        if(targetMethodName.contains(name) || name.equals("test")){
            targetMethodName.remove(name);
            MethodVisitor newMethodVisit = new MethodVisitor(api,methodVisitor) {
                @Override
                public void visitCode() {
                    mv.visitCode();

                    Label label0 = new Label();
                    mv.visitLabel(label0);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, "com/example/asmdemo/MainActivity", "ASM_lastClickTimeRecorder", "Ljava/util/HashMap;");

                    Label label1 = new Label();
                    mv.visitJumpInsn(IFNONNULL, label1);

                    Label label2 = new Label();
                    mv.visitLabel(label2);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitTypeInsn(NEW, "java/util/HashMap");
                    mv.visitInsn(DUP);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
                    mv.visitFieldInsn(PUTFIELD, "com/example/asmdemo/MainActivity", "ASM_lastClickTimeRecorder", "Ljava/util/HashMap;");
                    mv.visitLabel(label1);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, "com/example/asmdemo/MainActivity", "ASM_lastClickTimeRecorder", "Ljava/util/HashMap;");
                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
                    mv.visitVarInsn(ASTORE, 2);

                    Label label3 = new Label();
                    mv.visitLabel(label3);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, "com/example/asmdemo/MainActivity", "ASM_lastClickTimeRecorder", "Ljava/util/HashMap;");
                    mv.visitLdcInsn(name);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitInsn(POP);

                    Label label4 = new Label();
                    mv.visitLabel(label4);
                    mv.visitVarInsn(ALOAD, 2);

                    Label label5 = new Label();
                    mv.visitJumpInsn(IFNONNULL, label5);

                    Label label6 = new Label();
                    mv.visitLabel(label6);
                    mv.visitInsn(LCONST_0);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    mv.visitVarInsn(ASTORE, 2);
                    mv.visitLabel(label5);
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Long"}, 0, null);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    mv.visitVarInsn(ALOAD, 2);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                    mv.visitInsn(LSUB);
                    mv.visitLdcInsn(new Long(1000L));
                    mv.visitInsn(LCMP);

                    Label label7 = new Label();
                    mv.visitJumpInsn(IFGE, label7);

                    Label label8 = new Label();
                    mv.visitLabel(label8);
                    mv.visitInsn(RETURN);
                    mv.visitLabel(label7);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                    mv.visitLdcInsn("tag");
                    mv.visitLdcInsn("click2");

                    mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
                    mv.visitInsn(POP);

//                    Label label10 = new Label();
//                    methodVisitor.visitLabel(label10);
//                    methodVisitor.visitLocalVariable("this", "Lcom/example/asmdemo/MainActivity;", null, label0, label10, 0);
//                    methodVisitor.visitLocalVariable("v", "Landroid/view/View;", null, label0, label10, 1);
//                    methodVisitor.visitLocalVariable("ASM_lastClickTime", "Ljava/lang/Long;", null, label3, label10, 2);
                }

                @Override
                public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
                    super.visitLocalVariable(name, descriptor, signature, start, end, index);

                }
            };
            return newMethodVisit;
        }

        return new MethodVisitor(api,methodVisitor) {

            @Override
            public void visitCode() {
                super.visitCode();
            }



            @Override
            public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                // 找到使用Lambda的OnClickListener
                if(name.equals("onClick") && descriptor.endsWith("Landroid/view/View$OnClickListener;")){
                    isTargetLambdaClass = true;
                    // 提取lambda的方法名
                    for(Object object:bootstrapMethodArguments){
                        if(object instanceof Handle){
                            Handle handle = (Handle) object;
                            String lambdaName = handle.getName();
                            targetMethodName.add(lambdaName);
                            System.out.println("add targetMethod :"+lambdaName);
                        }
                    }
                }
                super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
            }
        };
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        System.out.println("visit Inner Class!!!! :"+name+","+outerName);
        super.visitInnerClass(name, outerName, innerName, access);
    }

    @Override
    public void visitEnd() {
        FieldVisitor fv = cv.visitField(ACC_PRIVATE, "lastClickTime", "J", null, null);
        if(fv != null){
            fv.visitEnd();

        }

        if(isTargetLambdaClass){
            FieldVisitor fv2  = cv.visitField(ACC_PRIVATE | ACC_FINAL, "ASM_lastClickTimeRecorder", "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Long;>;", null);
            if( fv2 != null){
                fv2.visitEnd();
            }
            isTargetLambdaClass = false;
        }

        super.visitEnd();
    }
}
