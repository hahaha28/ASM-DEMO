package com.example.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashSet;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.IFGE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.LCMP;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static proguard.classfile.ClassConstants.ACC_PRIVATE;

public class ClickAdapterClassVisitor extends ClassVisitor {

    /**
     * 当前类是否是实现了View.OnClickListener的类
     */
    private boolean isTargetOnClickListenerClass = false;


    /**
     * lambda对应的方法名
     */
    private HashSet<String> targetMethodName = new HashSet<>();

    /**
     * 当前类的类名
     */
    private String className = null;

    public ClickAdapterClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        isTargetOnClickListenerClass = false;
        className = name;
        System.out.println("------------------------");
        System.out.println("visitClass :"+className);
        if(interfaces.length != 0){

            for (String inter : interfaces) {
                if (inter.equals("android/view/View$OnClickListener")) {
                    isTargetOnClickListenerClass = true;
                    break;
                }
            }

        }
    }



    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        System.out.println("visitMethod :"+name+",descriptor="+descriptor+",signature="+signature);

        // 处理匿名内部类的写法
        if((isTargetOnClickListenerClass && methodVisitor != null && name.equals("onClick")) ){
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
                public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
                }
            };
            return newMethodVisitor;
        }

        // 处理Lambda的写法
        if(targetMethodName.contains(name) ){
            targetMethodName.remove(name);
            MethodVisitor newMethodVisit = new MethodVisitor(api,methodVisitor) {
                @Override
                public void visitCode() {
                    mv.visitCode();

                    Label label0 = new Label();
                    methodVisitor.visitLabel(label0);
                    methodVisitor.visitLineNumber(27, label0);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "getTag", "()Ljava/lang/Object;", false);
                    methodVisitor.visitVarInsn(ASTORE, 1);

                    Label label1 = new Label();
                    methodVisitor.visitLabel(label1);
                    methodVisitor.visitVarInsn(ALOAD, 1);

                    Label label2 = new Label();
                    methodVisitor.visitJumpInsn(IFNONNULL, label2);

                    Label label3 = new Label();
                    methodVisitor.visitLabel(label3);

                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitInsn(LCONST_0);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "setTag", "(Ljava/lang/Object;)V", false);
                    methodVisitor.visitLabel(label2);
                    methodVisitor.visitLineNumber(31, label2);
                    methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/Object"}, 0, null);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "getTag", "()Ljava/lang/Object;", false);
                    methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Long");
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
                    methodVisitor.visitInsn(LSUB);
                    methodVisitor.visitLdcInsn(new Long(1000L));
                    methodVisitor.visitInsn(LCMP);

                    Label label4 = new Label();
                    methodVisitor.visitJumpInsn(IFGE, label4);

                    Label label5 = new Label();
                    methodVisitor.visitLabel(label5);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "setTag", "(Ljava/lang/Object;)V", false);

                    Label label6 = new Label();
                    methodVisitor.visitLabel(label6);
                    methodVisitor.visitInsn(RETURN);
                    methodVisitor.visitLabel(label4);
                    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    methodVisitor.visitVarInsn(ALOAD, 0);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                    methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
                    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "setTag", "(Ljava/lang/Object;)V", false);



                }

                @Override
                public void visitMaxs(int maxStack, int maxLocals) {
                    super.visitMaxs(maxStack+2, maxLocals+1);
                }
            };
            return newMethodVisit;
        }

        return new MethodVisitor(api,methodVisitor) {

            @Override
            public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
                // 找到使用Lambda的OnClickListener
                if(name.equals("onClick") && descriptor.endsWith("Landroid/view/View$OnClickListener;")){
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
    public void visitEnd() {

        // 如果是目标类（实现了View.OnClickListener的类），则添加一个成员变量
        if(isTargetOnClickListenerClass) {
            FieldVisitor fv = cv.visitField(ACC_PRIVATE, "lastClickTime", "J", null, null);
            if (fv != null) {
                fv.visitEnd();

            }
            isTargetOnClickListenerClass = false;
        }


        super.visitEnd();
    }
}
