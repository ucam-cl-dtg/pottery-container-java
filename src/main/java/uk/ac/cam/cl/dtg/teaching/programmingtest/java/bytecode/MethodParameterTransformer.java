/*
 * pottery-container-java - Within-container library for testing Java code
 * Copyright © 2015 Andrew Rice (acr31@cam.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.cam.cl.dtg.teaching.programmingtest.java.bytecode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodParameterTransformer implements ClassFileTransformer {

  public static List<MethodCallTrackingRequest> TRACKING_REQUESTS = new LinkedList<>();

  private class TrackMethodVisitor extends MethodVisitor {

    private Method method;

    TrackMethodVisitor(MethodVisitor mv, Method method) {
      super(Opcodes.ASM5, mv);
      this.method = method;
    }

    @Override
    public void visitCode() {
      super.visitCode();
      mv.visitVarInsn(Opcodes.ALOAD, 0);
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          Type.getInternalName(method.getDeclaringClass()),
          method.getName(),
          Type.getMethodDescriptor(method),
          false);
      mv.visitVarInsn(Opcodes.ASTORE, 0);
      mv.visitCode();
    }
  }

  private Map<String, Map<String, Method>> toTrack;

  MethodParameterTransformer() {
    toTrack = new HashMap<>();
    for (MethodCallTrackingRequest m : TRACKING_REQUESTS) {
      toTrack
          .computeIfAbsent(m.getClassToInstrument(), f -> new HashMap<>())
          .put(m.getMethodToInstrument(), m.getTrackerMethod());
    }
  }

  @Override
  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classfileBuffer) {

    Map<String, Method> classMap = toTrack.get(className.replace("/", "."));
    if (classMap != null) {
      ClassWriter classWriter = new ClassWriter(Opcodes.ASM5);
      ClassReader classReader = new ClassReader(classfileBuffer);
      ClassVisitor classVisitor =
          new ClassVisitor(Opcodes.ASM5, classWriter) {
            @Override
            public MethodVisitor visitMethod(
                int access, String name, String desc, String signature, String[] exceptions) {
              MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
              Method method = classMap.get(name);
              if (method == null) {
                return mv;
              }
              if (mv != null) {
                return new TrackMethodVisitor(mv, method);
              }
              return null;
            }
          };
      classReader.accept(classVisitor, 0);
      return classWriter.toByteArray();
    }
    return classfileBuffer;
  }
}
