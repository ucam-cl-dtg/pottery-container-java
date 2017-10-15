/**
 * pottery-container-java - Within-container library for testing Java code Copyright © 2015 Andrew
 * Rice (acr31@cam.ac.uk)
 *
 * <p>This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Affero General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.cam.cl.dtg.teaching.programmingtest.java.bytecode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InstructionCounterTransformer implements ClassFileTransformer {

  public static boolean ENABLE_INSTRUCTION_COUNTING = false;

  private static final String[] blacklist = {
    "java.",
    "sun.",
    "org.objectweb.asm",
    "uk.ac.cam.cl.dtg.teaching.programmingtest.java",
    "com.sun.",
    "jdk.internal.",
  };

  private static final String[] allowedUnsafe = {"java.", "sun."};

  public static boolean isTransformable(String className) {
    for (String s : blacklist) {
      if (className.startsWith(s)) return false;
    }
    return true;
  }

  public static boolean isAllowedUnsafe(String className) {
    for (String s : allowedUnsafe) {
      if (className.startsWith(s)) return true;
    }
    return false;
  }

  @Override
  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classfileBuffer)
      throws IllegalClassFormatException {

    String dottedName = className.replaceAll("/", ".");

    if (!isTransformable(dottedName)) {
      return classfileBuffer;
    }

    ClassWriter classWriter = new ClassWriter(0);
    ClassReader classReader = new ClassReader(classfileBuffer);
    ClassVisitor classVisitor =
        new ClassVisitor(Opcodes.ASM5, classWriter) {
          @Override
          public MethodVisitor visitMethod(
              int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
            if (mv != null) {
              return new InstructionCounterMethodAdapter(mv);
            } else {
              return null;
            }
          }
        };
    try {
      classReader.accept(classVisitor, 0);
      byte[] result = classWriter.toByteArray();
      //            ClassReader r = new ClassReader(result);
      //			ClassVisitor v = new TraceClassVisitor(new PrintWriter(new
      // OutputStreamWriter(System.out)));
      //            r.accept(v, 0);

      return result;
    } catch (InstrumentationUnsafeError e) {
      if (isAllowedUnsafe(dottedName)) {
        return classfileBuffer;
      } else {
        throw e;
      }
    }
  }
}
