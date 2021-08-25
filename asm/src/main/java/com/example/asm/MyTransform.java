package com.example.asm;


import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MyTransform extends Transform {


    @Override
    public String getName() {
        return "my_transform_name";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        // 实现逻辑
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        if (outputProvider == null) {
            return;
        }
//        outputProvider.deleteAll();
        Collection<TransformInput> transformInputs = transformInvocation.getInputs();
        transformInputs.forEach(transformInput -> {
            // 存在两种输入，一种是目录，一种是jar文件（三方库）

//            Log.e("tag","test");
            // 处理目录输入
            transformInput.getDirectoryInputs().forEach(directoryInput -> {
                File directoryInputFile = directoryInput.getFile();
                // 找到转换输入中所有的class文件
                List<File> files = filterClassFiles(directoryInputFile);

                // TODO 编辑class文件，添加逻辑
                for (File file : files) {
                    System.out.println("file name=" + file.getName());
                    FileInputStream fis = null;
                    FileOutputStream fos = null;
                    try {
                        fis = new FileInputStream(file);
                        ClassReader classReader = new ClassReader(fis);
//
//                        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5) {
//                            @Override
//                            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//                                super.visit(version, access, name, signature, superName, interfaces);
//                                System.out.println("-----------------");
//                                System.out.println("className =" + name);
//                            }
//
//                            @Override
//                            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//                                System.out.println("visitMethod =" + name);
//                                return new MethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions)) {
//                                    @Override
//                                    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
//
//                                        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
//                                    }
//                                };
//                            }
//                        };

                        ClassWriter classWriter = new ClassWriter(0);

                        ClickAdapterClassVisitor toastVisitor = new ClickAdapterClassVisitor(Opcodes.ASM5,classWriter);
                        classReader.accept(toastVisitor, 0);

                        byte[] bytes = classWriter.toByteArray();

                        fis.close();

                        fos = new FileOutputStream(file);

                        fos.write(bytes);
                        fos.flush();
//                        fos.close();
//                        fis.close();
//                        fos.close();

                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    } finally {
//                        closeQuietly(fis);
                        closeQuietly(fos);
                    }
                }

                // 有输入就必须有输出，否则回出现类缺失的问题
                // 无论是否经过转换，都需要将输入目录复制到目标目录
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);

                try {
                    FileUtils.copyDirectory(directoryInput.getFile(), dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            // 处理 jar 输入
            transformInput.getJarInputs().forEach(jarInput -> {

                // 不修改，直接输出
                File jarInputFile = jarInput.getFile();
                File dest = outputProvider.getContentLocation(jarInput.getName(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);

                try {
                    FileUtils.copyFile(jarInputFile, dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private List<File> filterClassFiles(File file) {
        List<File> classFiles = new ArrayList<>();
        if (file != null) {
            listFiles(file, classFiles, new FileFilter() {
                @Override
                public boolean accept(File file2) {
                    return file2.getName().endsWith(".class");
                }
            });
        }
        return classFiles;
    }

    private void listFiles(File file, List<File> result, FileFilter filter) {
        if (result == null || file == null) {
            return;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    listFiles(child, result, filter);
                }
            }
        } else {
            if (filter == null || filter.accept(file)) {
                result.add(file);
            }
        }
    }
}
